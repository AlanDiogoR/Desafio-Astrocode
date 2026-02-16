interface RegisterPayload {
  name: string
  email: string
  password: string
}

export async function register(payload: RegisterPayload): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/users', payload)
}
