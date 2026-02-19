import { reactive } from 'vue'
import { z } from 'zod'
import { useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import { updateTransaction } from '~/services/transactions'
import { TRANSACTIONS_QUERY_KEY } from '~/composables/useTransactions'
import type { TransactionForEdit } from '~/composables/useDashboard'

const transactionSchema = z.object({
  amount: z.number().min(0.01, 'Valor inválido'),
  name: z.string().min(1, 'Nome é obrigatório'),
  categoryId: z.string().min(1, 'Categoria é obrigatória'),
  bankAccountId: z.string().min(1, 'Conta é obrigatória'),
  date: z.date(),
  type: z.enum(['INCOME', 'EXPENSE']),
  isRecurring: z.boolean().optional(),
})

export function useEditTransactionModalController(transaction: Ref<TransactionForEdit | null>) {
  const { closeEditTransactionModal, openConfirmDeleteModal } = useDashboard()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
  const queryClient = useQueryClient()
  const { accounts, invalidateBankAccounts } = useBankAccounts()
  const { invalidateDashboard } = useDashboardData()
  const { categories: categoriesComputed } = useCategories(
    computed(() => (transaction.value?.type === 'income' ? 'INCOME' : 'EXPENSE')),
  )

  const amount = ref<number | null>(null)
  const name = ref('')
  const categoryId = ref<string | null>(null)
  const bankAccountId = ref<string | null>(null)
  const date = ref<Date>(new Date())
  const isRecurring = ref(false)
  const errors = reactive<Record<string, string>>({})
  const isLoading = ref(false)

  const accountsOptions = computed(() =>
    accounts.value.map((a) => ({ label: a.name, value: a.id })),
  )
  const categories = computed(() => categoriesComputed?.value ?? [])

  watch(
    transaction,
    (t) => {
      if (t) {
        amount.value = t.amount
        name.value = t.name
        categoryId.value = t.categoryId
        bankAccountId.value = t.bankAccountId
        date.value = new Date(t.date)
        isRecurring.value = t.isRecurring ?? false
      }
    },
    { immediate: true },
  )

  const valueColor = computed(() =>
    transaction.value?.type === 'income' ? '#12b886' : '#fa5252',
  )

  async function save() {
    const t = transaction.value
    if (!t) return

    Object.keys(errors).forEach((key) => delete errors[key])
    const result = transactionSchema.safeParse({
      amount: amount.value ?? 0,
      name: name.value.trim(),
      categoryId: categoryId.value ?? '',
      bankAccountId: bankAccountId.value ?? '',
      date: date.value,
      type: t.type === 'income' ? 'INCOME' : 'EXPENSE',
      isRecurring: isRecurring.value,
    })

    if (!result.success) {
      result.error.issues.forEach((issue) => {
        const path = issue.path[0]
        if (path !== undefined) {
          const key = path === 'bankAccountId' ? 'account' : path === 'categoryId' ? 'category' : path.toString()
          errors[key] = issue.message
        }
      })
      return
    }

    isLoading.value = true
    try {
      await updateTransaction(t.id, {
        name: result.data.name,
        amount: result.data.amount,
        date: result.data.date.toISOString().slice(0, 10),
        type: result.data.type,
        bankAccountId: result.data.bankAccountId,
        categoryId: result.data.categoryId,
        isRecurring: result.data.isRecurring ?? false,
        frequency: result.data.isRecurring ? 'MONTHLY' : undefined,
      })
      toast.success('Transação atualizada!')
      await invalidateBankAccounts()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
      closeEditTransactionModal()
    } catch (err: unknown) {
      toast.error(getErrorMessage(err, 'Erro ao atualizar transação.'))
    } finally {
      isLoading.value = false
    }
  }

  function handleDelete() {
    const t = transaction.value
    if (!t) return
    closeEditTransactionModal()
    openConfirmDeleteModal('TRANSACTION', t.id)
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
    valueColor,
    save,
    handleDelete,
  }
}
