import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useDashboardController } from '~/composables/useDashboardController'

vi.mock('~/composables/useBankAccounts', () => ({
  useBankAccounts: () => ({
    accounts: { value: [] },
    isPending: { value: false },
  }),
}))

vi.mock('~/composables/useGoals', () => ({
  useGoals: () => ({
    goals: { value: [] },
    isPending: { value: false },
  }),
}))

vi.mock('~/composables/useDashboardData', () => ({
  useDashboardData: () => ({
    totalBalance: { value: 1500 },
    isPending: { value: false },
  }),
}))

describe('useDashboardController', () => {
  beforeEach(() => {
    const state = (globalThis as any).useState?.('dashboard-areValuesVisible', () => true)
    if (state) state.value = true
  })

  it('areValuesVisible começa como true', () => {
    const { areValuesVisible } = useDashboardController()
    expect(areValuesVisible.value).toBe(true)
  })

  it('formattedTotalBalance retorna valor formatado quando visível', () => {
    const { formattedTotalBalance } = useDashboardController()
    expect(formattedTotalBalance.value).toMatch(/R\$\s*1\.500/)
  })

  it('formattedTotalBalance retorna •••••• quando areValuesVisible é false', () => {
    const { areValuesVisible, formattedTotalBalance } = useDashboardController()
    areValuesVisible.value = false
    expect(formattedTotalBalance.value).toBe('••••••')
  })
})
