import { ref, computed } from 'vue'
import { useCookie } from '#imports'
import { defineStore } from 'pinia'

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

  async function logout() {
    clearAuth()
    await navigateTo('/login')
  }

  return { user, token, hasToken, isLoggedIn, getUser, setToken, setUser, clearAuth, logout }
})
