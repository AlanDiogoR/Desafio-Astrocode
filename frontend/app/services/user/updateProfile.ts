import type { User } from '~/stores/auth'

interface UpdateProfilePayload {
  name?: string
  currentPassword?: string
  newPassword?: string
}

export async function updateProfile(payload: UpdateProfilePayload): Promise<User> {
  const { $api } = useNuxtApp()
  const { data } = await $api.patch<User>('/users/me', payload)
  return data
}
