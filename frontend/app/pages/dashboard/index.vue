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
      <aside class="dashboard-col dashboard-col--left">
        <AccountOverview />
      </aside>
      <main class="dashboard-col dashboard-col--right">
        <TransactionList :show-privacy="areValuesVisible" />
      </main>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  background-color: white;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  flex: 1;
  min-height: 0;
  padding: 24px;
}

.dashboard-col {
  border-radius: 16px;
  overflow: hidden;
  min-width: 0;
}

.dashboard-col--left {
  order: 1;
}

.dashboard-col--right {
  order: 2;
}

@media (min-width: 960px) {
  .dashboard-page {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }

  .dashboard-grid {
    grid-template-columns: 1fr 1fr;
    flex: 1;
    min-height: 0;
    overflow: hidden;
  }

  .dashboard-col {
    min-height: 0;
    overflow: hidden;
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
  }

  .dashboard-col {
    min-height: 180px;
  }
}
</style>
