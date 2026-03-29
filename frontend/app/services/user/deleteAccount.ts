export async function deleteAccount(password: string): Promise<void> {
  const { $api } = useNuxtApp()
  await $api.delete('/users/me', { data: { password } })
}
