export async function deleteTransaction(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/transactions/${id}`)
}
