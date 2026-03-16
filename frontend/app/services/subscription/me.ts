const BASE = '/subscription'

export interface SubscriptionResponse {
  id: string
  planType: string
  status: string
  startsAt: string | null
  expiresAt: string | null
  amountPaid: number | null
  createdAt: string
}

export async function getSubscription($api: { get: (url: string) => Promise<{ data: SubscriptionResponse }> }): Promise<SubscriptionResponse> {
  const { data } = await $api.get(BASE + '/me')
  return data
}
