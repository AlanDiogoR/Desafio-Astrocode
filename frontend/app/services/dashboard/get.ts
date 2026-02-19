export interface DashboardResponse {
  totalBalance: number
  totalIncomeMonth: number
  totalExpenseMonth: number
}

export async function getDashboard(): Promise<DashboardResponse> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<DashboardResponse>('/dashboard')
  if (!data) {
    return { totalBalance: 0, totalIncomeMonth: 0, totalExpenseMonth: 0 }
  }
  return {
    totalBalance: Number(data.totalBalance ?? 0),
    totalIncomeMonth: Number(data.totalIncomeMonth ?? 0),
    totalExpenseMonth: Number(data.totalExpenseMonth ?? 0),
  }
}
