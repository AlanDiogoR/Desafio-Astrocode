<script setup lang="ts">
interface Props {
  /** Cor do ícone e texto (ex.: `#868E96`). Omitir ou `primary` usa o tema Vuetify. */
  color?: string
  size?: number
}

const props = withDefaults(defineProps<Props>(), {
  size: 32,
})

const useThemePrimary = computed(
  () => props.color === undefined || props.color === '' || props.color === 'primary',
)

const customStyle = computed(() => {
  if (useThemePrimary.value) return undefined
  return { color: props.color }
})
</script>

<template>
  <div
    class="app-logo d-flex align-center gap-2"
    :class="{ 'app-logo--theme': useThemePrimary }"
    :style="customStyle"
  >
    <svg
      :width="size"
      :height="size"
      viewBox="0 0 32 32"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      class="app-logo__svg flex-shrink-0"
    >
      <rect
        x="6"
        y="8"
        width="20"
        height="13"
        rx="3"
        fill="currentColor"
        opacity="0.35"
      />
      <rect
        x="4"
        y="11"
        width="20"
        height="13"
        rx="3"
        fill="currentColor"
      />
      <rect
        x="4"
        y="14"
        width="20"
        height="3"
        fill="currentColor"
        opacity="0.5"
      />
      <rect
        x="8"
        y="19"
        width="5"
        height="3"
        rx="1"
        fill="white"
        opacity="0.6"
      />
    </svg>
    <span class="app-logo__name font-weight-bold text-h6">Grivy</span>
  </div>
</template>

<style scoped>
.app-logo--theme {
  color: rgb(var(--v-theme-primary));
}

.app-logo {
  cursor: pointer;
  user-select: none;
}

.app-logo__name {
  letter-spacing: -0.5px;
  line-height: 1.2;
}
</style>
