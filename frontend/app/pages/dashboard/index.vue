<script setup lang="ts">
import AccountOverview from './components/AccountOverview.vue'
import DashboardHeader from './components/DashboardHeader.vue'
import TransactionList from './components/TransactionList.vue'

definePageMeta({
  layout: 'dashboard',
})

const showPrivacy = ref(true)
</script>

<template>
  <div class="dashboard-page">
    <DashboardHeader />
    <div class="dashboard-grid">
      <div class="dashboard-col dashboard-col--left">
        <AccountOverview
          :show-privacy="showPrivacy"
          @toggle-privacy="showPrivacy = !showPrivacy"
        />
      </div>
      <div class="dashboard-col dashboard-col--right">
        <TransactionList :show-privacy="showPrivacy" />
      </div>
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
  height: calc(100vh - 64px);
  padding: 24px;
  overflow: hidden;
}

.dashboard-col {
  min-height: 0;
  border-radius: 16px;
  overflow: hidden;
}

.dashboard-col > * {
  height: 100%;
}

@media (min-width: 960px) {
  .dashboard-col--left {
    grid-column: 1;
  }

  .dashboard-col--right {
    grid-column: 2;
  }
}

@media (min-width: 960px) {
  .dashboard-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 959px) {
  .dashboard-grid {
    height: auto;
    overflow: visible;
    padding: 16px;
    gap: 16px;
  }

  .dashboard-col {
    min-height: 200px;
  }
}
</style>
