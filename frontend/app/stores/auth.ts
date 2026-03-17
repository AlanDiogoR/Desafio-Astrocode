import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export type PlanType = 'FREE' | 'MONTHLY' | 'SEMIANNUAL' | 'ANNUAL'

export interface User {
  id: string
  name: string
  email: string
  plan: PlanType
  isPro: boolean
  isElite: boolean
  planExpiresAt: string | null
  createdAt?: Date
  updatedAt?: Date
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)

  const isLoggedIn = computed(() => !!user.value)
  const getUser = computed(() => user.value)
  const isProUser = computed(() => user.value?.isPro ?? false)
  const isEliteUser = computed(() => user.value?.isElite ?? false)
  const planLabel = computed(() => {
    const p = user.value?.plan ?? 'FREE'
    const labels: Record<PlanType, string> = {
      FREE: 'Grátis',
      MONTHLY: 'Premium Mensal',
      SEMIANNUAL: 'Premium Semestral',
      ANNUAL: 'Premium Anual',
    }
    return labels[p]
  })

  function setUser(newUser: User | null) {
    user.value = newUser
  }

  function clearAuth() {
    user.value = null
  }

  return { user, isLoggedIn, getUser, isProUser, isEliteUser, planLabel, setUser, clearAuth }
})
