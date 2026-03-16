import axios from 'axios'

const API_CONFIG_ERROR = 'API_CONFIG_MISSING'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  const apiBase = config.public.apiBase as string | undefined
  const hasValidApiBase = typeof apiBase === 'string' && apiBase.trim().length > 0

  if (!hasValidApiBase) {
    if (import.meta.client) {
      console.error('[Grivy] NUXT_PUBLIC_API_BASE está vazio ou indefinido. Defina a URL absoluta da API (ex: https://api.exemplo.com/api)')
    }
  }

  const api = axios.create({
    baseURL: hasValidApiBase ? apiBase : '',
    timeout: 30_000,
    withCredentials: true,
  })

  api.interceptors.request.use(
    (req) => {
      if (!hasValidApiBase) {
        const err = new Error(API_CONFIG_ERROR) as Error & { code?: string }
        err.code = API_CONFIG_ERROR
        return Promise.reject(err)
      }
      if (!req.baseURL || req.baseURL.startsWith('/')) {
        const err = new Error(API_CONFIG_ERROR) as Error & { code?: string }
        err.code = API_CONFIG_ERROR
        return Promise.reject(err)
      }
      return req
    },
    (error) => Promise.reject(error)
  )

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const isAuthRequest = error.config?.url?.includes('/auth/')
      const isBootstrapRequest = error.config?.url?.includes('/users/me')
      const path = typeof window !== 'undefined' ? window.location?.pathname : ''
      const isAuthPage = path === '/login' || path === '/register' || path.startsWith('/forgot-password')
      if (error.response?.status === 401 && !isAuthRequest) {
        authStore.clearAuth()
        if (!isAuthPage && !isBootstrapRequest) {
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
