import type { SubscriptionResponse } from '~/services/subscription/me'

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

/** Formato usado pelo composable useSubscription (derivado de /subscription/me). */
export interface SubscriptionStatus {
  plan: 'FREE' | 'PRO'
  isActive: boolean
  expiresAt: string | null
}

function mapSubscriptionMeToStatus(me: SubscriptionResponse): SubscriptionStatus {
  const isPaid = me.planType !== 'FREE' && me.status === 'ACTIVE'
  const exp = me.expiresAt ? new Date(me.expiresAt) : null
  const isActive = Boolean(isPaid && exp && exp.getTime() > Date.now())
  return {
    plan: isActive ? 'PRO' : 'FREE',
    isActive,
    expiresAt: me.expiresAt,
  }
}

export const subscriptionService = {
  async processPayment(data: PaymentRequest): Promise<PaymentResponse> {
    const { $api } = useNuxtApp()
    const { data: response } = await $api.post<PaymentResponse>('/subscriptions/process-payment', data)
    return response
  },

  /** Usa GET /api/subscription/me (mesmo contrato do restante do app). */
  async getStatus(): Promise<SubscriptionStatus> {
    const { $api } = useNuxtApp()
    const { data } = await $api.get<SubscriptionResponse>('/subscription/me')
    return mapSubscriptionMeToStatus(data)
  },
}
