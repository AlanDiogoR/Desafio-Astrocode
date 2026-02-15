interface CreateTransactionPayload {
  name: string
  amount: number
  date: string
  type: string
  bankAccountId: string
  categoryId: string
}

interface UpdateTransactionPayload {
  name?: string
  amount?: number
  date?: string
  type?: string
  bankAccountId?: string
  categoryId?: string
}

export async function createTransaction(payload: CreateTransactionPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/transactions', payload)
}

export async function updateTransaction(id: string, payload: UpdateTransactionPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/transactions/${id}`, payload)
}

export async function deleteTransaction(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/transactions/${id}`)
}
