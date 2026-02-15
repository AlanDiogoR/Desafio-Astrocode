<script setup lang="ts">
import AccountCard from './AccountCard.vue'
import GoalsList from '~/components/goals/GoalsList.vue'
import NewAccountModal from '~/components/modals/NewAccountModal.vue'
import NewGoalModal from '~/components/modals/NewGoalModal.vue'
import GoalInteractionModal from '~/components/modals/GoalInteractionModal.vue'
import EditGoalModal from '~/components/modals/EditGoalModal.vue'
import ConfirmDeleteModal from '~/components/modals/ConfirmDeleteModal.vue'
import { PlusIcon } from '@radix-icons/vue'

const { openNewAccountModal, isConfirmDeleteModalOpen, confirmDeleteEntityType, confirmDeleteEntityId, closeConfirmDeleteModal, openConfirmDeleteModal } = useDashboard()
const { handleConfirm } = useConfirmDelete()
const {
  accounts,
  goals,
  formattedTotalBalance,
  areValuesVisible,
  isLoading,
} = useDashboardController()

const carouselRef = ref<HTMLElement | null>(null)

const isEmpty = computed(() => accounts.value.length === 0)
const hasCarousel = computed(() => accounts.value.length >= 3)

const CARD_SCROLL_OFFSET = 236

function scrollAccounts(direction: number) {
  const el = carouselRef.value
  if (!el) return
  el.scrollBy({ left: direction * CARD_SCROLL_OFFSET, behavior: 'smooth' })
}

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
        <div class="d-flex align-center">
          <p v-if="isLoading" class="balance-skeleton ma-0">
            <v-skeleton-loader type="text" width="140" />
          </p>
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
          v-if="isLoading"
          class="accounts-skeleton"
        >
          <v-skeleton-loader
            type="list-item-avatar-two-line"
            class="accounts-skeleton__item"
          />
          <v-skeleton-loader
            type="list-item-avatar-two-line"
            class="accounts-skeleton__item"
          />
        </div>
        <div
          v-else-if="isEmpty"
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
    <ConfirmDeleteModal
      :is-open="isConfirmDeleteModalOpen && !!confirmDeleteEntityType && !!confirmDeleteEntityId"
      :entity-type="confirmDeleteEntityType ?? 'ACCOUNT'"
      :on-confirm="handleConfirm"
      :on-close="closeConfirmDeleteModal"
    />
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

.accounts-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 32px;
  border: 2px dashed #ffffff;
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
