import { reactive } from 'vue'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { updateBankAccount } from '~/services/bankAccounts'
import type { BankAccount } from '~/composables/useBankAccounts'

const accountSchema = z.object({
  name: z.string().min(1, 'Campo obrigatório').max(100, 'Nome deve ter no máximo 100 caracteres'),
  balance: z
    .union([z.number(), z.null()])
    .transform((v) => v ?? 0)
    .refine((v) => v >= 0, { message: 'Saldo não pode ser negativo' }),
  type: z
    .string()
    .min(1, 'Tipo da conta é obrigatório')
    .refine((v) => ['CHECKING', 'INVESTMENT', 'CASH'].includes(v), {
      message: 'Tipo da conta é obrigatório',
    }),
  color: z.string().max(30, 'Cor deve ter no máximo 30 caracteres').optional().nullable(),
})

export type EditAccountFormValues = z.infer<typeof accountSchema>

interface SelectOption {
  label: string
  value: string
}

const ACCOUNT_TYPE_OPTIONS: SelectOption[] = [
  { label: 'Conta Corrente', value: 'CHECKING' },
  { label: 'Investimentos', value: 'INVESTMENT' },
  { label: 'Dinheiro Físico', value: 'CASH' },
]

export function useEditAccountModalController() {
  const { closeEditAccountModal, openConfirmDeleteModal } = useDashboard()
  const { accounts, invalidateBankAccounts } = useBankAccounts()
  const { invalidateDashboard } = useDashboardData()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const accountId = ref<string | null>(null)
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

  function loadAccount(account: BankAccount | null) {
    if (!account) {
      resetForm()
      return
    }
    accountId.value = account.id
    balance.value = account.balance
    name.value = account.name
    type.value = account.type.toUpperCase()
    color.value = account.color ?? null
    Object.keys(errors).forEach((key) => delete errors[key])
    Object.keys(touched).forEach((key) => delete touched[key as keyof typeof touched])
    submittedOnce.value = false
  }

  async function saveAccount(payload: EditAccountFormValues) {
    if (!accountId.value) return
    isLoading.value = true
    try {
      await updateBankAccount(accountId.value, {
        name: payload.name.trim(),
        initialBalance: payload.balance,
        type: payload.type,
        color: payload.color ?? null,
      })
      toast.success('Conta atualizada com sucesso!')
      await invalidateBankAccounts()
      await invalidateDashboard()
      closeEditAccountModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao atualizar conta. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function openDeleteConfirm() {
    if (accountId.value) {
      openConfirmDeleteModal('ACCOUNT', accountId.value)
    }
  }

  function resetForm() {
    accountId.value = null
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
      balance: balance.value,
      type: type.value ?? '',
      color: color.value,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) {
            const key = path === 'balance' ? 'balance' : path.toString()
            errors[key] = issue.message
          }
        })
      }
      return
    }

    const nameTrimmed = result.data.name.trim()
    const duplicate = accounts.value.some(
      (a) => a.id !== accountId.value && a.name.trim().toLowerCase() === nameTrimmed.toLowerCase()
    )
    if (duplicate) {
      errors.name = 'Já existe uma conta com este nome. Escolha outro.'
      return
    }

    saveAccount(result.data)
  }

  return {
    accountId,
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
    loadAccount,
    resetForm,
    handleSubmit,
    openDeleteConfirm,
  }
}
