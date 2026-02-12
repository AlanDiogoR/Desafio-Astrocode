<script setup lang="ts">
import { TransitionRoot } from '@headlessui/vue'

defineProps<{
  show: boolean
}>()
</script>

<template>
  <TransitionRoot
    :show="show"
    enter="app-launch-screen-enter"
    enter-from="app-launch-screen-enter-from"
    enter-to="app-launch-screen-enter-to"
    leave="app-launch-screen-leave"
    leave-from="app-launch-screen-leave-from"
    leave-to="app-launch-screen-leave-to"
  >
    <div
      class="app-launch-screen"
      :class="{ 'app-launch-screen--hidden': !show }"
    >
      <div class="app-launch-screen__content">
        <AppLogo
          color="#FFFFFF"
          :size="64"
        />
        <v-progress-circular
          indeterminate
          color="white"
          bg-color="#087F5B"
          :size="32"
          :width="3"
          class="app-launch-screen__spinner"
        />
      </div>
    </div>
  </TransitionRoot>
</template>

<style scoped>
.app-launch-screen {
  position: fixed;
  inset: 0;
  z-index: 50;
  background-color: #087F5B;
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-launch-screen__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0;
}

.app-launch-screen__spinner {
  margin-top: 2rem;
}

.app-launch-screen-enter {
  transition: opacity 200ms ease-out;
}

.app-launch-screen-enter-from {
  opacity: 0;
}

.app-launch-screen-enter-to {
  opacity: 1;
}

.app-launch-screen-leave {
  transition: opacity 300ms ease-in;
}

.app-launch-screen-leave-from {
  opacity: 1;
}

.app-launch-screen-leave-to {
  opacity: 0;
}

.app-launch-screen--hidden {
  pointer-events: none;
}

</style>
