<script setup lang="ts">
useUser()
</script>

<template>
  <ClientOnly>
    <v-app>
      <NuxtLayout>
        <NuxtPage />
      </NuxtLayout>
      <Toaster />
    </v-app>
  </ClientOnly>
</template>
