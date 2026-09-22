<script setup lang="ts">
import { API_UNAVAILABLE_MESSAGE } from '~/utils/apiAvailability'

const route = useRoute()
const isLoading = useAppLoading()
const { isValid: hasApiConfig } = useApiConfig()
const { unavailable: apiUnavailable } = useApiAvailability()
const { isPending } = useUser()
const showServiceNotice = computed(() => !hasApiConfig || apiUnavailable.value)

const fallbackTimer = ref<ReturnType<typeof setTimeout> | null>(null)

function clearSplash() {
  isLoading.value = false
  if (fallbackTimer.value) {
    clearTimeout(fallbackTimer.value)
    fallbackTimer.value = null
  }
}

watch(
  isPending,
  (pending) => {
    if (!hasApiConfig || !pending) {
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
        v-if="showServiceNotice && !isLoading"
        type="warning"
        variant="tonal"
        class="grivy-service-notice"
        density="comfortable"
        role="status"
      >
        {{ API_UNAVAILABLE_MESSAGE }}
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

<style scoped>
.grivy-service-notice {
  position: fixed;
  z-index: 250;
  left: 16px;
  right: 88px;
  bottom: 16px;
  margin: 0;
}
</style>
