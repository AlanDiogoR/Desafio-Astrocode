<script setup lang="ts">
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
