<script setup lang="ts">
const emit = defineEmits<{
  (e: 'add-transaction'): void
}>()
</script>

<template>
  <div
    class="transaction-list__empty d-flex flex-column align-center justify-center flex-grow-1"
  >
    <img
      src="/images/Ilustra.png"
      alt="Sem transações"
      class="transaction-list__empty-img"
    >
    <p class="text-body-1 text-medium-emphasis text-center mt-2" style="max-width: 260px;">
      Nenhuma transação neste período.
    </p>
    <v-btn
      variant="tonal"
      color="primary"
      size="small"
      prepend-icon="mdi-plus"
      class="mt-2"
      @click="emit('add-transaction')"
    >
      Nova transação
    </v-btn>
  </div>
</template>

<style scoped>
.transaction-list__empty {
  gap: 16px;
  min-height: 200px;
}

.transaction-list__empty-img {
  width: auto;
  height: auto;
}
</style>
