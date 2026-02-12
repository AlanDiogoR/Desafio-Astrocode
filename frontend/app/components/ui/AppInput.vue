<script setup lang="ts">
import { Cross2Icon } from '@radix-icons/vue'

type Rule = (v: string) => boolean | string

interface Props {
  label: string
  type?: string
  rules?: Rule[]
  disabled?: boolean
  fieldError?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  rules: () => [],
  disabled: false,
  fieldError: '',
})

const model = defineModel<string>({ default: '' })

const emit = defineEmits<{
  clearError: []
}>()

const hasError = computed(() => !!props.fieldError)

const errorText = computed(() => props.fieldError ?? '')

function onInput() {
  if (props.fieldError) {
    emit('clearError')
  }
}
</script>

<template>
  <v-text-field
    v-model="model"
    :label="label"
    :type="type"
    :disabled="disabled"
    :rules="rules"
    :error="hasError"
    :error-messages="hasError ? [errorText] : undefined"
    variant="outlined"
    density="comfortable"
    color="primary"
    hide-details="auto"
    class="app-input"
    v-bind="$attrs"
    @update:model-value="onInput"
  >
    <template #message="{ message }">
      <div class="app-input-error">
        <Cross2Icon class="app-input-error-icon" aria-hidden />
        <span class="app-input-error-text">{{ message }}</span>
      </div>
    </template>
    <template v-if="$slots['append-inner']" #append-inner>
      <slot name="append-inner" />
    </template>
  </v-text-field>
</template>

<style scoped>
.app-input :deep(.v-field__overlay),
.app-input :deep(.v-field__background) {
  opacity: 0 !important;
  background-color: transparent !important;
  display: none !important;
}

.app-input :deep(.v-field) {
  --v-field-hover-opacity: 0 !important;
  --v-field-focus-opacity: 0 !important;
  background-color: transparent !important;
}

.app-input :deep(.v-field--focused) {
  background-color: transparent !important;
}

.app-input :deep(.v-field__outline) {
  --v-field-border-opacity: 1 !important;
  color: #ADB5BD !important;
}

.app-input :deep(.v-field--focused .v-field__outline) {
  color: #087F5B !important;
}

.app-input :deep(.v-field--error .v-field__outline) {
  color: #E03131 !important;
}

.app-input-error {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #E03131;
  font-size: 12px;
}

.app-input-error-icon {
  width: 12px;
  height: 12px;
  flex-shrink: 0;
  color: #E03131;
}

.app-input-error-text {
  font-size: 12px;
  font-weight: 400;
  color: #E03131;
  line-height: 1.4;
}

.app-input :deep(.v-input__details) {
  padding-left: 0;
}

.app-input :deep(.v-messages__message) {
  padding-left: 0;
}
</style>
