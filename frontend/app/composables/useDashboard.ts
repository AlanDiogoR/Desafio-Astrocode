export type TransactionModalType = 'INCOME' | 'EXPENSE'

export function useDashboard() {
  const isNewTransactionModalOpen = useState<boolean>('isNewTransactionModalOpen', () => false)
  const newTransactionType = useState<TransactionModalType | null>('newTransactionType', () => null)
  const isNewAccountModalOpen = useState<boolean>('isNewAccountModalOpen', () => false)
  const { hasAccounts } = useBankAccounts()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  function openNewTransactionModal(type: TransactionModalType) {
    if (!hasAccounts.value) {
      toast.error('Você precisa cadastrar uma conta primeiro!')
      return
    }
    newTransactionType.value = type
    isNewTransactionModalOpen.value = true
  }

  function closeNewTransactionModal() {
    isNewTransactionModalOpen.value = false
    newTransactionType.value = null
  }

  function openNewAccountModal() {
    isNewAccountModalOpen.value = true
  }

  function closeNewAccountModal() {
    isNewAccountModalOpen.value = false
  }

  return {
    isNewTransactionModalOpen,
    newTransactionType,
    isNewAccountModalOpen,
    openNewTransactionModal,
    closeNewTransactionModal,
    openNewAccountModal,
    closeNewAccountModal,
  }
}
