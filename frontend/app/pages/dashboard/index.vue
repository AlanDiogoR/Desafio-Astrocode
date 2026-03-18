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
    <ClientOnly>
      <div class="dashboard-grid">
        <aside class="dashboard-col dashboard-col--left">
          <AccountOverview />
        </aside>
        <main class="dashboard-col dashboard-col--right">
          <TransactionList :show-privacy="areValuesVisible" />
        </main>
      </div>
      <template #fallback>
        <div class="dashboard-grid">
          <aside class="dashboard-col dashboard-col--left">
            <div class="account-overview account-overview--skeleton">
              <div class="account-overview__content d-flex flex-column pa-4">
                <v-skeleton-loader type="text" width="120" class="mb-2" />
                <v-skeleton-loader type="text" width="80" />
              </div>
            </div>
          </aside>
          <main class="dashboard-col dashboard-col--right">
            <v-skeleton-loader type="table" class="pa-4" />
          </main>
        </div>
      </template>
    </ClientOnly>
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
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex: 1;
  min-height: 200px;
  padding: 24px;
}

.dashboard-col {
  border-radius: 16px;
  overflow: hidden;
  flex: 1 1 auto;
  min-height: 200px;
}

.dashboard-col--left {
  flex: 1 1 auto;
}

.dashboard-col--right {
  flex: 1 1 auto;
}

@media (min-width: 960px) {
  .dashboard-grid {
    flex-direction: row;
    gap: 24px;
    min-height: 0;
    overflow: hidden;
  }

  .dashboard-col {
    flex: 1 1 0;
    min-width: 0;
    min-height: 0;
  }

  .dashboard-col--left {
    flex: 1 1 0;
    min-width: 280px;
  }

  .dashboard-col > * {
    height: 100%;
    min-height: 200px;
  }
}

@media (max-width: 959px) {
  .dashboard-grid {
    padding: 16px;
    padding-bottom: calc(88px + env(safe-area-inset-bottom, 0px));
    gap: 16px;
  }
}

.account-overview--skeleton {
  min-height: 200px;
  background-color: var(--color-primary, #087F5B);
  border-radius: 16px;
}
</style>
