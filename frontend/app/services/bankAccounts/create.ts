interface CreateAccountPayload {
  name: string
  initialBalance: number
  type: string
  color: string | null
}

export async function createBankAccount(payload: CreateAccountPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/accounts', payload)
}
