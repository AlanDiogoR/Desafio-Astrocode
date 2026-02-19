import { useMutation } from '@tanstack/vue-query'
import type { AxiosError } from 'axios'
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { login as loginApi } from '~/services/auth/login'
import { register as registerApi } from '~/services/auth/register'

const loginSchema = z.object({
  email: z.string().min(1, 'Campo obrigatório').email('E-mail inválido'),
  password: z.string().min(1, 'Campo obrigatório').min(8, 'A senha deve ter pelo menos 8 caracteres'),
})

const registerSchema = loginSchema.extend({
  name: z.string().min(1, 'Campo obrigatório').min(3, 'Nome deve ter no mínimo 3 caracteres'),
})

const emailSchema = z.string().min(1, 'Campo obrigatório').email('E-mail inválido')
const passwordSchema = z.string().min(1, 'Campo obrigatório').min(8, 'A senha deve ter pelo menos 8 caracteres')
const nameSchema = z.string().min(1, 'Campo obrigatório').min(3, 'Nome deve ter no mínimo 3 caracteres')

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

  const touched = reactive({
    email: false,
    password: false,
    name: false,
  })
  const hasAttemptedSubmit = ref(false)

  const authStore = useAuthStore()
  const router = useRouter()
  const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

  function markAsTouched(field: 'email' | 'password' | 'name') {
    touched[field] = true
    validateFieldWithZod(field)
  }

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

  function shouldValidateField(field: 'email' | 'password' | 'name') {
    return touched[field] || hasAttemptedSubmit.value
  }

  function validateFieldWithZod(field: 'email' | 'password' | 'name') {
    if (!shouldValidateField(field)) return
    if (field === 'email') {
      const result = emailSchema.safeParse(email.value)
      emailError.value = result.success ? '' : result.error.issues[0]?.message ?? ''
    }
    if (field === 'password') {
      const result = passwordSchema.safeParse(password.value)
      passwordError.value = result.success ? '' : result.error.issues[0]?.message ?? ''
    }
    if (field === 'name') {
      const result = nameSchema.safeParse(name.value)
      nameError.value = result.success ? '' : result.error.issues[0]?.message ?? ''
    }
  }

  function validateLogin(): boolean {
    hasAttemptedSubmit.value = true
    touched.email = true
    touched.password = true
    clearErrors()
    try {
      loginSchema.parse({ email: email.value, password: password.value })
      return true
    } catch (error) {
      if (error instanceof z.ZodError) {
        error.issues.forEach((issue: z.ZodIssue) => {
          const f = issue.path[0] as string
          if (f === 'email') emailError.value = issue.message
          if (f === 'password') passwordError.value = issue.message
        })
      }
      return false
    }
  }

  function validateRegister(): boolean {
    hasAttemptedSubmit.value = true
    touched.name = true
    touched.email = true
    touched.password = true
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
          const f = issue.path[0] as string
          if (f === 'name') nameError.value = issue.message
          if (f === 'email') emailError.value = issue.message
          if (f === 'password') passwordError.value = issue.message
        })
      }
      return false
    }
  }

  function handleLoginError(error: unknown) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    const status = axiosError.response?.status
    if (status === 401) {
      passwordError.value = 'E-mail ou senha inválidos'
      toast.error('E-mail ou senha inválidos')
    } else {
      toast.error(getErrorMessage(error, 'Falha ao conectar com o servidor.'))
    }
  }

  function handleRegisterError(error: unknown) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    const status = axiosError.response?.status
    const fieldErrors = axiosError.response?.data?.errors
    if (status === 409) {
      emailError.value = 'Este e-mail já está cadastrado'
      toast.error('Este e-mail já está cadastrado')
    } else if (status === 400 && fieldErrors) {
      if (fieldErrors.name) nameError.value = fieldErrors.name
      if (fieldErrors.email) emailError.value = fieldErrors.email
      if (fieldErrors.password) passwordError.value = fieldErrors.password
      toast.error(getErrorMessage(error, 'Falha ao conectar com o servidor.'))
    } else {
      toast.error(getErrorMessage(error, 'Falha ao conectar com o servidor.'))
    }
  }

  const loginMutation = useMutation({
    mutationFn: (payload: { email: string; password: string }) => loginApi(payload),
    onSuccess: (data) => {
      authStore.setToken(data.token)
      authStore.setUser({
        id: data.id,
        name: data.name,
        email: data.email,
      })
      toast.success('Bem-vindo(a) ao Grivy!')
    },
    onError: handleLoginError,
  })

  const registerMutation = useMutation({
    mutationFn: async (payload: { name: string; email: string; password: string }) => {
      await registerApi(payload)
      return loginApi({ email: payload.email, password: payload.password })
    },
    onSuccess: (data) => {
      authStore.setToken(data.token)
      authStore.setUser({
        id: data.id,
        name: data.name,
        email: data.email,
      })
      toast.success('Bem-vindo ao Grivy!')
    },
    onError: handleRegisterError,
  })

  async function handleLogin() {
    if (!validateLogin()) return
    try {
      await loginMutation.mutateAsync({ email: email.value, password: password.value })
      router.replace('/dashboard')
    } catch {
      /* onError já tratou */
    }
  }

  async function handleRegister() {
    if (!validateRegister()) return
    try {
      await registerMutation.mutateAsync({
        name: name.value,
        email: email.value,
        password: password.value,
      })
      router.replace('/dashboard')
    } catch {
      /* onError já tratou */
    }
  }

  watch(email, () => {
    loginMutation.reset()
    registerMutation.reset()
    validateFieldWithZod('email')
  })

  watch(password, () => {
    loginMutation.reset()
    registerMutation.reset()
    validateFieldWithZod('password')
  })

  watch(name, () => {
    registerMutation.reset()
    validateFieldWithZod('name')
  })

  const emailErrorDisplay = computed(() =>
    (touched.email || hasAttemptedSubmit.value) ? emailError.value : ''
  )
  const passwordErrorDisplay = computed(() =>
    (touched.password || hasAttemptedSubmit.value) ? passwordError.value : ''
  )
  const nameErrorDisplay = computed(() =>
    (touched.name || hasAttemptedSubmit.value) ? nameError.value : ''
  )

  return {
    email,
    password,
    name,
    loginMutation,
    registerMutation,
    emailError,
    passwordError,
    nameError,
    emailErrorDisplay,
    passwordErrorDisplay,
    nameErrorDisplay,
    handleLogin,
    handleRegister,
    clearErrors,
    clearFieldError,
    markAsTouched,
  }
}
