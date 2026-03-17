<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

const { $api } = useNuxtApp()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default
const { refetch: refetchUser } = useUser()

const plans = ref<Array<{ id: string; name: string; price: number; months: number; description: string }>>([])
const subscription = ref<{ planType: string; status: string; expiresAt: string | null } | null>(null)
const isLoading = ref(true)
const checkoutLoading = ref(false)
const selectedPlan = ref<string | null>(null)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const currentPlan = computed(() => authStore.getUser?.plan ?? 'FREE')

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    try {
      const result = await refetchUser()
      if (result.data) {
        const { mapApiUserToStoreUser } = await import('~/utils/mapUser')
        authStore.setUser(mapApiUserToStoreUser(result.data))
      }
    } catch {
      /* silencioso */
    }
  }

  try {
    const { listPlans } = await import('~/services/subscription/listPlans')
    plans.value = await listPlans($api)
  } catch (e) {
    toast?.error('Erro ao carregar planos.')
  } finally {
    isLoading.value = false
  }

  if (authStore.isLoggedIn) {
    try {
      const { getSubscription } = await import('~/services/subscription/me')
      subscription.value = await getSubscription($api)
    } catch {
      subscription.value = null
      toast?.error('Erro ao carregar informações da assinatura.')
    }
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

async function handleUpgradeClick(planId: string) {
  if (!authStore.isLoggedIn) {
    const checkoutUrl = `/planos/checkout?plano=${planId}`
    return navigateTo('/login?redirect=' + encodeURIComponent(checkoutUrl))
  }
  selectedPlan.value = planId
  checkoutLoading.value = true
  try {
    await navigateTo(`/planos/checkout?plano=${planId}`)
  } finally {
    checkoutLoading.value = false
  }
}
</script>

<template>
  <div class="planos-page">
    <div class="planos-page__container">
      <div class="planos-page__nav d-flex justify-space-between align-center mb-6">
        <v-btn variant="text" color="primary" to="/">
          <v-icon start>mdi-home</v-icon>
          Home
        </v-btn>
      </div>
      <h1 class="planos-page__title">
        Planos Grivy
      </h1>
      <p class="planos-page__subtitle">
        Escolha o plano ideal para organizar suas finanças
      </p>

      <section v-if="isLoggedIn && subscription" class="planos-page__current mb-8">
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

      <div v-else class="planos-page__grid">
        <v-card
          v-for="(plan, index) in paidPlans"
          :key="plan.id"
          class="planos-page__card"
          variant="outlined"
        >
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
          <v-card-title class="text-h6 planos-page__card-title">
            {{ plan.name }}
          </v-card-title>
          <v-card-subtitle v-if="plan.description" class="planos-page__card-desc">
            {{ plan.description }}
          </v-card-subtitle>
          <v-card-text>
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
          </v-card-text>
          <v-card-actions>
            <v-btn
              v-if="plan.id === currentPlan && subscription?.status === 'ACTIVE'"
              block
              size="large"
              variant="outlined"
              disabled
            >
              Plano atual
            </v-btn>
            <v-btn
              v-else
              block
              size="large"
              color="primary"
              variant="flat"
              :loading="checkoutLoading && selectedPlan === plan.id"
              @click="handleUpgradeClick(plan.id)"
            >
              Assinar
            </v-btn>
          </v-card-actions>
        </v-card>
      </div>

      <div v-if="!isLoggedIn" class="planos-page__cta mt-8">
        <p class="text-body-1 text-medium-emphasis">
          Faça login ou cadastre-se para assinar um plano.
        </p>
        <div class="d-flex gap-2 mt-2">
          <v-btn color="primary" variant="flat" :to="'/login?redirect=' + encodeURIComponent('/planos')">
            Entrar
          </v-btn>
          <v-btn variant="outlined" to="/register">
            Cadastrar
          </v-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.planos-page {
  min-height: 100vh;
  padding: 48px 24px;
  background: var(--color-bg-page);
}

.planos-page__container {
  max-width: 900px;
  margin: 0 auto;
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
}

.planos-page__card {
  border-radius: 16px;
  border: 1px solid var(--color-border);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.07);
  transition: box-shadow 0.2s ease;
}

.planos-page__card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.planos-page__card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 24px;
  padding-inline: 24px;
}

.planos-page__card-icon {
  flex-shrink: 0;
}

.planos-page__badge {
  flex-shrink: 0;
}

.planos-page__card-title {
  padding-inline: 24px;
  padding-block-end: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.planos-page__card-desc {
  padding-inline: 24px;
  margin-top: 4px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.planos-page__card :deep(.v-card-text) {
  padding: 0 24px 16px;
}

.planos-page__features {
  list-style: none;
  padding: 0 0 12px;
  margin: 0 0 12px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.planos-page__feature {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.4;
}

.planos-page__card :deep(.v-card-actions) {
  padding: 16px 24px 24px;
  flex-direction: column;
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
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}
</style>
