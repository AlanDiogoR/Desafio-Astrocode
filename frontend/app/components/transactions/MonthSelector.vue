<script setup lang="ts">
export interface MonthQuickItem {
  label: string
  value: string
  year: number
  month: number
}

defineProps<{
  formattedMonth: string
  lastTwelveMonths: MonthQuickItem[]
  currentMonthKey: string
  isNextDisabled: boolean
  onPrev: () => void
  onNext: () => void
  onSelectMonthKey: (value: string) => void
}>()

const showDatePicker = ref(false)
</script>

<template>
  <div class="month-selector d-flex align-center gap-2">
    <v-btn
      icon="mdi-chevron-left"
      variant="text"
      size="small"
      class="month-nav"
      @click="onPrev"
    />
    <v-menu v-model="showDatePicker" :close-on-content-click="true">
      <template #activator="{ props: menuProps }">
        <v-btn
          v-bind="menuProps"
          variant="tonal"
          size="small"
          rounded="pill"
          class="month-selector__pill text-none"
        >
          {{ formattedMonth }}
          <v-icon end size="16">
            mdi-chevron-down
          </v-icon>
        </v-btn>
      </template>
      <v-card min-width="260" rounded="xl">
        <v-card-text class="pa-2">
          <v-list density="compact" nav>
            <v-list-item
              v-for="m in lastTwelveMonths"
              :key="m.value"
              :title="m.label"
              :active="m.value === currentMonthKey"
              color="primary"
              rounded="lg"
              @click="onSelectMonthKey(m.value); showDatePicker = false"
            />
          </v-list>
        </v-card-text>
      </v-card>
    </v-menu>
    <v-btn
      icon="mdi-chevron-right"
      variant="text"
      size="small"
      class="month-nav"
      :disabled="isNextDisabled"
      @click="onNext"
    />
  </div>
</template>

<style scoped>
.month-selector {
  padding: 12px 0 16px;
}

.month-nav {
  color: #868e96;
}

.month-selector__pill {
  max-width: min(100%, 220px);
  text-transform: capitalize;
}
</style>
