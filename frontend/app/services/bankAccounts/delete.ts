export async function deleteBankAccount(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/accounts/${id}`)
}
