<script setup lang="ts">
const { isOpen, message, close } = usePlanUpgradeGate()

function goPlanos() {
  close()
  void navigateTo('/dashboard/planos')
}

function goCheckoutElite() {
  close()
  void navigateTo('/subscription/checkout?plan=PRO_ANNUAL')
}
</script>

<template>
  <v-dialog
    :model-value="isOpen"
    max-width="520"
    scrollable
    persistent
    @update:model-value="(v: boolean) => { if (!v) close() }"
  >
    <v-card rounded="xl">
      <v-card-title class="text-h6 d-flex align-center gap-2">
        <v-icon color="primary" icon="mdi-rocket-launch" />
        Upgrade imediato
      </v-card-title>
      <v-card-text class="text-body-2">
        <p class="mb-4 text-high-emphasis">
          {{ message }}
        </p>
        <p class="text-medium-emphasis mb-4">
          Compare e escolha o melhor para você:
        </p>
        <v-row dense>
          <v-col cols="12" sm="6">
            <v-sheet border rounded="lg" class="pa-4 h-100" color="surface-variant">
              <div class="text-subtitle-2 font-weight-bold mb-2 text-primary">
                Grivy Pro
              </div>
              <ul class="text-caption pl-4 mb-0">
                <li>Transações e contas ilimitadas</li>
                <li>Cartões de crédito e categorias</li>
                <li>Pagamento seguro (Mercado Pago)</li>
              </ul>
            </v-sheet>
          </v-col>
          <v-col cols="12" sm="6">
            <v-sheet
              rounded="lg"
              class="pa-4 h-100"
              style="border: 2px solid rgb(var(--v-theme-primary)); background: rgba(var(--v-theme-primary), 0.06);"
            >
              <div class="text-subtitle-2 font-weight-bold mb-2 text-primary d-flex align-center gap-1">
                <v-icon icon="mdi-crown" size="20" />
                Elite Anual
              </div>
              <ul class="text-caption pl-4 mb-0">
                <li>Tudo do Pro</li>
                <li>Open Finance (Pluggy)</li>
                <li>Importação automática dos bancos</li>
              </ul>
            </v-sheet>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions class="flex-wrap ga-2 pa-4 pt-0">
        <v-btn variant="text" @click="close">
          Agora não
        </v-btn>
        <v-spacer />
        <v-btn variant="tonal" color="primary" @click="goPlanos">
          Ver todos os planos
        </v-btn>
        <v-btn color="primary" variant="flat" @click="goCheckoutElite">
          Quero Elite Anual
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
