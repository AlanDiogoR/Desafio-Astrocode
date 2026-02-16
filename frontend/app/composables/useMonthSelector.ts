import { MONTH_NAMES } from '~/constants/transactions'

export interface DisplayedMonth {
  date: Date
  label: string
  isCurrent: boolean
}

export function useMonthSelector() {
  const selectedDate = ref(new Date())

  const displayedMonths = computed<DisplayedMonth[]>(() => {
    const current = new Date(selectedDate.value.getFullYear(), selectedDate.value.getMonth(), 1)
    const prev = new Date(current)
    prev.setMonth(prev.getMonth() - 1)
    const next = new Date(current)
    next.setMonth(next.getMonth() + 1)
    return [
      { date: prev, label: MONTH_NAMES[prev.getMonth()], isCurrent: false },
      { date: current, label: MONTH_NAMES[current.getMonth()], isCurrent: true },
      { date: next, label: MONTH_NAMES[next.getMonth()], isCurrent: false },
    ]
  })

  function goToPrevMonth() {
    const d = new Date(selectedDate.value)
    d.setMonth(d.getMonth() - 1)
    selectedDate.value = d
  }

  function goToNextMonth() {
    const d = new Date(selectedDate.value)
    d.setMonth(d.getMonth() + 1)
    selectedDate.value = d
  }

  function selectMonth(date: Date) {
    selectedDate.value = new Date(date)
  }

  return { selectedDate, displayedMonths, goToPrevMonth, goToNextMonth, selectMonth }
}
