import { getCategoryIconPath } from '~/pages/dashboard/utils/categoryIcon'

const BANK_ICON_MAP: Record<string, string> = {
  Nubank: 'contaCorrenteConta.svg',
  'XP Investimentos': 'investimentosConta.svg',
  'Dinheiro Físico': 'dinheiroFisicoConta.svg',
}

const FALLBACK_INCOME = 'receitasGeral.svg'
const FALLBACK_EXPENSE = 'despesasGeral.svg'

export function getBankIconPath(bankName: string, type: 'income' | 'expense'): string {
  const key = Object.keys(BANK_ICON_MAP).find((k) =>
    bankName.toLowerCase().includes(k.toLowerCase()),
  )
  if (key) return `/images/${BANK_ICON_MAP[key]}`
  const filename = type === 'income' ? FALLBACK_INCOME : FALLBACK_EXPENSE
  return `/images/${filename}`
}

export function getTransactionCategoryIconPath(categoryName: string, type: 'income' | 'expense'): string {
  return getCategoryIconPath(categoryName, type)
}
