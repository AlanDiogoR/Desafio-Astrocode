<script setup lang="ts">
import type { CategoryExpenseItem } from '~/services/transactions'
import { formatCurrency } from '~/utils/format'

const props = defineProps<{
  categories: CategoryExpenseItem[]
  totalExpense: number
}>()

const CARD_COLORS = ['#12B886', '#4C6EF5', '#FD7E14']

const topThree = computed(() => props.categories.slice(0, 3))

function getPercentage(amount: number): number {
  const total = props.totalExpense || 1
  return Math.round((amount / total) * 100)
}

function getColor(index: number): string {
  return CARD_COLORS[index] ?? '#868e96'
}
</script>

<template>
  <div v-if="topThree.length > 0" class="top-spending-cards">
    <div
      v-for="(cat, i) in topThree"
      :key="cat.categoryId"
      class="top-spending-card"
      :style="{ '--card-accent': getColor(i) }"
    >
      <div class="top-spending-card__header">
        <span class="top-spending-card__label">{{ cat.categoryName }}</span>
        <span class="top-spending-card__value">{{ formatCurrency(cat.totalAmount) }}</span>
      </div>
      <div class="top-spending-card__bar">
        <div
          class="top-spending-card__bar-fill"
          :style="{ width: `${getPercentage(cat.totalAmount)}%` }"
        />
      </div>
      <span class="top-spending-card__pct">{{ getPercentage(cat.totalAmount) }}% dos gastos</span>
    </div>
  </div>
</template>

<style scoped>
.top-spending-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 12px;
}

.top-spending-card {
  padding: 12px 14px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  border-left: 3px solid var(--card-accent, #12B886);
}

.top-spending-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.top-spending-card__label {
  font-size: 13px;
  font-weight: 600;
  color: #212529;
}

.top-spending-card__value {
  font-size: 13px;
  font-weight: 600;
  color: #212529;
}

.top-spending-card__bar {
  height: 4px;
  background: #f1f3f5;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.top-spending-card__bar-fill {
  height: 100%;
  background: var(--card-accent, #12B886);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.top-spending-card__pct {
  font-size: 11px;
  color: #868e96;
}
</style>
