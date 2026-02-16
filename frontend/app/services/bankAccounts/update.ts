interface UpdateAccountPayload {
  name?: string
  type?: string
  color?: string | null
}

export async function updateBankAccount(id: string, payload: UpdateAccountPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/accounts/${id}`, payload)
}
