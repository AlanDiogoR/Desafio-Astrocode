<template>
  <div class="landing-layout">
    <slot />
  </div>
</template>

<style scoped>
.landing-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: rgb(var(--v-theme-surface));
}
</style>
