<script setup lang="ts">
import TransactionListHeader from '~/components/transactions/TransactionListHeader.vue'
import MonthSelector from '~/components/transactions/MonthSelector.vue'
import TransactionCard from '~/components/transactions/TransactionCard.vue'
import TransactionEmptyState from '~/components/transactions/TransactionEmptyState.vue'
import TransactionsFab from '~/components/transactions/TransactionsFab.vue'
import SpendingAlert from '~/components/transactions/SpendingAlert.vue'
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

const { selectedDate, displayedMonths, goToPrevMonth, goToNextMonth, selectMonth } = useMonthSelector()

const insightYear = computed(() => selectedDate.value.getFullYear())
const insightMonth = computed(() => selectedDate.value.getMonth() + 1)
const { topCategory, shouldShowAlert } = useInsightsController(insightYear, insightMonth)
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
const { transactions, isPending } = useTransactions(filtersRef)

function handleTypeSelect(type: TransactionTypeOption) {
  selectedType.value = type
}

function handleFabAction(action: string) {
  if (action === 'new-account') openNewAccountModal()
  else if (action === 'new-income') openNewTransactionModal('INCOME')
  else if (action === 'new-expense') openNewTransactionModal('EXPENSE')
  else if (action === 'monthly-summary') openMonthlySummaryModal()
}

function applyFilters(f: TransactionFiltersState) {
  transactionFilters.value = f
}

function handleIconError(id: string) {
  iconLoadFailed.set(id, true)
}

function handleTransactionClick(transaction: (typeof transactions.value)[0]) {
  openEditTransactionModal({
    id: transaction.id,
    name: transaction.name,
    amount: transaction.amount,
    date: transaction.date,
    type: transaction.type,
    bankAccountId: transaction.bankAccountId,
    categoryId: transaction.categoryId,
    bankName: transaction.bankName,
    categoryName: transaction.categoryName,
    isRecurring: transaction.isRecurring,
  })
}
</script>

<template>
  <div class="transaction-list">
    <div class="transaction-list__header-area">
      <TransactionListHeader
        :transaction-types="TRANSACTION_TYPES"
        :selected-type="selectedType"
        @type-select="handleTypeSelect"
        @open-filters="showFilters = true"
      />
      <MonthSelector
        :displayed-months="displayedMonths"
        :on-prev="goToPrevMonth"
        :on-next="goToNextMonth"
        :on-select-month="selectMonth"
      />
    </div>
    <div class="transaction-list__scroll">
      <div v-if="isPending" class="transaction-list__skeleton">
        <div class="transaction-list__skeleton-spinner">
          <v-progress-circular
            indeterminate
            color="primary"
            size="48"
          />
        </div>
        <v-skeleton-loader type="list-item-avatar-three-line" class="mb-3" />
        <v-skeleton-loader type="list-item-avatar-three-line" class="mb-3" />
        <v-skeleton-loader type="list-item-avatar-three-line" />
      </div>
      <div
        v-else-if="transactions.length > 0"
        class="transaction-list__cards"
      >
        <SpendingAlert
          v-if="topCategory"
          :category-name="topCategory.categoryName"
          :percentage="topCategory.percentage"
          :visible="shouldShowAlert"
        />
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
      <TransactionEmptyState v-else />
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
  display: flex;
  flex-direction: column;
  border-radius: 16px;
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
  display: flex;
  flex-direction: column;
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

.transaction-list__cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-list__skeleton {
  padding: 8px 0;
  position: relative;
}

.transaction-list__skeleton-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  opacity: 0.95;
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
