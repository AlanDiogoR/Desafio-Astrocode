<template>
  <div class="landing">
    <nav class="landing__nav">
      <div class="landing__nav-inner">
        <AppLogo style="cursor: pointer" @click="scrollToTop" />
        <div class="d-flex align-center ga-2">
          <v-btn variant="text" size="small" @click="navigateTo('/login')">
            Entrar
          </v-btn>
          <v-btn
            color="primary"
            size="small"
            rounded="pill"
            @click="navigateTo('/register')"
          >
            Começar grátis
          </v-btn>
        </div>
      </div>
    </nav>

    <section class="landing__hero">
      <v-chip color="primary" variant="tonal" size="small" class="mb-5">
        💰 Controle financeiro pessoal
      </v-chip>

      <h1 class="landing__headline">
        Todas suas finanças<br>
        <span class="text-primary">em um único lugar</span>
      </h1>

      <p class="landing__subheadline">
        Controle contas, transações e metas de economia com clareza.
        Comece gratuitamente e evolua quando quiser.
      </p>

      <div class="d-flex ga-3 justify-center flex-wrap mb-12">
        <v-btn
          color="primary"
          size="large"
          rounded="pill"
          elevation="2"
          @click="navigateTo('/register')"
        >
          Criar conta grátis
        </v-btn>
        <v-btn
          variant="outlined"
          size="large"
          rounded="pill"
          @click="navigateTo('/login')"
        >
          Já tenho conta
        </v-btn>
      </div>

      <v-card class="landing__preview mx-auto" rounded="xl" elevation="12">
        <v-card-text class="pa-5">
          <div class="d-flex align-center justify-space-between mb-4">
            <span class="text-caption text-medium-emphasis font-weight-medium">
              CONTAS ATIVAS
            </span>
            <v-chip color="success" size="x-small" variant="tonal">
              3 contas
            </v-chip>
          </div>

          <div
            v-for="account in previewAccounts"
            :key="account.name"
            class="d-flex align-center justify-space-between mb-3"
          >
            <div class="d-flex align-center ga-2">
              <v-avatar :color="account.color" size="32">
                <v-icon :icon="account.icon" size="16" color="white" />
              </v-avatar>
              <div>
                <p class="text-body-2 font-weight-medium" style="line-height: 1.2">
                  {{ account.name }}
                </p>
                <p class="text-caption text-medium-emphasis">
                  Atualizado: Hoje
                </p>
              </div>
            </div>
            <span class="text-body-2 font-weight-bold">
              {{ account.balance }}
            </span>
          </div>

          <v-divider class="my-4" />

          <div class="d-flex justify-space-between align-center">
            <div>
              <p class="text-caption text-medium-emphasis">
                Patrimônio Total
              </p>
              <p class="text-h6 font-weight-bold text-primary">
                R$ 25.576,80
              </p>
            </div>
            <div class="d-flex ga-2">
              <v-chip color="success" variant="flat" size="x-small">
                +R$ 8.200 este mês
              </v-chip>
              <v-chip color="error" variant="flat" size="x-small">
                -R$ 3.450
              </v-chip>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </section>

    <section class="landing__section">
      <div class="landing__section-header">
        <h2 class="landing__section-title">
          Seu controle financeiro. <span class="text-primary">Completo.</span>
        </h2>
        <p class="landing__section-sub">
          Como um painel central para suas finanças. Cadastre uma vez
          e tenha visibilidade total — saldos, transações, metas e cartões
          — tudo organizado do jeito que você precisa.
        </p>
      </div>

      <div class="landing__data-cards">
        <v-card
          v-for="d in dataCategories"
          :key="d.title"
          variant="tonal"
          rounded="xl"
          class="pa-1"
        >
          <v-card-text class="text-center pa-4">
            <div class="text-h4 mb-2">
              {{ d.emoji }}
            </div>
            <p class="text-subtitle-2 font-weight-bold mb-1">
              {{ d.title }}
            </p>
            <p class="text-caption text-medium-emphasis">
              {{ d.sub }}
            </p>
          </v-card-text>
        </v-card>
      </div>

      <div class="landing__stats mt-10">
        <div v-for="s in stats" :key="s.label" class="text-center">
          <p class="text-h4 font-weight-black text-primary">
            {{ s.value }}
          </p>
          <p class="text-caption text-medium-emphasis">
            {{ s.label }}
          </p>
        </div>
      </div>
    </section>

    <section class="landing__section landing__section--alt">
      <div class="landing__section-header">
        <h2 class="landing__section-title">
          O que o Grivy faz por você
        </h2>
        <p class="landing__section-sub">
          Clareza, controle e inteligência sobre suas finanças pessoais.
        </p>
      </div>

      <v-row justify="center" class="mx-auto" style="max-width: 900px">
        <v-col cols="12" sm="6">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-subtitle-1 font-weight-bold mb-1">
                Gastos por Categoria
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                Entenda para onde vai seu dinheiro com gráficos claros.
              </p>
              <div v-for="cat in previewCategories" :key="cat.name" class="mb-2">
                <div class="d-flex justify-space-between mb-1">
                  <span class="text-caption">{{ cat.name }}</span>
                  <span class="text-caption font-weight-bold">{{ cat.value }}</span>
                </div>
                <v-progress-linear
                  :model-value="cat.pct"
                  :color="cat.color"
                  rounded
                  height="6"
                  bg-color="rgba(0,0,0,0.06)"
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-subtitle-1 font-weight-bold mb-1">
                Evolução do Saldo
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                Acompanhe como seu patrimônio cresce mês a mês.
              </p>
              <div
                class="d-flex align-end justify-space-between ga-1 mt-6"
                style="height: 80px"
              >
                <div
                  v-for="(h, i) in balanceHistory"
                  :key="i"
                  class="flex-1 rounded-t"
                  :style="{
                    height: `${h}%`,
                    background: 'rgb(var(--v-theme-primary))',
                    opacity: 0.3 + i * 0.1,
                  }"
                />
              </div>
              <div class="d-flex justify-space-between mt-2">
                <span class="text-caption text-medium-emphasis">Jan</span>
                <span class="text-caption text-medium-emphasis">Jun</span>
              </div>
              <v-chip color="success" variant="flat" size="small" class="mt-3">
                +12,4% em 6 meses
              </v-chip>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-subtitle-1 font-weight-bold mb-1">
                Metas de Economia
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                Defina objetivos e acompanhe o progresso em tempo real.
              </p>
              <div v-for="goal in previewGoals" :key="goal.name" class="mb-3">
                <div class="d-flex justify-space-between mb-1">
                  <span class="text-caption font-weight-medium">{{ goal.name }}</span>
                  <span class="text-caption text-primary font-weight-bold">{{ goal.pct }}%</span>
                </div>
                <v-progress-linear
                  :model-value="goal.pct"
                  color="primary"
                  rounded
                  height="8"
                  bg-color="rgba(0,0,0,0.06)"
                />
              </div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-subtitle-1 font-weight-bold mb-1">
                Open Finance <v-chip color="primary" size="x-small" class="ml-1">Elite</v-chip>
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                Conecte todos os seus bancos e importe transações automaticamente.
              </p>
              <div class="d-flex flex-wrap ga-2 mt-2">
                <v-chip
                  v-for="bank in banks"
                  :key="bank"
                  size="small"
                  variant="tonal"
                >
                  {{ bank }}
                </v-chip>
              </div>
              <v-chip color="success" variant="tonal" size="small" class="mt-4">
                50+ instituições suportadas
              </v-chip>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="landing__section">
      <div class="landing__section-header">
        <h2 class="landing__section-title">
          Planos para cada momento
        </h2>
        <p class="landing__section-sub">
          Comece grátis. Evolua quando fizer sentido.
        </p>
      </div>

      <v-row justify="center" class="mx-auto" style="max-width: 960px">
        <v-col
          v-for="plan in plans"
          :key="plan.id"
          cols="12"
          sm="6"
          md="3"
        >
          <div
            v-if="plan.id === 'ANNUAL'"
            class="text-center mb-n3"
            style="position: relative; z-index: 1"
          >
            <v-chip color="primary" size="small" prepend-icon="mdi-fire">
              Mais popular
            </v-chip>
          </div>

          <v-card
            rounded="xl"
            class="h-100"
            :style="plan.id === 'ANNUAL' ? 'border: 2px solid rgb(var(--v-theme-primary))' : ''"
          >
            <v-card-text class="pa-5">
              <div class="d-flex align-center ga-2 mb-2">
                <p class="text-overline text-medium-emphasis">
                  {{ plan.name }}
                </p>
                <v-chip
                  v-if="plan.badge && plan.badgeColor"
                  :color="plan.badgeColor"
                  size="x-small"
                >
                  {{ plan.badge }}
                </v-chip>
              </div>

              <p class="text-h4 font-weight-bold">
                {{ plan.priceLabel }}
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                {{ plan.period }}
              </p>

              <v-divider class="mb-4" />

              <ul style="list-style: none; padding: 0; margin: 0">
                <li
                  v-for="f in plan.features"
                  :key="f.text"
                  class="d-flex align-center ga-2 mb-2 text-body-2"
                >
                  <v-icon
                    :icon="f.included ? 'mdi-check-circle' : 'mdi-close-circle'"
                    :color="f.included ? 'success' : 'medium-emphasis'"
                    size="16"
                  />
                  <span :class="f.included ? '' : 'text-medium-emphasis'">{{ f.text }}</span>
                </li>
              </ul>

              <v-btn
                :color="plan.id === 'ANNUAL' ? 'primary' : undefined"
                :variant="plan.id === 'ANNUAL' ? 'flat' : 'outlined'"
                block
                rounded="pill"
                class="mt-6"
                @click="navigateTo('/register')"
              >
                {{ plan.cta }}
              </v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="landing__section landing__section--alt">
      <div class="landing__section-header">
        <h2 class="landing__section-title">
          Perguntas frequentes
        </h2>
      </div>

      <v-expansion-panels
        variant="accordion"
        rounded="xl"
        class="mx-auto"
        style="max-width: 680px"
      >
        <v-expansion-panel
          v-for="faq in faqs"
          :key="faq.q"
          :title="faq.q"
          :text="faq.a"
        />
      </v-expansion-panels>
    </section>

    <section class="landing__cta">
      <h2 class="landing__section-title mb-4">
        Pronto para começar?
      </h2>
      <p class="landing__section-sub mb-8">
        Crie sua conta gratuitamente e organize suas finanças hoje mesmo.
      </p>
      <v-btn
        color="primary"
        size="large"
        rounded="pill"
        elevation="2"
        @click="navigateTo('/register')"
      >
        Criar conta grátis
      </v-btn>
    </section>

    <footer class="landing__footer">
      <div class="d-flex align-center justify-space-between flex-wrap ga-4">
        <AppLogo />
        <div class="d-flex ga-4">
          <NuxtLink to="/login" class="landing__footer-link">
            Entrar
          </NuxtLink>
          <NuxtLink to="/register" class="landing__footer-link">
            Criar conta
          </NuxtLink>
          <NuxtLink to="/dashboard/planos" class="landing__footer-link">
            Planos
          </NuxtLink>
        </div>
        <p class="text-caption text-medium-emphasis">
          © {{ currentYear }} Grivy. Todos os direitos reservados.
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'landing',
  middleware: [],
})

const authStore = useAuthStore()
const currentYear = new Date().getFullYear()

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  if (authStore.isLoggedIn)
    void navigateTo('/dashboard')
})

const previewAccounts = [
  { name: 'Nubank', icon: 'mdi-bank', color: '#8A05BE', balance: 'R$ 4.231,50' },
  { name: 'Inter', icon: 'mdi-bank', color: '#FF7A00', balance: 'R$ 12.890,00' },
  { name: 'Caixa', icon: 'mdi-bank', color: '#005CA9', balance: 'R$ 8.455,30' },
]

const dataCategories = [
  { emoji: '🏦', title: 'Contas e Saldos', sub: 'Corrente, poupança, dinheiro' },
  { emoji: '💳', title: 'Cartões de Crédito', sub: 'Faturas e transações' },
  { emoji: '🎯', title: 'Metas de Economia', sub: 'Objetivos e progresso' },
  { emoji: '📊', title: 'Relatórios Mensais', sub: 'Receitas, despesas e balanço' },
]

const stats = [
  { value: 'R$ 0', label: 'Para começar' },
  { value: '100%', label: 'Dados isolados' },
  { value: '50+', label: 'Bancos via Open Finance' },
  { value: '14 dias', label: 'Sessão segura JWT' },
]

const previewCategories = [
  { name: 'Moradia', value: 'R$ 2.4k', pct: 80, color: 'primary' },
  { name: 'Alimentação', value: 'R$ 1.8k', pct: 60, color: 'success' },
  { name: 'Transporte', value: 'R$ 890', pct: 30, color: 'warning' },
  { name: 'Lazer', value: 'R$ 1.2k', pct: 40, color: 'error' },
]

const balanceHistory = [40, 50, 45, 60, 70, 65, 80, 75, 85, 90, 95, 100]

const previewGoals = [
  { name: 'Viagem Europa', pct: 68 },
  { name: 'Reserva emergência', pct: 42 },
  { name: 'Notebook novo', pct: 85 },
]

const banks = ['Nubank', 'Inter', 'Itaú', 'Bradesco', 'Santander', 'Caixa', 'BB', 'XP']

const plans = [
  {
    id: 'FREE',
    name: 'Grátis',
    priceLabel: 'R$ 0',
    period: 'Para sempre',
    badge: null as string | null,
    badgeColor: null as string | null,
    cta: 'Começar grátis',
    features: [
      { text: '2 contas bancárias', included: true },
      { text: '30 transações/mês', included: true },
      { text: '2 metas de economia', included: true },
      { text: 'Cartões de crédito', included: false },
      { text: 'Open Finance', included: false },
    ],
  },
  {
    id: 'MONTHLY',
    name: 'Pro Mensal',
    priceLabel: 'R$ 19,90',
    period: 'por mês',
    badge: null as string | null,
    badgeColor: null as string | null,
    cta: 'Assinar',
    features: [
      { text: 'Tudo ilimitado', included: true },
      { text: 'Cartões de crédito', included: true },
      { text: 'Transações ilimitadas', included: true },
      { text: 'Metas ilimitadas', included: true },
      { text: 'Open Finance', included: false },
    ],
  },
  {
    id: 'SEMIANNUAL',
    name: 'Pro Semestral',
    priceLabel: 'R$ 49,90',
    period: 'por 6 meses',
    badge: '16% OFF',
    badgeColor: 'warning',
    cta: 'Assinar',
    features: [
      { text: 'Tudo ilimitado', included: true },
      { text: 'Cartões de crédito', included: true },
      { text: 'Transações ilimitadas', included: true },
      { text: 'Metas ilimitadas', included: true },
      { text: 'Open Finance', included: false },
    ],
  },
  {
    id: 'ANNUAL',
    name: 'Elite Anual',
    priceLabel: 'R$ 179,90',
    period: 'por 12 meses',
    badge: '24% OFF',
    badgeColor: 'primary',
    cta: 'Começar agora',
    features: [
      { text: 'Tudo ilimitado', included: true },
      { text: 'Cartões de crédito', included: true },
      { text: 'Open Finance', included: true },
      { text: 'Importação automática', included: true },
      { text: 'Suporte prioritário', included: true },
    ],
  },
]

const faqs = [
  {
    q: 'O plano grátis tem limitações?',
    a: 'Sim. No plano grátis você tem até 2 contas, 30 transações/mês e 2 metas. Para uso ilimitado, qualquer plano Pro resolve.',
  },
  {
    q: 'O que é Open Finance?',
    a: 'Com o Open Finance (Elite Anual) você conecta seus bancos via Pluggy e as transações são importadas automaticamente, sem digitar nada manualmente.',
  },
  {
    q: 'Meus dados estão seguros?',
    a: 'Sim. Usamos JWT para autenticação, BCrypt para senhas e todos os dados são completamente isolados por usuário. Nenhuma informação é compartilhada entre contas.',
  },
  {
    q: 'Posso cancelar a qualquer momento?',
    a: 'Sim. Sem fidelidade. Se cancelar, o acesso continua até o fim do período pago e depois volta automaticamente para o plano grátis.',
  },
  {
    q: 'Como funciona o pagamento?',
    a: 'Aceitamos cartão de crédito e débito via Mercado Pago. O pagamento é processado de forma segura diretamente no Grivy, sem sair da plataforma.',
  },
  {
    q: 'Existe aplicativo mobile?',
    a: 'O Grivy é um web app responsivo que funciona muito bem no celular pelo navegador. Um app nativo está nos planos futuros.',
  },
]
</script>

<style scoped>
.landing {
  min-height: 100vh;
  background: rgb(var(--v-theme-background));
}

.landing__nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(var(--v-theme-background), 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(var(--v-theme-on-surface), 0.06);
}

.landing__nav-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 14px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.landing__hero {
  text-align: center;
  padding: 80px 24px 64px;
  max-width: 800px;
  margin: 0 auto;
}

.landing__headline {
  font-size: clamp(2rem, 5vw, 3.2rem);
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -1px;
  margin-bottom: 20px;
}

.landing__subheadline {
  font-size: 1.1rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
  max-width: 500px;
  margin: 0 auto 36px;
  line-height: 1.7;
}

.landing__preview {
  max-width: 540px;
  border: 1px solid rgba(var(--v-theme-primary), 0.15);
  text-align: left;
}

.landing__section {
  padding: 80px 24px;
  max-width: 1100px;
  margin: 0 auto;
}

.landing__section--alt {
  background: rgba(var(--v-theme-surface-variant), 0.35);
  max-width: 100%;
  padding: 80px 24px;
}

.landing__section--alt > * {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}

.landing__section-header {
  text-align: center;
  margin-bottom: 48px;
}

.landing__section-title {
  font-size: clamp(1.6rem, 3.5vw, 2.2rem);
  font-weight: 800;
  letter-spacing: -0.5px;
  margin-bottom: 12px;
}

.landing__section-sub {
  font-size: 1rem;
  color: rgba(var(--v-theme-on-surface), 0.6);
  max-width: 500px;
  margin: 0 auto;
  line-height: 1.7;
}

.landing__data-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.landing__stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  flex-wrap: wrap;
}

.landing__cta {
  text-align: center;
  padding: 80px 24px;
  background: rgba(var(--v-theme-primary), 0.04);
}

.landing__footer {
  border-top: 1px solid rgba(var(--v-theme-on-surface), 0.08);
  padding: 24px 32px;
  max-width: 1100px;
  margin: 0 auto;
}

.landing__footer-link {
  color: rgba(var(--v-theme-on-surface), 0.5);
  text-decoration: none;
  font-size: 0.85rem;
  transition: color 0.2s;
}

.landing__footer-link:hover {
  color: rgb(var(--v-theme-primary));
}
</style>
