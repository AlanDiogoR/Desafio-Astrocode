<script setup lang="ts">
import AppButton from '~/components/ui/AppButton.vue'

defineProps<{
  hasFilters?: boolean
}>()

const emit = defineEmits<{
  'add-transaction': []
  'clear-filters': []
}>()
</script>

<template>
  <div
    class="transaction-list__empty d-flex flex-column align-center justify-center flex-grow-1 text-center py-10"
  >
    <v-icon icon="mdi-receipt-text-outline" size="56" color="primary" opacity="0.3" class="mb-4" />
    <p class="text-h6 font-weight-medium mb-2">
      Nenhuma transação encontrada
    </p>
    <p class="text-body-2 text-medium-emphasis mb-6" style="max-width: 280px">
      {{ hasFilters ? 'Tente ajustar os filtros.' : 'Adicione sua primeira transação usando o botão abaixo.' }}
    </p>
    <AppButton
      v-if="!hasFilters"
      color="primary"
      variant="tonal"
      prepend-icon="mdi-plus"
      :block="false"
      @click="emit('add-transaction')"
    >
      Adicionar transação
    </AppButton>
    <AppButton
      v-else
      variant="text"
      :block="false"
      @click="emit('clear-filters')"
    >
      Limpar filtros
    </AppButton>
  </div>
</template>

<style scoped>
.transaction-list__empty {
  gap: 0;
  min-height: 200px;
}
</style>
