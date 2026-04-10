import type { AxiosInstance } from 'axios'

/**
 * Entra na lista de espera do Open Finance (e-mail de confirmação via backend).
 */
export async function joinOpenFinanceWaitlist(
  api: AxiosInstance,
  email: string,
): Promise<void> {
  await api.post('/open-finance/waitlist', { email })
}
