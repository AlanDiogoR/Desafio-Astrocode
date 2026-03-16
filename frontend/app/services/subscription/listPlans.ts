const BASE = '/subscription' as const

export interface PlanInfo {
  id: string
  name: string
  price: number
  months: number
  description: string
}

export async function listPlans($api: { get: (url: string) => Promise<{ data: PlanInfo[] }> }): Promise<PlanInfo[]> {
  const { data } = await $api.get(BASE + '/plans')
  return data
}
