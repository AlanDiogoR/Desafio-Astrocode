<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

const route = useRoute()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const planId = computed(() => (route.query.plano as string) || 'MONTHLY')
const planLabel = computed(() => {
  const labels: Record<string, string> = {
    MONTHLY: 'Pro Mensal',
    SEMIANNUAL: 'Pro Semestral',
    ANNUAL: 'Elite Anual',
  }
  return labels[planId.value] ?? planId.value
})

const success = ref(false)
const errorMessage = ref<string | null>(null)
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

      <v-alert v-if="errorMessage" type="error" class="mb-4" closable>
        {{ errorMessage }}
      </v-alert>

      <v-alert v-if="success" type="success" class="mb-4">
        Assinatura ativada com sucesso! Você já pode aproveitar os benefícios Pro.
      </v-alert>

      <v-card v-if="!success" variant="outlined" class="pa-6">
        <p class="text-body-1 mb-4">
          Para concluir sua assinatura, a integração com o formulário de cartão do Mercado Pago está em implantação.
        </p>
        <p class="text-body-2 text-medium-emphasis">
          Entre em contato com o suporte ou tente novamente em breve.
        </p>
        <v-btn color="primary" class="mt-4" :to="'/planos'">
          Voltar aos planos
        </v-btn>
      </v-card>
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
  max-width: 480px;
  margin: 0 auto;
}

.checkout-page__title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 24px 0;
}
</style>
