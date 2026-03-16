export interface CreditCard {
  id: string
  name: string
  creditLimit: number
  closingDay: number
  dueDay: number
  color: string | null
  currentBillAmount: number
  createdAt: string
}

export interface CreditCardBill {
  id: string
  creditCardId: string
  month: number
  year: number
  totalAmount: number
  status: 'OPEN' | 'CLOSED' | 'PAID'
  dueDate: string
  closingDate: string
  paidDate: string | null
  createdAt?: string
}
