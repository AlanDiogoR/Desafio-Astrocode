export interface Category {
  id: string
  name: string
  icon: string
  type: 'INCOME' | 'EXPENSE'
}

const MOCK_CATEGORIES: Category[] = [
  { id: '1', name: 'Salário', icon: 'salary', type: 'INCOME' },
  { id: '2', name: 'Freelance', icon: 'freelance', type: 'INCOME' },
  { id: '3', name: 'Investimentos', icon: 'investment', type: 'INCOME' },
  { id: '4', name: 'Outros', icon: 'other', type: 'INCOME' },
  { id: '5', name: 'Alimentação', icon: 'food', type: 'EXPENSE' },
  { id: '6', name: 'Transporte', icon: 'transport', type: 'EXPENSE' },
  { id: '7', name: 'Lazer', icon: 'leisure', type: 'EXPENSE' },
  { id: '8', name: 'Contas', icon: 'bills', type: 'EXPENSE' },
  { id: '9', name: 'Mercado', icon: 'grocery', type: 'EXPENSE' },
  { id: '10', name: 'Outros', icon: 'other', type: 'EXPENSE' },
]

export function useCategories(transactionType?: Ref<'INCOME' | 'EXPENSE' | undefined>) {
  const categories = computed<{ label: string; value: string }[]>(() => {
    const filterType = transactionType?.value
    const filtered = filterType
      ? MOCK_CATEGORIES.filter((c) => c.type === filterType)
      : MOCK_CATEGORIES
    return filtered.map((c) => ({ label: c.name, value: c.id }))
  })

  return { categories }
}
