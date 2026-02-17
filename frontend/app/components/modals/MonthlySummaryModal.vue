<script setup lang="ts">
import {
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogRoot,
  DialogTitle,
} from 'radix-vue'
import { Cross2Icon } from '@radix-icons/vue'
import { useQuery } from '@tanstack/vue-query'
import ExpenseChart from '~/components/analytics/ExpenseChart.vue'
import { getMonthlySummary } from '~/services/transactions'
import { formatCurrency } from '~/utils/format'
import { MONTH_NAMES } from '~/constants/transactions'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()

const selectedDate = ref(new Date())
const year = computed(() => selectedDate.value.getFullYear())
const month = computed(() => selectedDate.value.getMonth() + 1)

const { data: summary, isPending } = useQuery({
  queryKey: ['monthly-summary-modal', year, month],
  queryFn: () => getMonthlySummary(year.value, month.value),
  enabled: computed(() => props.open && !!year.value && !!month.value),
})

const totalExpense = computed(() => summary.value?.totalExpense ?? 0)
const byCategory = computed(() => summary.value?.byCategory ?? [])

const topCategories = computed(() => byCategory.value.slice(0, 3))

const CHART_COLORS = [
  '#12B886',
  '#4C6EF5',
  '#FD7E14',
]

function getCategoryColor(index: number) {
  return CHART_COLORS[index] ?? '#868e96'
}

function getPercentage(value: number) {
  const total = totalExpense.value || 1
  return Math.round((value / total) * 100)
}

function goPrevMonth() {
  const d = new Date(selectedDate.value)
  d.setMonth(d.getMonth() - 1)
  selectedDate.value = d
}

function goNextMonth() {
  const d = new Date(selectedDate.value)
  d.setMonth(d.getMonth() + 1)
  selectedDate.value = d
}

const monthLabel = computed(
  () => `${MONTH_NAMES[month.value - 1]} ${year.value}`,
)

function close() {
  emit('update:open', false)
}

watch(() => props.open, (open) => {
  if (open) selectedDate.value = new Date()
})
</script>

<template>
  <DialogRoot
    :open="open"
    :modal="true"
    @update:open="(v: boolean) => emit('update:open', v as boolean)"
  >
    <DialogPortal>
      <DialogOverlay class="summary-modal-overlay" />
      <DialogContent
        class="summary-modal-content"
        :aria-describedby="undefined"
        @pointer-down-outside="close"
      >
        <header class="summary-modal-header">
          <button
            type="button"
            class="summary-modal-close"
            aria-label="Fechar"
            @click="close"
          >
            <Cross2Icon width="24" height="24" />
          </button>
          <DialogTitle class="summary-modal-title">
            Resumo de Gastos
          </DialogTitle>
          <div class="summary-modal-spacer" />
        </header>

        <div class="summary-modal-month-nav">
          <button
            type="button"
            class="summary-modal-nav-btn"
            aria-label="Mês anterior"
            @click="goPrevMonth"
          >
            <svg width="20" height="20" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M8.5 4.5L5.5 7.5L8.5 10.5" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <span class="summary-modal-month-label">{{ monthLabel }}</span>
          <button
            type="button"
            class="summary-modal-nav-btn"
            aria-label="Próximo mês"
            @click="goNextMonth"
          >
            <svg width="20" height="20" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M6.5 4.5L9.5 7.5L6.5 10.5" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <div id="summary-desc" class="summary-modal-body">
          <div v-if="isPending" class="summary-modal-loading">
            <v-progress-circular
              indeterminate
              color="primary"
              size="48"
              class="summary-modal-loading__spinner"
            />
            <div class="summary-modal-loading-skeleton">
              <v-skeleton-loader type="image" height="120" class="mb-4" />
              <v-skeleton-loader type="list-item-two-line" class="mb-3" />
              <v-skeleton-loader type="list-item-two-line" class="mb-3" />
              <v-skeleton-loader type="list-item-two-line" />
            </div>
          </div>
          <template v-else>
            <ExpenseChart
              :by-category="byCategory"
              :total-expense="totalExpense"
            />
            <div v-if="topCategories.length > 0" class="summary-modal-list">
              <div
                v-for="(cat, i) in topCategories"
                :key="cat.categoryId"
                class="summary-modal-item"
              >
                <div class="summary-modal-item__head">
                  <span
                    class="summary-modal-item__dot"
                    :style="{ backgroundColor: getCategoryColor(i) }"
                  />
                  <span class="summary-modal-item__name">{{ cat.categoryName }}</span>
                  <span class="summary-modal-item__value">{{ formatCurrency(cat.totalAmount) }}</span>
                </div>
                <div class="summary-modal-item__bar-bg">
                  <div
                    class="summary-modal-item__bar-fill"
                    :style="{
                      width: `${getPercentage(cat.totalAmount)}%`,
                      backgroundColor: getCategoryColor(i),
                    }"
                  />
                </div>
              </div>
            </div>
            <div v-else class="summary-modal-empty">
              <p class="summary-modal-empty__text">Nenhum gasto neste mês.</p>
            </div>
          </template>
        </div>
      </DialogContent>
    </DialogPortal>
  </DialogRoot>
</template>

<style scoped>
.summary-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(2px);
  z-index: 2000;
}

.summary-modal-content {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: calc(100% - 32px);
  max-width: 420px;
  max-height: 90vh;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 11px 20px 0 rgba(0, 0, 0, 0.1);
  z-index: 2001;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
}

.summary-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.summary-modal-close {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  color: #1f2937;
  border-radius: 8px;
}

.summary-modal-close:hover {
  background-color: #f3f4f6;
}

.summary-modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.summary-modal-spacer {
  width: 40px;
}

.summary-modal-month-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  flex-shrink: 0;
}

.summary-modal-nav-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: #f1f3f5;
  border-radius: 8px;
  color: #495057;
  cursor: pointer;
}

.summary-modal-nav-btn:hover {
  background: #e9ecef;
}

.summary-modal-month-label {
  font-size: 15px;
  font-weight: 600;
  color: #212529;
  min-width: 100px;
  text-align: center;
}

.summary-modal-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.summary-modal-loading {
  position: relative;
  min-height: 200px;
}

.summary-modal-loading__spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  opacity: 0.95;
}

.summary-modal-loading-skeleton {
  opacity: 0.5;
  pointer-events: none;
}

.summary-modal-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-modal-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.summary-modal-item__head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-modal-item__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.summary-modal-item__name {
  flex: 1;
  font-size: 14px;
  color: #212529;
}

.summary-modal-item__value {
  font-size: 14px;
  font-weight: 600;
  color: #212529;
}

.summary-modal-item__bar-bg {
  height: 6px;
  background: #f1f3f5;
  border-radius: 4px;
  overflow: hidden;
}

.summary-modal-item__bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.summary-modal-empty {
  text-align: center;
  padding: 32px 16px;
}

.summary-modal-empty__text {
  font-size: 14px;
  color: #868e96;
}

@media (max-width: 599px) {
  .summary-modal-content {
    width: calc(100% - 24px);
    max-height: 85vh;
  }
}
</style>
