import { ref } from 'vue'
import { vi } from 'vitest'

const stateMap = new Map<string, { value: unknown }>()

export function clearStubbedNuxtStateKey(key: string) {
  stateMap.delete(key)
}

vi.stubGlobal('useState', <T>(key: string, init: () => T) => {
  if (!stateMap.has(key)) {
    stateMap.set(key, { value: init() })
  }
  return stateMap.get(key) as { value: T }
})

vi.stubGlobal('useRuntimeConfig', () => ({
  public: {
    apiBase: 'http://127.0.0.1:8080/api',
    mpPublicKey: '',
  },
}))

vi.stubGlobal('useAuthCookies', () => ({
  authToken: ref(null),
  refreshToken: ref(null),
  setSessionTokens: vi.fn(),
  clearSessionTokens: vi.fn(),
}))
