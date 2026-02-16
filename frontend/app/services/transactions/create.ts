interface CreateTransactionPayload {
  name: string
  amount: number
  date: string
  type: string
  bankAccountId: string
  categoryId: string
}

export async function createTransaction(payload: CreateTransactionPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/transactions', payload)
}
