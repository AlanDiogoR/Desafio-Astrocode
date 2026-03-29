export interface PreferenceCheckoutResponse {
  checkoutUrl: string
  preferenceId: string
}

export type SubscriptionStatusLevel = 'FREE' | 'PRO' | 'CANCELLED'

export interface SubscriptionStatusApi {
  status: SubscriptionStatusLevel
  expiresAt: string | null
  isActive: boolean
}

export const subscriptionService = {
  async createCheckout(planId: string): Promise<PreferenceCheckoutResponse> {
    const { $api } = useNuxtApp()
    const { data } = await $api.post<PreferenceCheckoutResponse>('/subscriptions/checkout', { planId })
    return data
  },

  async getStatus(): Promise<SubscriptionStatusApi> {
    const { $api } = useNuxtApp()
    const { data } = await $api.get<SubscriptionStatusApi>('/subscriptions/status')
    return data
  },
}
