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
      <p class="text-caption text-medium-emphasis mb-3">
        Controle financeiro pessoal
      </p>

      <h1 class="landing__headline">
        Chega de não saber para onde vai o seu dinheiro.
      </h1>

      <p class="landing__subheadline">
        Grivy é o app que organiza suas finanças em minutos — sem planilhas, sem complicação. 🚀
      </p>

      <div class="d-flex ga-3 justify-center flex-wrap mb-12">
        <v-btn
          color="primary"
          size="large"
          rounded="pill"
          elevation="2"
          @click="navigateTo('/register')"
        >
          Começar agora, é grátis →
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
            <div class="d-flex flex-wrap gap-2 justify-end" style="max-width:180px">
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
          Problema → clareza → ação
        </h2>
        <p class="landing__section-sub">
          Você perde dinheiro sem perceber (problema). O Grivy mostra categorias e saldo em um painel (clareza).
          Você ajusta hábitos com dados reais (ação). Mais de 10 mil pessoas já usam apps como o nosso para ver o mês com precisão — comece grátis hoje.
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
              <div class="mt-4 mb-2">
                <!-- Barras do gráfico -->
                <div style="display: flex; align-items: flex-end; gap: 4px; height: 72px; padding: 0 2px;">
                  <div
                    v-for="(val, i) in [35, 42, 38, 55, 62, 58, 70, 68, 78, 82, 90, 100]"
                    :key="i"
                    :style="{
                      flex: 1,
                      height: val + '%',
                      minHeight: '6px',
                      borderRadius: '3px 3px 0 0',
                      background: 'rgb(var(--v-theme-primary))',
                      opacity: String(0.3 + i * 0.06),
                    }"
                  />
                </div>
                <!-- Linha de base -->
                <div style="border-top: 1px solid rgba(0,0,0,0.08); margin: 0 2px;" />
                <!-- Labels -->
                <div style="display: flex; justify-content: space-between; padding: 4px 2px 0;">
                  <span style="font-size: 0.7rem; color: rgba(var(--v-theme-on-surface), 0.5)">Jan</span>
                  <span style="font-size: 0.7rem; color: rgba(var(--v-theme-on-surface), 0.5)">Jun</span>
                  <span style="font-size: 0.7rem; color: rgba(var(--v-theme-on-surface), 0.5)">Dez</span>
                </div>
              </div>

              <v-chip color="success" variant="flat" size="small" class="mt-2">
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
                <div class="d-flex justify-space-between align-center mb-1">
                  <span class="text-caption font-weight-medium text-truncate" style="max-width: 140px">
                    {{ goal.name }}
                  </span>
                  <span class="text-caption text-primary font-weight-bold ml-2 flex-shrink-0">
                    {{ Math.round((goal.current / goal.target) * 100) }}%
                  </span>
                </div>
                <v-progress-linear
                  :model-value="Math.round((goal.current / goal.target) * 100)"
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
          Plano Pro com 30% OFF para os primeiros 100 usuários (promoção sujeita a alteração — veja valores em /planos).
          Teste 14 dias grátis. Cancele quando quiser. Sem cartão de crédito para começar.
        </p>
      </div>

      <div class="plans-grid mx-auto" style="max-width: 960px">
        <div
          v-for="plan in plans"
          :key="plan.id"
          class="plan-card-wrapper"
        >
          <!-- Badge popular — só para ANNUAL -->
          <div v-if="plan.id === 'ANNUAL'" class="plan-badge">
            <v-chip color="primary" size="x-small" prepend-icon="mdi-fire">
              Mais popular
            </v-chip>
          </div>

          <v-card
            rounded="xl"
            height="100%"
            :style="plan.id === 'ANNUAL'
              ? 'border: 2px solid rgb(var(--v-theme-primary));'
              : 'border: 1px solid rgba(var(--v-theme-on-surface), 0.1);'"
            flat
          >
            <v-card-text class="pa-5 d-flex flex-column" style="height: 100%">
              <div>
                <div class="d-flex align-center gap-2 mb-1">
                  <span class="text-overline text-medium-emphasis" style="line-height:1">
                    {{ plan.name }}
                  </span>
                  <v-chip v-if="plan.badge" :color="plan.badgeColor" size="x-small">
                    {{ plan.badge }}
                  </v-chip>
                </div>

                <p class="text-h4 font-weight-black mb-0">
                  {{ plan.priceLabel }}
                </p>
                <p class="text-caption text-medium-emphasis mb-4">
                  {{ plan.period }}
                </p>

                <v-divider class="mb-4" />

                <div
                  v-for="f in plan.features"
                  :key="f.text"
                  class="d-flex align-center gap-2 mb-2"
                >
                  <v-icon
                    :icon="f.included ? 'mdi-check-circle' : 'mdi-minus-circle-outline'"
                    :color="f.included ? 'success' : 'disabled'"
                    size="16"
                  />
                  <span
                    class="text-body-2"
                    :style="f.included ? '' : 'opacity: 0.45'"
                  >{{ f.text }}</span>
                </div>
              </div>

              <div class="mt-auto pt-5">
                <v-btn
                  :color="plan.id === 'ANNUAL' ? 'primary' : 'default'"
                  :variant="plan.id === 'ANNUAL' ? 'flat' : 'outlined'"
                  block
                  rounded="pill"
                  @click="navigateTo('/register')"
                >
                  {{ plan.cta }}
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </div>
      </div>
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

    <a
      class="landing__whatsapp-fab"
      href="https://wa.me/5511999999999?text=Oi%2C%20quero%20conhecer%20o%20Grivy!"
      target="_blank"
      rel="noopener"
      aria-label="Falar com a Grivy no WhatsApp"
    >
      <svg viewBox="0 0 24 24" width="28" height="28" fill="white">
        <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z" />
      </svg>
    </a>

    <footer class="landing__footer">
      <div class="d-flex align-center justify-space-between flex-wrap ga-4">
        <AppLogo />
        <div class="d-flex ga-4 flex-wrap">
          <NuxtLink to="/login" class="landing__footer-link">
            Entrar
          </NuxtLink>
          <NuxtLink to="/register" class="landing__footer-link">
            Criar conta
          </NuxtLink>
          <NuxtLink to="/dashboard/planos" class="landing__footer-link">
            Planos
          </NuxtLink>
          <NuxtLink to="/politica-de-privacidade" class="landing__footer-link">
            Privacidade
          </NuxtLink>
          <NuxtLink to="/termos-de-servico" class="landing__footer-link">
            Termos
          </NuxtLink>
          <NuxtLink to="/exclusao-de-dados" class="landing__footer-link">
            Exclusão de Dados
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
  { emoji: '⚡', title: 'Contas e Saldos', sub: 'Corrente, poupança, dinheiro em um só lugar' },
  { emoji: '💳', title: 'Cartões de Crédito', sub: 'Faturas e transações com clareza' },
  { emoji: '🎯', title: 'Metas de Economia', sub: 'Objetivos mensuráveis e progresso' },
  { emoji: '📊', title: 'Relatórios Mensais', sub: 'Receitas, despesas e balanço do mês' },
]

const stats = [
  { value: '10k+', label: 'Usuários ativos (referência)' },
  { value: '100%', label: 'Dados isolados por conta' },
  { value: '50+', label: 'Instituições (Open Finance Elite)' },
  { value: '14 dias', label: 'Teste grátis do Pro · cancele quando quiser' },
]

const previewCategories = [
  { name: 'Moradia', value: 'R$ 2.4k', pct: 80, color: 'primary' },
  { name: 'Alimentação', value: 'R$ 1.8k', pct: 60, color: 'success' },
  { name: 'Transporte', value: 'R$ 890', pct: 30, color: 'warning' },
  { name: 'Lazer', value: 'R$ 1.2k', pct: 40, color: 'error' },
]

const previewGoals = [
  { name: 'Viagem Europa', current: 6800, target: 10000 },
  { name: 'Reserva emergência', current: 4200, target: 10000 },
  { name: 'Moto', current: 25000, target: 25000 },
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
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-width: 700px;
  margin: 0 auto;
}

@media (min-width: 600px) {
  .landing__data-cards {
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
  }
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

@media (max-width: 600px) {
  .landing__hero {
    padding: 48px 16px 40px;
  }

  .landing__headline {
    font-size: 1.8rem;
    letter-spacing: -0.5px;
  }

  .landing__subheadline {
    font-size: 0.95rem;
  }

  .landing__section {
    padding: 48px 16px;
  }

  .landing__section--alt {
    padding: 48px 16px;
  }

  .landing__stats {
    gap: 24px;
  }

  .landing__nav-inner {
    padding: 12px 16px;
  }

  .landing__cta {
    padding: 48px 16px;
  }

  .landing__footer {
    padding: 20px 16px;
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

.landing__whatsapp-fab {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 200;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #25D366;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-decoration: none;
  cursor: pointer;
}

.landing__whatsapp-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(37, 211, 102, 0.45);
}

@media (max-width: 600px) {
  .landing__whatsapp-fab {
    bottom: 16px;
    right: 16px;
    width: 48px;
    height: 48px;
  }

  .landing__whatsapp-fab svg {
    width: 24px;
    height: 24px;
  }
}

/* Grid de planos */
.plans-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  padding: 0 8px;
}

@media (min-width: 600px) {
  .plans-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 960px) {
  .plans-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* Wrapper do card com espaço para o badge */
.plan-card-wrapper {
  position: relative;
  padding-top: 20px;
  display: flex;
  flex-direction: column;
}

/* Badge "Mais popular" */
.plan-badge {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 1;
}
</style>
