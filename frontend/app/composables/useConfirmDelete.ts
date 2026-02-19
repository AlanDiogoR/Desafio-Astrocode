import { useQueryClient } from '@tanstack/vue-query'
import { getErrorMessage } from '~/utils/errorHandler'
import { deleteBankAccount } from '~/services/bankAccounts'
import { deleteGoal } from '~/services/goals'
import { deleteTransaction } from '~/services/transactions'
import { TRANSACTIONS_QUERY_KEY } from '~/composables/useTransactions'

export function useConfirmDelete() {
  const queryClient = useQueryClient()
  const { confirmDeleteEntityType, confirmDeleteEntityId, closeConfirmDeleteModal, transactionFilters } = useDashboard()
  const { invalidateBankAccounts } = useBankAccounts()
  const { invalidateGoals } = useGoals()
  const { invalidateDashboard } = useDashboardData()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  async function executeDelete(): Promise<void> {
    const type = confirmDeleteEntityType.value
    const id = confirmDeleteEntityId.value
    if (!type || !id) return

    if (type === 'ACCOUNT') {
      if (transactionFilters.value?.bankAccountId === id) {
        const current = transactionFilters.value
        const { bankAccountId: _removed, ...rest } = current
        transactionFilters.value = Object.keys(rest).length > 0 ? (rest as typeof current) : undefined
      }
      await deleteBankAccount(id)
      toast.success('Conta deletada com sucesso!')
      await invalidateBankAccounts()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
    } else if (type === 'GOAL') {
      await deleteGoal(id)
      toast.success('Meta deletada com sucesso!')
      invalidateGoals()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
    } else if (type === 'TRANSACTION') {
      await deleteTransaction(id)
      toast.success('Transação excluída com sucesso!')
      await invalidateBankAccounts()
      await invalidateDashboard()
      queryClient.invalidateQueries({ queryKey: TRANSACTIONS_QUERY_KEY })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary'] })
      queryClient.invalidateQueries({ queryKey: ['monthly-summary-modal'] })
    }
  }

  async function handleConfirm(): Promise<void> {
    const type = confirmDeleteEntityType.value
    try {
      await executeDelete()
    } catch (err: unknown) {
      const msg = getErrorMessage(err, 'Erro ao excluir.')
      if (type === 'ACCOUNT') toast.error(msg)
      else if (type === 'GOAL') toast.error(msg)
      else toast.error(msg)
      throw err
    }
  }

  return { handleConfirm, closeConfirmDeleteModal }
}
