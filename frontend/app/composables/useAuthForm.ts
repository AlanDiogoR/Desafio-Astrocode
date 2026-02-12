import { useMutation } from '@tanstack/vue-query'
import type { AxiosError } from 'axios'
import { z } from 'zod'

const loginSchema = z.object({
  email: z.string().min(1, 'E-mail é obrigatório').email('E-mail inválido'),
  password: z.string().min(8, 'Senha deve ter no mínimo 8 caracteres'),
})

const registerSchema = loginSchema.extend({
  name: z.string().min(1, 'Nome é obrigatório').min(3, 'Nome deve ter no mínimo 3 caracteres'),
})

interface LoginPayload {
  email: string
  password: string
}

interface RegisterPayload {
  name: string
  email: string
  password: string
}

interface LoginResponse {
  token: string
  name: string
}

interface ApiErrorResponse {
  message?: string
  errors?: Record<string, string>
}

export function useAuthForm() {
  const email = ref('')
  const password = ref('')
  const name = ref('')
  const emailError = ref<string>('')
  const passwordError = ref<string>('')
  const nameError = ref<string>('')

  const authStore = useAuthStore()
  const { $api } = useNuxtApp()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  function clearErrors() {
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
      loginSchema.parse({ email: email.value, password: password.value })
      return true
    } catch (error) {
      if (error instanceof z.ZodError) {
        error.issues.forEach((issue: z.ZodIssue) => {
          const field = issue.path[0] as string
          if (field === 'email') emailError.value = issue.message
          if (field === 'password') passwordError.value = issue.message
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
        error.issues.forEach((issue: z.ZodIssue) => {
          const field = issue.path[0] as string
          if (field === 'name') nameError.value = issue.message
          if (field === 'email') emailError.value = issue.message
          if (field === 'password') passwordError.value = issue.message
        })
      }
      return false
    }
  }

  function handleLoginError(error: unknown) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    const status = axiosError.response?.status

    if (status === 401) {
      toast.error('E-mail ou senha inválidos')
      passwordError.value = 'E-mail ou senha inválidos'
    } else {
      toast.error('Erro na conexão com o servidor')
    }
  }

  function handleRegisterError(error: unknown) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    const status = axiosError.response?.status
    const data = axiosError.response?.data
    const message = data?.message
    const fieldErrors = data?.errors

    if (status === 409) {
      toast.error('Este e-mail já está cadastrado')
      emailError.value = message ?? 'Este e-mail já está cadastrado'
    } else if (status === 400 && fieldErrors) {
      if (fieldErrors.name) nameError.value = fieldErrors.name
      if (fieldErrors.email) emailError.value = fieldErrors.email
      if (fieldErrors.password) passwordError.value = fieldErrors.password
      toast.error(message ?? 'Erro na conexão com o servidor')
    } else {
      toast.error('Erro na conexão com o servidor')
    }
  }

  const loginMutation = useMutation({
    mutationFn: async (payload: LoginPayload) => {
      const { data } = await $api.post<LoginResponse>('/auth/login', payload)
      return data
    },
    onSuccess: (data) => {
      authStore.setToken(data.token)
      authStore.setUser({
        id: '',
        name: data.name,
        email: email.value,
      })
      toast.success('Bem-vindo(a) ao Grivy!')
      navigateTo('/dashboard')
    },
    onError: handleLoginError,
  })

  const registerMutation = useMutation({
    mutationFn: async (payload: RegisterPayload) => {
      await $api.post('/users', payload)
      const { data } = await $api.post<LoginResponse>('/auth/login', {
        email: payload.email,
        password: payload.password,
      })
      return data
    },
    onSuccess: (data) => {
      authStore.setToken(data.token)
      authStore.setUser({
        id: '',
        name: data.name,
        email: email.value,
      })
      toast.success('Bem-vindo ao Grivy!')
      navigateTo('/dashboard')
    },
    onError: handleRegisterError,
  })

  function handleLogin() {
    if (!validateLogin()) return
    loginMutation.mutate({ email: email.value, password: password.value })
  }

  function handleRegister() {
    if (!validateRegister()) return
    registerMutation.mutate({
      name: name.value,
      email: email.value,
      password: password.value,
    })
  }

  watch([email, password, name], () => {
    clearErrors()
    loginMutation.reset()
    registerMutation.reset()
  })

  return {
    email,
    password,
    name,
    loginMutation,
    registerMutation,
    emailError,
    passwordError,
    nameError,
    handleLogin,
    handleRegister,
    clearErrors,
    clearFieldError,
  }
}
