<script setup lang="ts">
import AppLogo from '~/components/ui/AppLogo.vue'
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()

const userInitials = computed(() => {
  const name = authStore.user?.name
  if (!name || !name.trim()) return 'AL'
  const parts = name.trim().split(/\s+/)
  if (parts.length >= 2) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
  }
  return name.slice(0, 2).toUpperCase()
})
</script>

<template>
  <header class="dashboard-header">
    <div class="dashboard-header__logo">
      <AppLogo
        color="#087F5B"
        :size="28"
      />
    </div>
    <v-avatar
      class="dashboard-header__avatar"
      size="40"
    >
      <span class="avatar-initials">{{ userInitials }}</span>
    </v-avatar>
  </header>
</template>

<style scoped>
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  flex-shrink: 0;
  background-color: white;
}

.dashboard-header__logo {
  flex-shrink: 0;
}

.dashboard-header__logo :deep(.app-logo) {
  width: auto;
}

.dashboard-header__avatar {
  flex-shrink: 0;
  background-color: #f1f3f5;
}

.avatar-initials {
  font-size: 14px;
  font-weight: 600;
  color: #087f5b;
}
</style>
