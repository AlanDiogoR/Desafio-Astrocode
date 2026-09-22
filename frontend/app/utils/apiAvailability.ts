/** Aviso quando a API não responde. Auth fica desabilitado — sem sucesso falso. */
export const API_UNAVAILABLE_MESSAGE =
  'Cadastro e login em breve. O acesso volta quando o serviço estiver no ar.'

export const AUTH_PAUSED_MESSAGE = API_UNAVAILABLE_MESSAGE

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

/** Sessão só existe com token e e-mail reais. Resposta vazia não é login. */
export function isRealSession(data: { accessToken?: string; email?: string } | null | undefined): boolean {
  return typeof data?.accessToken === 'string'
    && data.accessToken.trim().length > 0
    && typeof data.email === 'string'
    && data.email.includes('@')
}

/**
 * A API está no ar se o host responde (2xx–4xx). Queda de rede, timeout ou 5xx contam como fora.
 */
export async function probeApi(
  apiBase: string,
  fetchImpl: typeof fetch = fetch,
  timeoutMs = 4000,
): Promise<boolean> {
  const base = apiBase.trim()
  if (!base) return false
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeoutMs)
  try {
    const url = `${base.replace(/\/$/, '')}/health`
    const res = await fetchImpl(url, {
      method: 'GET',
      signal: controller.signal,
      credentials: 'omit',
    })
    return res.status > 0 && res.status < 500
  } catch {
    return false
  } finally {
    clearTimeout(timer)
  }
}
