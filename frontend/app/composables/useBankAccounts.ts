import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getAllAccounts } from '~/services/bankAccountsService'

type AccountType = 'checking' | 'investment' | 'cash'

export interface BankAccount {
  id: string
  name: string
  balance: number
  type: AccountType
  color: string
}

interface BankAccountApiResponse {
  id: string
  name: string
  currentBalance?: number
  current_balance?: number
  type: string
  color: string | null
}

const BANK_ACCOUNTS_QUERY_KEY = ['bankAccounts'] as const

function mapApiToBankAccount(raw: BankAccountApiResponse): BankAccount {
  const type = raw.type?.toLowerCase() as AccountType
  const rawAny = raw as Record<string, unknown>
  const currentBalance = raw.currentBalance ?? rawAny.current_balance
  const balanceValue =
    currentBalance != null ? Number(currentBalance) : 0
  return {
    id: raw.id,
    name: raw.name,
    balance: balanceValue,
    type: type === 'checking' || type === 'investment' || type === 'cash' ? type : 'checking',
    color: raw.color ?? '#868E96',
  }
}

export function useBankAccounts() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const {
    data: accountsData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: BANK_ACCOUNTS_QUERY_KEY,
    queryFn: async (): Promise<BankAccount[]> => {
      const data = await getAllAccounts()
      return data.map(mapApiToBankAccount)
    },
    enabled: computed(() => !!authStore.token),
  })

  const accounts = computed<BankAccount[]>(() => accountsData.value ?? [])

  const hasAccounts = computed(() => accounts.value.length > 0)

  const totalBalance = computed(() =>
    accounts.value.reduce((sum, a) => sum + a.balance, 0)
  )

  function invalidateBankAccounts() {
    queryClient.invalidateQueries({ queryKey: BANK_ACCOUNTS_QUERY_KEY })
  }

  if (import.meta.client) {
    watch(isError, (v) => {
      if (v) toast.error('Erro ao carregar contas. Tente novamente.')
    })
  }

  return {
    accounts,
    hasAccounts,
    totalBalance,
    isPending,
    isError,
    error,
    refetch,
    invalidateBankAccounts,
  }
}
