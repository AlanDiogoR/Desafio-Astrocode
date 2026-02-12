import axios from 'axios'

export default defineNuxtPlugin(() => {
  const authStore = useAuthStore()

  const api = axios.create({
    baseURL: 'https://desafio-astrocode-production.up.railway.app/api',
  })

  api.interceptors.request.use(
    (config) => {
      const token = authStore.token
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const isAuthRequest = error.config?.url?.includes('/auth/login')
      if (error.response?.status === 401 && !isAuthRequest) {
        authStore.clearAuth()
        navigateTo('/login')
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
