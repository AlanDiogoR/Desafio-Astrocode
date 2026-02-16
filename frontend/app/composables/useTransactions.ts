import { useQuery } from '@tanstack/vue-query'
import type { TransactionFilters } from '~/services/transactions'
import { listTransactions } from '~/services/transactions'

export const TRANSACTIONS_QUERY_KEY = ['transactions'] as const

export interface TransactionFiltersState {
  type?: 'INCOME' | 'EXPENSE'
  year?: number
  month?: number
  bankAccountId?: string
}

export function useTransactions(filters: Ref<TransactionFiltersState | undefined>) {
  const authStore = useAuthStore()
  const { accounts } = useBankAccounts()
  const { categoriesById } = useCategories()

  const apiFilters = computed<TransactionFilters | undefined>(() => {
    const f = filters.value
    if (!f) return undefined
    const out: TransactionFilters = {}
    if (f.year) out.year = f.year
    if (f.month) out.month = f.month
    if (f.bankAccountId) out.bankAccountId = f.bankAccountId
    if (f.type) out.type = f.type
    return Object.keys(out).length ? out : undefined
  })

  const { data: rawTransactions, isPending, isError, error, refetch } = useQuery({
    queryKey: [TRANSACTIONS_QUERY_KEY[0], apiFilters],
    queryFn: () => listTransactions(apiFilters.value ?? {}),
    enabled: computed(() => !!authStore.token),
  })

  const categoryNameById = computed(() => {
    const byId = categoriesById.value
    const map: Record<string, string> = {}
    Object.entries(byId).forEach(([id, { name }]) => { map[id] = name })
    return map
  })
  const accountNameById = computed(() => {
    const map: Record<string, string> = {}
    accounts.value.forEach((a) => { map[a.id] = a.name })
    return map
  })

  const transactions = computed(() => {
    const list = rawTransactions.value ?? []
    return list.map((t) => ({
      id: t.id,
      name: t.name,
      amount: Number(t.amount),
      date: t.date,
      type: (t.type === 'INCOME' ? 'income' : 'expense') as 'income' | 'expense',
      bankAccountId: t.bankAccountId,
      categoryId: t.categoryId,
      bankName: accountNameById.value[t.bankAccountId] ?? '',
      categoryName: categoryNameById.value[t.categoryId] ?? '',
    }))
  })

  return {
    transactions,
    isPending,
    isError,
    error,
    refetch,
  }
}
