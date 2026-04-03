<script setup lang="ts">
import DashboardHeader from './components/DashboardHeader.vue'
import UpgradeGate from '~/components/ui/UpgradeGate.vue'
import { subscriptionService } from '~/services/subscription/subscriptionService'

definePageMeta({
  layout: 'dashboard',
})

const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const { hasOpenFinance, subscriptionStatus, invalidate: refreshSubscription } = useSubscription()
const { openPluggyConnect, isConnecting } = useOpenFinance()

const showCancelDialog = ref(false)
const cancelLoading = ref(false)

function formatDate(date?: string | null) {
  if (!date)
    return '—'
  return new Date(date).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  })
}

async function handleCancelSubscription() {
  cancelLoading.value = true
  try {
    await subscriptionService.cancelSubscription()
    showCancelDialog.value = false
    await refreshSubscription()
    toast.success('Assinatura cancelada. Seu acesso Elite continua ativo até o fim do período.')
  }
  catch {
    toast.error('Erro ao cancelar. Tente novamente ou entre em contato.')
  }
  finally {
    cancelLoading.value = false
  }
}
</script>

<template>
  <div class="dashboard-open-finance">
    <DashboardHeader />
    <div class="dashboard-open-finance__body pa-6">
      <UpgradeGate
        v-if="!hasOpenFinance"
        icon="mdi-bank-transfer"
        title="Open Finance"
        description="Conecte seus bancos automaticamente e importe transações sem digitar nada."
        required-plan="Elite Anual"
        :benefits="[
          'Importação automática de transações',
          'Saldo em tempo real de todos os bancos',
          '50+ instituições financeiras',
          'Sincronização diária',
        ]"
        @retry="refreshSubscription"
      />

      <div v-else class="open-finance-elite">
        <div class="open-finance-elite__inner mx-auto" style="max-width: 520px; width: 100%;">
          <v-card rounded="xl" variant="tonal" color="primary" class="mb-5">
            <v-card-text class="pa-4">
              <div class="d-flex align-center justify-space-between flex-wrap gap-3">
                <div>
                  <div class="d-flex align-center gap-2 mb-1">
                    <v-icon icon="mdi-crown" color="primary" size="18" />
                    <span class="text-subtitle-2 font-weight-bold">Elite Anual</span>
                    <v-chip color="success" size="x-small" variant="flat">
                      Ativo
                    </v-chip>
                  </div>
                  <p class="text-caption text-medium-emphasis">
                    Ativo até {{ formatDate(subscriptionStatus?.expiresAt) }}
                  </p>
                </div>
                <v-btn
                  variant="outlined"
                  color="error"
                  size="small"
                  rounded="pill"
                  :loading="cancelLoading"
                  @click="showCancelDialog = true"
                >
                  Cancelar assinatura
                </v-btn>
              </div>
            </v-card-text>
          </v-card>

          <v-dialog v-model="showCancelDialog" max-width="420">
            <v-card rounded="xl">
              <v-card-text class="pa-6">
                <div class="text-center mb-4">
                  <v-icon icon="mdi-alert-circle-outline" color="warning" size="48" />
                </div>
                <h3 class="text-h6 font-weight-bold text-center mb-2">
                  Cancelar assinatura?
                </h3>
                <p class="text-body-2 text-medium-emphasis text-center mb-6">
                  Você continuará com acesso Elite até
                  <strong>{{ formatDate(subscriptionStatus?.expiresAt) }}</strong>.
                  Após essa data, sua conta voltará para o plano gratuito.
                </p>
                <div class="d-flex gap-3">
                  <v-btn
                    variant="text"
                    block
                    @click="showCancelDialog = false"
                  >
                    Manter assinatura
                  </v-btn>
                  <v-btn
                    color="error"
                    variant="flat"
                    block
                    rounded="pill"
                    :loading="cancelLoading"
                    @click="handleCancelSubscription"
                  >
                    Confirmar cancelamento
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-dialog>

          <v-card rounded="xl" variant="outlined" class="pa-6">
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
