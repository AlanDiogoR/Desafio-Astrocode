import axios from 'axios'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtPlugin(() => {
  const authStore = useAuthStore()

  // Configurar baseURL do Axios para Railway
  const api = axios.create({
    baseURL: 'https://desafio-astrocode-production.up.railway.app/api',
  })

  // Request Interceptor: Adicionar token de autenticação do cookie
  api.interceptors.request.use(
    (config) => {
      // Recuperar token do cookie via store (que usa useCookie)
      const token = authStore.token
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // Response Interceptor: Tratar erros 401
  api.interceptors.response.use(
    (response) => {
      return response
    },
    (error) => {
      if (error.response?.status === 401) {
        // Limpar estado de autenticação (limpa cookie também)
        authStore.clearAuth()

        // Redirecionar para login
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
