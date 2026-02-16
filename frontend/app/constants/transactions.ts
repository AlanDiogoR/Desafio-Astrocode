function iconPath(name: string): string {
  return `/images/${encodeURIComponent(name)}`
}

export const ICON_FILTERS = {
  GRAY: 'brightness(0) saturate(100%) invert(29%) sepia(15%) saturate(664%) hue-rotate(174deg) brightness(94%) contrast(89%)',
  GREEN: 'brightness(0) saturate(100%) invert(32%) sepia(96%) saturate(1836%) hue-rotate(137deg) brightness(93%) contrast(101%)',
  RED: 'brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%)',
} as const

export interface TransactionTypeOption {
  value: string
  label: string
  icon: string
  filter: string
  colorClass: 'income' | 'expense' | 'neutral'
}

export const TRANSACTION_TYPES: TransactionTypeOption[] = [
  {
    value: 'all',
    label: 'Transações',
    icon: iconPath('Nome=Transações.png'),
    filter: ICON_FILTERS.GRAY,
    colorClass: 'neutral',
  },
  {
    value: 'income',
    label: 'Receitas',
    icon: iconPath('Nome=Receitas.png'),
    filter: ICON_FILTERS.GREEN,
    colorClass: 'income',
  },
  {
    value: 'expense',
    label: 'Despesas',
    icon: iconPath('Nome=Despesas.png'),
    filter: ICON_FILTERS.RED,
    colorClass: 'expense',
  },
]

export interface TransactionFabOption {
  label: string
  action: string
  icon: string
  filter: string
  size?: number
}

export const TRANSACTION_FAB_OPTIONS: TransactionFabOption[] = [
  {
    label: 'Nova despesa',
    action: 'new-expense',
    icon: iconPath('Nome=Despesas.png'),
    filter: ICON_FILTERS.RED,
    size: 28,
  },
  {
    label: 'Nova receita',
    action: 'new-income',
    icon: iconPath('Nome=Receitas.png'),
    filter: ICON_FILTERS.GREEN,
    size: 28,
  },
  {
    label: 'Nova conta',
    action: 'new-account',
    icon: '/images/banco.svg',
    filter: '',
    size: 34,
  },
]

export const MONTH_NAMES = [
  'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
  'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez',
] as const
