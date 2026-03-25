import { vi } from 'vitest'

const stateMap = new Map<string, { value: unknown }>()

vi.stubGlobal('useState', <T>(key: string, init: () => T) => {
  if (!stateMap.has(key)) {
    stateMap.set(key, { value: init() })
  }
  return stateMap.get(key) as { value: T }
})

vi.stubGlobal('useRuntimeConfig', () => ({
  public: {
    apiBase: 'http://127.0.0.1:8080/api',
  },
}))
