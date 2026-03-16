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

const props = defineProps<{
  account: BankAccount
  showPrivacy: boolean
}>()

const { openEditAccountModal, openConfirmDeleteModal } = useDashboard()

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
    class="account-card d-flex flex-column pa-4 rounded-xl cursor-pointer"
    :style="{ borderBottomColor: account.color }"
    @click="openEditAccountModal(account)"
  >
    <v-btn
      icon
      variant="text"
      density="compact"
      size="small"
      class="account-card__edit-btn"
      aria-label="Editar conta"
      @click.stop="openEditAccountModal(account)"
    >
      <v-icon icon="mdi-pencil-outline" size="18" />
    </v-btn>
    <div class="account-card__icon d-flex align-center justify-center rounded-circle mb-3">
      <v-icon :icon="getAccountIcon(account.type)" size="24" class="account-card__icon-svg" />
    </div>
    <p class="account-card__name mb-1">
      {{ account.name }}
    </p>
    <p class="account-card__value mb-1">
      {{ showPrivacy ? formatCurrency(account.balance) : '••••' }}
    </p>
    <p class="account-card__label">
      Saldo atual
    </p>
  </div>
</template>

<style scoped>
.account-card {
  position: relative;
  background-color: white;
  flex-shrink: 0;
  border-bottom: 4px solid transparent;
  color: #212529;
  transition: opacity 0.2s;
}

.account-card__edit-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #868e96;
  min-width: 32px;
}

.account-card__edit-btn:hover {
  color: #495057;
  background-color: rgba(0, 0, 0, 0.05);
}

.account-card:hover {
  opacity: 0.95;
}

.account-card__icon {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  background-color: #e9ecef;
}

.account-card__icon-svg {
  color: #495057;
}

.account-card__name {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #212529;
}

.account-card__value {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #212529;
}

.account-card__label {
  font-size: 12px;
  color: #868e96;
  margin: 0;
}
</style>
