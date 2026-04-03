<script setup lang="ts">
import AccountOverview from './components/AccountOverview.vue'
import DashboardHeader from './components/DashboardHeader.vue'
import TransactionList from './components/TransactionList.vue'
import DashboardTour from '~/components/dashboard/DashboardTour.vue'
import ConfirmDeleteModal from '~/components/modals/ConfirmDeleteModal.vue'
import EditProfileModal from '~/components/modals/EditProfileModal.vue'
import MonthlyInsightBanner from '~/components/transactions/MonthlyInsightBanner.vue'

definePageMeta({
  layout: 'dashboard',
})

const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const { areValuesVisible } = useDashboardController()
const {
  isConfirmDeleteModalOpen,
  confirmDeleteEntityType,
  confirmDeleteEntityId,
  closeConfirmDeleteModal,
} = useDashboard()
const { handleConfirm } = useConfirmDelete()

const { selectedDate } = useMonthSelector()
const insightYear = computed(() => selectedDate.value.getFullYear())
const insightMonth = computed(() => selectedDate.value.getMonth() + 1)
const { dominantExpenseShare, isPending: isInsightPending } = useInsightsController(
  insightYear,
  insightMonth,
)

onMounted(() => {
  if (!import.meta.client) return
  if (!authStore.user) return
  if (localStorage.getItem('grivy_welcomed')) return
  localStorage.setItem('grivy_welcomed', 'true')
  const first = authStore.user.name?.trim().split(/\s+/)[0] ?? 'usuário'
  toast.success(`Bem-vindo ao Grivy, ${first}!`)
})
</script>

<template>
  <div class="dashboard-page">
    <ConfirmDeleteModal
      :is-open="!!(isConfirmDeleteModalOpen && confirmDeleteEntityType && confirmDeleteEntityId)"
      :entity-type="confirmDeleteEntityType ?? 'ACCOUNT'"
      :on-confirm="handleConfirm"
      :on-close="closeConfirmDeleteModal"
    />
    <EditProfileModal />
    <DashboardHeader />
    <DashboardTour />
    <div class="dashboard-grid">
      <div class="dashboard-col dashboard-col--left">
        <AccountOverview />
      </div>
      <div class="dashboard-col dashboard-col--right">
        <div class="dashboard-transactions-column">
          <MonthlyInsightBanner
            v-if="!isInsightPending && dominantExpenseShare && dominantExpenseShare.percentage / 100 >= 0.8"
            :category-name="dominantExpenseShare.categoryName"
            :percentage="dominantExpenseShare.percentage"
            class="flex-shrink-0"
          />
          <TransactionList :show-privacy="areValuesVisible" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  flex: 1;
  background-color: white;
}

.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex: 1;
  padding: 24px;
  min-height: 0;
}

.dashboard-col {
  border-radius: 16px;
  overflow: hidden;
  min-height: 0;
}

.dashboard-transactions-column {
  display: flex;
  flex-direction: column;
  min-height: 0;
  min-width: 0;
}

@media (min-width: 960px) {
  .dashboard-transactions-column {
    flex: 1;
    overflow: hidden;
  }

  .dashboard-transactions-column :deep(.transaction-list) {
    flex: 1;
    min-height: 0;
  }
}

@media (min-width: 960px) {
  .dashboard-page {
    min-height: 0;
    overflow: hidden;
  }

  .dashboard-grid {
    flex-direction: row;
    min-height: 0;
    height: calc(100vh - 64px);
    overflow: hidden;
    gap: 24px;
  }

  .dashboard-col {
    flex: 1 1 0;
    min-width: 280px;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }

  .dashboard-col > * {
    flex: 1;
    min-height: 0;
    min-width: 0;
    overflow: hidden;
  }
}

@media (max-width: 959px) {
  .dashboard-grid {
    padding: 16px;
    padding-bottom: calc(88px + env(safe-area-inset-bottom, 0px));
    gap: 16px;
    min-height: auto;
  }

  .dashboard-col {
    min-height: 180px;
  }
}
</style>
