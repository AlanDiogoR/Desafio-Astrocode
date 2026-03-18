<template>
  <div class="dashboard-layout">
    <slot />
  </div>
</template>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

@media (min-width: 960px) {
  .dashboard-layout {
    height: 100vh;
    overflow: hidden;
  }
}
</style>
