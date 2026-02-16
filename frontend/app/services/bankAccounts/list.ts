export interface BankAccountApiResponse {
  id: string
  name: string
  currentBalance?: number
  current_balance?: number
  type: string
  color: string | null
}

export async function listBankAccounts(): Promise<BankAccountApiResponse[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<BankAccountApiResponse[]>('/accounts')
  return Array.isArray(data) ? data : []
}
