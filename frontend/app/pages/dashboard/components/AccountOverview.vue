<script setup lang="ts">
import { formatCurrency } from '~/utils/format'
import AccountCard from './AccountCard.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import { PlusIcon } from '@radix-icons/vue'

type AccountType = 'checking' | 'investment' | 'cash'

interface BankAccount {
  id: string
  name: string
  balance: number
  type: AccountType
  color: string
}

defineProps<{
  showPrivacy: boolean
}>()

defineEmits<{
  togglePrivacy: []
}>()

const { openNewAccountModal } = useDashboard()
const { accounts: bankAccountsRef, totalBalance } = useBankAccounts()

const accounts = computed<BankAccount[]>(() => bankAccountsRef?.value ?? [])

const isEmpty = computed(() => (bankAccountsRef?.value ?? []).length === 0)

const carouselRef = ref<HTMLElement | null>(null)

const CARD_SCROLL_OFFSET = 280

function scrollAccounts(direction: number) {
  const el = carouselRef.value
  if (!el) return
  el.scrollBy({ left: direction * CARD_SCROLL_OFFSET, behavior: 'smooth' })
}
</script>

<template>
  <div class="account-overview">
    <div class="account-overview__content d-flex flex-column justify-space-between">
      <section class="balance-section">
        <p class="balance-label ma-0 mb-2">
          Saldo total
        </p>
        <div class="d-flex align-center">
          <p class="balance-value ma-0">
            {{ showPrivacy ? formatCurrency(totalBalance) : '••••••' }}
          </p>
          <v-btn
            icon
            variant="text"
            density="compact"
            color="white"
            class="opacity-80 ml-4"
            @click="$emit('togglePrivacy')"
          >
            <v-icon :icon="showPrivacy ? 'mdi-eye-outline' : 'mdi-eye-off-outline'" />
          </v-btn>
        </div>
      </section>
      <section class="accounts-section">
        <div class="accounts-header d-flex align-center justify-space-between">
          <h3 class="accounts-title">
            Minhas contas
          </h3>
          <div
            class="accounts-nav d-flex gap-1"
            :class="{ 'accounts-nav--hidden': isEmpty }"
          >
            <v-btn
              icon="mdi-chevron-left"
              variant="text"
              density="compact"
              color="white"
              @click="scrollAccounts(-1)"
            />
            <v-btn
              icon="mdi-chevron-right"
              variant="text"
              density="compact"
              color="white"
              @click="scrollAccounts(1)"
            />
          </div>
        </div>
        <div
          v-if="isEmpty"
          class="accounts-empty"
          role="button"
          tabindex="0"
          @click="openNewAccountModal()"
          @keydown.enter="openNewAccountModal()"
          @keydown.space.prevent="openNewAccountModal()"
        >
          <div class="accounts-empty__icon-wrapper">
            <PlusIcon class="accounts-empty__icon" />
          </div>
          <span class="accounts-empty__text">Cadastrar uma nova conta</span>
        </div>
        <div v-else ref="carouselRef" class="accounts-carousel">
          <AccountCard
            v-for="account in accounts"
            :key="account.id"
            :account="account"
            :show-privacy="showPrivacy"
            class="account-card-item"
          />
        </div>
      </section>
    </div>
    <NewAccountModal />
  </div>
</template>

<style scoped>
.account-overview {
  height: 100%;
  width: 100%;
  background-color: #087f5b;
  color: white;
  box-sizing: border-box;
  display: flex;
  border-radius: 16px;
}

.account-overview__content {
  width: 100%;
  height: 100%;
  padding: 24px 24px 32px;
  overflow: hidden;
}


.balance-section {
  flex-shrink: 0;
}

.balance-label {
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
  margin: 0 0 4px 0;
}

.balance-value {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  line-height: 1.2;
}

.accounts-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  opacity: 0.95;
}

.accounts-section {
  flex-shrink: 0;
}

.accounts-header {
  margin-bottom: 16px;
}

.accounts-nav :deep(.v-btn) {
  color: rgb(255, 255, 255);
}

.accounts-nav :deep(.v-icon) {
  color: rgb(255, 255, 255);
}

.accounts-nav--hidden {
  visibility: hidden;
}

.accounts-carousel {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  overflow-y: hidden;
  padding-bottom: 8px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.accounts-carousel::-webkit-scrollbar {
  height: 6px;
}

.accounts-carousel::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.account-card-item {
  flex: 0 0 calc((100% - 32px) / 2.5);
  min-width: 200px;
  max-width: 260px;
}

.accounts-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 32px;
  border: 2px dashed #ffffff; /* borda pontilhada branca */
  border-radius: 16px;
  cursor: pointer;
  min-height: 120px;
  background: transparent;
}

.accounts-empty__icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 2px dashed #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.accounts-empty__icon {
  width: 24px;
  height: 24px;
  color: #ffffff;
}

.accounts-empty__text {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  text-align: center;
}

@media (min-width: 960px) {
  .balance-value {
    font-size: 32px;
  }
}
</style>
