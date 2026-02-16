export interface GoalApiResponse {
  id: string
  name: string
  targetAmount: number
  currentAmount: number
  color: string | null
  progressPercentage: number
  status: string
  endDate: string | null
  createdAt: string
  updatedAt: string
}

export async function listGoals(): Promise<GoalApiResponse[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<GoalApiResponse[]>('/goals')
  return Array.isArray(data) ? data : []
}
