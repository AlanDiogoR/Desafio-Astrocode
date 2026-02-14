import { reactive } from 'vue'
import { z } from 'zod'
import type { BankAccount } from '~/composables/useBankAccounts'

const accountSchema = z.object({
  name: z.string().min(1, 'Campo obrigatório').max(100, 'Nome deve ter no máximo 100 caracteres'),
  initialBalance: z
    .union([z.number(), z.null()])
    .refine((v) => v !== null, { message: 'Campo obrigatório' }),
  type: z.enum(['CHECKING', 'INVESTMENT', 'CASH'], {
    errorMap: () => ({ message: 'Campo obrigatório' }),
  }),
  color: z.string().max(30, 'Cor deve ter no máximo 30 caracteres').optional().nullable(),
})

export type AccountFormValues = z.infer<typeof accountSchema>

interface SelectOption {
  label: string
  value: string
}

const ACCOUNT_TYPE_OPTIONS: SelectOption[] = [
  { label: 'Conta Corrente', value: 'CHECKING' },
  { label: 'Investimentos', value: 'INVESTMENT' },
  { label: 'Dinheiro Físico', value: 'CASH' },
]

export function useNewAccountModalController() {
  const { closeNewAccountModal } = useDashboard()
  const { addAccount } = useBankAccounts()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const balance = ref<number | null>(null)
  const name = ref('')
  const type = ref<string | null>(null)
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

  const accountTypeOptions = ACCOUNT_TYPE_OPTIONS

  async function createAccount(payload: AccountFormValues) {
    isLoading.value = true
    await new Promise((r) => setTimeout(r, 300))
    const newAccount: BankAccount = {
      id: crypto.randomUUID(),
      name: payload.name.trim(),
      balance: payload.initialBalance,
      type: payload.type.toLowerCase() as BankAccount['type'],
      color: payload.color ?? '#868E96',
    }
    addAccount(newAccount)
    toast.success('Conta criada com sucesso!')
    closeNewAccountModal()
    resetForm()
    isLoading.value = false
  }

  function resetForm() {
    balance.value = null
    name.value = ''
    type.value = null
    color.value = null
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  function handleSubmit() {
    submittedOnce.value = true
    Object.keys(errors).forEach((key) => delete errors[key])

    const result = accountSchema.safeParse({
      name: name.value.trim(),
      initialBalance: balance.value,
      type: type.value ?? '',
      color: color.value,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) {
            const key = path === 'initialBalance' ? 'balance' : path.toString()
            errors[key] = issue.message
          }
        })
      }
      return
    }

    createAccount(result.data)
  }

  return {
    balance,
    name,
    type,
    color,
    errors,
    touched,
    submittedOnce,
    markTouched,
    shouldShowError,
    isLoading,
    accountTypeOptions,
    resetForm,
    handleSubmit,
  }
}
