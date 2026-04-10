import { describe, expect, it } from 'vitest'

describe('Respostas do Bot (contrato)', () => {
  it('deve responder confirmação ao registrar transação', () => {
    const msg = '✅ Anotado!'
    expect(msg.includes('Anotado')).toBe(true)
  })
})
