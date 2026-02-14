<script setup lang="ts">
import { ChevronDownIcon, CheckIcon, CrossCircledIcon } from '@radix-icons/vue'
import {
  SelectContent,
  SelectItem,
  SelectItemIndicator,
  SelectItemText,
  SelectPortal,
  SelectRoot,
  SelectTrigger,
  SelectViewport,
} from 'radix-vue'

interface SelectOption {
  label: string
  value: string
}

interface Props {
  modelValue?: string | null
  options: SelectOption[]
  label?: string
  placeholder?: string
  errorText?: string
}

const props = withDefaults(defineProps<Props>(), {
  label: 'Tipo',
  placeholder: 'Selecione',
  errorText: '',
})

const modelValue = defineModel<string | null>()
const isOpen = ref(false)

const mockOptions = [
  { label: 'Conta Corrente', value: 'CHECKING' },
  { label: 'Investimentos', value: 'INVESTMENT' },
]

const resolvedOptions = computed(() =>
  props.options?.length ? props.options : mockOptions
)

const displayValue = computed(() => {
  if (!modelValue.value) return null
  const opt = resolvedOptions.value.find((o) => o.value === modelValue.value)
  return opt?.label ?? null
})

const isFloating = computed(() => !!modelValue.value || isOpen.value)
</script>

<template>
  <div class="app-select">
    <SelectRoot v-model:open="isOpen" v-model="modelValue">
      <SelectTrigger
        class="app-select__trigger"
        :class="{
          'app-select__trigger--filled': !!modelValue,
          'app-select__trigger--empty': !modelValue,
          'app-select__trigger--error': !!props.errorText,
        }"
      >
        <span
          v-show="isFloating"
          class="app-select__label app-select__label--float"
        >
          {{ label }}
        </span>
        <span class="app-select__value">{{ displayValue || placeholder }}</span>
        <ChevronDownIcon class="app-select__chevron" />
      </SelectTrigger>
      <SelectPortal>
        <SelectContent
          class="app-select__content"
          position="popper"
          :side-offset="5"
          :style="{ zIndex: 2500 }"
        >
          <SelectViewport>
            <SelectItem
              v-for="opt in resolvedOptions"
              :key="opt.value"
              :value="opt.value"
              class="app-select__item"
              :text-value="opt.label"
            >
              <SelectItemText>{{ opt.label }}</SelectItemText>
              <SelectItemIndicator class="app-select__indicator">
                <CheckIcon />
              </SelectItemIndicator>
            </SelectItem>
          </SelectViewport>
        </SelectContent>
      </SelectPortal>
    </SelectRoot>
    <div v-if="props.errorText" class="app-select__error">
      <CrossCircledIcon class="app-select__error-icon" />
      <span class="app-select__error-text">{{ props.errorText }}</span>
    </div>
  </div>
</template>

<style scoped>
.app-select {
  position: relative;
}

.app-select__trigger {
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
  cursor: pointer;
  outline: none;
  transition: border-color 0.2s;
}

.app-select__trigger:focus-visible {
  outline: 2px solid #087f5b;
  outline-offset: 2px;
}

.app-select__trigger[data-state="open"] {
  border-color: #087f5b;
}

.app-select__trigger--error {
  border-color: #e03131;
}

.app-select__trigger {
  position: relative;
}

.app-select__label {
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

.app-select__label--float {
  top: -8px;
  font-size: 12px;
  color: #087f5b;
  transform: none;
}

.app-select__value {
  flex: 1;
  font-size: 16px;
  color: #1f2937;
  min-height: 0;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-select__trigger--empty .app-select__value {
  color: #6b7280;
}

.app-select__error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 6px;
  color: #e03131;
}

.app-select__error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.app-select__error-text {
  font-size: 14px;
  font-weight: 500;
}

.app-select__chevron {
  width: 20px;
  height: 20px;
  color: #6b7280;
  flex-shrink: 0;
  margin-left: 8px;
}

.app-select__content {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #f3f4f6; 
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  z-index: 2500;
  overflow: hidden;
  padding: 8px;
}

.app-select__item {
  background: white;
  position: relative;
  width: 300px;
  min-width: auto;
  cursor: default;
  user-select: none;
  display: flex;
  align-items: center;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  color: #1f2937;
  transition: background-color 0.2s ease;
}

.app-select__item[data-highlighted] {
  background: #F1F3F5; 
}

.app-select__item[data-state="checked"] {
  font-weight: 700;
}

.app-select__indicator {
  margin-left: auto;
}

.app-select__indicator svg {
  width: 16px;
  height: 16px;
}
</style>
