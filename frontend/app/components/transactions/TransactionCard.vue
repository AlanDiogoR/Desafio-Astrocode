<script setup lang="ts">
import { UpdateIcon } from '@radix-icons/vue'
import { formatCurrency, formatDate } from '~/utils/format'
import { getBankIconPath, getTransactionCategoryIconPath } from '~/utils/transactionIcons'

interface TransactionItem {
  id: string
  name: string
  amount: number
  date: string
  type: 'income' | 'expense'
  bankAccountId: string
  categoryId: string
  bankName: string
  categoryName: string
  isRecurring?: boolean
}

const props = defineProps<{
  transaction: TransactionItem
  showPrivacy: boolean
  iconLoadFailed: Map<string, boolean>
}>()

const emit = defineEmits<{
  click: [transaction: TransactionItem]
  iconError: [id: string]
}>()

function handleIconError() {
  emit('iconError', props.transaction.id)
}

function getTransactionIcon() {
  const catPath = getTransactionCategoryIconPath(props.transaction.categoryName, props.transaction.type)
  if (props.transaction.categoryName) return catPath
  return getBankIconPath(props.transaction.bankName, props.transaction.type)
}

const formattedDate = computed(() => {
  const d = props.transaction.date
  if (!d) return ''
  try {
    return formatDate(new Date(d), 'd MMM yyyy')
  } catch {
    return d
  }
})
</script>

<template>
  <v-card
    class="transaction-card d-flex align-center ga-4 pa-4"
    variant="flat"
    elevation="0"
    role="button"
    tabindex="0"
    @click="emit('click', transaction as any)"
    @keydown.enter="emit('click', transaction as any)"
    @keydown.space.prevent="emit('click', transaction as any)"
  >
      <v-avatar
        size="56"
        variant="flat"
        rounded="circle"
        color="transparent"
        class="transaction-avatar"
      >
        <template v-if="iconLoadFailed.get(transaction.id)">
          <v-icon
            icon="mdi-cash"
            :color="transaction.type === 'income' ? 'green-700' : 'red-700'"
            size="40"
          />
        </template>
        <img
          v-else
          :src="getTransactionIcon()"
          alt=""
          class="transaction-avatar__img"
          @error="handleIconError"
        >
      </v-avatar>
      <div class="flex-grow-1 d-flex flex-column">
        <div class="d-flex align-center ga-1">
          <span class="transaction-title font-weight-bold">{{ transaction.name }}</span>
          <UpdateIcon
            v-if="transaction.isRecurring"
            class="transaction-recurring-icon"
            aria-label="Transação recorrente"
          />
        </div>
        <span class="transaction-subtitle text-caption">{{ formattedDate }}</span>
      </div>
      <span
        :class="[
          'transaction-amount font-weight-medium',
          transaction.type === 'income' ? 'amount-income' : 'amount-expense',
        ]"
      >
        {{ showPrivacy ? (transaction.type === 'income' ? '+' : '-') + formatCurrency(transaction.amount) : '••••' }}
      </span>
  </v-card>
</template>

<style scoped>
.transaction-card {
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
  border-radius: 16px;
  cursor: pointer;
}

.transaction-card:hover {
  background-color: #f9fafb;
}

.transaction-avatar {
  flex-shrink: 0;
}

.transaction-avatar__img {
  width: 52px;
  height: 52px;
  object-fit: contain;
  display: block;
}

.transaction-title {
  font-size: 15px;
  color: #212529;
}

.transaction-subtitle {
  font-size: 12px;
  color: #868e96;
}

.transaction-recurring-icon {
  width: 14px;
  height: 14px;
  color: #868e96;
  flex-shrink: 0;
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
</style>
