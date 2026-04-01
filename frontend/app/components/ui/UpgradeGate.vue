<script setup lang="ts">
import AppButton from '~/components/ui/AppButton.vue'

withDefaults(
  defineProps<{
    icon?: string
    title: string
    description: string
    requiredPlan?: string
    benefits?: string[]
  }>(),
  {
    icon: 'mdi-lock-outline',
    requiredPlan: 'Plano Elite Anual',
  },
)

defineEmits<{
  retry: []
}>()

function goPlanos() {
  void navigateTo('/dashboard/planos')
}
</script>

<template>
  <div
    class="upgrade-gate d-flex flex-column align-center justify-center text-center pa-8"
    style="min-height: 55vh"
  >
    <v-icon :icon="icon" size="64" color="primary" opacity="0.35" class="mb-4" />

    <v-chip color="primary" variant="tonal" size="small" prepend-icon="mdi-crown" class="mb-4">
      {{ requiredPlan }}
    </v-chip>

    <h2 class="text-h6 font-weight-bold mb-2">
      {{ title }}
    </h2>

    <p class="text-body-2 text-medium-emphasis mb-6" style="max-width: 340px">
      {{ description }}
    </p>

    <v-card
      v-if="benefits?.length"
      variant="tonal"
      color="primary"
      rounded="xl"
      class="mb-6 text-left"
      min-width="280"
      max-width="100%"
    >
      <v-card-text class="pa-4">
        <p class="text-caption font-weight-bold mb-3 text-uppercase">
          O que você vai desbloquear
        </p>
        <div
          v-for="benefit in benefits"
          :key="benefit"
          class="d-flex align-center gap-2 mb-2"
        >
          <v-icon icon="mdi-check-circle" color="primary" size="16" />
          <span class="text-body-2">{{ benefit }}</span>
        </div>
      </v-card-text>
    </v-card>

    <AppButton color="primary" size="large" @click="goPlanos">
      Ver planos e preços
    </AppButton>

    <p class="text-caption text-medium-emphasis mt-3">
      Já assinou?
      <a href="#" class="text-primary" @click.prevent="$emit('retry')">
        Clique aqui para atualizar
      </a>
    </p>
  </div>
</template>
