export interface CategoryApiResponse {
  id: string
  name: string
  icon: string
  type: string
}

export async function listCategories(): Promise<CategoryApiResponse[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<CategoryApiResponse[]>('/categories')
  return Array.isArray(data) ? data : []
}
