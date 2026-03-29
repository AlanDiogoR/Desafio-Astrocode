export interface PaymentRequest {
  token: string
  transactionAmount: number
  installments: number
  paymentMethodId: string
  planId: string
  payer: {
    email: string
    firstName?: string
    lastName?: string
    identification?: {
      type: string
      number: string
    }
  }
  issuerId?: string
}

export type PaymentStatus = 'approved' | 'rejected' | 'pending' | 'in_process'

export interface PaymentResponse {
  paymentId: number
  status: PaymentStatus | string
  statusDetail: string
  message: string
}

export interface SubscriptionStatus {
  plan: 'FREE' | 'PRO'
  isActive: boolean
  expiresAt: string | null
}

export const subscriptionService = {
  async processPayment(data: PaymentRequest): Promise<PaymentResponse> {
    const { $api } = useNuxtApp()
    const { data: response } = await $api.post<PaymentResponse>('/subscriptions/process-payment', data)
    return response
  },

  async getStatus(): Promise<SubscriptionStatus> {
    const { $api } = useNuxtApp()
    const { data } = await $api.get<SubscriptionStatus>('/subscriptions/status')
    return data
  },
}
