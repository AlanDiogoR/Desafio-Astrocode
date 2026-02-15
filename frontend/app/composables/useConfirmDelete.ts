import { deleteAccount } from '~/services/bankAccountsService'
import { deleteGoal } from '~/services/goalsService'
import { deleteTransaction } from '~/services/transactionsService'

export function useConfirmDelete() {
  const { confirmDeleteEntityType, confirmDeleteEntityId, closeConfirmDeleteModal } = useDashboard()
  const { invalidateBankAccounts } = useBankAccounts()
  const { invalidateGoals } = useGoals()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  async function executeDelete(): Promise<void> {
    const type = confirmDeleteEntityType.value
    const id = confirmDeleteEntityId.value
    if (!type || !id) return

    if (type === 'ACCOUNT') {
      await deleteAccount(id)
      toast.success('Conta deletada com sucesso!')
      invalidateBankAccounts()
    } else if (type === 'GOAL') {
      await deleteGoal(id)
      toast.success('Meta deletada com sucesso!')
      invalidateGoals()
    } else if (type === 'TRANSACTION') {
      await deleteTransaction(id)
      toast.success('Transação deletada com sucesso!')
      invalidateBankAccounts()
    }
  }

  async function handleConfirm(): Promise<void> {
    const type = confirmDeleteEntityType.value
    try {
      await executeDelete()
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message
      if (type === 'ACCOUNT') toast.error(message ?? 'Erro ao deletar conta.')
      else if (type === 'GOAL') toast.error(message ?? 'Erro ao deletar meta.')
      else toast.error(message ?? 'Erro ao deletar transação.')
      throw err
    }
  }

  return { handleConfirm, closeConfirmDeleteModal }
}
