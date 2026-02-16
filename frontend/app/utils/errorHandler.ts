import type { AxiosError } from 'axios'

const DEFAULT_MESSAGE = 'Algo deu errado. Tente novamente.'

const STATUS_MESSAGES: Record<number, string> = {
  400: 'Dados inválidos. Verifique as informações e tente novamente.',
  401: 'Usuário não autorizado. Faça login novamente.',
  403: 'Você não tem permissão para esta ação.',
  404: 'Recurso não encontrado.',
  409: 'Este registro já existe.',
  422: 'Dados inválidos.',
  500: 'Falha ao conectar com o servidor. Tente mais tarde.',
}

const MESSAGE_OVERRIDES: Record<string, string> = {
  'Conta bancária não encontrada': 'Conta não encontrada.',
  'Categoria não encontrada': 'Categoria não encontrada.',
  'Transação não encontrada': 'Transação não encontrada.',
  'Meta não encontrada': 'Meta não encontrada.',
  'Saldo insuficiente na conta': 'Saldo insuficiente.',
  'O tipo da transação': 'Categoria incompatível com o tipo da transação.',
}

export function getErrorMessage(error: unknown, fallback = DEFAULT_MESSAGE): string {
  const axiosError = error as AxiosError<{ message?: string; errors?: Record<string, string> }>
  const status = axiosError.response?.status
  const data = axiosError.response?.data

  if (data?.message) {
    const msg = data.message.trim()
    const override = Object.entries(MESSAGE_OVERRIDES).find(([key]) => msg.startsWith(key))
    if (override) return override[1]
    if (!/^\d{3}\s|Error|error|failed/i.test(msg)) return msg
  }

  if (status && STATUS_MESSAGES[status]) return STATUS_MESSAGES[status]
  if (axiosError.code === 'ERR_NETWORK') return 'Falha ao conectar com o servidor.'
  if (axiosError.message?.toLowerCase().includes('timeout')) return 'Tempo limite excedido. Tente novamente.'

  return fallback
}
