import { reactive } from 'vue'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { createGoal } from '~/services/goals'

const goalSchema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(120, 'Nome deve ter no máximo 120 caracteres'),
  targetAmount: z
    .union([z.number(), z.null()])
    .refine((v) => v !== null && v > 0, { message: 'Valor objetivo deve ser maior que zero' }),
  deadline: z.date().optional().nullable(),
  color: z.string().max(30, 'Cor deve ter no máximo 30 caracteres').optional().nullable(),
})

export type GoalFormValues = z.infer<typeof goalSchema>

export function useNewGoalModalController() {
  const { closeNewGoalModal } = useDashboard()
  const { invalidateGoals } = useGoals()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const name = ref('')
  const targetAmount = ref<number | null>(null)
  const deadline = ref<Date | null>(new Date())
  const color = ref<string | null>(null)
  const errors = reactive<Record<string, string>>({})
  const touched = reactive<Record<string, boolean>>({})
  const submittedOnce = ref(false)
  const isLoading = ref(false)

  function markTouched(field: string) {
    touched[field] = true
  }

  function shouldShowError(field: string): boolean {
    return !!(touched[field] || submittedOnce.value) && !!errors[field]
  }

  async function createGoal(payload: GoalFormValues) {
    isLoading.value = true
    try {
      const endDate = payload.deadline
        ? payload.deadline.toISOString().slice(0, 10)
        : null
      await createGoal({
        name: payload.name.trim(),
        targetAmount: payload.targetAmount,
        endDate,
        color: payload.color ?? null,
      })
      toast.success('Meta criada com sucesso!')
      invalidateGoals()
      closeNewGoalModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao criar meta. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function resetForm() {
    name.value = ''
    targetAmount.value = null
    deadline.value = new Date()
    color.value = null
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  function handleSubmit() {
    submittedOnce.value = true
    Object.keys(errors).forEach((key) => delete errors[key])

    const result = goalSchema.safeParse({
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
          if (path !== undefined) {
            const key = path === 'targetAmount' ? 'targetAmount' : path.toString()
            errors[key] = issue.message
          }
        })
      }
      return
    }

    createGoal(result.data)
  }

  return {
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
    resetForm,
    handleSubmit,
  }
}
