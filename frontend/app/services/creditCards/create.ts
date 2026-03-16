import type { CreditCard } from '~/types/creditCard'

interface CreateCreditCardPayload {
  name: string
  creditLimit: number
  closingDay: number
  dueDay: number
  color?: string | null
}

export async function createCreditCard(
  payload: CreateCreditCardPayload
): Promise<CreditCard> {
  const { $api } = useNuxtApp()
  const { data } = await $api.post<CreditCard>('/credit-cards', payload)
  return data
}
