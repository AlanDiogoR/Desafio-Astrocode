<script setup lang="ts">
import { useMonthSelector } from '~/composables/useMonthSelector'

const props = withDefaults(
  defineProps<{
    categoryName: string
    percent: number
    show?: boolean
  }>(),
  { show: true },
)

const { selectedDate } = useMonthSelector()

const monthKey = computed(
  () =>
    `${selectedDate.value.getFullYear()}-${String(selectedDate.value.getMonth() + 1).padStart(2, '0')}`,
)

const storageKey = computed(() => `grivy-insight-dismissed-${monthKey.value}`)

const dismissed = ref(false)

function readDismissed(): boolean {
  if (!import.meta.client)
    return false
  return sessionStorage.getItem(storageKey.value) === 'true'
}

watch(
  storageKey,
  () => {
    dismissed.value = readDismissed()
  },
  { immediate: true },
)

const visible = computed(() => props.show && !dismissed.value)

function dismiss() {
  dismissed.value = true
  if (import.meta.client)
    sessionStorage.setItem(storageKey.value, 'true')
}
</script>

<template>
  <div
    v-if="visible"
    class="d-flex align-center gap-2 px-3 py-2 rounded-lg mb-2 flex-shrink-0"
    style="
      background: rgba(var(--v-theme-warning), 0.08);
      border-left: 3px solid rgb(var(--v-theme-warning));
      font-size: 0.78rem;
      line-height: 1.3;
    "
  >
    <v-icon icon="mdi-alert-circle-outline" color="warning" size="14" />
    <span class="text-medium-emphasis flex-grow-1 min-width-0">
      <strong>{{ categoryName }}</strong> representa {{ percent }}% das despesas deste mês.
    </span>
    <v-btn
      icon
      variant="text"
      size="x-small"
      density="compact"
      class="flex-shrink-0"
      aria-label="Dispensar aviso"
      @click="dismiss"
    >
      <v-icon icon="mdi-close" size="14" />
    </v-btn>
  </div>
</template>
