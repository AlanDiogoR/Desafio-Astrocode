interface BankAccountApiResponse {
  id: string
  name: string
  currentBalance?: number
  current_balance?: number
  type: string
  color: string | null
}

interface CreateAccountPayload {
  name: string
  initialBalance: number
  type: string
  color: string | null
}

interface UpdateAccountPayload {
  name?: string
  type?: string
  color?: string | null
}

export async function getAllAccounts(): Promise<BankAccountApiResponse[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<BankAccountApiResponse[]>('/accounts')
  return Array.isArray(data) ? data : []
}

export async function createAccount(payload: CreateAccountPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/accounts', payload)
}

export async function updateAccount(id: string, payload: UpdateAccountPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.put(`/accounts/${id}`, payload)
}

export async function deleteAccount(id: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete(`/accounts/${id}`)
}
