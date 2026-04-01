<script setup lang="ts">
const route = useRoute()
const isLoading = useAppLoading()
const { isValid: hasApiConfig } = useApiConfig()
const { isPending } = useUser()

const fallbackTimer = ref<ReturnType<typeof setTimeout> | null>(null)

function clearSplash() {
  isLoading.value = false
  if (fallbackTimer.value) {
    clearTimeout(fallbackTimer.value)
    fallbackTimer.value = null
  }
}

watch(
  [isPending, hasApiConfig],
  ([pending, valid]) => {
    if (!valid) {
      clearSplash()
      return
    }
    if (!pending) {
      clearSplash()
    }
  },
  { immediate: true },
)

onMounted(() => {
  fallbackTimer.value = setTimeout(() => clearSplash(), 1500)
})
onUnmounted(() => {
  if (fallbackTimer.value) clearTimeout(fallbackTimer.value)
})
</script>

<template>
  <div class="grivy-root">
    <ClientOnly>
      <AppLaunchScreen :show="isLoading" />
    </ClientOnly>
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
        <NuxtPage :key="route.fullPath" />
      </NuxtLayout>
      <ClientOnly>
        <PlanUpgradeModal />
      </ClientOnly>
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
  </div>
</template>
