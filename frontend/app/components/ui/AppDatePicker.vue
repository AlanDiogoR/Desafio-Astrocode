<script setup lang="ts">
interface Props {
  placeholder?: string
  errorText?: string
}

const { placeholder, errorText } = defineProps<Props>()

const modelValue = defineModel<Date | null>()

const AppDatePickerClient = defineAsyncComponent(
  () => import('./AppDatePicker.client.vue')
)
</script>

<template>
  <ClientOnly>
    <AppDatePickerClient
      v-model="modelValue"
      :placeholder="placeholder"
      :error-text="errorText"
      v-bind="$attrs"
    />
    <template #fallback>
      <div class="app-datepicker-placeholder" />
    </template>
  </ClientOnly>
</template>

<style scoped>
.app-datepicker-placeholder {
  height: 56px;
  border-radius: 16px;
  border: 1px solid #dee2e6;
  background: white;
}
</style>
