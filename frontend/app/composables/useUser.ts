import { useQuery } from '@tanstack/vue-query'
import type { User } from '~/stores/auth'

const STALE_TIME_MS = 5 * 60 * 1000

export function useUser() {
  const authStore = useAuthStore()
  const { isValid: hasApiConfig } = useApiConfig()
  const { authToken } = useAuthCookies()

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
      const { $api } = useNuxtApp()
      const { data } = await $api.get<User>('/users/me')
      return data
    },
    enabled: computed(() => !!hasApiConfig && !!authToken.value),
    staleTime: STALE_TIME_MS,
    refetchOnWindowFocus: true,
  })

  watch(userData, (newUser) => {
    if (newUser) {
      authStore.setUser({
        id: newUser.id,
        name: newUser.name,
        email: newUser.email,
        plan: (newUser.plan ?? 'FREE') as import('~/stores/auth').PlanType,
        isPro: newUser.isPro ?? false,
        isElite: newUser.isElite ?? false,
        planExpiresAt: newUser.planExpiresAt ?? null,
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
