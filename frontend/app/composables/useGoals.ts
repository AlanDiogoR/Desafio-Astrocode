import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import { listGoals } from '~/services/goals'

export interface SavingsGoal {
  id: string
  name: string
  targetAmount: number
  currentAmount: number
  color: string | null
  progressPercentage: number
  status: string
  endDate: string | null
  createdAt: string
  updatedAt: string
}

interface SavingsGoalApiResponse {
  id: string
  name: string
  targetAmount: number
  currentAmount: number
  color: string | null
  progressPercentage: number
  status: string
  endDate: string | null
  createdAt: string
  updatedAt: string
}

const GOALS_QUERY_KEY = ['goals'] as const

function mapApiToSavingsGoal(raw: SavingsGoalApiResponse): SavingsGoal {
  return {
    id: raw.id,
    name: raw.name,
    targetAmount: Number(raw.targetAmount ?? 0),
    currentAmount: Number(raw.currentAmount ?? 0),
    color: raw.color,
    progressPercentage: Number(raw.progressPercentage ?? 0),
    status: raw.status ?? 'ACTIVE',
    endDate: raw.endDate ?? null,
    createdAt: raw.createdAt,
    updatedAt: raw.updatedAt,
  }
}

export function useGoals() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const {
    data: goalsData,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: GOALS_QUERY_KEY,
    queryFn: async (): Promise<SavingsGoal[]> => {
      const data = await listGoals()
      return (data as SavingsGoalApiResponse[]).map(mapApiToSavingsGoal)
    },
    enabled: computed(() => !!authStore.token),
  })

  if (import.meta.client) {
    watch(isError, (v) => {
      if (v) toast.error(getErrorMessage(error.value ?? new Error(), 'Erro ao carregar metas.'))
    })
  }

  const goals = computed<SavingsGoal[]>(() => {
    const list = goalsData.value ?? []
    return [...list].sort((a, b) => {
      const aDone = a.status === 'COMPLETED' || a.currentAmount >= a.targetAmount
      const bDone = b.status === 'COMPLETED' || b.currentAmount >= b.targetAmount
      if (aDone && !bDone) return 1
      if (!aDone && bDone) return -1
      return 0
    })
  })

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
