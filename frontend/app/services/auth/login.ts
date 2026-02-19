interface LoginPayload {
  email: string
  password: string
}

interface LoginResponse {
  token: string
  id: string
  name: string
  email: string
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { $api } = useNuxtApp()
  const { data } = await $api.post<LoginResponse>('/auth/login', payload)
  return data
}
