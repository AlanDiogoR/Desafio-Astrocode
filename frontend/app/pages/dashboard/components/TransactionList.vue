<script setup lang="ts">
import { formatCurrency } from '~/utils/format'
import { PlusIcon } from '@radix-icons/vue'
import AppDropdown from '~/components/ui/AppDropdown.vue'
import TransactionFiltersModal from '~/components/transactions/TransactionFiltersModal.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import NewTransactionModal from '~/components/modals/NewTransactionModal.vue'
import { DropdownMenuItem } from 'radix-vue'

interface TransactionType {
  value: string
  label: string
  icon: string
  filter: string
  colorClass: 'income' | 'expense' | 'neutral'
}

interface Transaction {
  id: number
  title: string
  category: string
  date: string
  amount: number
  type: 'income' | 'expense'
}

defineProps<{
  showPrivacy: boolean
}>()

function iconPath(name: string): string {
  return `/images/${encodeURIComponent(name)}`
}

const transactionTypes: TransactionType[] = [
  {
    value: 'all',
    label: 'Transações',
    icon: iconPath('Nome=Transações.png'),
    filter: 'brightness(0) saturate(100%) invert(29%) sepia(15%) saturate(664%) hue-rotate(174deg) brightness(94%) contrast(89%)',
    colorClass: 'neutral',
  },
  {
    value: 'income',
    label: 'Receitas',
    icon: iconPath('Nome=Receitas.png'),
    filter: 'brightness(0) saturate(100%) invert(32%) sepia(96%) saturate(1836%) hue-rotate(137deg) brightness(93%) contrast(101%)',
    colorClass: 'income',
  },
  {
    value: 'expense',
    label: 'Despesas',
    icon: iconPath('Nome=Despesas.png'),
    filter: 'brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%)',
    colorClass: 'expense',
  },
]

const defaultType = transactionTypes[0]

const selectedType = ref<TransactionType>(defaultType)

const GRAY_FILTER = 'brightness(0) saturate(100%) invert(29%) sepia(15%) saturate(664%) hue-rotate(174deg) brightness(94%) contrast(89%)'
const GREEN_FILTER = 'brightness(0) saturate(100%) invert(32%) sepia(96%) saturate(1836%) hue-rotate(137deg) brightness(93%) contrast(101%)'
const RED_FILTER = 'brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%)'

interface FabOption {
  label: string
  action: string
  icon: string
  filter: string
  size?: number
}

const fabOptions: FabOption[] = [
  {
    label: 'Nova despesa',
    action: 'new-expense',
    icon: iconPath('Nome=Despesas.png'),
    filter: RED_FILTER,
    size: 28,
  },
  {
    label: 'Nova receita',
    action: 'new-income',
    icon: iconPath('Nome=Receitas.png'),
    filter: GREEN_FILTER,
    size: 28,
  },
  {
    label: 'Nova conta',
    action: 'new-account',
    icon: '/images/banco.svg',
    filter: '',
    size: 34,
  },
]

const { openNewTransactionModal, openNewAccountModal } = useDashboard()

const showFilters = ref(false)
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

function handleTypeSelect(type: TransactionType) {
  selectedType.value = type
}

function handleFabAction(action: string) {
  if (action === 'new-account') {
    openNewAccountModal()
  } else if (action === 'new-income') {
    openNewTransactionModal('INCOME')
  } else if (action === 'new-expense') {
    openNewTransactionModal('EXPENSE')
  }
}

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
        <AppDropdown content-max-width="279px">
          <template #trigger>
            <div class="transactions-dropdown-trigger d-flex align-center gap-2">
              <img
                :src="selectedType.icon"
                :alt="selectedType.label"
                class="transactions-dropdown-icon"
                :style="{ filter: selectedType.filter }"
              >
              <h3 class="section-title text-h6 font-weight-medium mb-0">
                {{ selectedType.label }}
              </h3>
              <v-icon icon="mdi-chevron-down" size="20" class="transactions-dropdown-chevron" />
            </div>
          </template>
          <template #content>
            <DropdownMenuItem
              v-for="type in transactionTypes"
              :key="type.value"
              class="dropdown-item"
              :class="{
                'dropdown-item--income': type.colorClass === 'income',
                'dropdown-item--expense': type.colorClass === 'expense',
              }"
              :text-value="type.label"
              @select="handleTypeSelect(type)"
            >
              <div class="d-flex align-center dropdown-item__row" style="gap: 8px; width: 100%">
                <img
                  :src="type.icon"
                  alt=""
                  width="20"
                  height="20"
                  :style="{ filter: type.filter, objectFit: 'contain' }"
                  class="transition-all"
                >
                <span
                  class="text-body-2"
                  :class="{
                    'text-green-darken-2': type.value === 'income',
                    'text-red-darken-2': type.value === 'expense',
                    'text-grey-darken-3': type.value === 'all',
                  }"
                >
                  {{ type.label }}
                </span>
              </div>
            </DropdownMenuItem>
          </template>
        </AppDropdown>
        <v-btn
          variant="text"
          icon
          size="small"
          class="section-filter"
          @click="showFilters = true"
        >
          <v-icon icon="mdi-filter-outline" size="22" class="filter-icon" />
        </v-btn>
      </div>
      <div class="month-selector d-flex align-center">
        <v-btn
          icon
          variant="text"
          size="x-small"
          class="month-nav"
          color="grey"
          @click="goToPrevMonth"
        >
          <v-icon icon="mdi-chevron-left" size="20" />
        </v-btn>
        <div class="month-selector__months">
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
          color="grey"
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
              {{ showPrivacy ? (transaction.type === 'income' ? '+' : '-') + formatCurrency(transaction.amount) : '••••' }}
            </span>
          </div>
        </v-card>
      </div>
    </div>
    <div class="fab-wrapper">
      <AppDropdown
        class="fab-dropdown"
        content-side="top"
        content-align="end"
      >
      <template #trigger>
        <v-btn
          color="primary"
          icon
          width="48"
          height="48"
          min-width="48"
          rounded="circle"
          elevation="4"
          class="fab-trigger d-flex align-center justify-center"
        >
          <PlusIcon width="32" height="32" class="plus-icon transition-transform text-white" />
        </v-btn>
      </template>
      <template #content>
        <DropdownMenuItem
          v-for="(opt, i) in fabOptions"
          :key="i"
          class="dropdown-item"
          :text-value="opt.label"
          @select="handleFabAction(opt.action)"
        >
          <div class="d-flex align-center w-100">
            <div
              class="d-flex align-center justify-center mr-3"
              style="width: 40px; height: 40px; flex-shrink: 0"
            >
              <img
                :src="opt.icon"
                alt=""
                :width="opt.size ?? 24"
                :height="opt.size ?? 24"
                class="transition-opacity"
                style="object-fit: contain"
                :style="opt.filter ? { filter: opt.filter } : {}"
              >
            </div>
            <span class="text-body-2 font-weight-medium">{{ opt.label }}</span>
          </div>
        </DropdownMenuItem>
      </template>
    </AppDropdown>
    </div>
    <TransactionFiltersModal v-model="showFilters" />
    <NewAccountModal />
    <NewTransactionModal />
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

.fab-wrapper {
  position: absolute !important;
  bottom: 16px !important;
  right: 16px !important;
  z-index: 100;
}

.plus-icon {
  color: white;
  transition: transform 0.3s ease;
}

.fab-trigger[data-state="open"] .plus-icon {
  transform: rotate(135deg);
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
  gap: 16px;
}

.month-nav {
  color: #868e96;
}

.month-selector__months {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
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
  transition: color 0.2s, font-weight 0.2s, background-color 0.2s, box-shadow 0.2s;
}

.month-btn:hover {
  color: #495057;
}

.month-btn--current {
  font-weight: 700;
  color: #212529;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
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
  border-radius: 16px;
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
