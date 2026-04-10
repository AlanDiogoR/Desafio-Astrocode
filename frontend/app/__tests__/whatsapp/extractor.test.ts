import { describe, expect, it } from 'vitest'

/** Espelho leve da lógica de regex do backend para testes de contrato no front. */
function parseManually(message: string) {
  const lower = message.toLowerCase()
  if (lower.includes('resumo') && lower.includes('mês')) return { intent: 'resumo_mensal' }
  if (lower.includes('saldo')) return { intent: 'consulta_saldo' }
  const m = message.match(/(\d+[.,]?\d*)/)
  const val = m ? Number(m[1].replace(',', '.')) : null
  if (lower.includes('gastei') && val != null) return { tipo: 'despesa', valor: val, intent: 'transacao' }
  return { intent: 'desconhecido' }
}

describe('Extração de dados financeiros (contrato)', () => {
  it('deve extrair despesa simples', () => {
    const r = parseManually('gastei 50 no mercado')
    expect(r.tipo).toBe('despesa')
    expect(r.valor).toBe(50)
  })

  it('deve reconhecer intent de resumo', () => {
    expect(parseManually('me manda um resumo do mês').intent).toBe('resumo_mensal')
  })
})
