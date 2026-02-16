<script setup lang="ts">
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  type ChartOptions,
} from 'chart.js'
import { Doughnut } from 'vue-chartjs'
import type { CategoryExpenseItem } from '~/services/transactions'
import { formatCurrency } from '~/utils/format'

ChartJS.register(ArcElement, Tooltip, Legend)

const CHART_COLORS = [
  '#12B886',
  '#4C6EF5',
  '#FD7E14',
  '#BE4BDB',
  '#40C057',
  '#FAB005',
  '#15AABF',
  '#FA5252',
]

const props = defineProps<{
  byCategory: CategoryExpenseItem[]
  totalExpense: number
}>()

const chartData = computed(() => {
  const labels = props.byCategory.map((c) => c.categoryName)
  const data = props.byCategory.map((c) => c.totalAmount)
  const colors = CHART_COLORS.slice(0, props.byCategory.length)
  return {
    labels,
    datasets: [
      {
        data,
        backgroundColor: colors,
        borderColor: '#fff',
        borderWidth: 2,
        hoverOffset: 4,
      },
    ],
  }
})

const options: ChartOptions<'doughnut'> = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '65%',
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        padding: 16,
        usePointStyle: true,
        pointStyle: 'circle',
      },
    },
    tooltip: {
      callbacks: {
        label: (ctx) => {
          const value = ctx.raw as number
          const total = props.totalExpense || 1
          const pct = ((value / total) * 100).toFixed(1)
          return `${ctx.label}: ${formatCurrency(value)} (${pct}%)`
        },
      },
    },
  },
}
</script>

<template>
  <div class="expense-chart">
    <div class="expense-chart__canvas-wrapper">
      <Doughnut
        :data="chartData"
        :options="options"
        class="expense-chart__canvas"
      />
      <div class="expense-chart__center">
        <span class="expense-chart__total">{{ formatCurrency(totalExpense) }}</span>
        <span class="expense-chart__label">Total de gastos</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.expense-chart {
  width: 100%;
}

.expense-chart__canvas-wrapper {
  position: relative;
  width: 100%;
  min-height: 220px;
  max-height: 280px;
}

.expense-chart__canvas {
  max-height: 280px;
}

.expense-chart__center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.expense-chart__total {
  font-size: 18px;
  font-weight: 700;
  color: #212529;
}

.expense-chart__label {
  font-size: 12px;
  color: #868e96;
  margin-top: 2px;
}

@media (min-width: 600px) {
  .expense-chart__canvas-wrapper {
    min-height: 260px;
    max-height: 320px;
  }

  .expense-chart__center {
    .expense-chart__total {
      font-size: 20px;
    }
  }
}
</style>
