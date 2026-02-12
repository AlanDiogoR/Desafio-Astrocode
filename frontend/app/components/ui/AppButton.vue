<script setup lang="ts">
interface Props {
  loading?: boolean
  disabled?: boolean
  block?: boolean
  type?: 'button' | 'submit' | 'reset'
  color?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  disabled: false,
  block: true,
  type: 'button',
  color: 'primary',
})

const isLoading = computed(() => Boolean(props.loading))
const isDisabled = computed(() => Boolean(props.disabled))
</script>

<template>
  <v-btn
    :type="props.type"
    :loading="isLoading"
    :disabled="isDisabled"
    :block="props.block"
    :color="props.color"
    height="48"
    elevation="0"
    class="text-none app-button"
    v-bind="$attrs"
  >
    <slot />
  </v-btn>
</template>

<style scoped>
.app-button {
  border-radius: 16px;
  padding: 0 24px;
}

.app-button :deep(.v-btn__content) {
  font-weight: 500;
  font-size: 16px;
  letter-spacing: -0.5px;
  line-height: 1.4;
}
</style>
