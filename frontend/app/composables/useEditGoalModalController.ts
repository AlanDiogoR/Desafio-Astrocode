import { reactive } from 'vue'
import { z } from 'zod'
import type { SavingsGoal } from '~/composables/useGoals'
import { getErrorMessage } from '~/utils/errorHandler'
import { updateGoal, deleteGoal as deleteGoalApi } from '~/services/goals'

const editGoalSchema = z.object({
  goalId: z.string().min(1, 'Selecione uma meta'),
  name: z.string().min(1, 'Nome é obrigatório').max(120, 'Nome deve ter no máximo 120 caracteres'),
  targetAmount: z
    .union([z.number(), z.null()])
    .refine((v) => v !== null && v > 0, { message: 'Valor objetivo deve ser maior que zero' }),
  deadline: z.date().optional().nullable(),
  color: z.string().max(30, 'Cor deve ter no máximo 30 caracteres').optional().nullable(),
})

export type EditGoalFormValues = z.infer<typeof editGoalSchema>

export function useEditGoalModalController() {
  const { closeEditGoalModal, goalBeingEdited } = useDashboard()
  const { goals, invalidateGoals } = useGoals()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const goalId = ref<string | null>(null)
  const name = ref('')
  const targetAmount = ref<number | null>(null)
  const deadline = ref<Date | null>(null)
  const color = ref<string | null>(null)

  watch(goalBeingEdited, (edited) => {
    if (edited) {
      goalId.value = edited.id
      name.value = edited.name
      targetAmount.value = edited.targetAmount
      color.value = edited.color ?? null
      deadline.value = null
    }
  }, { immediate: true })
  const errors = reactive<Record<string, string>>({})
  const touched = reactive<Record<string, boolean>>({})
  const submittedOnce = ref(false)
  const isLoading = ref(false)

  const goalOptions = computed(() =>
    goals.value.map((g) => ({ label: g.name, value: g.id }))
  )

  watch(goalId, (id) => {
    if (!id) return
    const goal = goals.value.find((g) => g.id === id)
    if (goal) {
      name.value = goal.name
      targetAmount.value = goal.targetAmount
      color.value = goal.color ?? null
      deadline.value = null
    }
  })

  function markTouched(field: string) {
    touched[field] = true
  }

  function shouldShowError(field: string): boolean {
    return !!(touched[field] || submittedOnce.value) && !!errors[field]
  }

  async function updateGoal(payload: EditGoalFormValues) {
    isLoading.value = true
    try {
      const endDate = payload.deadline
        ? payload.deadline.toISOString().slice(0, 10)
        : null
      await updateGoal(payload.goalId, {
        name: payload.name.trim(),
        targetAmount: payload.targetAmount,
        endDate,
        color: payload.color ?? null,
      })
      toast.success('Meta atualizada com sucesso!')
      invalidateGoals()
      closeEditGoalModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao atualizar meta. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  async function deleteGoal(id: string) {
    isLoading.value = true
    try {
      await deleteGoalApi(id)
      toast.success('Meta excluída com sucesso!')
      invalidateGoals()
      closeEditGoalModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao excluir meta. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function resetForm() {
    goalId.value = null
    name.value = ''
    targetAmount.value = null
    deadline.value = null
    color.value = null
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  function handleSubmit() {
    submittedOnce.value = true
    Object.keys(errors).forEach((key) => delete errors[key])

    const result = editGoalSchema.safeParse({
      goalId: goalId.value ?? '',
      name: name.value.trim(),
      targetAmount: targetAmount.value,
      deadline: deadline.value,
      color: color.value,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) errors[path.toString()] = issue.message
        })
      }
      return
    }

    updateGoal(result.data)
  }

  function handleDelete() {
    if (!goalId.value) return
    deleteGoal(goalId.value)
  }

  return {
    goalId,
    name,
    targetAmount,
    deadline,
    color,
    errors,
    touched,
    submittedOnce,
    markTouched,
    shouldShowError,
    isLoading,
    goalOptions,
    resetForm,
    handleSubmit,
    handleDelete,
  }
}
