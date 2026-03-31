import { useQueryClient } from '@tanstack/vue-query'

export function useOpenFinance() {
  const { $api } = useNuxtApp()
  const authStore = useAuthStore()
  const { subscriptionStatus } = useSubscription()
  const { invalidateBankAccounts } = useBankAccounts()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  /** JWT pode ficar desatualizado; /subscription/me com planType ANNUAL é fonte de verdade. */
  const isElite = computed(() => {
    if (subscriptionStatus.value?.isElite) return true
    return authStore.isEliteUser
  })
  const isConnecting = ref(false)

  async function openPluggyConnect() {
    if (!isElite.value) {
      toast?.error('Open Finance está disponível no plano Elite Anual.')
      return
    }
    if (isConnecting.value) return
    isConnecting.value = true

    try {
      const { getConnectToken } = await import('~/services/openFinance/getConnectToken')
      const { syncOpenFinanceAccounts } = await import('~/services/openFinance/syncAccounts')
      const accessToken = await getConnectToken($api)

      const PluggyConnect = (await import('pluggy-connect-sdk')).default
      const pluggyConnect = new PluggyConnect({
        connectToken: accessToken,
        onSuccess: async (itemData: unknown) => {
          const item = itemData as { id?: string }
          const itemId = item?.id
          if (!itemId) {
            toast?.error('Dados da conexão incompletos.')
            isConnecting.value = false
            return
          }
          try {
            const synced = await syncOpenFinanceAccounts($api, itemId)
            await invalidateBankAccounts()
            queryClient.invalidateQueries({ queryKey: ['dashboard'] })
            toast?.success(synced.length > 0
              ? `${synced.length} conta(s) importada(s) com sucesso!`
              : 'Conexão realizada. Nenhuma conta bancária encontrada.')
          } catch {
            toast?.error('Erro ao importar contas.')
          } finally {
            isConnecting.value = false
          }
        },
        onError: (error: unknown) => {
          const msg = error instanceof Error ? error.message : 'Erro ao conectar'
          if (!msg.toLowerCase().includes('close') && !msg.toLowerCase().includes('cancel')) {
            toast?.error(msg)
          }
          isConnecting.value = false
        },
      })
      pluggyConnect.init()
    } catch (e) {
      const err = e as { response?: { status?: number; data?: { message?: string } } }
      const status = err?.response?.status
      const dataMsg = err?.response?.data?.message
      let msg = 'Erro ao abrir Open Finance.'
      if (status === 503) {
        msg = 'Open Finance temporariamente indisponível. Tente novamente mais tarde.'
      }
      else if (status === 402 || status === 403) {
        msg = 'Open Finance está disponível no plano Elite Anual.'
      }
      else if (typeof dataMsg === 'string' && dataMsg.length > 0) {
        msg = dataMsg
      }
      toast?.error(msg)
      isConnecting.value = false
    }
  }

  return { openPluggyConnect, isElite, isConnecting }
}
