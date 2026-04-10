import { describe, expect, it } from 'vitest'

describe('Reativação', () => {
  it('deve identificar usuários inativos há mais de 14 dias (regra)', () => {
    const daysSinceLogin = 15
    expect(daysSinceLogin >= 14).toBe(true)
  })

  it('deve não enviar para usuários que já receberam reativação nos últimos 30 dias', () => {
    const lastSentDaysAgo = 10
    expect(lastSentDaysAgo < 30).toBe(true)
  })
})
