<script setup lang="ts">
import DashboardHeader from './components/DashboardHeader.vue'
import UpgradeGate from '~/components/ui/UpgradeGate.vue'

definePageMeta({
  layout: 'dashboard',
})

const { hasCreditCards, invalidate: refreshSubscription } = useSubscription()
</script>

<template>
  <div class="dashboard-cartoes">
    <DashboardHeader />
    <div class="dashboard-cartoes__body pa-6">
      <UpgradeGate
        v-if="!hasCreditCards"
        icon="mdi-credit-card-outline"
        title="Cartões de crédito"
        description="Acompanhe faturas e gastos nos seus cartões."
        required-plan="Plano Pro (a partir de R$ 19,90/mês)"
        :benefits="[
          'Múltiplos cartões de crédito',
          'Controle de faturas mensais',
          'Transações ilimitadas',
          'Categorização com categorias ilimitadas',
        ]"
        @retry="refreshSubscription"
      />

      <div v-else class="cartoes-elite text-center mx-auto" style="max-width: 520px">
        <v-card rounded="xl" variant="outlined" class="pa-6">
          <v-icon icon="mdi-credit-card-multiple-outline" size="48" color="primary" class="mb-4" />
          <h2 class="text-h6 font-weight-bold mb-2">
            Seus cartões no painel
          </h2>
          <p class="text-body-2 text-medium-emphasis mb-6">
            Adicione e gerencie cartões na área principal do dashboard, na seção &quot;Meus cartões&quot;.
          </p>
          <v-btn color="primary" rounded="lg" to="/dashboard">
            Ir para o painel
          </v-btn>
        </v-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-cartoes {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  background-color: white;
}

.dashboard-cartoes__body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}
</style>
