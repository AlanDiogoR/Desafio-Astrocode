export interface BankAccountResponse {
  id: string
  name: string
  currentBalance: number
  type: string
  color: string | null
  createdAt: string
  updatedAt: string
}

export async function syncOpenFinanceAccounts(
  $api: { post: (url: string, body: { itemId: string }) => Promise<{ data: BankAccountResponse[] }> },
  itemId: string
): Promise<BankAccountResponse[]> {
  const { data } = await $api.post('/open-finance/sync', { itemId })
  return data
}
