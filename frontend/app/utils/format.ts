import { format as dateFnsFormat } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import { capitalizeFirstLetter } from './capitalize'

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
