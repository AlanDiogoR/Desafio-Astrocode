<script setup lang="ts">
import { CrossCircledIcon } from '@radix-icons/vue'

type Rule = (v: string) => boolean | string

interface Props {
  label: string
  type?: string
  rules?: Rule[]
  disabled?: boolean
  fieldError?: string
  errorText?: string
  /** Quando false, erros das rules só aparecem após interação (evita erro ao carregar) */
  showValidation?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  rules: () => [],
  disabled: false,
  fieldError: '',
  errorText: '',
  showValidation: false,
})

const model = defineModel<string>({ default: '' })

const emit = defineEmits<{
  clearError: []
  blur: []
}>()

const focused = ref(false)
const isFloating = computed(() => focused.value || !!model.value)

const hasError = computed(() => !!props.errorText || !!props.fieldError)
const errorMessage = computed(() => props.errorText || props.fieldError || '')

const computedRules = computed(() => {
  if (!props.rules?.length) return []
  const result = props.rules.map((rule) => rule(model.value))
  return result.filter((r): r is string => r !== true)
})

const validationError = computed(() =>
  props.showValidation ? computedRules.value[0] : undefined
)
const displayError = computed(
  () => validationError.value || errorMessage.value || ''
)

function onInput(e: Event) {
  const target = e.target as HTMLInputElement
  model.value = target.value
  if (errorMessage.value) emit('clearError')
}

function onBlur() {
  focused = false
  emit('blur')
}
</script>

<template>
  <div class="app-input-wrapper">
    <div class="app-input-inner">
      <input
        :value="model"
        :type="type"
        :disabled="disabled"
        class="app-input__field"
        :class="{
          'app-input__field--error': hasError || !!validationError,
        }"
        v-bind="$attrs"
        @input="onInput"
        @focus="focused = true"
        @blur="onBlur"
      >
      <label
        class="app-input__label"
        :class="{ 'app-input__label--float': isFloating }"
      >
        {{ label }}
      </label>
      <span v-if="$slots['append-inner']" class="app-input__append">
        <slot name="append-inner" />
      </span>
    </div>
    <div
      v-if="displayError"
      class="app-input__error"
    >
      <CrossCircledIcon width="16" height="16" class="app-input__error-icon" />
      <span class="app-input__error-text">{{ displayError }}</span>
    </div>
  </div>
</template>

<style scoped>
.app-input-wrapper {
  display: flex;
  flex-direction: column;
}

.app-input-inner {
  position: relative;
  height: 56px;
  width: 100%;
}

.app-input__field {
  height: 100%;
  width: 100%;
  border-radius: 16px;
  border: 1px solid #495057;
  padding: 0 16px;
  font-size: 16px;
  color: #1f2937;
  outline: none !important;
  box-shadow: none !important;
  transition: border-color 0.2s;
  background: white;
}

.app-input__field:focus,
.app-input__field:focus-visible {
  outline: none !important;
  box-shadow: none !important;
  border-color: #087f5b;
}

.app-input__field--error {
  border-color: #e03131;
}

.app-input__label {
  pointer-events: none;
  position: absolute;
  left: 16px;
  padding: 0 4px;
  background: white;
  transition: transform 0.2s ease, top 0.2s ease, font-size 0.2s ease, color 0.2s ease;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #6b7280;
}

.app-input__label--float {
  top: -8px;
  font-size: 12px;
  color: #087f5b;
  transform: translateY(0);
}

.app-input__append {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: auto;
}

.app-input__error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 6px;
  color: #e03131;
}

.app-input__error-icon {
  flex-shrink: 0;
}

.app-input__error-text {
  font-size: 14px;
  font-weight: 500;
}
</style>
