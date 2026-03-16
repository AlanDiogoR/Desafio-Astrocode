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
  if (authStore.isLoggedIn) {
    navigateTo(`/planos/checkout?plano=${planId}`)
  }
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
          v-for="plan in plans"
          :key="plan.id"
          class="planos-page__card"
          :class="{ 'planos-page__card--pro': plan.id !== 'FREE' }"
          variant="outlined"
        >
          <v-card-title class="text-h6">
            {{ plan.name }}
          </v-card-title>
          <v-card-subtitle v-if="plan.description" class="mt-1">
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
              v-if="plan.id === 'FREE'"
              variant="outlined"
              color="primary"
              disabled
            >
              Plano atual
            </v-btn>
            <v-btn
              v-else-if="plan.id === currentPlan && subscription?.status === 'ACTIVE'"
              variant="outlined"
              disabled
            >
              Plano atual
            </v-btn>
            <v-btn
              v-else
              color="primary"
              :loading="checkoutLoading && selectedPlan === plan.id"
              @click="handleUpgradeClick(plan.id)"
            >
              {{ plan.id !== currentPlan ? 'Assinar' : 'Gerenciar' }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </div>

      <div v-if="!isLoggedIn" class="planos-page__cta mt-8">
        <p class="text-body-1 text-medium-emphasis">
          Faça login ou cadastre-se para assinar um plano.
        </p>
        <div class="d-flex gap-2 mt-2">
          <v-btn color="primary" :to="'/login?redirect=' + encodeURIComponent('/planos')">
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
  padding: 32px 24px 48px;
  background: linear-gradient(180deg, #f8fafc 0%, #e2e8f0 100%);
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
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.planos-page__card {
  transition: box-shadow 0.2s ease;
}

.planos-page__card--pro {
  border-color: #087f5b;
  border-width: 2px;
}

.planos-page__card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.planos-page__price {
  font-size: 24px;
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
}
</style>
