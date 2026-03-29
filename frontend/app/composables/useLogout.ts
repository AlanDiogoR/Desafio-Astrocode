import { useQueryClient } from '@tanstack/vue-query'
import { logout as logoutApi } from '~/services/auth/logout'

export function useLogout() {
  const authStore = useAuthStore()
  const queryClient = useQueryClient()
  const router = useRouter()
  const { clearSessionTokens } = useAuthCookies()

  async function performLogout() {
    try {
      await logoutApi()
    } catch {
      /* ignora erro - cookie pode já estar expirado */
    }
    clearSessionTokens()
    authStore.clearAuth()
    if (import.meta.client) {
      queryClient.clear()
      useState('transactionFilters').value = undefined
      useState('isNewTransactionModalOpen').value = false
      useState('newTransactionType').value = null
      useState('isEditTransactionModalOpen').value = false
      useState('transactionBeingEdited').value = null
      useState('isNewAccountModalOpen').value = false
      useState('isEditAccountModalOpen').value = false
      useState('accountBeingEdited').value = null
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
      useState('isNewCreditCardModalOpen').value = false
      useState('isEditCreditCardModalOpen').value = false
      useState('creditCardBeingEdited').value = null
      useState('creditCardForBillModal').value = null
      useState('dashboard-areValuesVisible').value = true
    }
    router.replace('/login')
  }

  return { performLogout }
}
