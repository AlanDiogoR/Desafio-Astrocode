<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

const route = useRoute()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
const { $api } = useNuxtApp()

const planId = computed(() => (route.query.plano as string) || 'MONTHLY')
const planLabel = computed(() => {
  const labels: Record<string, string> = {
    MONTHLY: 'Pro Mensal',
    SEMIANNUAL: 'Pro Semestral',
    ANNUAL: 'Elite Anual',
  }
  return labels[planId.value] ?? planId.value
})

const planPrices: Record<string, number> = {
  MONTHLY: 9.9,
  SEMIANNUAL: 49.9,
  ANNUAL: 89.9,
}
const planAmount = computed(() => planPrices[planId.value] ?? 9.9)

const mpPublicKey = ref('')
const isLoadingConfig = ref(true)
const success = ref(false)
const errorMessage = ref<string | null>(null)
const resultData = ref<unknown>(null)

onMounted(async () => {
  try {
    const { data } = await $api.get<{ mpPublicKey: string }>('/config/public')
    mpPublicKey.value = data?.mpPublicKey ?? ''
  } catch {
    errorMessage.value = 'Não foi possível carregar a configuração de pagamento.'
  } finally {
    isLoadingConfig.value = false
  }
})

function handleSuccess(result: unknown) {
  resultData.value = result
  success.value = true
  errorMessage.value = null
  toast?.success('Assinatura ativada com sucesso!')
}

function handleError(msg: string) {
  errorMessage.value = msg
  toast?.error(msg)
}

function invalidateUser() {
  const { refetch } = useUser()
  refetch()
}
</script>

<template>
  <div class="checkout-page">
    <div class="checkout-page__container">
      <v-btn
        variant="text"
        color="primary"
        class="mb-4"
        :to="'/planos'"
      >
        <v-icon start>mdi-arrow-left</v-icon>
        Voltar aos planos
      </v-btn>

      <h1 class="checkout-page__title">
        Checkout - {{ planLabel }}
      </h1>

      <p class="checkout-page__amount text-h6 mb-4">
        {{ new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(planAmount) }}
      </p>

      <v-alert v-if="errorMessage" type="error" class="mb-4" closable @click:close="errorMessage = null">
        {{ errorMessage }}
      </v-alert>

      <v-alert v-if="success" type="success" class="mb-4">
        Assinatura ativada com sucesso! Você já pode aproveitar os benefícios Pro.
        <v-btn class="mt-2" color="primary" :to="'/dashboard'" @click="invalidateUser">
          Ir para o Dashboard
        </v-btn>
      </v-alert>

      <ClientOnly>
        <div v-if="!success && !isLoadingConfig && mpPublicKey" class="checkout-page__form">
          <MercadoPagoCardForm
            :public-key="mpPublicKey"
            :plan-type="planId"
            :amount="planAmount"
            @success="handleSuccess"
            @error="handleError"
          />
        </div>
        <div v-else-if="!success && !isLoadingConfig && !mpPublicKey" class="checkout-page__fallback">
          <v-alert type="warning">
            Chave do Mercado Pago não configurada. Entre em contato com o suporte.
          </v-alert>
        </div>
        <div v-else-if="!success" class="checkout-page__loading">
          <v-progress-circular indeterminate color="primary" size="48" />
        </div>
      </ClientOnly>
    </div>
  </div>
</template>

<style scoped>
.checkout-page {
  min-height: 100vh;
  padding: 32px 24px 48px;
  background: #f8fafc;
}

.checkout-page__container {
  max-width: 520px;
  margin: 0 auto;
}

.checkout-page__title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
}

.checkout-page__amount {
  color: #087f5b;
  font-weight: 600;
}

.checkout-page__form {
  margin-top: 16px;
}

.checkout-page__loading {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}
</style>
