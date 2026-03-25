import { format as dateFnsFormat } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import { capitalizeFirstLetter } from './capitalize'

/** yyyy-MM-dd local (evita UTC de `toISOString()`). */
export function toDateString(date: Date): string {
  return dateFnsFormat(date, 'yyyy-MM-dd')
}

/** Interpreta yyyy-MM-dd ou prefixo ISO como data local (não como instante UTC). */
export function parseDateString(value: string | null | undefined): Date | null {
  if (!value || typeof value !== 'string') return null
  const match = value.match(/^(\d{4})-(\d{2})-(\d{2})/)
  if (match) {
    const [, y, m, d] = match
    return new Date(Number(y), Number(m) - 1, Number(d))
  }
  return null
}

export function formatCurrency(value: number): string {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

export function formatDate(date: Date | null, pattern = 'd MMM yyyy'): string {
  if (!date) return ''
  const formatted = dateFnsFormat(date, pattern, { locale: ptBR })
  const parts = formatted.split(' ')
  if (parts.length >= 2) {
    parts[1] = capitalizeFirstLetter(parts[1])
  }
  return parts.join(' ')
}
