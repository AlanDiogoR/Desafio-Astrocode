import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import { listBankAccounts } from '~/services/bankAccounts'

type AccountType = 'checking' | 'investment' | 'cash'

export interface BankAccount {
  id: string
  name: string
  balance: number
  type: AccountType
  color: string
}

interface BankAccountApiResponseRaw {
  id: string
  name: string
  currentBalance?: number
  current_balance?: number
  type: string
  color: string | null
}

const BANK_ACCOUNTS_QUERY_KEY = ['bankAccounts'] as const

function mapApiToBankAccount(raw: BankAccountApiResponseRaw): BankAccount {
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
      const data = await listBankAccounts()
      return data.map(mapApiToBankAccount)
    },
    enabled: computed(() => !!authStore.token),
    staleTime: 0,
  })

  const accounts = computed<BankAccount[]>(() => accountsData.value ?? [])

  const hasAccounts = computed(() => accounts.value.length > 0)

  const totalBalance = computed(() =>
    accounts.value.reduce((sum, a) => sum + a.balance, 0)
  )

  async function invalidateBankAccounts() {
    await queryClient.refetchQueries({ queryKey: BANK_ACCOUNTS_QUERY_KEY })
  }

  if (import.meta.client) {
    watch(isError, (v) => {
      if (v) toast.error(getErrorMessage(error.value ?? new Error(), 'Erro ao carregar contas.'))
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
