import { describe, expect, it } from 'vitest'
import {
  createApiConfigError,
  createApiUnavailableError,
  isApiUnavailableError,
  isRealSession,
  probeApi,
} from '~/utils/apiAvailability'

describe('isApiUnavailableError', () => {
  it('reconhece rede, timeout, CORS e configuração ausente', () => {
    expect(isApiUnavailableError({ code: 'ERR_NETWORK', message: 'Network Error' })).toBe(true)
    expect(isApiUnavailableError({ code: 'ECONNABORTED', message: 'timeout of 30000ms exceeded' })).toBe(true)
    expect(isApiUnavailableError({ code: 'API_CONFIG_MISSING' })).toBe(true)
    expect(isApiUnavailableError({ code: 'API_UNAVAILABLE' })).toBe(true)
    expect(isApiUnavailableError({ message: 'Failed to fetch' })).toBe(true)
    expect(isApiUnavailableError({ message: 'CORS policy blocked' })).toBe(true)
    expect(isApiUnavailableError({ request: {}, response: undefined })).toBe(true)
  })

  it('não trata erro HTTP nem cancelamento como serviço fora', () => {
    expect(isApiUnavailableError({ code: 'ERR_BAD_REQUEST', response: { status: 400 } })).toBe(false)
    expect(isApiUnavailableError({ response: { status: 401 }, message: 'Request failed' })).toBe(false)
    expect(isApiUnavailableError({ code: 'ERR_CANCELED', message: 'canceled' })).toBe(false)
    expect(isApiUnavailableError(null)).toBe(false)
  })

  it('cria erros com código estável', () => {
    expect(createApiUnavailableError().code).toBe('API_UNAVAILABLE')
    expect(createApiConfigError().code).toBe('API_CONFIG_MISSING')
  })
})

describe('isRealSession', () => {
  it('exige token e e-mail', () => {
    expect(isRealSession({ accessToken: 'abc', email: 'a@b.co' })).toBe(true)
    expect(isRealSession({ accessToken: '', email: 'a@b.co' })).toBe(false)
    expect(isRealSession({ accessToken: 'abc', email: 'sem-arroba' })).toBe(false)
    expect(isRealSession(undefined)).toBe(false)
  })
})

describe('probeApi', () => {
  it('considera a API no ar quando o host responde abaixo de 500', async () => {
    const fetchImpl = (async () => new Response(null, { status: 404 })) as typeof fetch
    await expect(probeApi('https://api.exemplo.com/api', fetchImpl)).resolves.toBe(true)
  })

  it('considera a API fora em queda de rede ou 5xx', async () => {
    const down = (async () => {
      throw new Error('failed to fetch')
    }) as typeof fetch
    await expect(probeApi('https://api.exemplo.com/api', down)).resolves.toBe(false)
    const unavailable = (async () => new Response(null, { status: 503 })) as typeof fetch
    await expect(probeApi('https://api.exemplo.com/api', unavailable)).resolves.toBe(false)
    await expect(probeApi('  ', fetch)).resolves.toBe(false)
  })
})
