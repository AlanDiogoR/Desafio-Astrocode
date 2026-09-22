/** Mensagem única para o usuário quando a API não responde (offline, CORS ou base ausente). */
export const API_UNAVAILABLE_MESSAGE =
  'Não foi possível conectar ao serviço agora. Você pode continuar no site; criar conta e entrar voltam a funcionar em alguns minutos.'

const USER_RETRY_PATHS = [
  '/auth/login',
  '/auth/forgot-password',
  '/auth/reset-password',
  '/auth/resend-verification',
] as const

interface ErrorLike {
  code?: string
  message?: string
  response?: unknown
  request?: unknown
}

function asErrorLike(error: unknown): ErrorLike | null {
  if (!error || typeof error !== 'object') return null
  return error as ErrorLike
}

/** Falha de rede, timeout, CORS (o browser entrega como network error) ou API não configurada. */
export function isApiUnavailableError(error: unknown): boolean {
  const err = asErrorLike(error)
  if (!err) return false

  if (
    err.code === 'API_UNAVAILABLE' ||
    err.code === 'API_CONFIG_MISSING' ||
    err.code === 'ERR_NETWORK' ||
    err.code === 'ECONNABORTED'
  ) {
    return true
  }

  const message = (err.message ?? '').toLowerCase()
  if (
    message.includes('network error') ||
    message.includes('failed to fetch') ||
    message.includes('networkerror') ||
    message.includes('cors')
  ) {
    return true
  }

  // Axios: requisição saiu e não houve resposta (queda, DNS, CORS).
  if (err.request && !err.response && err.code !== 'ERR_CANCELED') return true

  return false
}

export function createApiUnavailableError(): Error & { code: string } {
  const err = new Error(API_UNAVAILABLE_MESSAGE) as Error & { code: string }
  err.code = 'API_UNAVAILABLE'
  return err
}

export function createApiConfigError(): Error & { code: string } {
  const err = new Error(API_UNAVAILABLE_MESSAGE) as Error & { code: string }
  err.code = 'API_CONFIG_MISSING'
  return err
}

/**
 * Login, cadastro e recuperação tentam a rede mesmo depois de uma falha,
 * para o usuário conseguir de novo quando o serviço voltar.
 * O cadastro usa POST /users (não /users/me).
 */
export function allowsRetryWhileApiDown(url: unknown, method: unknown): boolean {
  const path = String(url ?? '').split('?')[0]
  const verb = String(method ?? 'get').toLowerCase()

  if (verb === 'post' && (path === '/users' || path.endsWith('/users'))) return true

  return USER_RETRY_PATHS.some((allowed) => path === allowed || path.endsWith(allowed))
}
