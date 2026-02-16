interface WithdrawPayload {
  amount: number
  bankAccountId: string
}

export async function withdrawFromGoal(goalId: string, payload: WithdrawPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.patch(`/goals/${goalId}/withdraw`, payload)
}
