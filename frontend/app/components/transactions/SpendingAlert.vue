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
      class="spending-alert"
    >
      <LightningBoltIcon class="spending-alert__icon" aria-hidden />
      <div class="spending-alert__content flex-grow-1">
        <span class="spending-alert__text">
          Atenção: {{ percentage }}% dos seus gastos este mês foram em {{ categoryName }}.
        </span>
      </div>
      <button
        type="button"
        class="spending-alert__dismiss"
        aria-label="Dispensar alerta"
        @click="dismiss"
      >
        <Cross2Icon />
      </button>
    </div>
  </Transition>
</template>

<style scoped>
.spending-alert {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  background: #fff7ed;
  border: 1px solid #fed7aa;
  border-radius: 12px;
  margin-bottom: 12px;
}

.spending-alert__icon {
  width: 22px;
  height: 22px;
  color: #ea580c;
  flex-shrink: 0;
  margin-top: 2px;
}

.spending-alert__content {
  min-width: 0;
}

.spending-alert__text {
  font-size: 13px;
  color: #9a3412;
  line-height: 1.4;
}

.spending-alert__dismiss {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: #9a3412;
  border-radius: 6px;
  cursor: pointer;
  opacity: 0.7;
}

.spending-alert__dismiss:hover {
  opacity: 1;
  background: rgba(234, 88, 12, 0.1);
}

.spending-alert__dismiss svg {
  width: 14px;
  height: 14px;
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
