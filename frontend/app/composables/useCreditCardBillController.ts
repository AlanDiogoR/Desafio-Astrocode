import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import {
  getCreditCardCurrentBill,
  getCreditCardBillHistory,
  payCreditCardBill,
} from '~/services/creditCards'
import type { CreditCardBill } from '~/types/creditCard'

interface UseCreditCardBillControllerOptions {
  creditCardId: Ref<string | null>
}

export function useCreditCardBillController(
  options: UseCreditCardBillControllerOptions
) {
  const { creditCardId } = options
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const currentBillQueryKey = computed(
    () => ['creditCardBill', creditCardId.value] as const
  )
  const billHistoryQueryKey = computed(
    () => ['creditCardBillHistory', creditCardId.value] as const
  )

  const {
    data: currentBill,
    isPending: isBillPending,
    refetch: refetchBill,
  } = useQuery({
    queryKey: currentBillQueryKey,
    queryFn: () =>
      getCreditCardCurrentBill(creditCardId.value!),
    enabled: computed(() => !!creditCardId.value),
    staleTime: 0,
  })

  const {
    data: billHistory,
    isPending: isHistoryPending,
    refetch: refetchHistory,
  } = useQuery({
    queryKey: billHistoryQueryKey,
    queryFn: () =>
      getCreditCardBillHistory(creditCardId.value!),
    enabled: computed(() => !!creditCardId.value),
    staleTime: 0,
  })

  const isPayLoading = ref(false)

  async function payBill(
    billId: string,
    payload: { bankAccountId: string; payDate: string; amount: number }
  ): Promise<CreditCardBill | null> {
    isPayLoading.value = true
    try {
      const result = await payCreditCardBill(billId, payload)
      toast.success('Pagamento registrado com sucesso!')
      await refetchBill()
      await refetchHistory()
      queryClient.invalidateQueries({ queryKey: ['creditCards'] })
      queryClient.invalidateQueries({ queryKey: ['transactions'] })
      queryClient.invalidateQueries({ queryKey: ['bankAccounts'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      return result
    } catch (err: unknown) {
      toast.error(
        getErrorMessage(err, 'Erro ao registrar pagamento. Tente novamente.')
      )
      return null
    } finally {
      isPayLoading.value = false
    }
  }

  return {
    currentBill: computed(() => currentBill.value ?? null),
    billHistory: computed(() => billHistory.value ?? []),
    isBillPending,
    isHistoryPending,
    isPayLoading,
    payBill,
    refetchBill,
    refetchHistory,
  }
}
