<template>
  <div class="dashboard-layout">
    <slot />
  </div>
</template>

<style scoped>
.dashboard-layout {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

@media (max-width: 959px) {
  .dashboard-layout {
    height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
  }
}
</style>
