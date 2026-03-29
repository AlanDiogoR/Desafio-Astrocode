import { useQuery } from '@tanstack/vue-query'
import { subscriptionService } from '~/services/subscription/subscriptionService'

export function useSubscription() {
  const { isValid: hasApiConfig } = useApiConfig()
  const { authToken } = useAuthCookies()

  const { data: subscriptionStatus, isLoading } = useQuery({
    queryKey: ['subscription-status'],
    queryFn: () => subscriptionService.getStatus(),
    staleTime: 1000 * 60 * 5,
    enabled: computed(() => !!hasApiConfig && !!authToken.value),
  })

  const isPro = computed(() => subscriptionStatus.value?.isActive === true)
  const isFree = computed(() => !isPro.value)

  return { subscriptionStatus, isPro, isFree, isLoading }
}
