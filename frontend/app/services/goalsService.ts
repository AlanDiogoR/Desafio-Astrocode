interface CreateGoalPayload {
  name: string
  targetAmount: number
  endDate: string | null
  color: string | null
}

interface UpdateGoalPayload {
  name?: string
  targetAmount?: number
  endDate?: string | null
  color?: string | null
}

export async function getAllGoals(): Promise<unknown[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<unknown[]>('/goals')
  return Array.isArray(data) ? data : []
}

export async function createGoal(payload: CreateGoalPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/goals', payload)
}

export async function updateGoal(id: string, payload: UpdateGoalPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/goals/${id}`, payload)
}

export async function deleteGoal(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/goals/${id}`)
}
