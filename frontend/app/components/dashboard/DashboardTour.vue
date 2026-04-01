<script setup lang="ts">
const TOUR_KEY = 'grivy_dashboard_tour_done'

const open = ref(false)
const step = ref(0)

const steps = [
  {
    title: 'Visão em um só lugar',
    text: 'No Grivy você vê saldo, metas e transações lado a lado — sem planilhas espalhadas.',
    icon: 'mdi-view-dashboard-outline',
  },
  {
    title: 'Mês a mês, no seu ritmo',
    text: 'Use o seletor de mês para revisar o passado e planejar o próximo — o filtro fica salvo ao navegar.',
    icon: 'mdi-calendar-month',
  },
  {
    title: 'Três cliques para organizar',
    text: 'Conecte o banco ou crie conta, defina uma meta e lance um gasto. Pronto: seu painel reflete sua vida financeira.',
    icon: 'mdi-gesture-tap',
  },
]

onMounted(() => {
  if (!import.meta.client) return
  if (localStorage.getItem(TOUR_KEY) === '1') return
  open.value = true
})

function next() {
  if (step.value < steps.length - 1) {
    step.value += 1
  } else {
    finish()
  }
}

function skip() {
  finish()
}

function finish() {
  open.value = false
  if (import.meta.client) {
    localStorage.setItem(TOUR_KEY, '1')
  }
}
</script>

<template>
  <v-dialog v-model="open" max-width="440" persistent>
    <v-card rounded="xl">
      <v-card-text class="pa-6 pt-8">
        <div class="text-center mb-4">
          <v-avatar color="primary" size="56" class="mb-3">
            <v-icon :icon="steps[step]!.icon" color="white" size="32" />
          </v-avatar>
          <h3 class="text-h6 font-weight-bold mb-2">
            {{ steps[step]!.title }}
          </h3>
          <p class="text-body-2 text-medium-emphasis mb-0">
            {{ steps[step]!.text }}
          </p>
        </div>
        <v-progress-linear
          :model-value="((step + 1) / steps.length) * 100"
          color="primary"
          height="6"
          rounded
          class="mb-2"
        />
        <p class="text-caption text-center text-medium-emphasis">
          Passo {{ step + 1 }} de {{ steps.length }}
        </p>
      </v-card-text>
      <v-card-actions class="px-6 pb-6 flex-wrap ga-2">
        <v-btn variant="text" @click="skip">
          Pular
        </v-btn>
        <v-spacer />
        <v-btn color="primary" variant="flat" @click="next">
          {{ step < steps.length - 1 ? 'Próximo' : 'Entendi' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
