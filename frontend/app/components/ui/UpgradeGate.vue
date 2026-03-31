<script setup lang="ts">
import AppButton from '~/components/ui/AppButton.vue'

withDefaults(
  defineProps<{
    icon?: string
    title: string
    description: string
    requiredPlan?: string
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
    style="min-height: 60vh"
  >
    <div class="upgrade-gate__icon mb-4">
      <v-icon :icon="icon" size="64" color="primary" opacity="0.4" />
    </div>

    <h2 class="text-h6 font-weight-bold mb-2">
      {{ title }}
    </h2>
    <p class="text-medium-emphasis text-body-2 mb-6" style="max-width: 340px">
      {{ description }}
    </p>

    <v-chip
      color="primary"
      variant="tonal"
      size="small"
      class="mb-6"
      prepend-icon="mdi-crown"
    >
      Disponível no {{ requiredPlan }}
    </v-chip>

    <AppButton color="primary" @click="goPlanos">
      Ver planos
    </AppButton>

    <p class="text-caption text-medium-emphasis mt-4">
      Já assinou?
      <a href="#" class="text-primary" @click.prevent="$emit('retry')">
        Clique aqui para atualizar
      </a>
    </p>
  </div>
</template>
