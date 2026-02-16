<script setup lang="ts">
import { Cross2Icon, LightningBoltIcon } from '@radix-icons/vue'

const props = defineProps<{
  categoryName: string
  percentage: number
  visible: boolean
}>()

const emit = defineEmits<{
  dismiss: []
}>()

const dismissed = ref(false)

const isVisible = computed(
  () => props.visible && !dismissed.value && props.percentage > 40,
)

function dismiss() {
  dismissed.value = true
  emit('dismiss')
}
</script>

<template>
  <Transition name="slide-fade">
    <div
      v-if="isVisible"
      class="d-flex align-start ga-3 pa-3 mb-3 rounded-lg border spending-alert__bg"
    >
      <LightningBoltIcon
        class="flex-shrink-0 mt-1 spending-alert__icon"
        width="22"
        height="22"
        aria-hidden
      />
      <span class="flex-grow-1 min-width-0 text-body-2 spending-alert__text">
        Atenção: {{ percentage }}% dos seus gastos este mês foram em {{ categoryName }}.
      </span>
      <v-btn
        icon
        variant="text"
        size="small"
        density="compact"
        class="flex-shrink-0 spending-alert__dismiss"
        aria-label="Dispensar alerta"
        @click="dismiss"
      >
        <Cross2Icon width="14" height="14" />
      </v-btn>
    </div>
  </Transition>
</template>

<style scoped>
.spending-alert__bg {
  background-color: #fff7ed;
  border-color: #fed7aa;
}

.spending-alert__text {
  color: #9a3412;
}

.spending-alert__dismiss {
  color: #9a3412;
}

.spending-alert__dismiss:hover {
  background: rgba(234, 88, 12, 0.1) !important;
}

.spending-alert__icon {
  color: #ea580c;
}

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
