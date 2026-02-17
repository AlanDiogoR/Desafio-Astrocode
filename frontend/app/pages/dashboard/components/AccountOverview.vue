<script setup lang="ts">
import AccountCard from './AccountCard.vue'
import AccountsEmpty from '~/components/accounts/AccountsEmpty.vue'
import GoalsList from '~/components/goals/GoalsList.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import NewGoalModal from '~/components/modals/NewGoalModal.vue'
import GoalInteractionModal from '~/components/modals/GoalInteractionModal.vue'
import EditGoalModal from '~/components/modals/EditGoalModal.vue'
import { useCarousel } from '~/composables/useCarousel'

const { openNewAccountModal, openConfirmDeleteModal } = useDashboard()
const {
  accounts,
  goals,
  formattedTotalBalance,
  areValuesVisible,
  isLoading,
} = useDashboardController()

const { carouselRef, scroll } = useCarousel()
const isEmpty = computed(() => accounts.value.length === 0)
const hasCarousel = computed(() => accounts.value.length >= 3)

function togglePrivacy() {
  areValuesVisible.value = !areValuesVisible.value
}
</script>

<template>
  <div class="account-overview">
    <div class="account-overview__content d-flex flex-column justify-space-between">
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
      />
      <section class="accounts-section">
        <div class="accounts-header d-flex align-center justify-space-between">
          <h3 class="accounts-title">
            Minhas contas
          </h3>
          <div
            class="accounts-nav d-flex gap-1"
            :class="{ 'accounts-nav--hidden': !hasCarousel }"
          >
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
          class="accounts-skeleton"
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
            @click="openConfirmDeleteModal('ACCOUNT', account.id)"
          />
        </div>
      </section>
    </div>
    <NewAccountModal />
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
}

@media (min-width: 960px) {
  .account-overview {
    height: 100%;
  }

  .account-overview__content {
    height: 100%;
    overflow: hidden;
  }
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

.accounts-skeleton {
  display: flex;
  gap: 16px;
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

@media (min-width: 960px) {
  .balance-value {
    font-size: 32px;
  }
}
</style>
