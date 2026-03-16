interface UpdateAccountPayload {
  name: string
  initialBalance: number
  type: string
  color: string | null
}

export async function updateBankAccount(id: string, payload: UpdateAccountPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/accounts/${id}`, payload)
}
