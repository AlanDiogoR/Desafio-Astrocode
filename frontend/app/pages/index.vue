<script setup lang="ts">
definePageMeta({
  layout: 'default',
})

onMounted(() => {
  navigateTo('/dashboard', { replace: true })
})
</script>

<template>
  <div class="pa-4">
    <v-progress-linear indeterminate color="primary" />
  </div>
</template>
