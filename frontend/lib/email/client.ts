/**
 * Contrato de envio de e-mail alinhado ao backend (Spring Boot + Resend/Brevo).
 * O envio real ocorre no servidor; não exponha {@code RESEND_API_KEY} no bundle do Nuxt.
 *
 * @see backend/src/main/java/com/astrocode/backend/domain/services/MailService.java
 */

export type SendEmailParams = {
  to: string
  subject: string
  html?: string
  text: string
}

/**
 * Placeholder tipado para integrações futuras (ex.: rota Nitro server-only).
 * Em produção, use as APIs REST do backend (`EmailMarketingService`).
 *
 * @throws Error sempre — use o backend para envio.
 */
export async function sendEmail(_params: SendEmailParams): Promise<void> {
  throw new Error(
    'sendEmail: envio é feito pelo backend Java (Resend). Chame POST /api/... no servidor.',
  )
}
