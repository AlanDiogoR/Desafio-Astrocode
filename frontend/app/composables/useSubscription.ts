import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { subscriptionService } from '~/services/subscription/subscriptionService'

export function useSubscription() {
  const queryClient = useQueryClient()
  const { isValid: hasApiConfig } = useApiConfig()
  const { authToken } = useAuthCookies()

  const { data: subscriptionStatus, isLoading } = useQuery({
    queryKey: ['subscription-status'],
    queryFn: () => subscriptionService.getStatus(),
    staleTime: 1000 * 60 * 5,
    enabled: computed(() => !!hasApiConfig && !!authToken.value),
  })

  const planType = computed(
    () => subscriptionStatus.value?.planType ?? 'FREE',
  )

  const isPro = computed(() => subscriptionStatus.value?.isActive === true)
  const isFree = computed(() => !isPro.value)
  const isElite = computed(() => subscriptionStatus.value?.isElite === true)

  const hasOpenFinance = computed(() => planType.value === 'ANNUAL' && isPro.value)

  const hasCreditCards = computed(() =>
    ['MONTHLY', 'SEMIANNUAL', 'ANNUAL'].includes(planType.value) && isPro.value,
  )

  function invalidate() {
    queryClient.invalidateQueries({ queryKey: ['subscription-status'] })
  }

  return {
    subscriptionStatus,
    isPro,
    isFree,
    isElite,
    isLoading,
    invalidate,
    refresh: invalidate,
    planType,
    hasOpenFinance,
    hasCreditCards,
  }
}
