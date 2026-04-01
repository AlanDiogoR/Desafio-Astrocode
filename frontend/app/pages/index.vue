<script setup lang="ts">
definePageMeta({
  layout: 'landing',
})

const features = [
  {
    icon: 'mdi-bank-outline',
    title: 'Múltiplas contas',
    description: 'Gerencie corrente, poupança e dinheiro em caixa tudo em um único dashboard.',
  },
  {
    icon: 'mdi-swap-horizontal',
    title: 'Transações organizadas',
    description: 'Filtre por mês, categoria ou conta. Veja exatamente para onde vai seu dinheiro.',
  },
  {
    icon: 'mdi-target',
    title: 'Metas de economia',
    description: 'Defina objetivos, contribua aos poucos e acompanhe o progresso em tempo real.',
  },
  {
    icon: 'mdi-credit-card-outline',
    title: 'Cartões de crédito',
    description: 'Acompanhe faturas e gastos nos seus cartões sem surpresas no fim do mês.',
  },
  {
    icon: 'mdi-bank-transfer',
    title: 'Open Finance',
    description: 'Conecte todos os seus bancos automaticamente e importe transações sem digitar nada.',
  },
  {
    icon: 'mdi-shield-check-outline',
    title: 'Dados protegidos',
    description: 'JWT, criptografia BCrypt e isolamento total dos seus dados financeiros.',
  },
]

const faqs = [
  {
    question: 'O plano grátis tem alguma limitação?',
    answer:
      'Sim. No plano grátis você pode ter até 2 contas bancárias, 30 transações por mês e 2 metas de economia. Para uso ilimitado, qualquer plano Pro já resolve.',
  },
  {
    question: 'Como funciona o Open Finance?',
    answer:
      'Com o Open Finance (disponível no Elite Anual), você conecta seus bancos via Pluggy e suas transações são importadas automaticamente, sem precisar lançar nada manualmente.',
  },
  {
    question: 'Meus dados estão seguros?',
    answer:
      'Sim. Usamos JWT para autenticação, BCrypt para criptografia de senhas e todos os dados são isolados por usuário. Nenhum dado é compartilhado entre contas.',
  },
  {
    question: 'Posso cancelar a qualquer momento?',
    answer:
      'Sim. Não há fidelidade. Se cancelar, você mantém o acesso até o fim do período pago e depois volta automaticamente para o plano grátis.',
  },
  {
    question: 'Como faço o pagamento?',
    answer:
      'Aceitamos cartão de crédito e débito via Mercado Pago. O pagamento é processado de forma segura diretamente no Grivy, sem sair da plataforma.',
  },
]

const year = new Date().getFullYear()
</script>

<template>
  <div class="landing-page">
    <nav class="landing-nav d-flex align-center justify-space-between pa-4 pa-md-6 flex-wrap ga-3">
      <AppLogo color="primary" :size="32" />
      <div class="d-flex align-center gap-2 gap-md-3 flex-wrap">
        <AppButton variant="text" :block="false" @click="navigateTo('/login')">
          Entrar
        </AppButton>
        <AppButton color="primary" :block="false" @click="navigateTo('/register')">
          Começar grátis
        </AppButton>
      </div>
    </nav>

    <section class="landing-hero text-center py-16 px-4">
      <v-chip color="primary" variant="tonal" size="small" class="mb-6">
        ✨ Controle financeiro simplificado
      </v-chip>

      <h1 class="text-h3 text-md-h2 font-weight-bold mb-4" style="max-width: 700px; margin: 0 auto">
        Suas finanças organizadas,<br>
        <span style="color: rgb(var(--v-theme-primary))">do jeito que você merece</span>
      </h1>

      <p class="text-body-1 text-medium-emphasis mb-8" style="max-width: 520px; margin: 0 auto">
        Controle contas, transações e metas de economia em um só lugar.
        Comece gratuitamente e evolua quando quiser.
      </p>

      <div class="d-flex gap-3 justify-center flex-wrap mb-12">
        <AppButton color="primary" size="large" @click="navigateTo('/register')">
          Criar conta grátis
        </AppButton>
        <AppButton variant="outlined" size="large" @click="navigateTo('/login')">
          Já tenho conta
        </AppButton>
      </div>

      <v-card
        class="mx-auto"
        max-width="720"
        rounded="xl"
        elevation="8"
        style="overflow: hidden; border: 1px solid rgba(var(--v-theme-primary), 0.15)"
      >
        <v-card-text class="pa-6">
          <div class="d-flex justify-space-between align-center mb-4">
            <span class="text-body-2 text-medium-emphasis">Saldo total</span>
            <v-chip color="success" size="x-small">+12% este mês</v-chip>
          </div>
          <p class="text-h4 font-weight-bold mb-6">
            R$ 24.850,00
          </p>
          <v-divider class="mb-4" />
          <div class="d-flex gap-4 flex-wrap justify-center">
            <div class="flex-grow-1 text-center" style="min-width: 90px">
              <p class="text-caption text-medium-emphasis">
                Receitas
              </p>
              <p class="text-h6 text-success font-weight-bold">
                R$ 8.200
              </p>
            </div>
            <v-divider vertical class="d-none d-sm-block" />
            <div class="flex-grow-1 text-center" style="min-width: 90px">
              <p class="text-caption text-medium-emphasis">
                Despesas
              </p>
              <p class="text-h6 text-error font-weight-bold">
                R$ 3.450
              </p>
            </div>
            <v-divider vertical class="d-none d-sm-block" />
            <div class="flex-grow-1 text-center" style="min-width: 90px">
              <p class="text-caption text-medium-emphasis">
                Metas
              </p>
              <p class="text-h6 text-primary font-weight-bold">
                68%
              </p>
            </div>
          </div>
        </v-card-text>
      </v-card>
    </section>

    <section class="landing-features py-16 px-4">
      <div class="text-center mb-12">
        <h2 class="text-h4 font-weight-bold mb-3">
          Tudo que você precisa
        </h2>
        <p class="text-medium-emphasis">
          Ferramentas simples para decisões financeiras inteligentes
        </p>
      </div>

      <v-row justify="center" class="mx-auto" style="max-width: 900px">
        <v-col
          v-for="feature in features"
          :key="feature.title"
          cols="12"
          sm="6"
          md="4"
        >
          <v-card rounded="xl" variant="tonal" class="pa-2 h-100">
            <v-card-text>
              <v-icon :icon="feature.icon" color="primary" size="32" class="mb-3" />
              <h3 class="text-subtitle-1 font-weight-bold mb-2">
                {{ feature.title }}
              </h3>
              <p class="text-body-2 text-medium-emphasis">
                {{ feature.description }}
              </p>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section
      class="landing-pricing py-16 px-4"
      style="background: rgba(var(--v-theme-surface-variant), 0.4)"
    >
      <div class="text-center mb-12">
        <h2 class="text-h4 font-weight-bold mb-3">
          Planos para cada momento
        </h2>
        <p class="text-medium-emphasis">
          Comece grátis. Evolua quando fizer sentido.
        </p>
      </div>

      <v-row justify="center" class="mx-auto" style="max-width: 900px">
        <v-col cols="12" sm="6" md="3">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-overline text-medium-emphasis">
                Grátis
              </p>
              <p class="text-h4 font-weight-bold mb-1">
                R$ 0
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                Para sempre
              </p>
              <v-divider class="mb-4" />
              <ul class="text-body-2" style="list-style: none; padding: 0">
                <li class="mb-2">✅ 2 contas bancárias</li>
                <li class="mb-2">✅ 30 transações/mês</li>
                <li class="mb-2">✅ 2 metas de economia</li>
                <li class="mb-2 text-medium-emphasis">❌ Cartões de crédito</li>
                <li class="text-medium-emphasis">❌ Open Finance</li>
              </ul>
              <AppButton block variant="outlined" class="mt-6" @click="navigateTo('/register')">
                Começar grátis
              </AppButton>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <p class="text-overline text-medium-emphasis">
                Pro Mensal
              </p>
              <p class="text-h4 font-weight-bold mb-1">
                R$ 19,90
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                por mês
              </p>
              <v-divider class="mb-4" />
              <ul class="text-body-2" style="list-style: none; padding: 0">
                <li class="mb-2">✅ Tudo ilimitado</li>
                <li class="mb-2">✅ Cartões de crédito</li>
                <li class="mb-2">✅ Categorias ilimitadas</li>
                <li class="text-medium-emphasis">❌ Open Finance</li>
              </ul>
              <AppButton block color="primary" variant="outlined" class="mt-6" @click="navigateTo('/register')">
                Assinar
              </AppButton>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card rounded="xl" class="h-100">
            <v-card-text class="pa-5">
              <div class="d-flex align-center gap-2 mb-1">
                <p class="text-overline text-medium-emphasis mb-0">
                  Pro Semestral
                </p>
                <v-chip color="warning" size="x-small">16% OFF</v-chip>
              </div>
              <p class="text-h4 font-weight-bold mb-1">
                R$ 49,90
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                por 6 meses
              </p>
              <v-divider class="mb-4" />
              <ul class="text-body-2" style="list-style: none; padding: 0">
                <li class="mb-2">✅ Tudo ilimitado</li>
                <li class="mb-2">✅ Cartões de crédito</li>
                <li class="mb-2">✅ Categorias ilimitadas</li>
                <li class="text-medium-emphasis">❌ Open Finance</li>
              </ul>
              <AppButton block color="primary" variant="outlined" class="mt-6" @click="navigateTo('/register')">
                Assinar
              </AppButton>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card rounded="xl" class="h-100" style="border: 2px solid rgb(var(--v-theme-primary))">
            <v-card-text class="pa-5">
              <div class="d-flex align-center gap-2 mb-1 flex-wrap">
                <p class="text-overline text-medium-emphasis mb-0">
                  Elite Anual
                </p>
                <v-chip color="primary" size="x-small" prepend-icon="mdi-crown">24% OFF</v-chip>
              </div>
              <p class="text-h4 font-weight-bold mb-1">
                R$ 179,90
              </p>
              <p class="text-caption text-medium-emphasis mb-4">
                por 12 meses
              </p>
              <v-divider class="mb-4" />
              <ul class="text-body-2" style="list-style: none; padding: 0">
                <li class="mb-2">✅ Tudo ilimitado</li>
                <li class="mb-2">✅ Cartões de crédito</li>
                <li class="mb-2">✅ Open Finance</li>
                <li class="mb-2">✅ Importação automática</li>
                <li>✅ Suporte prioritário</li>
              </ul>
              <AppButton block color="primary" class="mt-6" @click="navigateTo('/register')">
                Começar agora
              </AppButton>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </section>

    <section class="landing-faq py-16 px-4 mx-auto" style="max-width: 700px">
      <h2 class="text-h4 font-weight-bold text-center mb-10">
        Perguntas frequentes
      </h2>

      <v-expansion-panels variant="accordion" rounded="xl">
        <v-expansion-panel
          v-for="faq in faqs"
          :key="faq.question"
          :title="faq.question"
          :text="faq.answer"
        />
      </v-expansion-panels>
    </section>

    <section class="landing-cta text-center py-16 px-4">
      <h2 class="text-h4 font-weight-bold mb-4">
        Pronto para começar?
      </h2>
      <p class="text-medium-emphasis mb-8">
        Crie sua conta gratuitamente e organize suas finanças hoje mesmo.
      </p>
      <AppButton color="primary" size="large" @click="navigateTo('/register')">
        Criar conta grátis
      </AppButton>
    </section>

    <footer class="landing-footer text-center pa-6 text-caption text-medium-emphasis">
      <p>© {{ year }} Grivy. Todos os direitos reservados.</p>
      <div class="d-flex justify-center gap-4 mt-2 flex-wrap">
        <NuxtLink to="/login" class="text-medium-emphasis text-decoration-none">
          Entrar
        </NuxtLink>
        <NuxtLink to="/register" class="text-medium-emphasis text-decoration-none">
          Criar conta
        </NuxtLink>
        <NuxtLink to="/planos" class="text-medium-emphasis text-decoration-none">
          Planos
        </NuxtLink>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.landing-nav {
  border-bottom: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}
</style>
