/**
 * Cookies de sessão no domínio do frontend (Netlify).
 * Tradeoff: httpOnly=false é necessário para o middleware Nuxt e o axios lerem o JWT em SPA
 * (sem SSR real de API). O token continua protegido por HTTPS (secure) e SameSite=strict.
 * O backend aceita Authorization: Bearer ou cookie auth_token na API (mesmo nome).
 */
const FOURTEEN_DAYS = 60 * 60 * 24 * 14

export function useAuthCookies() {
  const isHttps = import.meta.client ? window.location.protocol === 'https:' : true

  const authToken = useCookie<string | null>('auth_token', {
    httpOnly: false,
    secure: import.meta.dev ? false : isHttps,
    sameSite: 'strict',
    maxAge: FOURTEEN_DAYS,
    path: '/',
  })

  const refreshToken = useCookie<string | null>('refresh_token', {
    httpOnly: false,
    secure: import.meta.dev ? false : isHttps,
    sameSite: 'strict',
    maxAge: FOURTEEN_DAYS,
    path: '/',
  })

  function setSessionTokens(access: string, refresh: string) {
    authToken.value = access
    refreshToken.value = refresh
  }

  function clearSessionTokens() {
    authToken.value = null
    refreshToken.value = null
  }

  return { authToken, refreshToken, setSessionTokens, clearSessionTokens }
}
