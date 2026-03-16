import type { CreditCard } from '~/types/creditCard'

export async function listCreditCards(): Promise<CreditCard[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<CreditCard[]>('/credit-cards')
  return Array.isArray(data) ? data : []
}
