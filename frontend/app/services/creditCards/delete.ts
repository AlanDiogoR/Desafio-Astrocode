export async function deleteCreditCard(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/credit-cards/${id}`)
}
