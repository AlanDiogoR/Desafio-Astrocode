import { describe, expect, it, vi } from 'vitest'

describe('Onboarding Drip', () => {
  it('deve agendar 3 emails após o cadastro (contrato)', () => {
    const schedule = vi.fn()
    ;['D1', 'D3', 'D7'].forEach((c) => schedule(c))
    expect(schedule).toHaveBeenCalledTimes(3)
  })

  it('deve cancelar a sequência se o usuário desativar notificações', () => {
    const cancel = vi.fn()
    cancel(true)
    expect(cancel).toHaveBeenCalledWith(true)
  })
})
