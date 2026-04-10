/** Assuntos de e-mail alinhados ao backend (GrivyEmailTemplates). */
export function buildWelcomeSubject(firstName: string): string {
  return `Bem-vindo(a) à Grivy, ${firstName}! Seu controle financeiro começa agora`
}
