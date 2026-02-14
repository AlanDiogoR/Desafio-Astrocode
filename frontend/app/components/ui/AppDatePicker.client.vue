<script setup lang="ts">
import { VueDatePicker } from '@vuepic/vue-datepicker'
import { CalendarIcon, CrossCircledIcon } from '@radix-icons/vue'
import { PopoverContent, PopoverPortal, PopoverRoot, PopoverTrigger } from 'radix-vue'
import { ptBR } from 'date-fns/locale'
import { formatDate } from '~/utils/format'
import '@vuepic/vue-datepicker/dist/main.css'

interface Props {
  modelValue?: Date | null
  placeholder?: string
  errorText?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Data',
  errorText: '',
})

const modelValue = defineModel<Date | null>()
const isOpen = ref(false)

const displayText = computed(() =>
  modelValue.value ? formatDate(modelValue.value) : ''
)

const isFloating = computed(() => !!modelValue.value || isOpen.value)

function closePicker() {
  isOpen.value = false
}
</script>

<template>
  <div class="app-datepicker">
    <PopoverRoot v-model:open="isOpen">
      <PopoverTrigger as-child>
        <button
          type="button"
          class="app-datepicker__trigger"
          :class="{
            'app-datepicker__trigger--filled': !!modelValue,
            'app-datepicker__trigger--error': !!props.errorText,
          }"
          aria-haspopup="dialog"
          :aria-label="placeholder"
        >
          <span
            :class="{
              'app-datepicker__label': true,
              'app-datepicker__label--float': isFloating,
            }"
          >
            {{ placeholder }}
          </span>
          <span class="app-datepicker__value">
            <CalendarIcon class="app-datepicker__icon" />
            {{ displayText || placeholder }}
          </span>
        </button>
      </PopoverTrigger>
      <PopoverPortal>
        <PopoverContent
          class="app-datepicker__popover"
          side="bottom"
          align="start"
          :side-offset="5"
          :style="{ zIndex: 2500 }"
        >
          <VueDatePicker
            v-model="modelValue"
            :locale="ptBR"
            :formats="{ month: 'LLLL', input: 'dd/MM/yyyy' }"
            :enable-time-picker="false"
            auto-apply
            inline
            @update:model-value="closePicker"
          />
        </PopoverContent>
      </PopoverPortal>
    </PopoverRoot>
    <div v-if="props.errorText" class="app-datepicker__error">
      <CrossCircledIcon class="app-datepicker__error-icon" />
      <span class="app-datepicker__error-text">{{ props.errorText }}</span>
    </div>
  </div>
</template>

<style scoped>
.app-datepicker__trigger {
  position: relative;
  width: 100%;
  height: 56px;
  background: white;
  border: 1px solid #495057;
  border-radius: 16px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: left;
  cursor: pointer;
  outline: none;
  transition: border-color 0.2s;
}

.app-datepicker__trigger:focus-visible {
  outline: 2px solid #087f5b;
  outline-offset: 2px;
  border-color: #087f5b;
}

.app-datepicker__trigger[data-state="open"] {
  border-color: #087f5b;
}

.app-datepicker__trigger--error {
  border-color: #e03131;
}

.app-datepicker__label {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #6b7280;
  pointer-events: none;
  transition: all 0.2s ease;
  padding: 0 4px;
  background: white;
}

.app-datepicker__label--float {
  top: -8px;
  font-size: 12px;
  color: #087f5b;
  left: 14px;
  transform: none;
}

.app-datepicker__value {
  flex: 1;
  padding-top: 12px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  font-size: 16px;
  color: #1f2937;
  text-align: left;
  min-width: 0;
}

.app-datepicker__trigger:not(.app-datepicker__trigger--filled) .app-datepicker__value {
  color: #6b7280;
}

.app-datepicker__icon {
  width: 20px;
  height: 20px;
  color: #6b7280;
  flex-shrink: 0;
}

.app-datepicker__popover {
  background: white;
  border-radius: 16px;
  border: 1px solid #f3f4f6;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  padding: 8px;
}

.app-datepicker__error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 6px;
  color: #e03131;
}

.app-datepicker__error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.app-datepicker__error-text {
  font-size: 14px;
  font-weight: 500;
}
</style>

<style>
.app-datepicker :deep(.dp__menu) {
  border: none !important;
  box-shadow: none !important;
}

.app-datepicker :deep(.dp__main) {
  border: none;
}

.app-datepicker :deep(.dp__calendar_header) {
  font-weight: 600;
  color: #1f2937;
}

.app-datepicker :deep(.dp__calendar_item) {
  font-size: 14px;
}

.app-datepicker :deep(.dp__cell_inner) {
  border-radius: 8px;
}

.app-datepicker :deep(.dp__active_date) {
  background: #087f5b;
}

.app-datepicker :deep(.dp__today) {
  border-color: #087f5b;
}
</style>
