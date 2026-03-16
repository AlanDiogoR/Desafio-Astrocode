<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

const { $api } = useNuxtApp()
const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const plans = ref<Array<{ id: string; name: string; price: number; months: number; description: string }>>([])
const subscription = ref<{ planType: string; status: string; expiresAt: string | null } | null>(null)
const isLoading = ref(true)
const checkoutLoading = ref(false)
const selectedPlan = ref<string | null>(null)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const currentPlan = computed(() => authStore.getUser?.plan ?? 'FREE')

onMounted(async () => {
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
    }
  }
})

const paidPlans = computed(() => plans.value.filter((p) => p.id !== 'FREE'))

function formatPrice(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
  }).format(value)
}

async function handleUpgradeClick(planId: string) {
  if (!authStore.isLoggedIn) {
    return navigateTo('/login?redirect=' + encodeURIComponent('/planos'))
  }
  selectedPlan.value = planId
  await navigateTo(`/planos/checkout?plano=${planId}`)
}
</script>

<template>
  <div class="planos-page">
    <div class="planos-page__container">
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
          v-for="plan in paidPlans"
          :key="plan.id"
          class="planos-page__card"
          variant="outlined"
        >
          <div class="planos-page__card-header">
            <v-icon icon="mdi-star-four-points" size="32" color="#087f5b" class="planos-page__card-icon" />
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
            <div class="planos-page__price">
              {{ formatPrice(plan.price) }}
              <span v-if="plan.months > 0" class="planos-page__price-unit">/mês</span>
            </div>
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
  background: #f8faf9;
}

.planos-page__container {
  max-width: 900px;
  margin: 0 auto;
}

.planos-page__title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
  text-align: center;
}

.planos-page__subtitle {
  font-size: 16px;
  color: #64748b;
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
  border: 1px solid #e2e8f0;
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
  padding-inline: 24px 24px 0;
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.planos-page__card-desc {
  padding-inline: 24px;
  margin-top: 4px;
  font-size: 14px;
  color: #64748b;
}

.planos-page__card :deep(.v-card-text) {
  padding: 16px 24px;
}

.planos-page__card :deep(.v-card-actions) {
  padding: 16px 24px 24px;
  flex-direction: column;
}

.planos-page__price {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
}

.planos-page__price-unit {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
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
