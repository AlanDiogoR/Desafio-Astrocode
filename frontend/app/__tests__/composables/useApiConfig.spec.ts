import { describe, it, expect } from 'vitest'
import { useApiConfig } from '~/composables/useApiConfig'

describe('useApiConfig', () => {
  it('retorna estrutura esperada com apiBase e isValid', () => {
    const { apiBase, isValid, isMissing } = useApiConfig()
    expect(typeof isValid).toBe('boolean')
    expect(typeof isMissing).toBe('boolean')
    expect(isValid === !isMissing).toBe(true)
    expect(typeof apiBase).toBe('string')
  })
})
