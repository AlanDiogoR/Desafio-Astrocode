<script setup lang="ts">
const isLoading = useAppLoading()

useUser()

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 800)
})
</script>

<template>
  <ClientOnly>
    <AppLaunchScreen :show="isLoading" />
    <v-app>
      <NuxtLayout>
        <NuxtPage />
      </NuxtLayout>
      <Toaster />
    </v-app>
  </ClientOnly>
</template>
