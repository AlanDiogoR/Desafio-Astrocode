export function useOpenFinance() {
  const { $api } = useNuxtApp()
  const authStore = useAuthStore()
  const { invalidateBankAccounts } = useBankAccounts()
  const queryClient = useQueryClient()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  const isElite = computed(() => authStore.isEliteUser)
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
      const msg = err?.response?.data?.message ?? err?.response?.status === 402
        ? 'Open Finance está disponível no plano Elite Anual.'
        : 'Erro ao abrir Open Finance.'
      toast?.error(msg)
      isConnecting.value = false
    }
  }

  return { openPluggyConnect, isElite, isConnecting }
}
