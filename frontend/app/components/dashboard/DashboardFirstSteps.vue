<script setup lang="ts">
const { openNewGoalModal, openNewTransactionModal } = useDashboard()

function goOpenFinance() {
  void navigateTo('/dashboard/open-finance')
}
</script>

<template>
  <v-sheet
    color="primary"
    class="dashboard-first-steps pa-4 mb-3 mb-md-4 rounded-xl"
    elevation="0"
  >
    <div class="d-flex align-center gap-2 mb-3">
      <v-icon icon="mdi-flag-checkered" color="white" />
      <h2 class="text-subtitle-1 font-weight-bold text-white mb-0">
        Seus primeiros passos
      </h2>
    </div>
    <p class="text-body-2 text-white mb-4" style="opacity: 0.95">
      Em poucos minutos você organiza tudo. Siga a ordem ou escolha por onde começar:
    </p>
    <v-list bg-color="transparent" class="pa-0 dashboard-first-steps__list">
      <v-list-item
        class="rounded-lg mb-2"
        base-color="white"
        @click="goOpenFinance"
      >
        <template #prepend>
          <v-avatar color="white" size="36" class="text-primary font-weight-bold">
            1
          </v-avatar>
        </template>
        <v-list-item-title class="text-white font-weight-medium">
          Conectar banco
        </v-list-item-title>
        <v-list-item-subtitle class="text-white" style="opacity: 0.9">
          Open Finance (Pluggy) ou cadastre uma conta manualmente.
        </v-list-item-subtitle>
        <template #append>
          <v-icon icon="mdi-chevron-right" color="white" />
        </template>
      </v-list-item>
      <v-list-item
        class="rounded-lg mb-2"
        base-color="white"
        @click="openNewGoalModal()"
      >
        <template #prepend>
          <v-avatar color="white" size="36" class="text-primary font-weight-bold">
            2
          </v-avatar>
        </template>
        <v-list-item-title class="text-white font-weight-medium">
          Definir uma meta
        </v-list-item-title>
        <v-list-item-subtitle class="text-white" style="opacity: 0.9">
          Viagem, reserva de emergência ou qualquer objetivo.
        </v-list-item-subtitle>
        <template #append>
          <v-icon icon="mdi-chevron-right" color="white" />
        </template>
      </v-list-item>
      <v-list-item
        class="rounded-lg"
        base-color="white"
        @click="openNewTransactionModal('EXPENSE')"
      >
        <template #prepend>
          <v-avatar color="white" size="36" class="text-primary font-weight-bold">
            3
          </v-avatar>
        </template>
        <v-list-item-title class="text-white font-weight-medium">
          Lançar um gasto
        </v-list-item-title>
        <v-list-item-subtitle class="text-white" style="opacity: 0.9">
          Registre uma despesa e veja o painel ganhar vida.
        </v-list-item-subtitle>
        <template #append>
          <v-icon icon="mdi-chevron-right" color="white" />
        </template>
      </v-list-item>
    </v-list>
  </v-sheet>
</template>

<style scoped>
.dashboard-first-steps {
  background: linear-gradient(135deg, rgb(var(--v-theme-primary)) 0%, #065f46 100%);
}

.dashboard-first-steps__list :deep(.v-list-item) {
  background: rgba(255, 255, 255, 0.12);
}

.dashboard-first-steps__list :deep(.v-list-item:hover) {
  background: rgba(255, 255, 255, 0.2);
}
</style>
