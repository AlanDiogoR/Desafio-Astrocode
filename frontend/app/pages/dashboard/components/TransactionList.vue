<script setup lang="ts">
import { formatCurrency } from '~/utils/format'

interface Transaction {
  id: number
  title: string
  category: string
  date: string
  amount: number
  type: 'income' | 'expense'
}

const selectedDate = ref(new Date())

const monthNames = [
  'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
  'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez',
]

const displayedMonths = computed(() => {
  const current = new Date(selectedDate.value.getFullYear(), selectedDate.value.getMonth(), 1)
  const prev = new Date(current)
  prev.setMonth(prev.getMonth() - 1)
  const next = new Date(current)
  next.setMonth(next.getMonth() + 1)
  return [
    { date: prev, label: monthNames[prev.getMonth()], isCurrent: false },
    { date: current, label: monthNames[current.getMonth()], isCurrent: true },
    { date: next, label: monthNames[next.getMonth()], isCurrent: false },
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

const CATEGORY_ICONS: Record<string, string> = {
  Salário: 'mdi-cash-multiple',
  Almoço: 'mdi-silverware-fork-knife',
  Mercado: 'mdi-cart-outline',
  Transporte: 'mdi-bus',
  Freelance: 'mdi-briefcase-outline',
  Streaming: 'mdi-television',
  Receita: 'mdi-cash-plus',
  Alimentação: 'mdi-silverware-fork-knife',
  Lazer: 'mdi-party-popper',
  Investimento: 'mdi-chart-line',
  Casa: 'mdi-home-outline',
  Educação: 'mdi-school-outline',
  Saúde: 'mdi-medical-bag',
  Roupas: 'mdi-hanger',
  Viagem: 'mdi-airplane',
  Contas: 'mdi-receipt',
}

const DEFAULT_ICON = 'mdi-tag-outline'

const transactionsIconSrc = '/images/' + encodeURIComponent('Nome=Transações.png')

function getCategoryIcon(transaction: Transaction): string {
  return CATEGORY_ICONS[transaction.title] ?? CATEGORY_ICONS[transaction.category] ?? DEFAULT_ICON
}

const transactions = ref<Transaction[]>([
  { id: 1, title: 'Salário', category: 'Receita', date: '05/02/2025', amount: 5000, type: 'income' },
  { id: 2, title: 'Almoço', category: 'Alimentação', date: '04/02/2025', amount: 25, type: 'expense' },
  { id: 3, title: 'Mercado', category: 'Mercado', date: '03/02/2025', amount: 320, type: 'expense' },
  { id: 4, title: 'Transporte', category: 'Transporte', date: '02/02/2025', amount: 18.5, type: 'expense' },
  { id: 5, title: 'Freelance', category: 'Receita', date: '01/02/2025', amount: 1200, type: 'income' },
  { id: 6, title: 'Streaming', category: 'Lazer', date: '28/01/2025', amount: 49.9, type: 'expense' },
])
</script>

<template>
  <div class="transaction-list">
    <div class="transaction-list__header-area">
      <div class="transaction-list__header d-flex align-center justify-space-between mb-3">
        <button
          type="button"
          class="transactions-dropdown-trigger d-flex align-center gap-2"
          @click="() => {}"
        >
          <img
            :src="transactionsIconSrc"
            alt="Transações"
            class="transactions-dropdown-icon"
          >
          <span class="section-title text-h6 font-weight-medium">Transações</span>
          <v-icon icon="mdi-chevron-down" size="20" class="transactions-dropdown-chevron" />
        </button>
        <v-btn
          variant="text"
          icon
          size="small"
          class="section-filter"
        >
          <v-icon icon="mdi-filter-outline" size="22" class="filter-icon" />
        </v-btn>
      </div>
      <div class="month-selector d-flex align-center gap-2">
        <v-btn
          icon
          variant="text"
          size="x-small"
          class="month-nav"
          @click="goToPrevMonth"
        >
          <v-icon icon="mdi-chevron-left" size="20" />
        </v-btn>
        <div class="month-selector__months d-flex gap-2">
          <button
            v-for="(month, index) in displayedMonths"
            :key="index"
            type="button"
            class="month-btn"
            :class="{ 'month-btn--current': month.isCurrent }"
            @click="selectMonth(month.date)"
          >
            {{ month.label }}
          </button>
        </div>
        <v-btn
          icon
          variant="text"
          size="x-small"
          class="month-nav"
          @click="goToNextMonth"
        >
          <v-icon icon="mdi-chevron-right" size="20" />
        </v-btn>
      </div>
    </div>
    <div class="transaction-list__scroll">
      <div class="transaction-list__cards">
        <v-card
          v-for="transaction in transactions"
          :key="transaction.id"
          class="transaction-card"
          rounded="lg"
          variant="flat"
          elevation="0"
        >
          <div class="transaction-card__content d-flex align-center gap-4">
            <v-avatar
              size="48"
              variant="flat"
              rounded="circle"
              :color="transaction.type === 'income' ? 'green-100' : 'red-100'"
              class="transaction-avatar"
            >
              <v-icon
                :icon="getCategoryIcon(transaction)"
                :color="transaction.type === 'income' ? 'green-700' : 'red-700'"
                size="24"
              />
            </v-avatar>
            <div class="transaction-content flex-grow-1 d-flex flex-column">
              <span class="transaction-title font-weight-bold">{{ transaction.title }}</span>
              <span class="transaction-subtitle text-caption">{{ transaction.date }}</span>
            </div>
            <span
              :class="[
                'transaction-amount font-weight-medium',
                transaction.type === 'income' ? 'amount-income' : 'amount-expense',
              ]"
            >
              {{ transaction.type === 'income' ? '+' : '-' }}{{ formatCurrency(transaction.amount) }}
            </span>
          </div>
        </v-card>
      </div>
    </div>
    <v-btn
      icon="mdi-plus"
      color="primary"
      rounded="circle"
      elevation="4"
      class="transaction-fab"
    />
  </div>
</template>

<style scoped>
.transaction-list {
  position: relative;
  height: 100%;
  width: 100%;
  background-color: #f1f3f5;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
  border-radius: 16px;
}

.transaction-list__header-area {
  flex-shrink: 0;
  padding: 24px 32px 0;
}

.transaction-list__scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px 32px 100px;
  min-height: 0;
}

.transactions-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 8px;
}

.transactions-dropdown-trigger:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.transactions-dropdown-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
  filter: brightness(0.3);
}

.transactions-dropdown-chevron {
  color: #495057;
}

.section-title {
  color: #212529;
}

.transaction-fab {
  position: absolute;
  bottom: 16px;
  right: 16px;
  width: 48px;
  height: 48px;
  min-width: 48px;
  z-index: 10;
}

.section-filter {
  min-width: 0;
  color: #495057;
}

.filter-icon {
  color: #495057;
}

.month-selector {
  padding: 12px 0 16px;
}

.month-nav {
  color: #868e96;
}

.month-selector__months {
  flex: 1;
  justify-content: center;
}

.month-btn {
  padding: 8px 16px;
  border: none;
  background: none;
  font-size: 14px;
  font-weight: 500;
  color: #868e96;
  cursor: pointer;
  border-radius: 8px;
  transition: color 0.2s, font-weight 0.2s;
}

.month-btn:hover {
  color: #495057;
}

.month-btn--current {
  font-weight: 700;
  color: #087f5b;
}

.transaction-list__cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-card {
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
}

.transaction-card__content {
  padding: 16px 20px;
  gap: 16px;
}

.transaction-avatar {
  flex-shrink: 0;
}

.transaction-title {
  font-size: 15px;
  color: #212529;
}

.transaction-subtitle {
  font-size: 12px;
  color: #868e96;
}

.transaction-amount {
  font-size: 15px;
  text-align: right;
  white-space: nowrap;
  flex-shrink: 0;
}

.amount-income {
  color: #12b886;
}

.amount-expense {
  color: #ff6b6b;
}

@media (max-width: 959px) {
  .transaction-list {
    height: auto;
    overflow: visible;
  }

  .transaction-list__header-area {
    padding: 24px 16px 0;
  }

  .transaction-list__scroll {
    overflow: visible;
    padding: 16px 16px 100px;
  }
}
</style>
