import { describe, expect, it } from 'vitest'
import { API_UNAVAILABLE_MESSAGE } from '~/utils/apiAvailability'
import { getErrorMessage } from '~/utils/errorHandler'

describe('getErrorMessage', () => {
  it('devolve mensagem amigável quando a API está fora', () => {
    expect(getErrorMessage({ code: 'ERR_NETWORK', message: 'Network Error' })).toBe(API_UNAVAILABLE_MESSAGE)
    expect(getErrorMessage({ code: 'API_CONFIG_MISSING' })).toBe(API_UNAVAILABLE_MESSAGE)
    expect(getErrorMessage({ request: {}, message: 'Network Error' })).toBe(API_UNAVAILABLE_MESSAGE)
  })

  it('mantém mensagens de status HTTP quando o servidor responde', () => {
    expect(getErrorMessage({ response: { status: 404, data: {} } })).toBe('Recurso não encontrado.')
  })
})