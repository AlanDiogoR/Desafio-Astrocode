export type ValidationRule = (v: string) => boolean | string

export function useFieldValidation() {
  function validateField(value: string, rules: ValidationRule[]): string {
    for (const rule of rules) {
      const result = rule(value)
      if (result !== true) return typeof result === 'string' ? result : 'Erro'
    }
    return ''
  }

  return { validateField }
}
