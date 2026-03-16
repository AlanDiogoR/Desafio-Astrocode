import type { CreditCardBill } from '~/types/creditCard'

export async function getCreditCardCurrentBill(
  creditCardId: string
): Promise<CreditCardBill> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<CreditCardBill>(
    `/credit-cards/${creditCardId}/bill/current`
  )
  return data
}
