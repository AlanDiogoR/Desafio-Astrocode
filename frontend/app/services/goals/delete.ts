export async function deleteGoal(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/goals/${id}`)
}
