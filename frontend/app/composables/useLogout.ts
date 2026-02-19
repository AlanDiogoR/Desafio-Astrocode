import { useQueryClient } from '@tanstack/vue-query'

export function useLogout() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()
  const router = useRouter()

  function performLogout() {
    authStore.clearAuth()
    if (import.meta.client) {
      queryClient.clear()
      useState('transactionFilters').value = undefined
      useState('isNewTransactionModalOpen').value = false
      useState('newTransactionType').value = null
      useState('isEditTransactionModalOpen').value = false
      useState('transactionBeingEdited').value = null
      useState('isNewAccountModalOpen').value = false
      useState('isNewGoalModalOpen').value = false
      useState('isNewGoalValueModalOpen').value = false
      useState('goalInteractionType').value = 'DEPOSIT'
      useState('isEditGoalModalOpen').value = false
      useState('goalBeingEdited').value = null
      useState('goalForValueAddition').value = null
      useState('isConfirmDeleteModalOpen').value = false
      useState('confirmDeleteEntityType').value = null
      useState('confirmDeleteEntityId').value = null
      useState('isMonthlySummaryModalOpen').value = false
      useState('isEditProfileModalOpen').value = false
    }
    router.replace('/login')
  }

  return { performLogout }
}
