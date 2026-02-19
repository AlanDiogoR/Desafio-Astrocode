export function useDashboardController() {
  const { accounts, isPending: accountsPending } = useBankAccounts()
  const { goals, isPending: goalsPending } = useGoals()
  const { totalBalance, isPending: dashboardPending } = useDashboardData()

  const areValuesVisible = useState<boolean>('dashboard-areValuesVisible', () => true)

  const isLoading = computed(
    () => accountsPending.value || goalsPending.value || dashboardPending.value
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
    totalBalance,
    formattedTotalBalance,
    areValuesVisible,
    isLoading,
  }
}
