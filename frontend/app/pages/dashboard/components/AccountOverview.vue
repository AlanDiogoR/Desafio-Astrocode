<script setup lang="ts">
import AccountCard from './AccountCard.vue'
import AccountsEmpty from '~/components/accounts/AccountsEmpty.vue'
import CreditCardCard from '~/components/cards/CreditCardCard.vue'
import GoalsList from '~/components/goals/GoalsList.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import NewGoalModal from '~/components/modals/NewGoalModal.vue'
import GoalInteractionModal from '~/components/modals/GoalInteractionModal.vue'
import EditGoalModal from '~/components/modals/EditGoalModal.vue'
import EditAccountModal from '~/components/modals/EditAccountModal.vue'
import NewCreditCardModal from '~/components/modals/NewCreditCardModal.vue'
import EditCreditCardModal from '~/components/modals/EditCreditCardModal.vue'
import CreditCardBillModal from '~/components/modals/CreditCardBillModal.vue'
import { useCarousel } from '~/composables/useCarousel'

const { openNewAccountModal, openNewCreditCardModal } = useDashboard()
const { openPluggyConnect, isElite, isConnecting } = useOpenFinance()
const {
  accounts,
  goals,
  creditCards,
  formattedTotalBalance,
  areValuesVisible,
  isLoading,
} = useDashboardController()

const { carouselRef, scroll } = useCarousel()
const { carouselRef: creditCardsCarouselRef, scroll: creditCardsScroll } = useCarousel()
const isEmpty = computed(() => accounts.value.length === 0)
const hasCarousel = computed(() => accounts.value.length >= 3)
const isEmptyCreditCards = computed(() => creditCards.value.length === 0)
const hasCreditCardsCarousel = computed(() => creditCards.value.length >= 3)

function togglePrivacy() {
  areValuesVisible.value = !areValuesVisible.value
}
</script>

<template>
  <div class="account-overview">
    <div class="account-overview__content d-flex flex-column">
      <section class="balance-section">
        <p class="balance-label ma-0 mb-2">
          Saldo total
        </p>
        <div class="d-flex align-center balance-section__row">
          <div v-if="isLoading" class="balance-skeleton-wrapper">
            <v-progress-circular
              indeterminate
              color="white"
              size="36"
              class="balance-skeleton__spinner"
            />
            <v-skeleton-loader type="text" width="140" class="balance-skeleton ma-0" />
          </div>
          <p v-else class="balance-value ma-0">
            {{ formattedTotalBalance }}
          </p>
          <v-btn
            icon
            variant="text"
            density="compact"
            color="white"
            class="opacity-80 ml-4"
            :aria-label="areValuesVisible ? 'Ocultar saldos' : 'Mostrar saldos'"
            @click="togglePrivacy"
          >
            <v-icon :icon="areValuesVisible ? 'mdi-eye-outline' : 'mdi-eye-off-outline'" />
          </v-btn>
        </div>
      </section>
      <GoalsList
        :goals="goals"
        :show-privacy="areValuesVisible"
        :is-loading="isLoading"
        class="goals-section-spacing"
      />
      <v-divider
        v-if="!isEmpty || goals.length > 0"
        class="accounts-section-divider"
        color="rgba(255, 255, 255, 0.35)"
      />
      <section class="accounts-section">
        <div class="accounts-header d-flex align-center justify-space-between mb-4">
          <h3 class="accounts-title accounts-title--primary d-flex align-center gap-1">
            Minhas contas
            <v-btn
              icon
              variant="text"
              density="compact"
              color="white"
              size="small"
              aria-label="Nova conta"
              class="accounts-title__add-btn"
              @click="openNewAccountModal()"
            >
              <v-icon icon="mdi-plus" size="20" />
            </v-btn>
            <v-btn
              v-if="isElite"
              size="small"
              variant="outlined"
              color="white"
              :loading="isConnecting"
              class="ml-1"
              @click="openPluggyConnect()"
            >
              <v-icon start size="18">mdi-bank-plus</v-icon>
              Open Finance
            </v-btn>
          </h3>
          <div v-if="hasCarousel" class="accounts-nav d-flex ga-1">
            <v-btn
              icon="mdi-chevron-left"
              variant="text"
              density="compact"
              color="white"
              @click="scroll(-1)"
            />
            <v-btn
              icon="mdi-chevron-right"
              variant="text"
              density="compact"
              color="white"
              @click="scroll(1)"
            />
          </div>
        </div>
    <div
      v-if="isLoading"
      class="accounts-skeleton d-flex ga-4"
    >
          <div class="accounts-skeleton__spinner">
            <v-progress-circular
              indeterminate
              color="white"
              size="48"
            />
          </div>
          <v-skeleton-loader
            type="list-item-avatar-two-line"
            class="accounts-skeleton__item"
          />
          <v-skeleton-loader
            type="list-item-avatar-two-line"
            class="accounts-skeleton__item"
          />
        </div>
        <AccountsEmpty
          v-else-if="isEmpty"
          @click="openNewAccountModal()"
        />
        <div v-else ref="carouselRef" class="accounts-carousel">
          <AccountCard
            v-for="account in accounts"
            :key="account.id"
            :account="account"
            :show-privacy="areValuesVisible"
            class="account-card-item"
          />
        </div>
      </section>
      <v-divider
        v-if="!isEmpty || !isEmptyCreditCards || goals.length > 0"
        class="accounts-section-divider"
        color="rgba(255, 255, 255, 0.35)"
      />
      <section class="accounts-section">
        <div class="accounts-header d-flex align-center justify-space-between mb-4">
          <h3 class="accounts-title d-flex align-center gap-1">
            Meus cartões
            <v-btn
              icon
              variant="text"
              density="compact"
              color="white"
              size="small"
              aria-label="Novo cartão"
              class="accounts-title__add-btn"
              @click="openNewCreditCardModal()"
            >
              <v-icon icon="mdi-plus" size="20" />
            </v-btn>
          </h3>
          <div
            v-if="hasCreditCardsCarousel"
            class="accounts-nav d-flex ga-1"
          >
            <v-btn
              icon="mdi-chevron-left"
              variant="text"
              density="compact"
              color="white"
              @click="creditCardsScroll(-1)"
            />
            <v-btn
              icon="mdi-chevron-right"
              variant="text"
              density="compact"
              color="white"
              @click="creditCardsScroll(1)"
            />
          </div>
        </div>
        <div
          v-if="isLoading"
          class="accounts-skeleton d-flex ga-4"
        >
          <div class="accounts-skeleton__spinner">
            <v-progress-circular indeterminate color="white" size="48" />
          </div>
          <v-skeleton-loader type="list-item-avatar-two-line" class="accounts-skeleton__item" />
        </div>
        <div
          v-else-if="!isEmptyCreditCards"
          ref="creditCardsCarouselRef"
          class="accounts-carousel"
        >
          <CreditCardCard
            v-for="cc in creditCards"
            :key="cc.id"
            :credit-card="cc"
            :show-privacy="areValuesVisible"
            class="account-card-item"
          />
        </div>
        <div
          v-else
          class="credit-cards-empty-state d-flex flex-column align-center justify-center py-4"
          role="button"
          tabindex="0"
          @click="openNewCreditCardModal()"
          @keydown.enter="openNewCreditCardModal()"
        >
          <v-icon icon="mdi-credit-card-plus-outline" size="32" color="rgba(255,255,255,0.7)" class="mb-2" />
          <p class="credit-cards-empty-text ma-0">
            Adicionar cartão de crédito
          </p>
        </div>
      </section>
    </div>
    <NewAccountModal />
    <EditAccountModal />
    <NewCreditCardModal />
    <EditCreditCardModal />
    <CreditCardBillModal />
    <NewGoalModal />
    <GoalInteractionModal />
    <EditGoalModal />
  </div>
</template>

<style scoped>
.account-overview {
  width: 100%;
  background-color: #087f5b;
  color: white;
  box-sizing: border-box;
  display: flex;
  border-radius: 16px;
}

.account-overview__content {
  width: 100%;
  padding: 24px 24px 32px;
  gap: 24px;
}

@media (min-width: 960px) {
  .account-overview {
    height: 100%;
  }

  .account-overview__content {
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    gap: 20px;
    justify-content: flex-start;
    scrollbar-width: none;
  }

  .account-overview__content::-webkit-scrollbar {
    display: none;
  }
}

/* Separador visual no mobile entre metas e contas */
.accounts-section-divider {
  flex-shrink: 0;
  margin: 8px 0;
  opacity: 0.8;
}

@media (min-width: 960px) {
  .accounts-section-divider {
    margin: 4px 0;
  }
}

.goals-section-spacing {
  flex-shrink: 0;
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

.balance-skeleton-wrapper {
  position: relative;
  min-width: 140px;
}

.balance-skeleton__spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  opacity: 0.95;
}

.balance-skeleton-wrapper .balance-skeleton {
  opacity: 0.5;
}

.balance-skeleton :deep(.v-skeleton-loader__text) {
  background: rgba(255, 255, 255, 0.3);
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

.accounts-title--primary {
  flex: 1;
  min-width: 0;
}

.accounts-title__add-btn {
  flex-shrink: 0;
}

.accounts-section {
  flex-shrink: 0;
}

.accounts-nav :deep(.v-btn) {
  color: rgb(255, 255, 255);
}

.accounts-nav :deep(.v-icon) {
  color: rgb(255, 255, 255);
}


.accounts-skeleton {
  padding-bottom: 8px;
  position: relative;
}

.accounts-skeleton__spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  opacity: 0.95;
}

.accounts-skeleton__item {
  flex: 0 0 220px;
  min-width: 220px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
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
  flex: 0 0 220px;
  min-width: 220px;
}

.credit-cards-empty-state {
  cursor: pointer;
  border: 1px dashed rgba(255, 255, 255, 0.4);
  border-radius: 12px;
  padding: 16px;
  transition: border-color 0.2s;
}

.credit-cards-empty-state:hover,
.credit-cards-empty-state:focus-visible {
  border-color: rgba(255, 255, 255, 0.7);
  outline: none;
}

.credit-cards-empty-text {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  font-weight: 500;
}

@media (min-width: 960px) {
  .balance-value {
    font-size: 32px;
  }
}
</style>
