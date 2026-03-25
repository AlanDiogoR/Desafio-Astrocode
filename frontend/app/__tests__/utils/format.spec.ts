import { describe, it, expect } from 'vitest'
import { formatCurrency, formatDate, parseDateString, toDateString } from '~/utils/format'

describe('formatCurrency', () => {
  it('formata 1500 no padrão pt-BR (moeda BRL)', () => {
    expect(formatCurrency(1500)).toMatch(/^R\$\s*1\.500,00$/)
  })

  it('formata 0 no padrão pt-BR', () => {
    expect(formatCurrency(0)).toMatch(/^R\$\s*0,00$/)
  })
})

describe('formatDate', () => {
  it('formata data no padrão d MMM yyyy', () => {
    const date = new Date(2024, 0, 15) // 15 jan 2024
    expect(formatDate(date)).toMatch(/15.*jan.*2024/i)
  })

  it('retorna string vazia para null', () => {
    expect(formatDate(null)).toBe('')
  })

  it('com parseDateString, data UTC não muda o dia (bug de timezone)', () => {
    const parsed = parseDateString('2024-01-05T00:00:00Z')
    expect(parsed).not.toBeNull()
    expect(parsed!.getDate()).toBe(5)
    expect(parsed!.getMonth()).toBe(0)
    expect(parsed!.getFullYear()).toBe(2024)
    const formatted = formatDate(parsed)
    expect(formatted).toMatch(/5.*jan.*2024/i)
  })
})

describe('parseDateString', () => {
  it('parse yyyy-MM-dd para Date local', () => {
    const result = parseDateString('2024-01-15')
    expect(result).toEqual(new Date(2024, 0, 15))
  })

  it('retorna null para string vazia', () => {
    expect(parseDateString('')).toBeNull()
    expect(parseDateString(null)).toBeNull()
  })
})

describe('toDateString', () => {
  it('converte Date para yyyy-MM-dd', () => {
    const date = new Date(2024, 0, 5)
    expect(toDateString(date)).toBe('2024-01-05')
  })
})
