import type { CreditCardBill } from '~/types/creditCard'

export async function getCreditCardBillHistory(
  creditCardId: string
): Promise<CreditCardBill[]> {
  const { $api } = useNuxtApp()
  const { data } = await $api.get<CreditCardBill[]>(
    `/credit-cards/${creditCardId}/bills`
  )
  return Array.isArray(data) ? data : []
}
