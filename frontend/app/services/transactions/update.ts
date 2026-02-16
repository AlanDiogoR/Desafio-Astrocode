interface UpdateTransactionPayload {
  name?: string
  amount?: number
  date?: string
  type?: string
  bankAccountId?: string
  categoryId?: string
}

export async function updateTransaction(id: string, payload: UpdateTransactionPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/transactions/${id}`, payload)
}
