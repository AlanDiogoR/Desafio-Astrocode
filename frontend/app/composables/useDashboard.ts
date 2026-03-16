import type { SavingsGoal } from '~/composables/useGoals'
import type { ConfirmDeleteEntityType } from '~/types/confirmDelete'
import type { TransactionFiltersState } from '~/composables/useTransactions'

export type TransactionModalType = 'INCOME' | 'EXPENSE'
export type GoalInteractionType = 'DEPOSIT' | 'WITHDRAW'

export interface TransactionForEdit {
  id: string
  name: string
  amount: number
  date: string
  type: 'income' | 'expense'
  bankAccountId: string
  creditCardId?: string | null
  categoryId: string
  bankName: string
  creditCardName?: string
  categoryName: string
  isRecurring?: boolean
}

export function useDashboard() {
  const transactionFilters = useState<TransactionFiltersState | undefined>('transactionFilters', () => undefined)
  const isNewTransactionModalOpen = useState<boolean>('isNewTransactionModalOpen', () => false)
  const newTransactionType = useState<TransactionModalType | null>('newTransactionType', () => null)
  const isEditTransactionModalOpen = useState<boolean>('isEditTransactionModalOpen', () => false)
  const transactionBeingEdited = useState<TransactionForEdit | null>('transactionBeingEdited', () => null)
  const isNewAccountModalOpen = useState<boolean>('isNewAccountModalOpen', () => false)
  const isEditAccountModalOpen = useState<boolean>('isEditAccountModalOpen', () => false)
  const accountBeingEdited = useState<import('~/composables/useBankAccounts').BankAccount | null>('accountBeingEdited', () => null)
  const isNewGoalModalOpen = useState<boolean>('isNewGoalModalOpen', () => false)
  const isNewGoalValueModalOpen = useState<boolean>('isNewGoalValueModalOpen', () => false)
  const goalInteractionType = useState<GoalInteractionType>('goalInteractionType', () => 'DEPOSIT')
  const isEditGoalModalOpen = useState<boolean>('isEditGoalModalOpen', () => false)
  const goalBeingEdited = useState<SavingsGoal | null>('goalBeingEdited', () => null)
  const goalForValueAddition = useState<SavingsGoal | null>('goalForValueAddition', () => null)
  const isConfirmDeleteModalOpen = useState<boolean>('isConfirmDeleteModalOpen', () => false)
  const confirmDeleteEntityType = useState<ConfirmDeleteEntityType | null>('confirmDeleteEntityType', () => null)
  const confirmDeleteEntityId = useState<string | null>('confirmDeleteEntityId', () => null)
  const isMonthlySummaryModalOpen = useState<boolean>('isMonthlySummaryModalOpen', () => false)
  const isEditProfileModalOpen = useState<boolean>('isEditProfileModalOpen', () => false)
  const isNewCreditCardModalOpen = useState<boolean>('isNewCreditCardModalOpen', () => false)
  const isEditCreditCardModalOpen = useState<boolean>('isEditCreditCardModalOpen', () => false)
  const creditCardBeingEdited = useState<import('~/types/creditCard').CreditCard | null>('creditCardBeingEdited', () => null)
  const creditCardForBillModal = useState<import('~/types/creditCard').CreditCard | null>('creditCardForBillModal', () => null)
  const { hasAccounts } = useBankAccounts()
  const { hasCreditCards } = useCreditCards()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  function openNewTransactionModal(type: TransactionModalType) {
    const needAccount = type === 'INCOME' ? hasAccounts.value : (hasAccounts.value || hasCreditCards.value)
    if (!needAccount) {
      toast.error(type === 'INCOME' ? 'Você precisa cadastrar uma conta primeiro!' : 'Cadastre uma conta ou um cartão de crédito para registrar despesas.')
      return
    }
    newTransactionType.value = type
    isNewTransactionModalOpen.value = true
  }

  function closeNewTransactionModal() {
    isNewTransactionModalOpen.value = false
    newTransactionType.value = null
  }

  function openEditTransactionModal(transaction: TransactionForEdit) {
    transactionBeingEdited.value = transaction
    isEditTransactionModalOpen.value = true
  }

  function closeEditTransactionModal() {
    isEditTransactionModalOpen.value = false
    transactionBeingEdited.value = null
  }

  function openNewAccountModal() {
    isNewAccountModalOpen.value = true
  }

  function closeNewAccountModal() {
    isNewAccountModalOpen.value = false
  }

  function openEditAccountModal(account?: import('~/composables/useBankAccounts').BankAccount) {
    accountBeingEdited.value = account ?? null
    isEditAccountModalOpen.value = true
  }

  function closeEditAccountModal() {
    isEditAccountModalOpen.value = false
    accountBeingEdited.value = null
  }

  function openNewGoalModal() {
    isNewGoalModalOpen.value = true
  }

  function closeNewGoalModal() {
    isNewGoalModalOpen.value = false
  }

  function openGoalInteractionModal(type: GoalInteractionType, goal?: SavingsGoal) {
    if (!hasAccounts.value) {
      toast.error('Você precisa cadastrar uma conta bancária primeiro!')
      return
    }
    goalInteractionType.value = type
    goalForValueAddition.value = goal ?? null
    isNewGoalValueModalOpen.value = true
  }

  function closeNewGoalValueModal() {
    isNewGoalValueModalOpen.value = false
    goalForValueAddition.value = null
  }

  function openEditGoalModal(goal?: SavingsGoal) {
    goalBeingEdited.value = goal ?? null
    isEditGoalModalOpen.value = true
  }

  function closeEditGoalModal() {
    isEditGoalModalOpen.value = false
    goalBeingEdited.value = null
  }

  function openConfirmDeleteModal(type: ConfirmDeleteEntityType, id: string) {
    confirmDeleteEntityType.value = type
    confirmDeleteEntityId.value = id
    isConfirmDeleteModalOpen.value = true
    isEditGoalModalOpen.value = false
    isEditAccountModalOpen.value = false
  }

  function closeConfirmDeleteModal() {
    isConfirmDeleteModalOpen.value = false
    confirmDeleteEntityType.value = null
    confirmDeleteEntityId.value = null
  }

  function openMonthlySummaryModal() {
    isMonthlySummaryModalOpen.value = true
  }

  function closeMonthlySummaryModal() {
    isMonthlySummaryModalOpen.value = false
  }

  function openEditProfileModal() {
    isEditProfileModalOpen.value = true
  }

  function closeEditProfileModal() {
    isEditProfileModalOpen.value = false
  }

  function openNewCreditCardModal() {
    isNewCreditCardModalOpen.value = true
  }

  function closeNewCreditCardModal() {
    isNewCreditCardModalOpen.value = false
  }

  function openEditCreditCardModal(card?: import('~/types/creditCard').CreditCard) {
    creditCardBeingEdited.value = card ?? null
    isEditCreditCardModalOpen.value = true
  }

  function closeEditCreditCardModal() {
    isEditCreditCardModalOpen.value = false
    creditCardBeingEdited.value = null
  }

  function openCreditCardBillModal(card: import('~/types/creditCard').CreditCard) {
    creditCardForBillModal.value = card
  }

  function closeCreditCardBillModal() {
    creditCardForBillModal.value = null
  }

  return {
    transactionFilters,
    isNewTransactionModalOpen,
    newTransactionType,
    isNewAccountModalOpen,
    openNewTransactionModal,
    closeNewTransactionModal,
    isEditTransactionModalOpen,
    transactionBeingEdited,
    openEditTransactionModal,
    closeEditTransactionModal,
    openNewAccountModal,
    closeNewAccountModal,
    isEditAccountModalOpen,
    accountBeingEdited,
    openEditAccountModal,
    closeEditAccountModal,
    isNewGoalModalOpen,
    openNewGoalModal,
    closeNewGoalModal,
    isNewGoalValueModalOpen,
    goalInteractionType,
    openGoalInteractionModal,
    closeNewGoalValueModal,
    isEditGoalModalOpen,
    goalBeingEdited,
    openEditGoalModal,
    closeEditGoalModal,
    goalForValueAddition,
    isConfirmDeleteModalOpen,
    confirmDeleteEntityType,
    confirmDeleteEntityId,
    openConfirmDeleteModal,
    closeConfirmDeleteModal,
    isMonthlySummaryModalOpen,
    openMonthlySummaryModal,
    closeMonthlySummaryModal,
    isEditProfileModalOpen,
    openEditProfileModal,
    closeEditProfileModal,
    isNewCreditCardModalOpen,
    openNewCreditCardModal,
    closeNewCreditCardModal,
    isEditCreditCardModalOpen,
    creditCardBeingEdited,
    openEditCreditCardModal,
    closeEditCreditCardModal,
    creditCardForBillModal,
    openCreditCardBillModal,
    closeCreditCardBillModal,
  }
}
