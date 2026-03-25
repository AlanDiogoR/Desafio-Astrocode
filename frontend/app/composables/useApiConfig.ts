/** Indica se `NUXT_PUBLIC_API_BASE` está definida (evita chamadas com base vazia). */
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
