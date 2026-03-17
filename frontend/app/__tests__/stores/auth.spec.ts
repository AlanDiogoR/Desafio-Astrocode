import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '~/stores/auth'

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('isLoggedIn é false quando user é null', () => {
    const store = useAuthStore()
    expect(store.isLoggedIn).toBe(false)
  })

  it('isLoggedIn é true quando user está definido', () => {
    const store = useAuthStore()
    store.setUser({
      id: '1',
      name: 'Test',
      email: 'test@example.com',
      plan: 'FREE',
      isPro: false,
      isElite: false,
      planExpiresAt: null,
    })
    expect(store.isLoggedIn).toBe(true)
  })

  it('clearAuth remove o user', () => {
    const store = useAuthStore()
    store.setUser({
      id: '1',
      name: 'Test',
      email: 'test@example.com',
      plan: 'MONTHLY',
      isPro: true,
      isElite: false,
      planExpiresAt: '2025-12-31',
    })
    expect(store.isProUser).toBe(true)
    store.clearAuth()
    expect(store.isLoggedIn).toBe(false)
    expect(store.user).toBeNull()
  })

  it('planLabel retorna label correto para FREE', () => {
    const store = useAuthStore()
    store.setUser({
      id: '1',
      name: 'Test',
      email: 'test@example.com',
      plan: 'FREE',
      isPro: false,
      isElite: false,
      planExpiresAt: null,
    })
    expect(store.planLabel).toBe('Grátis')
  })

  it('planLabel retorna label para MONTHLY', () => {
    const store = useAuthStore()
    store.setUser({
      id: '1',
      name: 'Test',
      email: 'test@example.com',
      plan: 'MONTHLY',
      isPro: true,
      isElite: false,
      planExpiresAt: null,
    })
    expect(store.planLabel).toBe('Premium Mensal')
  })
})
