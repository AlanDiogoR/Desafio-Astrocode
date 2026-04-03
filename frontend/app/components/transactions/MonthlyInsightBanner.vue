<script setup lang="ts">
withDefaults(
  defineProps<{
    categoryName: string
    percent: number
    show?: boolean
  }>(),
  { show: true },
)
</script>

<template>
  <div
    v-if="show"
    class="d-flex align-center gap-2 px-3 py-2 rounded-lg mb-2 flex-shrink-0"
    style="
      background: rgba(var(--v-theme-warning), 0.08);
      border-left: 3px solid rgb(var(--v-theme-warning));
      font-size: 0.78rem;
      line-height: 1.3;
    "
  >
    <v-icon icon="mdi-alert-circle-outline" color="warning" size="14" />
    <span class="text-medium-emphasis">
      <strong>{{ categoryName }}</strong> representa {{ percent }}% das despesas deste mês.
    </span>
  </div>
</template>
