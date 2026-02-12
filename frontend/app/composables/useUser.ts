import { useQuery } from '@tanstack/vue-query'
import type { User } from '~/stores/auth'

const STALE_TIME_MS = 5 * 60 * 1000

export function useUser() {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  const queryKey = ['user', 'me'] as const

  const {
    data: userData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey,
    queryFn: async (): Promise<User> => {
      const token = authStore.token
      if (!token) throw new Error('No token')

      const url = `${config.public.apiBase}/users/me`
      return $fetch<User>(url, {
        headers: { Authorization: `Bearer ${token}` },
      })
    },
    enabled: computed(() => !!authStore.token),
    staleTime: STALE_TIME_MS,
    refetchOnWindowFocus: true,
  })

  watch(userData, (newUser) => {
    if (newUser) {
      authStore.setUser({
        id: newUser.id,
        name: newUser.name,
        email: newUser.email,
      })
    }
  }, { immediate: true })

  return {
    user: userData,
    isPending,
    isError,
    error,
    refetch,
  }
}
