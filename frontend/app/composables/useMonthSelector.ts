import { computed, ref, watch } from 'vue'
import { MONTH_NAMES } from '~/constants/transactions'

const DASHBOARD_MONTH_KEY = 'dashboard-month-key'

function parseMonthKey(key: string): Date {
  const [y, m] = key.split('-').map(Number)
  if (Number.isFinite(y) && Number.isFinite(m)) {
    return new Date(y, m - 1, 1)
  }
  return new Date()
}

function toMonthKey(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

export interface DisplayedMonth {
  date: Date
  label: string
  isCurrent: boolean
}

export function useMonthSelector() {
  const persistedMonthKey = useState<string>(DASHBOARD_MONTH_KEY, () => toMonthKey(new Date()))
  const selectedDate = ref(parseMonthKey(persistedMonthKey.value))

  watch(
    selectedDate,
    (d) => {
      persistedMonthKey.value = toMonthKey(d)
    },
    { deep: true },
  )

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

  const formattedMonth = computed(() =>
    selectedDate.value.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }),
  )

  const currentMonthKey = computed(() => {
    const d = selectedDate.value
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  })

  const lastTwelveMonths = computed(() => {
    const months: Array<{ label: string; value: string; year: number; month: number }> = []
    const now = new Date()
    for (let i = 0; i < 12; i++) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
      months.push({
        label: d.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }),
        value: `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`,
        year: d.getFullYear(),
        month: d.getMonth() + 1,
      })
    }
    return months
  })

  const isCurrentCalendarMonth = computed(() => {
    const d = selectedDate.value
    const now = new Date()
    return d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth()
  })

  function selectMonthByKey(value: string) {
    const [y, m] = value.split('-').map(Number)
    if (Number.isFinite(y) && Number.isFinite(m)) {
      selectedDate.value = new Date(y, m - 1, 1)
    }
  }

  return {
    selectedDate,
    displayedMonths,
    goToPrevMonth,
    goToNextMonth,
    selectMonth,
    formattedMonth,
    currentMonthKey,
    lastTwelveMonths,
    isCurrentCalendarMonth,
    selectMonthByKey,
  }
}
