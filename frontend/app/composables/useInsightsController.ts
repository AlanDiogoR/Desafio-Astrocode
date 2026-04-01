import { useQuery } from '@tanstack/vue-query'
import { getMonthlySummary } from '~/services/transactions'

const INSIGHT_THRESHOLD = 0.4

export function useInsightsController(year: Ref<number>, month: Ref<number>) {
  const authStore = useAuthStore()
  const { data: summary, isPending } = useQuery({
    queryKey: ['monthly-summary', year, month],
    queryFn: () => getMonthlySummary(year.value, month.value),
    enabled: computed(() => !!authStore.user && !!year.value && !!month.value),
  })

  const topCategory = computed(() => {
    const byCat = summary.value?.byCategory ?? []
    if (byCat.length === 0) return null
    const top = byCat[0]
    const total = summary.value?.totalExpense ?? 0
    if (total <= 0) return null
    const percentage = (top.totalAmount / total) * 100
    if (percentage <= INSIGHT_THRESHOLD * 100) return null
    return {
      categoryName: top.categoryName,
      percentage: Math.round(percentage),
      totalAmount: top.totalAmount,
    }
  })

  /** Maior categoria de despesa do mês (para insight, mesmo abaixo do limite do SpendingAlert). */
  const dominantExpenseShare = computed(() => {
    const byCat = summary.value?.byCategory ?? []
    if (byCat.length === 0) return null
    const top = byCat[0]
    const total = summary.value?.totalExpense ?? 0
    if (total <= 0) return null
    const percentage = Math.round((top.totalAmount / total) * 100)
    return {
      categoryName: top.categoryName,
      percentage,
      totalAmount: top.totalAmount,
    }
  })

  const shouldShowAlert = computed(() => topCategory.value !== null)

  const byCategory = computed(() => summary.value?.byCategory ?? [])
  const totalExpense = computed(() => summary.value?.totalExpense ?? 0)

  return {
    topCategory,
    dominantExpenseShare,
    shouldShowAlert,
    isPending,
    byCategory,
    totalExpense,
  }
}
