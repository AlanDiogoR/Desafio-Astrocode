export async function logout(): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.post('/auth/logout')
}
