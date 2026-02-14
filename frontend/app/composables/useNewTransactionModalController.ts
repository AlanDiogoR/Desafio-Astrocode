import { reactive } from 'vue'
import { z } from 'zod'

const transactionSchema = z.object({
  amount: z.number().min(0.01, 'Valor inválido'),
  name: z.string().min(1, 'Nome é obrigatório'),
  categoryId: z.string().min(1, 'Categoria é obrigatória'),
  bankAccountId: z.string().min(1, 'Conta é obrigatória'),
  date: z.date(),
  type: z.enum(['INCOME', 'EXPENSE']),
})

export type TransactionFormValues = z.infer<typeof transactionSchema>

interface SelectOption {
  label: string
  value: string
}

export function useNewTransactionModalController() {
  const { newTransactionType, closeNewTransactionModal } = useDashboard()
  const type = newTransactionType
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const { accounts: bankAccountsRef } = useBankAccounts()
  const { categories: categoriesComputed } = useCategories(type)

  const amount = ref<number | null>(null)
  const name = ref('')
  const categoryId = ref<string | null>(null)
  const bankAccountId = ref<string | null>(null)
  const date = ref<Date>(new Date())
  const errors = reactive<Record<string, string>>({})

  const accounts = computed<SelectOption[]>(() => {
    const list = bankAccountsRef?.value ?? []
    return list.map((a) => ({ label: a.name, value: a.id }))
  })

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
    await new Promise((r) => setTimeout(r, 300))
    toast.success('Transação salva com sucesso!')
    closeNewTransactionModal()
    resetForm()
    isLoading.value = false
  }

  function resetForm() {
    amount.value = null
    name.value = ''
    categoryId.value = null
    bankAccountId.value = null
    date.value = new Date()
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
    errors,
    isLoading,
    categories,
    accounts,
    title,
    valueColor,
    valueLabel,
    accountLabel,
    accountPlaceholder,
    resetForm,
    handleSubmit,
  }
}
