import { reactive } from 'vue'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { useQueryClient } from '@tanstack/vue-query'
import { createTransaction as createTransactionApi } from '~/services/transactions'

const transactionSchema = z.object({
  amount: z.number().min(0.01, 'Valor inválido'),
  name: z.string().min(1, 'Nome é obrigatório'),
  categoryId: z.string().min(1, 'Categoria é obrigatória'),
  bankAccountId: z.string().min(1, 'Conta é obrigatória'),
  date: z.date(),
  type: z.enum(['INCOME', 'EXPENSE']),
  isRecurring: z.boolean().optional(),
})

export type TransactionFormValues = z.infer<typeof transactionSchema>

interface SelectOption {
  label: string
  value: string
}

const TRANSACTIONS_QUERY_KEY = ['transactions'] as const

export function useNewTransactionModalController() {
  const { newTransactionType, closeNewTransactionModal } = useDashboard()
  const type = newTransactionType
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
  const queryClient = useQueryClient()

  const { accounts, invalidateBankAccounts } = useBankAccounts()
  const { categories: categoriesComputed } = useCategories(type)

  const amount = ref<number | null>(null)
  const name = ref('')
  const categoryId = ref<string | null>(null)
  const bankAccountId = ref<string | null>(null)
  const date = ref<Date>(new Date())
  const isRecurring = ref(false)
  const errors = reactive<Record<string, string>>({})

  const accountsOptions = computed<SelectOption[]>(() =>
    accounts.value.map((a) => ({ label: a.name, value: a.id }))
  )

  const categories = computed(() => categoriesComputed?.value ?? [])

  const title = computed(() =>
    type.value === 'INCOME' ? 'Nova Receita' : 'Nova Despesa'
  )
  const valueColor = computed(() =>
    type.value === 'INCOME' ? '#12b886' : '#fa5252'
  )
  const valueLabel = computed(() =>
    type.value === 'INCOME' ? 'Valor da receita' : 'Valor da despesa'
  )
  const accountLabel = computed(() =>
    type.value === 'INCOME' ? 'Receber com' : 'Pagar com'
  )
  const accountPlaceholder = computed(() =>
    type.value === 'INCOME' ? 'Receber com' : 'Pagar com'
  )

  const isLoading = ref(false)

  async function createTransaction(payload: TransactionFormValues) {
    isLoading.value = true
    try {
      await createTransactionApi({
        name: payload.name.trim(),
        amount: payload.amount,
        date: payload.date.toISOString().slice(0, 10),
        type: payload.type,
        bankAccountId: payload.bankAccountId,
        categoryId: payload.categoryId,
        isRecurring: payload.isRecurring ?? false,
        frequency: payload.isRecurring ? 'MONTHLY' : undefined,
      })
      toast.success('Transação salva com sucesso!')
      invalidateBankAccounts()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      closeNewTransactionModal()
      resetForm()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao salvar transação. Tente novamente.'))
    } finally {
      isLoading.value = false
    }
  }

  function resetForm() {
    amount.value = null
    name.value = ''
    categoryId.value = null
    bankAccountId.value = null
    date.value = new Date()
    isRecurring.value = false
    Object.keys(errors).forEach((key) => delete errors[key])
  }

  function handleSubmit() {
    Object.keys(errors).forEach((key) => delete errors[key])

    const result = transactionSchema.safeParse({
      amount: amount.value ?? 0,
      name: name.value.trim(),
      categoryId: categoryId.value ?? '',
      bankAccountId: bankAccountId.value ?? '',
      date: date.value,
      type: type.value ?? '',
      isRecurring: isRecurring.value,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) {
            const key = path === 'bankAccountId' ? 'account' : path === 'categoryId' ? 'category' : path.toString()
            errors[key] = issue.message
          }
        })
      }
      return
    }

    createTransaction(result.data)
  }

  return {
    amount,
    name,
    category: categoryId,
    account: bankAccountId,
    date,
    isRecurring,
    errors,
    isLoading,
    categories,
    accounts: accountsOptions,
    title,
    valueColor,
    valueLabel,
    accountLabel,
    accountPlaceholder,
    resetForm,
    handleSubmit,
  }
}
