import { reactive } from 'vue'
import { z } from 'zod'
import { useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import type { SavingsGoal } from '~/composables/useGoals'
import { useBankAccounts } from '~/composables/useBankAccounts'
import { contributeToGoal, withdrawFromGoal } from '~/services/goals'

type InteractionMode = 'ADD' | 'REMOVE'

const baseAmountSchema = z
  .union([z.number(), z.null()])
  .refine((v) => v !== null && v > 0, { message: 'Valor deve ser maior que zero' })

const contributeSchema = z.object({
  amount: baseAmountSchema,
  bankAccountId: z.string().min(1, 'Selecione uma conta'),
})

const withdrawSchema = z.object({
  amount: baseAmountSchema,
  bankAccountId: z.string().min(1, 'Selecione uma conta'),
})

const TRANSACTIONS_QUERY_KEY = ['transactions'] as const

export function useGoalInteractionController() {
  const { closeNewGoalValueModal, goalForValueAddition, goalInteractionType } = useDashboard()
  const { goals, invalidateGoals } = useGoals()
  const { accounts, invalidateBankAccounts } = useBankAccounts()
  const { invalidateDashboard } = useDashboardData()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const mode = ref<InteractionMode>('ADD')
  const goalId = ref<string | null>(null)

  const selectedGoal = computed<SavingsGoal | null>(() => {
    const id = goalId.value ?? goalForValueAddition.value?.id
    if (!id) return null
    return goals.value.find((g) => g.id === id) ?? goalForValueAddition.value ?? null
  })
  const amount = ref<number | null>(null)
  const bankAccountId = ref<string | null>(null)
  const errors = reactive<Record<string, string>>({})
  const touched = reactive<Record<string, boolean>>({})
  const submittedOnce = ref(false)
  const isLoading = ref(false)

  const goalOptions = computed(() =>
    goals.value.map((g) => ({ label: g.name, value: g.id }))
  )
  const accountOptions = computed(() =>
    accounts.value.map((a) => ({ label: a.name, value: a.id }))
  )
  const goalLockedFromCard = computed(() => !!goalForValueAddition.value)
  const canAdd = computed(() => {
    const g = selectedGoal.value
    return g && g.currentAmount < g.targetAmount && g.status !== 'COMPLETED'
  })
  const canRemove = computed(() => {
    const g = selectedGoal.value
    return g && g.currentAmount > 0
  })

  watch([goalForValueAddition, goalInteractionType], ([goal, type]) => {
    amount.value = null
    bankAccountId.value = null
    if (goal) {
      goalId.value = goal.id
      mode.value = type === 'DEPOSIT' ? 'ADD' : 'REMOVE'
    } else {
      goalId.value = null
      mode.value = type === 'DEPOSIT' ? 'ADD' : 'REMOVE'
    }
  }, { immediate: true })

  function markTouched(field: string) {
    touched[field] = true
  }

  function shouldShowError(field: string): boolean {
    return !!(touched[field] || submittedOnce.value) && !!errors[field]
  }

  async function submit() {
    const goal = selectedGoal.value
    if (!goal) {
      errors.goalId = 'Selecione uma meta'
      return
    }
    if (mode.value === 'ADD' && !canAdd.value) return
    if (mode.value === 'REMOVE' && !canRemove.value) return

    submittedOnce.value = true
    Object.keys(errors).forEach((key) => delete errors[key])

    const schema = mode.value === 'ADD' ? contributeSchema : withdrawSchema
    const result = schema.safeParse({
      amount: amount.value,
      bankAccountId: bankAccountId.value ?? '',
    })

    if (!result.success) {
      result.error.issues.forEach((issue) => {
        const path = issue.path[0]
        if (path !== undefined) errors[path.toString()] = issue.message
      })
      return
    }

    const remaining = goal.targetAmount - goal.currentAmount
    if (mode.value === 'ADD' && (result.data.amount ?? 0) > remaining) {
      errors.amount = 'Valor não pode ultrapassar o que falta para a meta'
      return
    }
    if (mode.value === 'REMOVE' && (result.data.amount ?? 0) > goal.currentAmount) {
      errors.amount = 'Valor não pode ser maior que o saldo da meta'
      return
    }

    isLoading.value = true
    try {
      const payload = result.data
      if (mode.value === 'ADD') {
        await contributeToGoal(goal.id, { amount: payload.amount!, bankAccountId: payload.bankAccountId })
      } else {
        await withdrawFromGoal(goal.id, { amount: payload.amount!, bankAccountId: payload.bankAccountId })
      }

      const newCurrent = mode.value === 'ADD'
        ? goal.currentAmount + (payload.amount ?? 0)
        : goal.currentAmount - (payload.amount ?? 0)
      const isCompleted = newCurrent >= goal.targetAmount

      if (mode.value === 'ADD' && isCompleted) {
        toast.success('Parabéns! Meta concluída 🎉')
      } else {
        toast.success(mode.value === 'ADD' ? 'Aporte realizado!' : 'Resgate realizado!')
      }

      invalidateGoals()
      await invalidateBankAccounts()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
      closeNewGoalValueModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro na operação. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function resetForm() {
    amount.value = null
    bankAccountId.value = null
    goalId.value = goalForValueAddition.value?.id ?? null
    mode.value = goalInteractionType.value === 'DEPOSIT' ? 'ADD' : 'REMOVE'
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  return {
    mode,
    selectedGoal,
    amount,
    bankAccountId,
    errors,
    touched,
    submittedOnce,
    markTouched,
    shouldShowError,
    isLoading,
    goalId,
    goalOptions,
    accountOptions,
    goalLockedFromCard,
    canAdd,
    canRemove,
    resetForm,
    submit,
  }
}
