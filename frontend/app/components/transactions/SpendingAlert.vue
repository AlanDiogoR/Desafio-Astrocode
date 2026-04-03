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

const isVisible = computed(
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
    <v-alert
      v-if="isVisible"
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
      <span class="d-inline-flex align-center flex-wrap" style="gap: 4px;">
        {{ categoryName }} representa {{ percentage }}% dos gastos deste mês.
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
      </span>
    </v-alert>
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
