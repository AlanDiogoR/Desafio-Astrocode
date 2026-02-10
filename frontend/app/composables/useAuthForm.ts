export function useAuthForm() {
  const email = ref('')
  const password = ref('')
  const name = ref('')
  const isLoading = ref(false)
  const errorMessage = ref('')

  const authStore = useAuthStore()
  const { $api } = useNuxtApp()

  function clearError() {
    errorMessage.value = ''
  }

  async function handleLogin(): Promise<void> {
    clearError()
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
      errorMessage.value =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Credenciais inválidas.'
    } finally {
      isLoading.value = false
    }
  }

  async function handleRegister(): Promise<void> {
    clearError()
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
      errorMessage.value =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        'Erro ao criar conta.'
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
    handleLogin,
    handleRegister,
    clearError,
  }
}
