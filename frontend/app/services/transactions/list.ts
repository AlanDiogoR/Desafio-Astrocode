export interface TransactionApiResponse {
  id: string
  name: string
  amount: number
  date: string
  type: string
  bankAccountId: string
  categoryId: string
  createdAt?: string
  updatedAt?: string
}

export interface TransactionFilters {
  year?: number
  month?: number
  bankAccountId?: string
  type?: 'INCOME' | 'EXPENSE'
}

export async function listTransactions(filters?: TransactionFilters): Promise<TransactionApiResponse[]> {
  const { $api } = useNuxtApp()
  const params = new URLSearchParams()

  if (filters?.year) params.append('year', String(filters.year))
  if (filters?.month) params.append('month', String(filters.month))
  if (filters?.bankAccountId) params.append('bankAccountId', filters.bankAccountId)
  if (filters?.type) params.append('type', filters.type)

  const query = params.toString()
  const url = query ? `/transactions?${query}` : '/transactions'
  const { data } = await $api.get<TransactionApiResponse[]>(url)
  return Array.isArray(data) ? data : []
}
