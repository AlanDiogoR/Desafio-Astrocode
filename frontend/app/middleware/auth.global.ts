const PUBLIC_PATHS = ['/login', '/register']

function isPublicRoute(path: string): boolean {
  return PUBLIC_PATHS.includes(path)
}

export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  if (isPublicRoute(to.path)) {
    if (authStore.hasToken) {
      return navigateTo('/dashboard')
    }
    return
  }

  if (!authStore.hasToken) {
    return navigateTo('/login')
  }
})
