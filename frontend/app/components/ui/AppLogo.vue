<script setup lang="ts">
interface Props {
  color?: string
  size?: number
}

withDefaults(defineProps<Props>(), {
  color: '#087F5B',
  size: 24,
})
</script>

<template>
  <div class="app-logo d-flex align-center justify-center gap-2 w-100">
    <svg
      :width="size"
      :height="size * 0.8125"
      viewBox="0 0 32 26"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      class="app-logo__icon"
    >
      <g opacity="0.7">
        <rect
          x="2"
          y="3"
          width="24"
          height="16"
          rx="2.5"
          :fill="color"
          transform="rotate(-12 14 11)"
        />
      </g>
      <rect
        x="6"
        y="7"
        width="24"
        height="16"
        rx="2.5"
        :fill="color"
      />
      <rect
        x="9.5"
        y="11"
        width="5"
        height="4"
        rx="0.8"
        fill="white"
        opacity="0.35"
      />
      <rect x="9.5" y="17.5" width="17" height="1" rx="0.5" fill="white" opacity="0.3" />
      <rect x="9.5" y="20" width="12" height="1" rx="0.5" fill="white" opacity="0.3" />
    </svg>
    <span
      class="app-logo__text font-weight-normal"
      :style="{ color, fontSize: `${Math.round(size * 1.1)}px` }"
    >
      Grivy
    </span>
  </div>
</template>

<style scoped>
.app-logo__text {
  line-height: 1.2;
  letter-spacing: -0.5px;
}
</style>
