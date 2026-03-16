import type { CreditCard } from '~/types/creditCard'

interface UpdateCreditCardPayload {
  name: string
  creditLimit: number
  closingDay: number
  dueDay: number
  color?: string | null
}

export async function updateCreditCard(
  id: string,
  payload: UpdateCreditCardPayload
): Promise<CreditCard> {
  const { $api } = useNuxtApp()
  const { data } = await $api.put<CreditCard>(`/credit-cards/${id}`, payload)
  return data
}
