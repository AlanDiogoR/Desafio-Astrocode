import { useQuery, useQueryClient } from '@tanstack/vue-query'

export interface SavingsGoal {
  id: string
  name: string
  targetAmount: number
  currentAmount: number
  color: string | null
  progressPercentage: number
  status: string
  createdAt: string
  updatedAt: string
}

const GOALS_QUERY_KEY = ['goals'] as const

export function useGoals() {
  const { $api } = useNuxtApp()
  const authStore = useAuthStore()
  const queryClient = useQueryClient()

  const {
    data: goalsData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: GOALS_QUERY_KEY,
    queryFn: async (): Promise<SavingsGoal[]> => {
      const { data } = await $api.get<SavingsGoal[]>('/goals')
      return data ?? []
    },
    enabled: computed(() => !!authStore.token),
  })

  const goals = computed<SavingsGoal[]>(() => goalsData.value ?? [])

  function invalidateGoals() {
    queryClient.invalidateQueries({ queryKey: GOALS_QUERY_KEY })
  }

  return {
    goals,
    isPending,
    isError,
    error,
    refetch,
    invalidateGoals,
  }
}
