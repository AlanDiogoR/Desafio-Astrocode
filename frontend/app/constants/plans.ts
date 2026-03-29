/** Preços alinhados ao backend (checkout valida o valor). */
export const Plans = {
  PRO_MONTHLY: {
    id: 'PRO_MONTHLY',
    name: 'Grivy Pro — Mensal',
    price: 19.9,
    description: 'Acesso completo por 1 mês',
    billingCycle: 'mês',
  },
  PRO_SEMIANNUAL: {
    id: 'PRO_SEMIANNUAL',
    name: 'Grivy Pro — Semestral',
    price: 49.9,
    description: 'Acesso completo por 6 meses',
    billingCycle: 'semestre',
  },
  PRO_ANNUAL: {
    id: 'PRO_ANNUAL',
    name: 'Grivy Pro — Anual',
    price: 179.9,
    description: 'Acesso completo por 12 meses',
    billingCycle: 'ano',
    savings: 'Economia vs. mensal',
  },
} as const

export type MpPlanId = keyof typeof Plans
