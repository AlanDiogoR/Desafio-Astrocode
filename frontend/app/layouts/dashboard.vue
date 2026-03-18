<template>
  <v-main class="dashboard-layout pa-0">
    <div class="dashboard-layout__inner">
      <slot />
    </div>
  </v-main>
</template>

<style scoped>
.dashboard-layout {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.dashboard-layout__inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

@media (min-width: 960px) {
  .dashboard-layout__inner {
    height: 100%;
    overflow: hidden;
  }
}

@media (max-width: 959px) {
  .dashboard-layout__inner {
    overflow: visible;
    min-height: 100%;
    flex: 1 1 auto;
  }
}
</style>
