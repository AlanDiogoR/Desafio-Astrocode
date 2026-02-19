interface ResetPasswordPayload {
  email: string
  code: string
  newPassword: string
}

interface ResetPasswordResponse {
  token: string
  id: string
  name: string
  email: string
}

export async function resetPassword(payload: ResetPasswordPayload): Promise<ResetPasswordResponse> {
  const { $api } = useNuxtApp()
  const { data } = await $api.post<ResetPasswordResponse>('/auth/reset-password', payload)
  return data
}
