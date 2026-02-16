const CATEGORY_ICON_MAP: Record<string, string> = {
  Alimentação: 'alimentacaoDespesa.svg',
  Casa: 'casaDespesa.svg',
  Compras: 'mercadoDespesa.svg',
  Educação: 'educacaoDespesa.svg',
  Investimentos: 'investimentosReceita.svg',
  Lazer: 'lazerDespesa.svg',
  Mercado: 'mercadoDespesa.svg',
  Receita: 'receitasGeral.svg',
  Roupas: 'roupasDespesa.svg',
  Saúde: 'saudeDespesa.svg',
  Transporte: 'transporteDespesa.svg',
  Uber: 'transporteDespesa.svg',
  Viagem: 'viagemDespesa.svg',
  Salário: 'receitasGeral.svg',
  Freelance: 'receitasGeral.svg',
  Outro: 'despesasGeral.svg',
  Outros: 'despesasGeral.svg',
  'Conta Corrente': 'contaCorrenteReceita.svg',
  'Dinheiro Físico': 'dinheiroFisicoReceita.svg',
}

const FALLBACK_INCOME_ICON = 'receitasGeral.svg'
const FALLBACK_EXPENSE_ICON = 'despesasGeral.svg'

function normalizeCategory(name: string): string {
  return name?.trim() ?? ''
}

export function getCategoryIconPath(category: string, type: 'income' | 'expense'): string {
  const fallback = type === 'income' ? FALLBACK_INCOME_ICON : FALLBACK_EXPENSE_ICON
  const name = normalizeCategory(category)
  if (!name) return `/images/${fallback}`
  const mapped = CATEGORY_ICON_MAP[name] ?? Object.entries(CATEGORY_ICON_MAP).find(
    ([key]) => key.toLowerCase() === name.toLowerCase(),
  )?.[1]
  if (mapped) return `/images/${mapped}`
  return `/images/${fallback}`
}

