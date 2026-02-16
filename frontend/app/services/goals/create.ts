interface CreateGoalPayload {
  name: string
  targetAmount: number
  endDate: string | null
  color: string | null
}

export async function createGoal(payload: CreateGoalPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/goals', payload)
}
