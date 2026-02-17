export async function requestPasswordReset(email: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/auth/forgot-password', { email })
}
