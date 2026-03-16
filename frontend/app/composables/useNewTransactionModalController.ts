import { reactive } from 'vue'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { toDateString } from '~/utils/format'
import { useQueryClient } from '@tanstack/vue-query'
import { createTransaction as createTransactionApi } from '~/services/transactions'

const transactionSchema = z.object({
  amount: z.number().min(0.01, 'Valor inválido'),
  name: z.string().min(1, 'Nome é obrigatório'),
  categoryId: z.string().min(1, 'Categoria é obrigatória'),
  bankAccountId: z.string().optional().nullable(),
  creditCardId: z.string().optional().nullable(),
  date: z.date(),
  type: z.enum(['INCOME', 'EXPENSE']),
  isRecurring: z.boolean().optional(),
})
  .refine(
    (data) => (data.bankAccountId != null) !== (data.creditCardId != null),
    { message: 'Selecione conta (débito) ou cartão (crédito)', path: ['bankAccountId'] }
  )
  .refine(
    (data, ctx) => {
      if (!data.creditCardId || !data.amount) return true
      try {
        const { creditCards } = useCreditCards()
        const card = creditCards.value.find((c: { id: string; creditLimit?: number; currentBillAmount?: number }) => c.id === data.creditCardId)
        if (!card) return true
        const available = (card.creditLimit ?? 0) - (card.currentBillAmount ?? 0)
        return data.amount <= available
      } catch {
        return true
      }
    },
    { message: 'Valor excede o limite disponível do cartão', path: ['amount'] }
  )

export type TransactionFormValues = z.infer<typeof transactionSchema>

interface SelectOption {
  label: string
  value: string
}

const TRANSACTIONS_QUERY_KEY = ['transactions'] as const

export type TransactionSourceMode = 'DEBIT' | 'CREDIT'

export function useNewTransactionModalController() {
  const { newTransactionType, closeNewTransactionModal } = useDashboard()
  const type = newTransactionType
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
  const queryClient = useQueryClient()

  const { accounts, invalidateBankAccounts } = useBankAccounts()
  const { creditCards, invalidateCreditCards } = useCreditCards()
  const { invalidateDashboard } = useDashboardData()
  const { categories: categoriesComputed } = useCategories(type)

  const amount = ref<number | null>(null)
  const name = ref('')
  const categoryId = ref<string | null>(null)
  const bankAccountId = ref<string | null>(null)
  const creditCardId = ref<string | null>(null)
  const sourceMode = ref<TransactionSourceMode>('DEBIT')
  const date = ref<Date>(new Date())
  const isRecurring = ref(false)
  const errors = reactive<Record<string, string>>({})

  const accountsOptions = computed<SelectOption[]>(() =>
    accounts.value.map((a) => ({ label: a.name, value: a.id }))
  )
  const creditCardsOptions = computed<SelectOption[]>(() =>
    creditCards.value.map((c) => ({
      label: `${c.name} (R$ ${((c.creditLimit ?? 0) - (c.currentBillAmount ?? 0)).toLocaleString('pt-BR')} disp.)`,
      value: c.id,
    }))
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
  const accountLabel = computed(() => {
    if (type.value === 'INCOME') return 'Receber com'
    return sourceMode.value === 'DEBIT' ? 'Pagar com' : 'Cartão'
  })
  const accountPlaceholder = computed(() => {
    if (type.value === 'INCOME') return 'Receber com'
    return sourceMode.value === 'DEBIT' ? 'Pagar com' : 'Selecione o cartão'
  })
  const showAccountSelector = computed(() => type.value === 'INCOME' || sourceMode.value === 'DEBIT')
  const showCreditCardSelector = computed(() => type.value === 'EXPENSE' && sourceMode.value === 'CREDIT')
  const selectedCreditCard = computed(() =>
    creditCards.value.find((c) => c.id === creditCardId.value)
  )
  const availableLimit = computed(() => {
    const cc = selectedCreditCard.value
    if (!cc) return 0
    return (cc.creditLimit ?? 0) - (cc.currentBillAmount ?? 0)
  })

  const isLoading = ref(false)

  async function createTransaction(payload: TransactionFormValues) {
    isLoading.value = true
    try {
      await createTransactionApi({
        name: payload.name.trim(),
        amount: payload.amount,
        date: toDateString(payload.date),
        type: payload.type,
        bankAccountId: payload.bankAccountId ?? undefined,
        creditCardId: payload.creditCardId ?? undefined,
        categoryId: payload.categoryId,
        isRecurring: payload.isRecurring ?? false,
        frequency: payload.isRecurring ? 'MONTHLY' : undefined,
      })
      toast.success('Transação salva com sucesso!')
      await invalidateBankAccounts()
      await invalidateCreditCards()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
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
    creditCardId.value = null
    sourceMode.value = 'DEBIT'
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
      bankAccountId: sourceMode.value === 'DEBIT' ? bankAccountId.value : null,
      creditCardId: sourceMode.value === 'CREDIT' ? creditCardId.value : null,
      date: date.value,
      type: type.value === 'EXPENSE' && sourceMode.value === 'CREDIT' ? 'EXPENSE' : type.value ?? '',
      isRecurring: isRecurring.value,
    })

    if (!result.success) {
      const zodError = result.error
      if (zodError?.issues) {
        zodError.issues.forEach((issue) => {
          const path = issue.path[0]
          if (path !== undefined) {
            const key = path === 'bankAccountId' ? 'account' : path === 'creditCardId' ? 'account' : path === 'categoryId' ? 'category' : path.toString()
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
    creditCard: creditCardId,
    accounts: accountsOptions,
    creditCardsOptions,
    creditCards: creditCardsOptions,
    sourceMode,
    date,
    isRecurring,
    errors,
    isLoading,
    categories,
    showAccountSelector,
    showCreditCardSelector,
    availableLimit,
    title,
    valueColor,
    valueLabel,
    accountLabel,
    accountPlaceholder,
    resetForm,
    handleSubmit,
  }
}
