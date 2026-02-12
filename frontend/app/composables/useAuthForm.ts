import { z } from 'zod'

const loginSchema = z.object({
  email: z
    .string()
    .min(1, 'E-mail é obrigatório')
    .email('E-mail inválido'),
  password: z
    .string()
    .min(8, 'Senha deve ter no mínimo 8 caracteres'),
})

const registerSchema = loginSchema.extend({
  name: z
    .string()
    .min(1, 'Nome é obrigatório')
    .min(3, 'Nome deve ter no mínimo 3 caracteres'),
})

export function useAuthForm() {
  const email = ref('')
  const password = ref('')
  const name = ref('')
  const isLoading = ref(false)
  const errorMessage = ref('')
  
  const emailError = ref('')
  const passwordError = ref('')
  const nameError = ref('')

  const authStore = useAuthStore()
  const { $api } = useNuxtApp()

  function clearErrors() {
    errorMessage.value = ''
    emailError.value = ''
    passwordError.value = ''
    nameError.value = ''
  }

  function clearFieldError(field: 'email' | 'password' | 'name') {
    if (field === 'email') emailError.value = ''
    if (field === 'password') passwordError.value = ''
    if (field === 'name') nameError.value = ''
  }

  function validateLogin(): boolean {
    clearErrors()
    
    try {
      loginSchema.parse({
        email: email.value,
        password: password.value,
      })
      return true
    } catch (error) {
      if (error instanceof z.ZodError) {
        error.errors.forEach((err) => {
          const field = err.path[0] as string
          if (field === 'email') emailError.value = err.message
          if (field === 'password') passwordError.value = err.message
        })
      }
      return false
    }
  }

  function validateRegister(): boolean {
    clearErrors()
    
    try {
      registerSchema.parse({
        name: name.value,
        email: email.value,
        password: password.value,
      })
      return true
    } catch (error) {
      if (error instanceof z.ZodError) {
        error.errors.forEach((err) => {
          const field = err.path[0] as string
          if (field === 'name') nameError.value = err.message
          if (field === 'email') emailError.value = err.message
          if (field === 'password') passwordError.value = err.message
        })
      }
      return false
    }
  }

  async function handleLogin(): Promise<void> {
    if (!validateLogin()) return
    
    isLoading.value = true

    try {
      const { data } = await $api.post<{ token: string; name: string }>('/auth/login', {
        email: email.value,
        password: password.value,
      })

      authStore.setToken(data.token)
      authStore.setUser({
        id: '',
        name: data.name,
        email: email.value,
      })

      await navigateTo('/dashboard')
    } catch (err: unknown) {
      const response = (err as { response?: { status?: number; data?: { message?: string } } })?.response
      const status = response?.status
      const message = response?.data?.message ?? 'Credenciais inválidas.'
      errorMessage.value = message
      if (status === 401 || status === 400) {
        passwordError.value = message
      }
    } finally {
      isLoading.value = false
    }
  }

  async function handleRegister(): Promise<void> {
    if (!validateRegister()) return
    
    isLoading.value = true

    try {
      await $api.post('/users', {
        name: name.value,
        email: email.value,
        password: password.value,
      })

      const { data } = await $api.post<{ token: string; name: string }>('/auth/login', {
        email: email.value,
        password: password.value,
      })

      authStore.setToken(data.token)
      authStore.setUser({
        id: '',
        name: data.name,
        email: email.value,
      })

      await navigateTo('/dashboard')
    } catch (err: unknown) {
      const response = (err as { response?: { status?: number; data?: { message?: string; errors?: Record<string, string> } } })?.response
      const status = response?.status
      const data = response?.data
      const message = data?.message ?? 'Erro ao criar conta.'
      const fieldErrors = data?.errors
      errorMessage.value = message
      if (status === 409) {
        emailError.value = message
      } else if (status === 400 && fieldErrors) {
        if (fieldErrors.name) nameError.value = fieldErrors.name
        if (fieldErrors.email) emailError.value = fieldErrors.email
        if (fieldErrors.password) passwordError.value = fieldErrors.password
      } else if (status === 400) {
        passwordError.value = message
      }
    } finally {
      isLoading.value = false
    }
  }

  return {
    email,
    password,
    name,
    isLoading,
    errorMessage,
    emailError,
    passwordError,
    nameError,
    handleLogin,
    handleRegister,
    clearErrors,
    clearFieldError,
  }
}
