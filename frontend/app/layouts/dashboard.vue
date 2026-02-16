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
    min-height: 0;
  }
}

@media (max-width: 959px) {
  .dashboard-layout {
    min-height: 100%;
    overflow: visible;
  }
}
</style>
