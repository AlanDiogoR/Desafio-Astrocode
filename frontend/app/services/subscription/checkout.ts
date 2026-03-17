const BASE = '/subscription'

export interface CheckoutRequest {
  planType: string
  token: string
  paymentMethodId: string
  installments: number
  payerEmail: string
  payerIdentificationType?: string
  payerIdentificationNumber?: string
  issuerId?: string
}

export interface CheckoutResponse {
  id: string
  planType: string
  status: string
  amountPaid: number | null
  mpPaymentId: string | null
  expiresAt: string | null
}

export async function checkout(
  $api: { post: (url: string, body: CheckoutRequest) => Promise<{ data: CheckoutResponse }> },
  payload: CheckoutRequest
): Promise<CheckoutResponse> {
  const { data } = await $api.post(BASE + '/checkout', payload)
  return data
}
