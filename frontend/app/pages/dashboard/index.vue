<script setup lang="ts">
import AccountOverview from './components/AccountOverview.vue'
import DashboardHeader from './components/DashboardHeader.vue'
import TransactionList from './components/TransactionList.vue'
import DashboardFirstSteps from '~/components/dashboard/DashboardFirstSteps.vue'
import DashboardTour from '~/components/dashboard/DashboardTour.vue'
import ConfirmDeleteModal from '~/components/modals/ConfirmDeleteModal.vue'
import EditProfileModal from '~/components/modals/EditProfileModal.vue'

definePageMeta({
  layout: 'dashboard',
})

const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const { accounts, isPending: accountsPending } = useBankAccounts()
const txProbeFilters = computed(() => ({ size: 1 }))
const { totalElements: totalTransactionsApprox, isPending: txProbePending } = useTransactions(txProbeFilters)

const showFirstSteps = computed(
  () =>
    !accountsPending.value
    && !txProbePending.value
    && (accounts.value.length === 0 || totalTransactionsApprox.value === 0),
)

const { areValuesVisible } = useDashboardController()
const {
  isConfirmDeleteModalOpen,
  confirmDeleteEntityType,
  confirmDeleteEntityId,
  closeConfirmDeleteModal,
} = useDashboard()
const { handleConfirm } = useConfirmDelete()

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
    <div v-if="showFirstSteps" class="dashboard-onboarding-wrap">
      <DashboardFirstSteps />
    </div>
    <DashboardTour />
    <div class="dashboard-grid">
      <div class="dashboard-col dashboard-col--left">
        <AccountOverview />
      </div>
      <div class="dashboard-col dashboard-col--right">
        <TransactionList :show-privacy="areValuesVisible" />
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

.dashboard-onboarding-wrap {
  padding: 0 24px 8px;
  flex-shrink: 0;
}

@media (max-width: 959px) {
  .dashboard-onboarding-wrap {
    padding: 0 16px 8px;
  }
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
