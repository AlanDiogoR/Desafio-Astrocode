<script setup lang="ts">
import TransactionListHeader from '~/components/transactions/TransactionListHeader.vue'
import MonthSelector from '~/components/transactions/MonthSelector.vue'
import TransactionCard from '~/components/transactions/TransactionCard.vue'
import TransactionEmptyState from '~/components/transactions/TransactionEmptyState.vue'
import TransactionsFab from '~/components/transactions/TransactionsFab.vue'
import MonthlyInsightBanner from '~/components/transactions/MonthlyInsightBanner.vue'
import TransactionFiltersModal from '~/components/transactions/TransactionFiltersModal.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import NewTransactionModal from '~/components/modals/NewTransactionModal.vue'
import EditTransactionModal from '~/components/modals/EditTransactionModal.vue'
import MonthlySummaryModal from '~/components/modals/MonthlySummaryModal.vue'
import { TRANSACTION_TYPES, type TransactionTypeOption } from '~/constants/transactions'
import type { TransactionFiltersState } from '~/composables/useTransactions'
import { useMonthSelector } from '~/composables/useMonthSelector'

defineProps<{
  showPrivacy: boolean
}>()

const {
  openNewTransactionModal,
  openNewAccountModal,
  openEditTransactionModal,
  openMonthlySummaryModal,
  closeMonthlySummaryModal,
  isMonthlySummaryModalOpen,
  transactionFilters,
} = useDashboard()

const {
  selectedDate,
  goToPrevMonth,
  goToNextMonth,
  formattedMonth,
  currentMonthKey,
  lastTwelveMonths,
  isCurrentCalendarMonth,
  selectMonthByKey,
} = useMonthSelector()

const insightYear = computed(() => selectedDate.value.getFullYear())
const insightMonth = computed(() => selectedDate.value.getMonth() + 1)
const { dominantExpenseShare } = useInsightsController(insightYear, insightMonth)
const showMonthlyInsight = computed(
  () => dominantExpenseShare.value != null && dominantExpenseShare.value.percentage >= 35,
)
const selectedType = ref<TransactionTypeOption>(TRANSACTION_TYPES[0])
const showFilters = ref(false)
const iconLoadFailed = reactive(new Map<string, boolean>())

const mergedFilters = computed<TransactionFiltersState>(() => ({
  type: selectedType.value.value === 'all' ? undefined : (selectedType.value.value === 'income' ? 'INCOME' : 'EXPENSE'),
  month: selectedDate.value.getMonth() + 1,
  year: selectedDate.value.getFullYear(),
  ...transactionFilters.value,
}))

const filtersRef = computed(() => mergedFilters.value)
const {
  transactions,
  isPending,
  isError,
  refetch,
  totalPages,
  currentPage,
  setPage,
} = useTransactions(filtersRef)

const displayPage = computed({
  get: () => currentPage.value + 1,
  set: (v) => setPage(v - 1),
})

function handleTypeSelect(type: TransactionTypeOption) {
  selectedType.value = type
}

function handleFabAction(action: string) {
  if (action === 'new-account') openNewAccountModal()
  else if (action === 'new-income') openNewTransactionModal('INCOME')
  else if (action === 'new-expense') openNewTransactionModal('EXPENSE')
}

function applyFilters(f: TransactionFiltersState) {
  transactionFilters.value = f
}

function handleIconError(id: string) {
  iconLoadFailed.set(id, true)
}

const hasListFilters = computed(
  () => selectedType.value.value !== 'all' || transactionFilters.value?.bankAccountId != null,
)

const quickFilters = [
  { value: 'ALL' as const, label: 'Todas', icon: 'mdi-format-list-bulleted', type: TRANSACTION_TYPES[0] },
  { value: 'INCOME' as const, label: 'Receitas', icon: 'mdi-trending-up', type: TRANSACTION_TYPES[1] },
  { value: 'EXPENSE' as const, label: 'Despesas', icon: 'mdi-trending-down', type: TRANSACTION_TYPES[2] },
]

const activeQuickFilter = computed(() => {
  const v = selectedType.value.value
  if (v === 'all') return 'ALL'
  if (v === 'income') return 'INCOME'
  return 'EXPENSE'
})

function setQuickFilter(entry: (typeof quickFilters)[number]) {
  handleTypeSelect(entry.type)
}

function clearTransactionFilters() {
  selectedType.value = TRANSACTION_TYPES[0]
  transactionFilters.value = undefined
}

function handleTransactionClick(transaction: (typeof transactions.value)[0]) {
  openEditTransactionModal({
    id: transaction.id,
    name: transaction.name,
    amount: transaction.amount,
    date: transaction.date,
    type: transaction.type,
    bankAccountId: transaction.bankAccountId,
    creditCardId: transaction.creditCardId,
    categoryId: transaction.categoryId,
    bankName: transaction.bankName,
    creditCardName: transaction.creditCardName,
    categoryName: transaction.categoryName,
    isRecurring: transaction.isRecurring,
  })
}
</script>

<template>
  <div class="transaction-list d-flex flex-column rounded-xl">
    <div class="transaction-list__header-area">
      <TransactionListHeader
        :transaction-types="TRANSACTION_TYPES"
        :selected-type="selectedType"
        @type-select="handleTypeSelect"
        @open-filters="showFilters = true"
        @open-monthly-summary="openMonthlySummaryModal"
      />
      <MonthSelector
        :formatted-month="formattedMonth"
        :last-twelve-months="lastTwelveMonths"
        :current-month-key="currentMonthKey"
        :is-next-disabled="isCurrentCalendarMonth"
        :on-prev="goToPrevMonth"
        :on-next="goToNextMonth"
        :on-select-month-key="selectMonthByKey"
      />
    </div>
    <div class="transaction-list__scroll d-flex flex-column">
      <MonthlyInsightBanner
        v-if="!isPending && showMonthlyInsight && dominantExpenseShare"
        :category-name="dominantExpenseShare.categoryName"
        :percentage="dominantExpenseShare.percentage"
        class="px-1"
      />
      <div class="d-flex gap-2 flex-wrap mb-3 px-1">
        <v-chip
          v-for="filter in quickFilters"
          :key="filter.value"
          :color="activeQuickFilter === filter.value ? 'primary' : undefined"
          :variant="activeQuickFilter === filter.value ? 'flat' : 'tonal'"
          size="small"
          @click="setQuickFilter(filter)"
        >
          <v-icon start size="14">
            {{ filter.icon }}
          </v-icon>
          {{ filter.label }}
        </v-chip>
      </div>
      <div v-if="isPending" class="transaction-list__skeleton">
        <v-skeleton-loader type="card" rounded="xl" class="mb-3" />
        <v-skeleton-loader type="list-item-avatar-three-line" class="mb-3" />
        <v-skeleton-loader type="list-item-avatar-three-line" class="mb-3" />
        <v-skeleton-loader type="list-item-avatar-three-line" />
      </div>
      <div
        v-else-if="isError"
        class="transaction-list__error d-flex flex-column align-center justify-center flex-grow-1"
        style="gap: 16px; min-height: 200px;"
      >
        <v-icon icon="mdi-alert-circle-outline" size="48" color="grey" />
        <p class="text-body-1 text-medium-emphasis text-center">
          Erro ao carregar transações
        </p>
        <v-btn variant="tonal" color="primary" size="small" @click="refetch()">
          Tentar novamente
        </v-btn>
      </div>
      <div
        v-else-if="transactions.length > 0"
        class="transaction-list__cards d-flex flex-column ga-3"
      >
        <TransactionCard
          v-for="transaction in transactions"
          :key="transaction.id"
          :transaction="transaction"
          :show-privacy="showPrivacy"
          :icon-load-failed="iconLoadFailed"
          @click="handleTransactionClick"
          @icon-error="handleIconError"
        />
      </div>
      <TransactionEmptyState
        v-else
        :has-filters="hasListFilters"
        @add-transaction="openNewTransactionModal('EXPENSE')"
        @clear-filters="clearTransactionFilters"
      />
      <v-pagination
        v-if="totalPages > 1"
        v-model="displayPage"
        :length="totalPages"
        :total-visible="5"
        density="compact"
        class="mt-4"
      />
    </div>
    <TransactionsFab @action="handleFabAction" />
    <TransactionFiltersModal
      v-model="showFilters"
      :filters="transactionFilters ?? { year: selectedDate.getFullYear() }"
      @apply="applyFilters"
    />
    <NewAccountModal />
    <NewTransactionModal />
    <EditTransactionModal />
    <MonthlySummaryModal
      :open="isMonthlySummaryModalOpen"
      @update:open="(v) => !v && closeMonthlySummaryModal()"
    />
  </div>
</template>

<style scoped>
.transaction-list {
  position: relative;
  width: 100%;
  background-color: #f1f3f5;
  box-sizing: border-box;
  min-height: 400px;
}

@media (min-width: 960px) {
  .transaction-list {
    height: 100%;
    min-height: 0;
    overflow: hidden;
  }
}

.transaction-list__header-area {
  flex-shrink: 0;
  padding: 24px 32px 0;
}

.transaction-list__scroll {
  flex: 1;
  overflow-x: hidden;
  padding: 16px 32px 100px;
}

@media (min-width: 960px) {
  .transaction-list__scroll {
    overflow-y: auto;
    min-height: 0;
  }
}

@media (max-width: 959px) {
  .transaction-list__scroll {
    overflow-y: visible;
  }
}

.transaction-list__skeleton {
  padding: 8px 0;
  position: relative;
}

@media (max-width: 959px) {
  .transaction-list__header-area {
    padding: 24px 16px 0;
  }

  .transaction-list__scroll {
    padding: 16px 16px 120px;
  }
}
</style>
