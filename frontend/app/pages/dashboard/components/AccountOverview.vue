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
import AppButton from '~/components/ui/AppButton.vue'
import { useCarousel } from '~/composables/useCarousel'

const { openNewAccountModal, openNewCreditCardModal } = useDashboard()
const {
  accounts,
  goals,
  creditCards,
  formattedTotalBalance,
  areValuesVisible,
  isLoading,
} = useDashboardController()

const { totalIncomeMonth, totalExpenseMonth, totalBalance } = useDashboardData()
const { isFree } = useSubscription()

/** Atualiza com o dashboard (ex.: após novas transações) e mantém mês/ano correntes. */
const monthTxFilters = computed(() => {
  void totalBalance.value
  const n = new Date()
  return { year: n.getFullYear(), month: n.getMonth() + 1, size: 1 }
})
const { totalElements: transactionCountMonth } = useTransactions(monthTxFilters)

const transactionUsagePercent = computed(() =>
  Math.min(100, (Number(transactionCountMonth.value) / 30) * 100),
)

const balance = computed(() => totalIncomeMonth.value - totalExpenseMonth.value)

function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

const dismissedUpgradeBanner = ref(false)

onMounted(() => {
  if (import.meta.client) {
    dismissedUpgradeBanner.value = sessionStorage.getItem('dismissed_upgrade_banner') === 'true'
  }
})

watch(dismissedUpgradeBanner, (val) => {
  if (val && import.meta.client) {
    sessionStorage.setItem('dismissed_upgrade_banner', 'true')
  }
})

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
          <div v-if="isLoading" class="balance-skeleton-wrapper balance-skeleton-wrapper--card">
            <v-skeleton-loader type="card" width="100%" max-width="280" class="balance-skeleton-card" />
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
        <div v-if="isLoading" class="d-flex gap-2 flex-wrap mt-3">
          <v-skeleton-loader type="chip" width="140" height="28" rounded="pill" />
          <v-skeleton-loader type="chip" width="140" height="28" rounded="pill" />
          <v-skeleton-loader type="chip" width="160" height="28" rounded="pill" />
        </div>
        <div v-else class="d-flex gap-3 flex-wrap mt-3">
          <v-chip
            color="success"
            variant="flat"
            size="small"
            prepend-icon="mdi-arrow-up"
            class="font-weight-medium"
          >
            +{{ formatCurrency(totalIncomeMonth) }}
          </v-chip>
          <v-chip
            color="error"
            variant="flat"
            size="small"
            prepend-icon="mdi-arrow-down"
            class="font-weight-medium"
          >
            -{{ formatCurrency(totalExpenseMonth) }}
          </v-chip>
          <v-chip
            :color="balance >= 0 ? 'primary' : 'warning'"
            variant="flat"
            size="small"
            prepend-icon="mdi-scale-balance"
            class="font-weight-medium"
          >
            {{ formatCurrency(balance) }}
          </v-chip>
        </div>
        <v-alert
          v-if="isFree && !dismissedUpgradeBanner"
          type="info"
          variant="tonal"
          rounded="xl"
          class="mt-4 mb-0"
          closable
          @click:close="dismissedUpgradeBanner = true"
        >
          <template #prepend>
            <v-icon icon="mdi-crown" color="primary" />
          </template>
          <div class="d-flex flex-column flex-sm-row align-sm-center gap-2">
            <span>
              <strong>Desbloqueie tudo com o Pro</strong> — cartões de crédito,
              transações ilimitadas e Open Finance.
            </span>
            <AppButton
              size="small"
              color="primary"
              variant="text"
              :block="false"
              class="align-self-start"
              @click="navigateTo('/dashboard/planos')"
            >
              Ver planos
            </AppButton>
          </div>
        </v-alert>
        <v-alert
          v-if="isFree && transactionUsagePercent >= 80"
          type="warning"
          variant="tonal"
          rounded="xl"
          class="mt-3 mb-0"
        >
          <div class="d-flex flex-column flex-sm-row align-sm-center gap-2">
            <span>
              Você usou {{ transactionCountMonth }}/30 transações este mês.
            </span>
            <AppButton
              size="small"
              variant="text"
              :block="false"
              class="align-self-start"
              @click="navigateTo('/dashboard/planos')"
            >
              Fazer upgrade
            </AppButton>
          </div>
        </v-alert>
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
              size="small"
              variant="outlined"
              color="white"
              class="ml-1"
              to="/dashboard/open-finance"
            >
              <v-icon start size="18">mdi-bank-transfer</v-icon>
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
  height: 100%;
  min-width: 0;
  min-height: 0;
  background-color: #087f5b;
  color: white;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  overflow: hidden;
}

.account-overview__content {
  width: 100%;
  min-width: 0;
  padding: 24px 24px 32px;
  gap: 24px;
}

@media (min-width: 960px) {
  .account-overview {
    height: 100%;
  }

  .account-overview__content {
    flex: 1 1 0;
    min-height: 0;
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

.balance-skeleton-wrapper--card {
  position: relative;
  min-width: 200px;
  width: 100%;
  max-width: 280px;
}

.balance-skeleton-card :deep(.v-skeleton-loader__bone) {
  background: rgba(255, 255, 255, 0.22);
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
