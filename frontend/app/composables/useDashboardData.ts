import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getDashboard } from '~/services/dashboard'

export const DASHBOARD_QUERY_KEY = ['dashboard'] as const

export function useDashboardData() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()

  const {
    data: dashboardData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: DASHBOARD_QUERY_KEY,
    queryFn: getDashboard,
    enabled: computed(() => !!authStore.token),
  })

  const totalBalance = computed(() => dashboardData.value?.totalBalance ?? 0)
  const totalIncomeMonth = computed(() => dashboardData.value?.totalIncomeMonth ?? 0)
  const totalExpenseMonth = computed(() => dashboardData.value?.totalExpenseMonth ?? 0)

  function invalidateDashboard() {
    return queryClient.invalidateQueries({ queryKey: DASHBOARD_QUERY_KEY })
  }

  return {
    totalBalance,
    totalIncomeMonth,
    totalExpenseMonth,
    isPending,
    isError,
    error,
    refetch,
    invalidateDashboard,
  }
}
