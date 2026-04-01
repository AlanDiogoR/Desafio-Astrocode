<script setup lang="ts">
import { Plans, type MpPlanId } from '~/constants/plans'
import type { PaymentResponse } from '~/services/subscription/subscriptionService'

definePageMeta({
  layout: 'default',
})

const route = useRoute()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
const { $api } = useNuxtApp()
const { refetch: refetchUser } = useUser()

const VALID_MP_PLANS: MpPlanId[] = ['PRO_MONTHLY', 'PRO_SEMIANNUAL', 'PRO_ANNUAL']
const REDIRECT_DELAY_MS = 2500

const planId = computed(() => {
  const p = (route.query.plan as string) || 'PRO_MONTHLY'
  return VALID_MP_PLANS.includes(p as MpPlanId) ? (p as MpPlanId) : 'PRO_MONTHLY'
})

const selectedPlan = computed(() => Plans[planId.value])

const plans = ref<Array<{ id: string; price: number }>>([])
const planAmount = computed(() => {
  const map: Record<MpPlanId, string> = {
    PRO_MONTHLY: 'MONTHLY',
    PRO_SEMIANNUAL: 'SEMIANNUAL',
    PRO_ANNUAL: 'ANNUAL',
  }
  const apiId = map[planId.value]
  const plan = plans.value.find((p) => p.id === apiId)
  return plan?.price ?? selectedPlan.value.price
})

const mpPublicKey = ref('')
const isLoadingConfig = ref(true)
const success = ref(false)
const showEliteCelebration = ref(false)
const pendingInfo = ref<PaymentResponse | null>(null)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    const redirectUrl = `/subscription/checkout?plan=${planId.value}`
    return navigateTo('/login?redirect=' + encodeURIComponent(redirectUrl), { replace: true })
  }
  try {
    const { listPlans: fetchPlans } = await import('~/services/subscription/listPlans')
    const [{ data: configData }, plansData] = await Promise.all([
      $api.get<{ mpPublicKey: string }>('/config/public'),
      fetchPlans($api),
    ])
    mpPublicKey.value = configData?.mpPublicKey ?? ''
    plans.value = (plansData ?? []).map((p) => ({ id: p.id, price: p.price }))
  } catch (e) {
    const err = e as Error & { code?: string }
    errorMessage.value = err?.code === 'API_CONFIG_MISSING'
      ? 'API não configurada. Verifique NUXT_PUBLIC_API_BASE no ambiente.'
      : 'Não foi possível carregar a configuração de pagamento.'
  } finally {
    isLoadingConfig.value = false
  }
})

async function handleSuccess(result: PaymentResponse) {
  pendingInfo.value = null
  success.value = true
  showEliteCelebration.value = planId.value === 'PRO_ANNUAL'
  errorMessage.value = null
  toast?.success(result.message || 'Assinatura ativada! Redirecionando...')
  const { mapApiUserToStoreUser } = await import('~/utils/mapUser')
  const res = await refetchUser()
  if (res.data) {
    authStore.setUser(mapApiUserToStoreUser(res.data))
  }
  const { useQueryClient } = await import('@tanstack/vue-query')
  useQueryClient().invalidateQueries({ queryKey: ['subscription-status'] })
  setTimeout(() => {
    navigateTo('/dashboard', { replace: true })
  }, REDIRECT_DELAY_MS)
}

function handlePending(result: PaymentResponse) {
  pendingInfo.value = result
  success.value = false
  errorMessage.value = null
  toast?.success(result.message || 'Pagamento em análise.')
}

function handleError(msg: string) {
  errorMessage.value = msg
  toast?.error(msg)
}
</script>

<template>
  <div class="checkout-page">
    <div v-if="showEliteCelebration && success" class="checkout-confetti" aria-hidden="true">
      <span
        v-for="n in 48"
        :key="n"
        class="checkout-confetti__piece"
        :style="{
          '--t': `${0.7 + (n % 6) * 0.15}s`,
          '--l': `${(n * 37) % 100}%`,
          '--h': `${(n * 53) % 360}`,
        }"
      />
    </div>
    <div class="checkout-page__container">
      <div class="checkout-page__nav d-flex gap-2 mb-4">
        <v-btn variant="text" color="primary" :to="'/dashboard/planos'">
          <v-icon start>mdi-arrow-left</v-icon>
          Voltar aos planos
        </v-btn>
        <v-btn variant="text" color="primary" to="/">
          <v-icon start>mdi-home</v-icon>
          Home
        </v-btn>
      </div>

      <div v-if="isLoadingConfig" class="checkout-page__skeleton mb-4" aria-busy="true" aria-label="Carregando checkout">
        <v-skeleton-loader type="heading" class="mb-3" />
        <v-skeleton-loader type="text" width="200" class="mb-4" />
        <v-skeleton-loader type="card" height="240" />
      </div>
      <template v-else>
        <h1 class="checkout-page__title">
          Assinar {{ selectedPlan.name }}
        </h1>
        <p class="checkout-page__amount text-h6 mb-4">
          {{ new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(planAmount) }}
        </p>
      </template>

      <v-alert v-if="errorMessage" type="error" class="mb-4" closable @click:close="errorMessage = null">
        {{ errorMessage }}
        <v-btn variant="text" size="small" class="mt-2" :to="'/dashboard/planos'">
          Voltar aos planos
        </v-btn>
      </v-alert>

      <v-alert
        v-if="success && showEliteCelebration"
        type="success"
        variant="flat"
        class="mb-4 checkout-elite-alert"
        prominent
      >
        <div class="text-h6 font-weight-bold mb-2">
          Parabéns! Você agora é Grivy Elite.
        </div>
        <p class="text-body-1 mb-3">
          Sua jornada para a liberdade financeira começa agora. Estamos redirecionando você para o painel.
        </p>
        <v-btn color="primary" variant="flat" to="/dashboard">
          Ir para o dashboard
        </v-btn>
      </v-alert>
      <v-alert v-else-if="success" type="success" class="mb-4">
        Pagamento aprovado! Redirecionando para sua conta...
        <v-btn class="mt-2" color="primary" to="/dashboard">
          Ir agora
        </v-btn>
      </v-alert>

      <v-alert v-if="pendingInfo" type="info" class="mb-4" border="start">
        {{ pendingInfo.message }}
      </v-alert>

      <div v-if="!success && !isLoadingConfig && mpPublicKey" class="checkout-page__form">
        <MercadoPagoCardForm
          :public-key="mpPublicKey"
          :plan-id="planId"
          :amount="planAmount"
          @success="handleSuccess"
          @pending="handlePending"
          @error="handleError"
        />
      </div>
      <div v-else-if="!success && !isLoadingConfig && !mpPublicKey" class="checkout-page__fallback">
        <v-alert type="warning">
          Chave do Mercado Pago não configurada. Configure MP_PUBLIC_KEY no backend e reinicie o servidor.
        </v-alert>
      </div>
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

.checkout-elite-alert {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%) !important;
  color: #065f46 !important;
}

.checkout-confetti {
  pointer-events: none;
  position: fixed;
  inset: 0;
  overflow: hidden;
  z-index: 3000;
}

.checkout-confetti__piece {
  position: absolute;
  top: -12px;
  left: var(--l);
  width: 10px;
  height: 14px;
  border-radius: 2px;
  opacity: 0.95;
  animation: checkout-confetti-fall var(--t) ease-in forwards;
  background: hsl(var(--h) 72% 48%);
}

@keyframes checkout-confetti-fall {
  0% {
    transform: translateY(0) rotate(0deg);
  }
  100% {
    transform: translateY(110vh) rotate(720deg);
  }
}
</style>
