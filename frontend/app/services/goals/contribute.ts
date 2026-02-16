interface ContributePayload {
  amount: number
  bankAccountId: string
}

export async function contributeToGoal(goalId: string, payload: ContributePayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.patch(`/goals/${goalId}/contribute`, payload)
}
