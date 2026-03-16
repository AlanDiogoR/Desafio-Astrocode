import { useQuery } from '@tanstack/vue-query'
import type { TransactionFilters } from '~/services/transactions'
import { listTransactions } from '~/services/transactions'

export const TRANSACTIONS_QUERY_KEY = ['transactions'] as const

export interface TransactionFiltersState {
  type?: 'INCOME' | 'EXPENSE'
  year?: number
  month?: number
  bankAccountId?: string
  page?: number
  size?: number
}

const DEFAULT_PAGE_SIZE = 20

export function useTransactions(filters: Ref<TransactionFiltersState | undefined>) {
  const authStore = useAuthStore()
  const { accounts } = useBankAccounts()
  const { creditCards } = useCreditCards()
  const { categoriesById } = useCategories()

  const currentPage = ref(0)

  const apiFilters = computed<TransactionFilters | undefined>(() => {
    const f = filters.value
    if (!f) return undefined
    const out: TransactionFilters = {
      page: currentPage.value,
      size: f.size ?? DEFAULT_PAGE_SIZE,
    }
    if (f.year) out.year = f.year
    if (f.month) out.month = f.month
    if (f.bankAccountId) out.bankAccountId = f.bankAccountId
    if (f.type) out.type = f.type
    return out
  })

  watch(filters, () => {
    currentPage.value = 0
  }, { deep: true })

  const { data: rawPage, isPending, isError, error, refetch } = useQuery({
    queryKey: [TRANSACTIONS_QUERY_KEY[0], apiFilters],
    queryFn: () => listTransactions(apiFilters.value ?? {}),
    enabled: computed(() => !!authStore.user),
  })

  const rawTransactions = computed(() => rawPage.value?.content ?? [])
  const totalPages = computed(() => rawPage.value?.totalPages ?? 0)
  const totalElements = computed(() => rawPage.value?.totalElements ?? 0)

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
  const creditCardNameById = computed(() => {
    const map: Record<string, string> = {}
    creditCards.value.forEach((c) => { map[c.id] = c.name })
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
      bankAccountId: t.bankAccountId ?? '',
      creditCardId: t.creditCardId ?? null,
      categoryId: t.categoryId,
      bankName: t.bankAccountId ? (accountNameById.value[t.bankAccountId] ?? '') : '',
      creditCardName: t.creditCardName ?? (t.creditCardId ? (creditCardNameById.value[t.creditCardId] ?? '') : ''),
      categoryName: categoryNameById.value[t.categoryId] ?? '',
      isRecurring: t.isRecurring ?? false,
    }))
  })

  function setPage(page: number) {
    currentPage.value = Math.max(0, page)
  }

  return {
    transactions,
    isPending,
    isError,
    error,
    refetch,
    currentPage,
    totalPages,
    totalElements,
    setPage,
  }
}
