const STATE_KEY = 'grivy-api-unavailable'

/** Estado compartilhado: a API não respondeu e o restante do app deve falhar sem nova rajada de rede. */
export function useApiAvailability() {
  const unavailable = useState(STATE_KEY, () => false)

  function markUnavailable() {
    unavailable.value = true
  }

  function markAvailable() {
    unavailable.value = false
  }

  return {
    unavailable,
    markUnavailable,
    markAvailable,
  }
}
