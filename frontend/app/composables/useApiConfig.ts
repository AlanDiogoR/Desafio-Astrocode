/**
 * Fail-fast: verifica se a API está configurada antes de fazer chamadas.
 * Evita 502 silenciosos por NUXT_PUBLIC_API_BASE ausente.
 */
export function useApiConfig() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase as string | undefined
  const isValid = typeof apiBase === 'string' && apiBase.trim().length > 0

  return {
    apiBase: isValid ? apiBase : '',
    isValid,
    isMissing: !isValid,
  }
}
