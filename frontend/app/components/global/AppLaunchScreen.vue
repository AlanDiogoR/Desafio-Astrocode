<script setup lang="ts">
defineProps<{
  show: boolean
}>()
</script>

<template>
  <Transition
    enter-active-class="app-launch-screen-enter"
    enter-from-class="app-launch-screen-enter-from"
    enter-to-class="app-launch-screen-enter-to"
    leave-active-class="app-launch-screen-leave"
    leave-from-class="app-launch-screen-leave-from"
    leave-to-class="app-launch-screen-leave-to"
  >
    <div
      v-if="show"
      key="launch"
      class="app-launch-screen"
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
  </Transition>
</template>

<style scoped>
.app-launch-screen {
  position: fixed;
  inset: 0;
  z-index: 9999;
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


</style>
