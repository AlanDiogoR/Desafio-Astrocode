<script setup lang="ts">
definePageMeta({
  layout: 'dashboard',
})

const { $api } = useNuxtApp()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const plans = ref<Array<{ id: string; name: string; price: number; months: number; description: string }>>([])
const subscription = ref<{ planType: string; status: string; expiresAt: string | null } | null>(null)
const isLoading = ref(true)

const currentPlan = computed(() => authStore.user?.plan ?? 'FREE')

function isCurrentPlanActive(planId: string) {
  return planId === currentPlan.value && subscription.value?.status === 'ACTIVE'
}

onMounted(async () => {
  try {
    const { listPlans } = await import('~/services/subscription/listPlans')
    plans.value = await listPlans($api)
  } catch {
    toast?.error('Erro ao carregar planos.')
  } finally {
    isLoading.value = false
  }

  try {
    const { getSubscription } = await import('~/services/subscription/me')
    subscription.value = await getSubscription($api)
  } catch {
    subscription.value = null
    toast?.error('Erro ao carregar informações da assinatura.')
  }
})

const paidPlans = computed(() => plans.value.filter((p) => p.id !== 'FREE'))

const planFeatures: Record<string, string[]> = {
  MONTHLY: [
    'Contas bancárias ilimitadas',
    'Transações ilimitadas por mês',
    'Metas de economia ilimitadas',
    'Cartões de crédito com controle de fatura',
    'Relatórios e insights mensais',
    'Suporte prioritário',
  ],
  SEMIANNUAL: [
    'Tudo do plano Mensal',
    'Economia de 16% vs mensal',
    'Contas bancárias ilimitadas',
    'Transações ilimitadas por mês',
    'Metas de economia ilimitadas',
    'Cartões de crédito com controle de fatura',
    'Relatórios e insights mensais',
    'Suporte prioritário',
  ],
  ANNUAL: [
    'Tudo do plano Semestral',
    'Economia de 24% vs mensal',
    'Contas bancárias ilimitadas',
    'Transações ilimitadas por mês',
    'Metas de economia ilimitadas',
    'Cartões de crédito com controle de fatura',
    'Open Finance - Conecte seus bancos',
    'Relatórios e insights avançados',
    'Suporte VIP',
  ],
}

function formatPrice(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
  }).format(value)
}

function getPriceUnit(months: number): string {
  if (months <= 1) return '/mês'
  if (months === 6) return '/semestre'
  if (months === 12) return '/ano'
  return `/${months} meses`
}

function getMonthlyEquivalent(price: number, months: number): string | null {
  if (months <= 1) return null
  return formatPrice(price / months)
}

const monthlyEquivalents = computed(() => paidPlans.value.map((p) => getMonthlyEquivalent(p.price, p.months)))

type MpPlanId = 'PRO_MONTHLY' | 'PRO_SEMIANNUAL' | 'PRO_ANNUAL'

function apiPlanToMpPlanId(planId: string): MpPlanId {
  const map: Record<string, MpPlanId> = {
    MONTHLY: 'PRO_MONTHLY',
    SEMIANNUAL: 'PRO_SEMIANNUAL',
    ANNUAL: 'PRO_ANNUAL',
  }
  const id = map[planId]
  if (!id) {
    throw new Error(`Plano não suportado no checkout Mercado Pago: ${planId}`)
  }
  return id
}

const isMpLoading = ref(false)

async function handleMpSubscribe(planId: string) {
  isMpLoading.value = true
  try {
    const { subscriptionService } = await import('~/services/subscription/subscriptionService')
    const mpPlanId = apiPlanToMpPlanId(planId)
    const { checkoutUrl } = await subscriptionService.createCheckout(mpPlanId)
    window.location.href = checkoutUrl
  } catch {
    toast?.error('Erro ao iniciar o pagamento. Tente novamente.')
  } finally {
    isMpLoading.value = false
  }
}
</script>

<template>
  <div class="dashboard-planos">
    <div class="dashboard-planos__inner">
      <div class="planos-page__nav d-flex flex-wrap justify-space-between align-center gap-2 mb-6">
        <div class="d-flex flex-wrap gap-2">
          <v-btn variant="text" color="primary" to="/dashboard">
            <v-icon start>mdi-view-dashboard</v-icon>
            Painel
          </v-btn>
          <v-btn variant="text" color="primary" to="/">
            <v-icon start>mdi-home</v-icon>
            Home
          </v-btn>
        </div>
      </div>
      <h1 class="planos-page__title">
        Planos Grivy
      </h1>
      <p class="planos-page__subtitle">
        Escolha o plano e conclua o pagamento no Mercado Pago
      </p>

      <section v-if="subscription" class="planos-page__current mb-8">
        <v-alert type="info" variant="tonal" density="compact">
          Seu plano atual: <strong>{{ authStore.planLabel }}</strong>
          <span v-if="subscription?.expiresAt">
            · Renova em {{ new Date(subscription.expiresAt).toLocaleDateString('pt-BR') }}
          </span>
        </v-alert>
      </section>

      <div v-if="isLoading" class="planos-page__loading">
        <v-progress-circular indeterminate color="primary" size="48" />
      </div>

      <div v-else-if="paidPlans.length === 0" class="planos-page__empty text-center py-8">
        <p class="text-body-1 text-medium-emphasis">
          Nenhum plano pago disponível no momento.
        </p>
      </div>

      <div v-else class="planos-page__grid">
        <article
          v-for="(plan, index) in paidPlans"
          :key="plan.id"
          class="planos-page__card"
        >
          <div class="planos-page__card-inner">
            <div class="planos-page__card-header">
              <v-icon icon="mdi-star-four-points" size="32" color="primary" class="planos-page__card-icon" />
              <v-chip
                v-if="plan.id === 'MONTHLY' && paidPlans.length > 1"
                size="small"
                color="primary"
                variant="flat"
                class="planos-page__badge"
              >
                Mais popular
              </v-chip>
            </div>
            <h3 class="planos-page__card-title">
              {{ plan.name }}
            </h3>
            <p v-if="plan.description" class="planos-page__card-desc">
              {{ plan.description }}
            </p>
            <div class="planos-page__card-body">
              <ul class="planos-page__features">
                <li v-for="(feature, fi) in (planFeatures[plan.id] ?? [])" :key="fi" class="planos-page__feature">
                  <v-icon icon="mdi-check-circle" size="18" color="primary" class="mr-2 flex-shrink-0" />
                  <span>{{ feature }}</span>
                </li>
              </ul>
              <div class="planos-page__price">
                {{ formatPrice(plan.price) }}
                <span v-if="plan.months > 0" class="planos-page__price-unit">{{ getPriceUnit(plan.months) }}</span>
              </div>
              <p v-if="monthlyEquivalents[index]" class="planos-page__price-monthly">
                equivale a {{ monthlyEquivalents[index] }}/mês
              </p>
            </div>
            <div class="planos-page__card-actions">
              <v-btn
                v-if="isCurrentPlanActive(plan.id)"
                block
                size="large"
                variant="outlined"
                disabled
              >
                Plano atual
              </v-btn>
              <v-btn
                v-else
                type="button"
                color="primary"
                variant="flat"
                block
                size="large"
                rounded="lg"
                class="planos-page__assinar-btn"
                :loading="isMpLoading"
                :disabled="isMpLoading"
                @click.stop.prevent="handleMpSubscribe(plan.id)"
              >
                Assinar
              </v-btn>
            </div>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-planos {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-overflow-scrolling: touch;
  background: var(--color-bg-page);
  padding: 16px 16px 32px;
}

.dashboard-planos__inner {
  max-width: 1100px;
  margin: 0 auto;
  width: 100%;
}

.planos-page__title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 8px 0;
  text-align: center;
}

.planos-page__subtitle {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin: 0 0 32px 0;
  text-align: center;
}

.planos-page__grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  align-items: stretch;
}

.planos-page__card {
  border-radius: 16px;
  border: 1px solid var(--color-border);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  transition: box-shadow 0.2s ease;
  min-width: 0;
  background: var(--color-surface);
}

.planos-page__card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.planos-page__card-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 100%;
  padding: 24px;
  min-width: 0;
}

.planos-page__card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.planos-page__card-icon {
  flex-shrink: 0;
}

.planos-page__badge {
  flex-shrink: 0;
}

.planos-page__card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 8px 0 4px 0;
}

.planos-page__card-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 16px 0;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.planos-page__card-body {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.planos-page__features {
  list-style: none;
  padding: 0 0 12px;
  margin: 0 0 12px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1 1 auto;
  min-height: 0;
}

.planos-page__feature {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.planos-page__feature .v-icon {
  margin-top: 2px;
  flex-shrink: 0;
}

.planos-page__feature span {
  min-width: 0;
  flex: 1;
}

.planos-page__card-actions {
  flex-shrink: 0;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
  position: relative;
  z-index: 5;
  isolation: isolate;
  pointer-events: auto;
}

.planos-page__assinar-btn {
  font-weight: 600;
  letter-spacing: 0.02em;
  min-height: 48px;
}

.planos-page__price {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.planos-page__price-unit {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.planos-page__price-monthly {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 4px 0 0;
  font-weight: 500;
}

.planos-page__loading {
  display: flex;
  justify-content: center;
  padding: 48px;
}

@media (min-width: 600px) {
  .planos-page__title {
    font-size: 32px;
  }

  .planos-page__grid {
    grid-template-columns: repeat(auto-fill, minmax(min(100%, 300px), 1fr));
  }

  .planos-page__features {
    max-height: min(280px, 45vh);
    overflow-y: auto;
    overflow-x: hidden;
    -webkit-overflow-scrolling: touch;
  }
}

@media (min-width: 900px) {
  .planos-page__grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    align-items: stretch;
  }

  .planos-page__features {
    max-height: min(320px, 40vh);
  }

  .dashboard-planos__inner {
    max-width: 1200px;
  }
}
</style>
