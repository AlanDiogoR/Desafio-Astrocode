export async function resendVerificationEmail(email: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/auth/resend-verification', { email })
}
