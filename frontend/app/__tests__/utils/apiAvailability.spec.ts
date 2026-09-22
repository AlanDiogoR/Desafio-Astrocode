import { describe, expect, it } from 'vitest'
import {
  allowsRetryWhileApiDown,
  createApiConfigError,
  createApiUnavailableError,
  isApiUnavailableError,
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

describe('allowsRetryWhileApiDown', () => {
  it('libera só ações de conta iniciadas pelo usuário', () => {
    expect(allowsRetryWhileApiDown('/auth/login', 'post')).toBe(true)
    expect(allowsRetryWhileApiDown('/auth/forgot-password', 'post')).toBe(true)
    expect(allowsRetryWhileApiDown('/auth/reset-password', 'POST')).toBe(true)
    expect(allowsRetryWhileApiDown('/auth/resend-verification', 'post')).toBe(true)
    expect(allowsRetryWhileApiDown('/users', 'post')).toBe(true)
    expect(allowsRetryWhileApiDown('/users/me', 'get')).toBe(false)
    expect(allowsRetryWhileApiDown('/auth/refresh', 'post')).toBe(false)
    expect(allowsRetryWhileApiDown('/dashboard', 'get')).toBe(false)
  })
})
