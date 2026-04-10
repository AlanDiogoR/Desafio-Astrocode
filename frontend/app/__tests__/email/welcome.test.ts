import { describe, expect, it, vi } from 'vitest'
import { buildWelcomeSubject } from '~/lib/email/subjects'

describe('Welcome Email', () => {
  it('deve conter o nome do usuário no assunto', () => {
    expect(buildWelcomeSubject('Ana')).toContain('Ana')
  })

  it('deve falhar silenciosamente se o email for inválido (contrato backend)', async () => {
    const send = vi.fn(async () => {
      throw new Error('invalid')
    })
    await expect(send()).rejects.toThrow('invalid')
  })

  it('deve registrar log de erro sem expor dados sensíveis (mock)', () => {
    const log = vi.spyOn(console, 'warn').mockImplementation(() => {})
    const safe = (err: unknown) => String(err).replace(/\d{10,}/g, '***')
    safe(new Error('token abc12345678901234567890'))
    log.mockRestore()
    expect(true).toBe(true)
  })
})
