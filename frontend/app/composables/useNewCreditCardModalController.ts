import { reactive } from 'vue'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { createCreditCard } from '~/services/creditCards'

const creditCardSchema = z.object({
  name: z.string().min(1, 'Campo obrigatório').max(100, 'Nome deve ter no máximo 100 caracteres'),
  creditLimit: z.number().min(0.01, 'Limite deve ser maior que zero'),
  closingDay: z.number().min(1, 'Dia inválido').max(28, 'Use um dia entre 1 e 28'),
  dueDay: z.number().min(1, 'Dia inválido').max(28, 'Use um dia entre 1 e 28'),
  color: z.string().max(30).optional().nullable(),
})

export type CreditCardFormValues = z.infer<typeof creditCardSchema>

export function useNewCreditCardModalController() {
  const { closeNewCreditCardModal } = useDashboard()
  const { creditCards, invalidateCreditCards } = useCreditCards()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const name = ref('')
  const creditLimit = ref<number | null>(null)
  const closingDay = ref<number | null>(null)
  const dueDay = ref<number | null>(null)
  const color = ref<string | null>(null)
  const errors = reactive<Record<string, string>>({})
  const isLoading = ref(false)

  async function create(payload: CreditCardFormValues) {
    isLoading.value = true
    try {
      await createCreditCard({
        name: payload.name.trim(),
        creditLimit: payload.creditLimit,
        closingDay: payload.closingDay,
        dueDay: payload.dueDay,
        color: payload.color ?? null,
      })
      toast.success('Cartão criado com sucesso!')
      await invalidateCreditCards()
      closeNewCreditCardModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao criar cartão. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function resetForm() {
    name.value = ''
    creditLimit.value = null
    closingDay.value = null
    dueDay.value = null
    color.value = null
    Object.keys(errors).forEach((key) => delete errors[key])
  }

  function handleSubmit() {
    Object.keys(errors).forEach((key) => delete errors[key])

    const result = creditCardSchema.safeParse({
      name: name.value.trim(),
      creditLimit: creditLimit.value ?? 0,
      closingDay: closingDay.value ?? 0,
      dueDay: dueDay.value ?? 0,
      color: color.value,
    })

    if (!result.success) {
      result.error.issues.forEach((issue) => {
        const path = issue.path[0]
        if (path !== undefined) errors[path.toString()] = issue.message
      })
      return
    }

    const nameTrimmed = result.data.name.trim()
    const duplicate = creditCards.value.some(
      (c) => c.name.trim().toLowerCase() === nameTrimmed.toLowerCase()
    )
    if (duplicate) {
      errors.name = 'Já existe um cartão com este nome. Escolha outro.'
      return
    }

    create(result.data)
  }

  return {
    name,
    creditLimit,
    closingDay,
    dueDay,
    color,
    errors,
    isLoading,
    resetForm,
    handleSubmit,
  }
}
