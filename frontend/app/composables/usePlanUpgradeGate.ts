/**
 * Estado global para modal de upgrade quando a API retorna 402 Payment Required.
 */
export function usePlanUpgradeGate() {
  const isOpen = useState<boolean>('plan-upgrade-modal-open', () => false)
  const message = useState<string>('plan-upgrade-modal-message', () => '')
  const feature = useState<string | null>('plan-upgrade-modal-feature', () => null)

  function showUpgrade(msg: string, feat?: string | null) {
    message.value = msg?.trim() || 'Faça upgrade para continuar usando o Grivy sem limites.'
    feature.value = feat ?? null
    isOpen.value = true
  }

  function close() {
    isOpen.value = false
  }

  return {
    isOpen,
    message,
    feature,
    showUpgrade,
    close,
  }
}
