import { useQuery, useQueryClient } from '@tanstack/vue-query'

interface AccountsQueryItem {
  id: string
  name: string
  initialBalance: number
  type: string
  color: string | null
}

const ACCOUNTS_QUERY_KEY = ['accounts'] as const

export function useAccounts() {
  const { $api } = useNuxtApp()
  const authStore = useAuthStore()
  const queryClient = useQueryClient()

  const {
    data: accountsData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ACCOUNTS_QUERY_KEY,
    queryFn: async (): Promise<AccountsQueryItem[]> => {
      const { data } = await $api.get<AccountsQueryItem[]>('/accounts')
      return data ?? []
    },
    enabled: computed(() => !!authStore.token),
  })

  const accounts = computed<AccountsQueryItem[]>(() => accountsData.value ?? [])

  function invalidateAccounts() {
    queryClient.invalidateQueries({ queryKey: ACCOUNTS_QUERY_KEY })
  }

  return {
    accounts,
    isPending,
    isError,
    error,
    refetch,
    invalidateAccounts,
  }
}
