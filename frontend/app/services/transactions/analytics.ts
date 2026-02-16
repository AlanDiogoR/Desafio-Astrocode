export interface CategoryExpenseItem {
  categoryId: string
  categoryName: string
  totalAmount: number
}

export interface MonthlySummaryResponse {
  totalExpense: number
  byCategory: CategoryExpenseItem[]
}

export async function getMonthlySummary(
  year: number,
  month: number,
): Promise<MonthlySummaryResponse> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<MonthlySummaryResponse>(
    `/transactions/analytics/monthly-summary?year=${year}&month=${month}`,
  )
  return data ?? { totalExpense: 0, byCategory: [] }
}
