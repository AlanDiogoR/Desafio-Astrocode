<script setup lang="ts">
import { CrossCircledIcon } from '@radix-icons/vue'

interface Props {
  label?: string
  placeholder?: string
  valueColor?: string
  errorText?: string
}

withDefaults(defineProps<Props>(), {
  label: 'Saldo',
  placeholder: '0,00',
  valueColor: undefined,
  errorText: '',
})

const modelValue = defineModel<number | null>({ default: null })
defineEmits<{ blur: [] }>()

const currencyOptions = {
  prefix: '',
  suffix: '',
  decimal: ',',
  thousand: '.',
  precision: 2,
  acceptNegative: false,
  isInteger: false,
}

</script>

<template>
  <div class="input-currency">
    <label v-if="label" class="input-currency__label">{{ label }}</label>
    <div
      class="input-currency__wrapper"
      :class="{ 'input-currency__wrapper--error': !!errorText }"
    >
      <span class="input-currency__prefix">R$</span>
      <VueNumberFormat
        :value="modelValue ?? 0"
        :options="currencyOptions"
        class="input-currency__input"
        :style="valueColor ? { color: valueColor } : undefined"
        :placeholder="placeholder"
        @update:value="(v) => modelValue = v"
        @blur="$emit('blur')"
      />
    </div>
    <div v-if="errorText" class="input-currency__error">
      <CrossCircledIcon class="input-currency__error-icon" />
      <span class="input-currency__error-text">{{ errorText }}</span>
    </div>
  </div>
</template>

<style scoped>
.input-currency {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-currency__label {
  font-size: 18px;
  color: #495057;
  letter-spacing: -0.5px;
  font-weight: 500;
}

.input-currency__wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-currency__prefix {
  font-size: 18px;
  color: #868e96;
  letter-spacing: -0.5px;
  flex-shrink: 0;
}

.input-currency__input {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937; /* gray-800 */
  letter-spacing: -1px;
  width: 100%;
  border: none;
  background: transparent;
  outline: none !important;
  box-shadow: none !important;
}

.input-currency__input:focus,
.input-currency__input:focus-visible {
  outline: none !important;
  box-shadow: none !important;
}

.input-currency__wrapper :deep(input),
.input-currency__wrapper :deep(input:focus),
.input-currency__wrapper :deep(input:focus-visible) {
  outline: none !important;
  box-shadow: none !important;
}

.input-currency__input::placeholder {
  color: #adb5bd;
  font-weight: 500;
}

.input-currency__wrapper--error .input-currency__input,
.input-currency__wrapper--error .input-currency__prefix {
  color: #e03131;
}

.input-currency__error {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: #e03131;
}

.input-currency__error-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.input-currency__error-text {
  font-size: 14px;
  font-weight: 500;
}
</style>
