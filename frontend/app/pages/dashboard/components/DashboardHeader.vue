<script setup lang="ts">
import AppLogo from '~/components/ui/AppLogo.vue'
import { useAuthStore } from '~/stores/auth'
import { useDashboard } from '~/composables/useDashboard'

const authStore = useAuthStore()
const { openEditProfileModal } = useDashboard()

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
    <button
      type="button"
      class="dashboard-header__avatar-btn"
      aria-label="Editar perfil"
      @click="openEditProfileModal"
    >
      <v-avatar
        class="dashboard-header__avatar"
        size="40"
      >
        <span class="avatar-initials">{{ userInitials }}</span>
      </v-avatar>
    </button>
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

.dashboard-header__avatar-btn {
  flex-shrink: 0;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 50%;
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
