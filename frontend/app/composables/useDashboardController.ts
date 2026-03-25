import { computed } from 'vue'
import { useBankAccounts } from '~/composables/useBankAccounts'
import { useCreditCards } from '~/composables/useCreditCards'
import { useDashboardData } from '~/composables/useDashboardData'
import { useGoals } from '~/composables/useGoals'

export function useDashboardController() {
  const { accounts, isPending: accountsPending } = useBankAccounts()
  const { goals, isPending: goalsPending } = useGoals()
  const { creditCards, isPending: creditCardsPending } = useCreditCards()
  const { totalBalance, isPending: dashboardPending } = useDashboardData()

  const areValuesVisible = useState<boolean>('dashboard-areValuesVisible', () => true)

  const isLoading = computed(
    () => accountsPending.value || goalsPending.value || creditCardsPending.value || dashboardPending.value
  )

  const formattedTotalBalance = computed(() => {
    if (!areValuesVisible.value) return '••••••'
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(totalBalance.value)
  })

  return {
    accounts,
    goals,
    creditCards,
    totalBalance,
    formattedTotalBalance,
    areValuesVisible,
    isLoading,
  }
}
