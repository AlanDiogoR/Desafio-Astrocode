<script setup lang="ts">
import type { DisplayedMonth } from '~/composables/useMonthSelector'

defineProps<{
  displayedMonths: DisplayedMonth[]
  onPrev: () => void
  onNext: () => void
  onSelectMonth: (date: Date) => void
}>()
</script>

<template>
  <div class="month-selector d-flex align-center">
    <v-btn
      icon
      variant="text"
      size="x-small"
      class="month-nav"
      color="grey"
      @click="onPrev"
    >
      <v-icon icon="mdi-chevron-left" size="20" />
    </v-btn>
    <div class="month-selector__months">
      <button
        v-for="(month, index) in displayedMonths"
        :key="index"
        type="button"
        class="month-btn"
        :class="{ 'month-btn--current': month.isCurrent }"
        @click="onSelectMonth(month.date)"
      >
        {{ month.label }}
      </button>
    </div>
    <v-btn
      icon
      variant="text"
      size="x-small"
      class="month-nav"
      color="grey"
      @click="onNext"
    >
      <v-icon icon="mdi-chevron-right" size="20" />
    </v-btn>
  </div>
</template>

<style scoped>
.month-selector {
  padding: 12px 0 16px;
  gap: 16px;
}

.month-nav {
  color: #868e96;
}

.month-selector__months {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.month-btn {
  padding: 8px 16px;
  border: none;
  background: none;
  font-size: 14px;
  font-weight: 500;
  color: #868e96;
  cursor: pointer;
  border-radius: 8px;
  transition: color 0.2s, font-weight 0.2s, background-color 0.2s, box-shadow 0.2s;
}

.month-btn:hover {
  color: #495057;
}

.month-btn--current {
  font-weight: 700;
  color: #212529;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}
</style>
