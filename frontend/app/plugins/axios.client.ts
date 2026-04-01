import axios from 'axios'
import type { LoginResponse } from '~/services/auth/login'

const API_CONFIG_ERROR = 'API_CONFIG_MISSING'

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()
  const { authToken, refreshToken, setSessionTokens, clearSessionTokens } = useAuthCookies()
  const { showUpgrade } = usePlanUpgradeGate()

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

  let refreshInFlight: Promise<void> | null = null

  async function applySessionFromLoginResponse(data: LoginResponse) {
    setSessionTokens(data.accessToken, data.refreshToken)
    authStore.setUser({
      id: data.id,
      name: data.name,
      email: data.email,
      plan: (data.plan ?? 'FREE') as import('~/stores/auth').PlanType,
      isPro: data.isPro ?? false,
      isElite: data.isElite ?? false,
      planExpiresAt: data.planExpiresAt ?? null,
    })
  }

  async function performRefresh(): Promise<void> {
    const rt = refreshToken.value
    if (!rt) {
      throw new Error('NO_REFRESH')
    }
    if (!refreshInFlight) {
      refreshInFlight = (async () => {
        const { data } = await axios.post<LoginResponse>(
          `${apiBase}/auth/refresh`,
          { refreshToken: rt },
          { timeout: 30_000, withCredentials: true, headers: { 'Content-Type': 'application/json' } }
        )
        await applySessionFromLoginResponse(data)
      })().finally(() => {
        refreshInFlight = null
      })
    }
    await refreshInFlight
  }

  function redirectToLoginWithToast() {
    const path = typeof window !== 'undefined' ? window.location?.pathname : ''
    const isAuthPage =
      path === '/login' ||
      path === '/register' ||
      path.startsWith('/forgot-password') ||
      path.startsWith('/verify-email')
    if (import.meta.client && !isAuthPage) {
      const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
      toast.error('Sessão expirada. Faça login novamente.')
      const currentPath = typeof window !== 'undefined' ? window.location?.pathname + window.location?.search : ''
      void navigateTo({ path: '/login', query: currentPath ? { redirect: currentPath } : undefined })
    }
  }

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
      const token = authToken.value
      if (token) {
        req.headers.Authorization = `Bearer ${token}`
      }
      return req
    },
    (error) => Promise.reject(error)
  )

  api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config as typeof error.config & { _retry?: boolean }
      const status = error.response?.status
      const url = String(originalRequest?.url ?? '')

      const isRefreshCall = url.includes('/auth/refresh')
      const isLoginCall = url.includes('/auth/login')

      if (status === 401 && originalRequest && !originalRequest._retry && !isRefreshCall && !isLoginCall) {
        originalRequest._retry = true
        try {
          await performRefresh()
          const t = authToken.value
          if (t) {
            originalRequest.headers.Authorization = `Bearer ${t}`
          }
          return api(originalRequest)
        } catch {
          clearSessionTokens()
          authStore.clearAuth()
          redirectToLoginWithToast()
          return Promise.reject(error)
        }
      }

      const isAuthRequest = url.includes('/auth/')
      if (status === 401 && !isAuthRequest && !isRefreshCall && !isLoginCall) {
        clearSessionTokens()
        authStore.clearAuth()
        redirectToLoginWithToast()
      }

      if (status === 402 && import.meta.client) {
        const data = error.response?.data as { message?: string; feature?: string } | undefined
        showUpgrade(
          data?.message ?? 'Faça upgrade para continuar.',
          data?.feature ?? null,
        )
      }

      return Promise.reject(error)
    }
  )

  return {
    provide: {
      api,
      applySessionFromLoginResponse,
    },
  }
})
