import { ref, computed } from 'vue'
import { useCookie } from '#imports'
import { defineStore } from 'pinia'
import { useQueryClient } from '@tanstack/vue-query'

export interface User {
  id: string
  name: string
  email: string
  createdAt?: string
  updatedAt?: string
}

export const useAuthStore = defineStore('auth', () => {
  const tokenCookie = useCookie<string | null>('auth_token', {
    default: () => null,
    secure: true,
    sameSite: 'strict',
    maxAge: 60 * 60 * 24 * 14,
  })

  const user = ref<User | null>(null)

  const token = computed(() => tokenCookie.value)
  const hasToken = computed(() => !!tokenCookie.value)
  const isLoggedIn = computed(() => !!tokenCookie.value && !!user.value)
  const getUser = computed(() => user.value)

  function setToken(newToken: string) {
    tokenCookie.value = newToken
  }

  function setUser(newUser: User) {
    user.value = newUser
  }

  function clearAuth() {
    user.value = null
    tokenCookie.value = null
  }

  function resetDashboardState() {
    if (import.meta.server) return
    useState('transactionFilters').value = undefined
    useState('isNewTransactionModalOpen').value = false
    useState('newTransactionType').value = null
    useState('isEditTransactionModalOpen').value = false
    useState('transactionBeingEdited').value = null
    useState('isNewAccountModalOpen').value = false
    useState('isNewGoalModalOpen').value = false
    useState('isNewGoalValueModalOpen').value = false
    useState('goalInteractionType').value = 'DEPOSIT'
    useState('isEditGoalModalOpen').value = false
    useState('goalBeingEdited').value = null
    useState('goalForValueAddition').value = null
    useState('isConfirmDeleteModalOpen').value = false
    useState('confirmDeleteEntityType').value = null
    useState('confirmDeleteEntityId').value = null
    useState('isMonthlySummaryModalOpen').value = false
    useState('isEditProfileModalOpen').value = false
  }

  async function logout() {
    clearAuth()
    if (import.meta.client) {
      const queryClient = useQueryClient()
      queryClient.clear()
      resetDashboardState()
    }
    await navigateTo('/login')
  }

  return { user, token, hasToken, isLoggedIn, getUser, setToken, setUser, clearAuth, logout }
})
