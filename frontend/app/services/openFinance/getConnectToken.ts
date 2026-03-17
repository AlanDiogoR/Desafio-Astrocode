export async function getConnectToken($api: { get: (url: string) => Promise<{ data: { accessToken: string } }> }): Promise<string> {
  const { data } = await $api.get('/open-finance/connect-token')
  return data.accessToken
}
