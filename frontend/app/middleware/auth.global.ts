interface FetchError {
  status?: number
  response?: { status?: number }
}

const PUBLIC_PATHS = ['/login', '/register'] as const

function isPublicRoute(path: string): path is (typeof PUBLIC_PATHS)[number] {
  return PUBLIC_PATHS.includes(path as (typeof PUBLIC_PATHS)[number])
}

function isUnauthorized(error: unknown): boolean {
  const err = error as FetchError
  return err?.status === 401 || err?.response?.status === 401
}

export default defineNuxtRouteMiddleware(async (to) => {
  const authStore = useAuthStore()

  if (isPublicRoute(to.path)) {
    if (authStore.hasToken) return navigateTo('/dashboard')
    return
  }

  if (!authStore.hasToken) return navigateTo('/login')

  const { refetch } = useUser()
  const result = await refetch()

  if (result.isError && isUnauthorized(result.error)) {
    authStore.clearAuth()
    if (import.meta.client) {
      const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
      toast.error('Sua sessão expirou. Por favor, faça login novamente.')
    }
    return navigateTo('/login')
  }
})
