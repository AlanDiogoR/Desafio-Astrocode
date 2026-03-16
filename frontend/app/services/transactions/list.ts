export interface TransactionApiResponse {
  id: string
  name: string
  amount: number
  date: string
  type: string
  bankAccountId: string
  categoryId: string
  isRecurring?: boolean
  frequency?: string
  createdAt?: string
  updatedAt?: string
}

export interface TransactionFilters {
  year?: number
  month?: number
  bankAccountId?: string
  type?: 'INCOME' | 'EXPENSE'
  page?: number
  size?: number
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export async function listTransactions(filters?: TransactionFilters): Promise<PaginatedResponse<TransactionApiResponse>> {
  const { $api } = useNuxtApp()
  const params = new URLSearchParams()

  if (filters?.year) params.append('year', String(filters.year))
  if (filters?.month) params.append('month', String(filters.month))
  if (filters?.bankAccountId) params.append('bankAccountId', filters.bankAccountId)
  if (filters?.type) params.append('type', filters.type)
  if (filters?.page !== undefined) params.append('page', String(filters.page))
  if (filters?.size !== undefined) params.append('size', String(filters.size))

  const query = params.toString()
  const url = query ? `/transactions?${query}` : '/transactions'
  const { data } = await $api.get<PaginatedResponse<TransactionApiResponse>>(url)

  if (!data) {
    return { content: [], totalElements: 0, totalPages: 0, number: 0, size: 20 }
  }
  return {
    content: Array.isArray(data.content) ? data.content : [],
    totalElements: data.totalElements ?? 0,
    totalPages: data.totalPages ?? 0,
    number: data.number ?? 0,
    size: data.size ?? 20,
  }
}
