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
    rounded="md"
    density="compact"
    class="monthly-insight-banner mb-2"
    border="start"
  >
    <div class="d-flex align-start ga-1">
      <v-icon
        :icon="severity === 'warning' ? 'mdi-alert' : 'mdi-lightbulb-on-outline'"
        size="18"
        class="flex-shrink-0"
      />
      <span class="monthly-insight-banner__text">{{ text }}</span>
    </div>
  </v-alert>
</template>

<style scoped>
.monthly-insight-banner {
  font-size: 0.75rem;
  line-height: 1.35;
}

.monthly-insight-banner :deep(.v-alert__content) {
  padding-block: 4px;
  min-width: 0;
}

.monthly-insight-banner__text {
  font-size: inherit;
  line-height: inherit;
}
</style>
