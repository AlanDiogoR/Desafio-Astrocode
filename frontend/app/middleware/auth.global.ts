interface FetchError {
  status?: number
  response?: { status?: number }
}

const PUBLIC_PATHS = ['/login', '/register', '/forgot-password', '/planos'] as const

function isPublicRoute(path: string): path is (typeof PUBLIC_PATHS)[number] {
  return PUBLIC_PATHS.includes(path as (typeof PUBLIC_PATHS)[number])
}

function isUnauthorized(error: unknown): boolean {
  const err = error as FetchError
  return err?.status === 401 || err?.response?.status === 401
}

export default defineNuxtRouteMiddleware(async (to) => {
  const authStore = useAuthStore()
  const { isValid: hasApiConfig } = useApiConfig()

  if (!hasApiConfig) return

  const isPublic = isPublicRoute(to.path)

  if (authStore.isLoggedIn) {
    if (isPublic && (to.path === '/login' || to.path === '/register')) {
      return navigateTo('/dashboard')
    }
    if (to.path === '/planos') {
      return navigateTo('/dashboard/planos')
    }
    return
  }

  const { refetch } = useUser()
  const result = await refetch()

  if (result.data && !result.isError) {
    const { mapApiUserToStoreUser } = await import('~/utils/mapUser')
    authStore.setUser(mapApiUserToStoreUser(result.data))
    if (to.path === '/login' || to.path === '/register') {
      return navigateTo('/dashboard')
    }
    return
  }

  if (isPublic) return

  authStore.clearAuth()
  if (result.isError && isUnauthorized(result.error) && import.meta.client) {
    const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
    toast.error('Sua sessão expirou. Por favor, faça login novamente.')
  }
  return navigateTo({ path: '/login', query: { redirect: to.fullPath } })
})
