import { describe, it, expect, beforeEach } from 'vitest'
import { clearStubbedNuxtStateKey } from '../../../vitest.setup'
import { useMonthSelector } from '~/composables/useMonthSelector'

describe('useMonthSelector', () => {
  beforeEach(() => {
    clearStubbedNuxtStateKey('dashboard-month-key')
  })

  it('goToNextMonth a partir de dezembro vai para janeiro do ano seguinte', () => {
    const { selectedDate, goToNextMonth } = useMonthSelector()
    selectedDate.value = new Date(2024, 11, 15) // 15 dez 2024

    goToNextMonth()

    expect(selectedDate.value.getMonth()).toBe(0) // janeiro
    expect(selectedDate.value.getFullYear()).toBe(2025)
  })

  it('goToPrevMonth a partir de janeiro vai para dezembro do ano anterior', () => {
    const { selectedDate, goToPrevMonth } = useMonthSelector()
    selectedDate.value = new Date(2024, 0, 15) // 15 jan 2024

    goToPrevMonth()

    expect(selectedDate.value.getMonth()).toBe(11) // dezembro
    expect(selectedDate.value.getFullYear()).toBe(2023)
  })

  it('selectMonth atualiza selectedDate', () => {
    const { selectedDate, selectMonth } = useMonthSelector()
    const targetDate = new Date(2024, 5, 1) // jun 2024

    selectMonth(targetDate)

    expect(selectedDate.value.getMonth()).toBe(5)
    expect(selectedDate.value.getFullYear()).toBe(2024)
  })

  it('displayedMonths contém 3 meses centrados no mês atual', () => {
    const { selectedDate, displayedMonths } = useMonthSelector()
    selectedDate.value = new Date(2024, 5, 15) // jun 2024

    const months = displayedMonths.value
    expect(months).toHaveLength(3)
    expect(months[0].date.getMonth()).toBe(4) // mai
    expect(months[1].date.getMonth()).toBe(5) // jun (atual)
    expect(months[2].date.getMonth()).toBe(6) // jul
    expect(months[1].isCurrent).toBe(true)
    expect(months[0].isCurrent).toBe(false)
    expect(months[2].isCurrent).toBe(false)
  })
})
