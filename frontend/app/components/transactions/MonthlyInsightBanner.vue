<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  categoryName: string
  percentage: number
}>()

const severity = computed(() => {
  if (props.percentage >= 80) return 'warning' as const
  if (props.percentage >= 35) return 'info' as const
  return null
})

const text = computed(() => {
  if (props.percentage >= 80) {
    return `Atenção: você já direcionou cerca de ${props.percentage}% das despesas do mês para ${props.categoryName}. Vale revisar antes de fechar o mês.`
  }
  if (props.percentage >= 35) {
    return `Insight do mês: ${props.categoryName} concentra ${props.percentage}% do que você gastou — acompanhe para não extrapolar o “teto” desta categoria.`
  }
  return ''
})
</script>

<template>
  <v-alert
    v-if="severity && text"
    :type="severity"
    variant="tonal"
    rounded="lg"
    density="comfortable"
    class="mb-3"
    border="start"
  >
    <div class="d-flex align-start gap-2">
      <v-icon :icon="severity === 'warning' ? 'mdi-alert' : 'mdi-lightbulb-on-outline'" class="mt-1 flex-shrink-0" />
      <span class="text-body-2">{{ text }}</span>
    </div>
  </v-alert>
</template>
