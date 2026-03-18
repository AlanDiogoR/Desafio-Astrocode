<script setup lang="ts">
const isLoading = useAppLoading()
const { isValid: hasApiConfig } = useApiConfig()
const { status } = useUser()

const fallbackTimer = ref<ReturnType<typeof setTimeout> | null>(null)

watch(status, (s) => {
  if (s !== 'pending') {
    isLoading.value = false
    if (fallbackTimer.value) clearTimeout(fallbackTimer.value)
  }
}, { immediate: true })

onMounted(() => {
  fallbackTimer.value = setTimeout(() => {
    isLoading.value = false
  }, 1200)
})
onUnmounted(() => {
  if (fallbackTimer.value) clearTimeout(fallbackTimer.value)
})
</script>

<template>
  <ClientOnly>
    <AppLaunchScreen :show="isLoading" />
    <v-app>
      <v-alert
        v-if="!hasApiConfig && !isLoading"
        type="error"
        class="ma-4"
        closable
        prominent
      >
        Configuração de API ausente em produção. Verifique se NUXT_PUBLIC_API_BASE está definida com a URL absoluta da API (ex: https://api.exemplo.com/api).
      </v-alert>
      <NuxtLayout>
        <NuxtPage />
      </NuxtLayout>
      <Toaster
        :toast-options="{
          success: {
            iconTheme: {
              primary: '#0d9488',
              secondary: '#ffffff',
            },
          },
        }"
      />
    </v-app>
  </ClientOnly>
</template>
