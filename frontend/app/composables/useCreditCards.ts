import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import { listCreditCards } from '~/services/creditCards'
import type { CreditCard } from '~/types/creditCard'

const CREDIT_CARDS_QUERY_KEY = ['creditCards'] as const

export function useCreditCards() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const {
    data: creditCardsData,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: CREDIT_CARDS_QUERY_KEY,
    queryFn: listCreditCards,
    enabled: computed(() => !!authStore.user),
    staleTime: 0,
  })

  const creditCards = computed<CreditCard[]>(
    () => creditCardsData.value ?? []
  )

  const hasCreditCards = computed(() => creditCards.value.length > 0)

  const totalCreditBillAmount = computed(() =>
    creditCards.value.reduce((sum, cc) => sum + (cc.currentBillAmount ?? 0), 0)
  )

  async function invalidateCreditCards() {
    await queryClient.refetchQueries({ queryKey: CREDIT_CARDS_QUERY_KEY })
  }

  if (import.meta.client) {
    watch(isError, (v) => {
      if (v)
        toast.error(
          getErrorMessage(error.value ?? new Error(), 'Erro ao carregar cartões.')
        )
    })
  }

  return {
    creditCards,
    hasCreditCards,
    totalCreditBillAmount,
    isPending,
    isError,
    invalidateCreditCards,
  }
}
