import axios from 'axios'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  const api = axios.create({
    baseURL: config.public.apiBase,
    timeout: 30_000,
    withCredentials: true, // envia cookie httpOnly auth_token automaticamente
  })

  api.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
  )

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const isAuthRequest = error.config?.url?.includes('/auth/')
      const path = typeof window !== 'undefined' ? window.location?.pathname : ''
      const isAuthPage = path === '/login' || path === '/register' || path.startsWith('/forgot-password')
      if (error.response?.status === 401 && !isAuthRequest) {
        authStore.clearAuth()
        if (!isAuthPage) {
          const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
          toast.error('Sessão expirada. Faça login novamente.')
          navigateTo('/login')
        }
      }
      return Promise.reject(error)
    }
  )

  return {
    provide: {
      api,
    },
  }
})
