<script setup lang="ts">
import AccountOverview from './components/AccountOverview.vue'
import DashboardHeader from './components/DashboardHeader.vue'
import TransactionList from './components/TransactionList.vue'
import ConfirmDeleteModal from '~/components/modals/ConfirmDeleteModal.vue'
import EditProfileModal from '~/components/modals/EditProfileModal.vue'

definePageMeta({
  layout: 'dashboard',
})

const { areValuesVisible } = useDashboardController()
const {
  isConfirmDeleteModalOpen,
  confirmDeleteEntityType,
  confirmDeleteEntityId,
  closeConfirmDeleteModal,
} = useDashboard()
const { handleConfirm } = useConfirmDelete()
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

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  flex: 1;
  padding: 24px;
}

.dashboard-col {
  border-radius: 16px;
  overflow: hidden;
}

@media (min-width: 960px) {
  .dashboard-page {
    min-height: 0;
  }

  .dashboard-grid {
    grid-template-columns: 1fr 1fr;
    min-height: 0;
    height: calc(100vh - 64px);
    overflow: hidden;
  }

  .dashboard-col {
    min-height: 0;
  }

  .dashboard-col > * {
    height: 100%;
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
