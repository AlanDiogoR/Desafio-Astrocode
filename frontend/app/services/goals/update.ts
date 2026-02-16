interface UpdateGoalPayload {
  name?: string
  targetAmount?: number
  endDate?: string | null
  color?: string | null
}

export async function updateGoal(id: string, payload: UpdateGoalPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/goals/${id}`, payload)
}
