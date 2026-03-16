interface LoginPayload {
  email: string
  password: string
}

export interface LoginResponse {
  token?: string | null
  id: string
  name: string
  email: string
  plan: string
  isPro: boolean
  isElite: boolean
  planExpiresAt: string | null
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { $api } = useNuxtApp()
  const { data } = await $api.post<LoginResponse>('/auth/login', payload)
  return data
}
