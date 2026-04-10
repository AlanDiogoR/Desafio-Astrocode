import type { AxiosInstance } from 'axios'

export async function requestWhatsappVerification(api: AxiosInstance, phone: string): Promise<void> {
  await api.post('/users/me/whatsapp/request', { phone })
}

export async function verifyWhatsappCode(api: AxiosInstance, code: string): Promise<void> {
  await api.post('/users/me/whatsapp/verify', { code })
}
