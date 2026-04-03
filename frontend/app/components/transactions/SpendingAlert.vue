<script setup lang="ts">
const props = defineProps<{
  categoryName: string
  percentage: number
  visible: boolean
  currentMonth?: number
  currentYear?: number
}>()

const emit = defineEmits<{
  dismiss: []
}>()

const now = new Date()
const currentMonth = computed(() => props.currentMonth ?? now.getMonth() + 1)
const currentYear = computed(() => props.currentYear ?? now.getFullYear())
const storageKey = computed(() => `spending-alert-dismissed-${currentYear.value}-${currentMonth.value}`)
const dismissed = ref(
  typeof sessionStorage !== 'undefined' && sessionStorage.getItem(storageKey.value) === 'true',
)

const show = computed(
  () => props.visible && !dismissed.value && props.percentage > 40,
)

function dismiss() {
  dismissed.value = true
  if (typeof sessionStorage !== 'undefined') {
    sessionStorage.setItem(storageKey.value, 'true')
  }
  emit('dismiss')
}
</script>

<template>
  <Transition name="slide-fade">
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
      <span class="text-medium-emphasis flex-grow-1 min-width-0">
        <strong>{{ categoryName }}</strong> representa {{ percentage }}% dos gastos deste mês.
      </span>
      <v-btn
        icon
        variant="text"
        size="x-small"
        density="compact"
        class="flex-shrink-0"
        aria-label="Dispensar alerta"
        @click="dismiss"
      >
        <v-icon icon="mdi-close" size="14" />
      </v-btn>
    </div>
  </Transition>
</template>

<style scoped>
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.2s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
