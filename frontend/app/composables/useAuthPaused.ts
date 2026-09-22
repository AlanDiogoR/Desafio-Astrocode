import { AUTH_PAUSED_MESSAGE } from '~/utils/apiAvailability'

/** Cadastro e login ficam parados quando a API não está configurada ou não responde. */
export function useAuthPaused() {
  const { isValid: hasApiConfig } = useApiConfig()
  const { unavailable } = useApiAvailability()
  const authPaused = computed(() => !hasApiConfig || unavailable.value)

  return {
    authPaused,
    authPausedMessage: AUTH_PAUSED_MESSAGE,
  }
}
