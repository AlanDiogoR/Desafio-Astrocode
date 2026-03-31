<script setup lang="ts">
import DashboardHeader from './components/DashboardHeader.vue'
import UpgradeGate from '~/components/ui/UpgradeGate.vue'

definePageMeta({
  layout: 'dashboard',
})

const { hasOpenFinance, invalidate: refreshSubscription } = useSubscription()
const { openPluggyConnect, isConnecting } = useOpenFinance()
</script>

<template>
  <div class="dashboard-open-finance">
    <DashboardHeader />
    <div class="dashboard-open-finance__body pa-6">
      <UpgradeGate
        v-if="!hasOpenFinance"
        icon="mdi-bank-transfer"
        title="Open Finance"
        description="Conecte suas contas bancárias automaticamente e importe transações de todos os seus bancos em um só lugar."
        required-plan="Elite Anual"
        @retry="refreshSubscription"
      />

      <div v-else class="open-finance-elite">
        <v-card rounded="xl" variant="outlined" class="pa-6 mx-auto" max-width="520">
          <h2 class="text-h6 font-weight-bold mb-2">
            Conectar bancos (Pluggy)
          </h2>
          <p class="text-body-2 text-medium-emphasis mb-6">
            Abra o assistente de conexão para vincular suas instituições financeiras e importar contas automaticamente.
          </p>
          <v-btn
            color="primary"
            size="large"
            block
            rounded="lg"
            :loading="isConnecting"
            prepend-icon="mdi-bank-plus"
            @click="openPluggyConnect()"
          >
            Conectar Open Finance
          </v-btn>
        </v-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-open-finance {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  background-color: white;
}

.dashboard-open-finance__body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.open-finance-elite {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 8px;
}
</style>
