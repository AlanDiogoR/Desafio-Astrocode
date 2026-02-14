type AccountType = 'checking' | 'investment' | 'cash'

export interface BankAccount {
  id: string
  name: string
  balance: number
  type: AccountType
  color: string
}

const MOCK_ACCOUNTS = ref<BankAccount[]>([
  { id: '1', name: 'Nubank', balance: 5420, type: 'checking', color: '#820ad1' },
  { id: '2', name: 'Carteira', balance: 380, type: 'cash', color: '#12b886' },
  { id: '2', name: 'Rico', balance: 571, type: 'investment', color: '#ec6e27' },
])

export function useBankAccounts() {
  const accounts = MOCK_ACCOUNTS

  const hasAccounts = computed(() => (accounts?.value ?? []).length > 0)

  const totalBalance = computed(() =>
    (accounts?.value ?? []).reduce((sum, a) => sum + a.balance, 0)
  )

  function addAccount(account: BankAccount) {
    accounts.value = [...(accounts?.value ?? []), account]
  }

  function setAccounts(list: BankAccount[]) {
    accounts.value = list
  }

  return {
    accounts,
    hasAccounts,
    totalBalance,
    addAccount,
    setAccounts,
  }
}
