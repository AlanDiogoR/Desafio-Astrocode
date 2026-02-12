const CATEGORY_ICON_MAP: Record<string, string> = {
  Alimentação: 'Categoria=Alimentação, Tipo=Despesa.svg',
  Casa: 'Categoria=Casa, Tipo=Despesa.svg',
  Compras: 'Categoria=Mercado, Tipo=Despesa.svg',
  Educação: 'Categoria=Educação, Tipo=Despesa.svg',
  Investimentos: 'Categoria=Investimentos, Tipo=Receita.svg',
  Lazer: 'Categoria=Lazer, Tipo=Despesa.svg',
  Mercado: 'Categoria=Mercado, Tipo=Despesa.svg',
  Receita: 'Categoria=Receitas, Tipo=Geral.svg',
  Roupas: 'Categoria=Roupas, Tipo=Despesa.svg',
  Saúde: 'Categoria=Saúde, Tipo=Despesa.svg',
  Transporte: 'Categoria=Transporte, Tipo=Despesa.svg',
  Uber: 'Categoria=Transporte, Tipo=Despesa.svg',
  Viagem: 'Categoria=Viagem, Tipo=Despesa.svg',
}

const DEFAULT_INCOME_ICON = 'Categoria=Conta Corrente, Tipo=Receita.svg'
const DEFAULT_EXPENSE_ICON = 'Categoria=Despesas, Tipo=Geral.svg'

export function getCategoryIconPath(category: string, type: 'income' | 'expense'): string {
  const filename = CATEGORY_ICON_MAP[category]
    ?? (type === 'income' ? DEFAULT_INCOME_ICON : DEFAULT_EXPENSE_ICON)
  return `/images/${encodeURIComponent(filename)}`
}
