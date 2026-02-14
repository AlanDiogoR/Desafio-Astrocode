import type { SavingsGoal } from '~/composables/useGoals'

export type TransactionModalType = 'INCOME' | 'EXPENSE'
export type GoalInteractionType = 'DEPOSIT' | 'WITHDRAW'

export function useDashboard() {
  const isNewTransactionModalOpen = useState<boolean>('isNewTransactionModalOpen', () => false)
  const newTransactionType = useState<TransactionModalType | null>('newTransactionType', () => null)
  const isNewAccountModalOpen = useState<boolean>('isNewAccountModalOpen', () => false)
  const isNewGoalModalOpen = useState<boolean>('isNewGoalModalOpen', () => false)
  const isNewGoalValueModalOpen = useState<boolean>('isNewGoalValueModalOpen', () => false)
  const goalInteractionType = useState<GoalInteractionType>('goalInteractionType', () => 'DEPOSIT')
  const isEditGoalModalOpen = useState<boolean>('isEditGoalModalOpen', () => false)
  const goalBeingEdited = useState<SavingsGoal | null>('goalBeingEdited', () => null)
  const goalForValueAddition = useState<SavingsGoal | null>('goalForValueAddition', () => null)
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

  function openNewGoalModal() {
    isNewGoalModalOpen.value = true
  }

  function closeNewGoalModal() {
    isNewGoalModalOpen.value = false
  }

  function openGoalInteractionModal(type: GoalInteractionType, goal?: SavingsGoal) {
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

  return {
    isNewTransactionModalOpen,
    newTransactionType,
    isNewAccountModalOpen,
    openNewTransactionModal,
    closeNewTransactionModal,
    openNewAccountModal,
    closeNewAccountModal,
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
    goalForValueAddition
  }
}
