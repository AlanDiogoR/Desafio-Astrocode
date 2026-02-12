<template>
  <svg
    :width="width"
    :height="height"
    viewBox="0 0 32 26"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <!-- Cartão de trás (rotacionado) -->
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
    
    <!-- Cartão da frente -->
    <rect
      x="6"
      y="7"
      width="24"
      height="16"
      rx="2.5"
      :fill="color"
    />
    
    <!-- Chip do cartão -->
    <rect
      x="9.5"
      y="11"
      width="5"
      height="4"
      rx="0.8"
      fill="white"
      opacity="0.35"
    />
    
    <!-- Linhas do cartão (número e nome) -->
    <rect x="9.5" y="17.5" width="17" height="1" rx="0.5" fill="white" opacity="0.3" />
    <rect x="9.5" y="20" width="12" height="1" rx="0.5" fill="white" opacity="0.3" />
  </svg>
</template>

<script setup lang="ts">
interface Props {
  width?: number | string
  height?: number | string
  color?: string
}

withDefaults(defineProps<Props>(), {
  width: 32,
  height: 26,
  color: '#087F5B' // teal-900 do Figma
})
</script>
