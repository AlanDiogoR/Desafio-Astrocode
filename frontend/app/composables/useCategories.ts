import { useQuery } from '@tanstack/vue-query'
import { getAllCategories } from '~/services/categoriesService'

export interface Category {
  id: string
  name: string
  icon: string
  type: 'INCOME' | 'EXPENSE'
}

export const CATEGORIES_QUERY_KEY = ['categories'] as const

function mapApiToCategory(raw: { id: string; name: string; icon: string; type: string }): Category {
  const type = raw.type?.toUpperCase() as 'INCOME' | 'EXPENSE'
  return {
    id: raw.id,
    name: raw.name,
    icon: raw.icon ?? '',
    type: type === 'INCOME' || type === 'EXPENSE' ? type : 'EXPENSE',
  }
}

export function useCategories(transactionType?: Ref<'INCOME' | 'EXPENSE' | undefined>) {
  const authStore = useAuthStore()

  const { data: categoriesData } = useQuery({
    queryKey: CATEGORIES_QUERY_KEY,
    queryFn: async () => {
      const raw = await getAllCategories()
      return raw.map(mapApiToCategory)
    },
    enabled: computed(() => !!authStore.token),
  })

  const categories = computed<{ label: string; value: string }[]>(() => {
    const list = categoriesData.value ?? []
    const filterType = transactionType?.value
    const filtered = filterType ? list.filter((c) => c.type === filterType) : list
    return filtered.map((c) => ({ label: c.name, value: c.id }))
  })

  return { categories }
}
