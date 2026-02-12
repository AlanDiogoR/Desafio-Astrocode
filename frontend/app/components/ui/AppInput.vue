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
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  rules: () => [],
  disabled: false,
  fieldError: '',
  errorText: '',
})

const model = defineModel<string>({ default: '' })

const emit = defineEmits<{
  clearError: []
}>()

const errorMessages = computed(() => props.errorText || props.fieldError || '')
const errorMessagesArray = computed(() =>
  errorMessages.value ? [errorMessages.value] : []
)

function onInput() {
  if (errorMessages.value) emit('clearError')
}
</script>

<template>
  <v-text-field
    v-model="model"
    :label="label"
    :type="type"
    :disabled="disabled"
    :rules="rules"
    :error="!!errorMessages"
    :error-messages="errorMessagesArray"
    variant="outlined"
    density="comfortable"
    color="primary"
    class="app-input"
    v-bind="$attrs"
    @update:model-value="onInput"
  >
    <template #message="{ message }">
      <div class="error-message-wrapper">
        <CrossCircledIcon
          :width="16"
          :height="16"
          class="error-icon"
        />
        <span class="error-text">{{ message }}</span>
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

.app-input :deep(.v-input__details) {
  padding-inline: 0 !important;
  padding-top: 6px !important;
  justify-content: flex-start !important;
}

.app-input :deep(.v-messages__message) {
  text-align: left !important;
  line-height: 1.2;
}

.error-message-wrapper {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  color: #E03131;
}

.error-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.error-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1;
  color: inherit;
}
</style>
