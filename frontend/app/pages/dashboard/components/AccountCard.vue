<script setup lang="ts">
import { formatCurrency } from '~/utils/format'

type AccountType = 'checking' | 'investment' | 'cash'

interface BankAccount {
  id: string
  name: string
  balance: number
  type: AccountType
  color: string
}

defineProps<{
  account: BankAccount
}>()

function getAccountIcon(type: AccountType): string {
  switch (type) {
    case 'investment':
      return 'mdi-chart-line'
    case 'cash':
      return 'mdi-cash'
    case 'checking':
    default:
      return 'mdi-credit-card'
  }
}
</script>

<template>
  <div
    class="account-card"
    :style="{ borderBottomColor: account.color }"
  >
    <div class="account-card__icon">
      <v-icon :icon="getAccountIcon(account.type)" size="24" class="account-card__icon-svg" />
    </div>
    <p class="account-card__name">
      {{ account.name }}
    </p>
    <p class="account-card__value">
      {{ formatCurrency(account.balance) }}
    </p>
    <p class="account-card__label">
      Saldo atual
    </p>
  </div>
</template>

<style scoped>
.account-card {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  flex-shrink: 0;
  border-bottom: 4px solid transparent;
  color: #212529;
}

.account-card__icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e9ecef;
  border-radius: 50%;
  margin-bottom: 12px;
}

.account-card__icon-svg {
  color: #495057;
}

.account-card__name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #212529;
}

.account-card__value {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #212529;
}

.account-card__label {
  font-size: 12px;
  color: #868e96;
  margin: 0;
}
</style>
