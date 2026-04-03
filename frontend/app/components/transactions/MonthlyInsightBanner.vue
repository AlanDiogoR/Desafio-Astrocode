<script setup lang="ts">
defineProps<{
  categoryName: string
  percentage: number
}>()
</script>

<template>
  <v-alert
    type="warning"
    variant="text"
    density="compact"
    class="mb-1 px-0"
    style="font-size: 0.78rem"
  >
    <template #prepend>
      <v-icon size="14" class="mr-1">
        mdi-alert-circle-outline
      </v-icon>
    </template>
    {{ categoryName }} representa {{ percentage }}% das despesas deste mês.
  </v-alert>
</template>
