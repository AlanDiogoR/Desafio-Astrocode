import type { CreditCardBill } from '~/types/creditCard'

interface PayBillPayload {
  bankAccountId: string
  payDate: string
  amount: number
}

export async function payCreditCardBill(
  billId: string,
  payload: PayBillPayload
): Promise<CreditCardBill> {
  const { $api } = useNuxtApp()
  const { data } = await $api.post<CreditCardBill>(
    `/credit-cards/bills/${billId}/pay`,
    payload
  )
  return data
}
