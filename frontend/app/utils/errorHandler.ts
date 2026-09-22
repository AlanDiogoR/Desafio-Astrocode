import type { AxiosError } from 'axios'
import { API_UNAVAILABLE_MESSAGE, isApiUnavailableError } from '~/utils/apiAvailability'

const DEFAULT_MESSAGE = 'Algo deu errado. Tente novamente.'

const STATUS_MESSAGES: Record<number, string> = {
  400: 'Dados inválidos. Verifique as informações e tente novamente.',
  401: 'Usuário não autorizado. Faça login novamente.',
  403: 'Você não tem permissão para esta ação.',
  404: 'Recurso não encontrado.',
  409: 'Este registro já existe.',
  422: 'Dados inválidos.',
  429: 'Muitas tentativas. Aguarde um instante e tente novamente.',
  500: 'Falha ao conectar com o servidor. Tente mais tarde.',
  503: 'Serviço temporariamente indisponível. Tente novamente em alguns minutos.',
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

  if (status === 402) {
    return ''
  }

  if (!status && isApiUnavailableError(error)) {
    return API_UNAVAILABLE_MESSAGE
  }

  if (data?.message) {
    const msg = data.message.trim()
    const override = Object.entries(MESSAGE_OVERRIDES).find(([key]) => msg.startsWith(key))
    if (override) return override[1]
    if (!/^\d{3}\s|Error|error|failed/i.test(msg)) return msg
  }

  if (status && STATUS_MESSAGES[status]) return STATUS_MESSAGES[status]
  if (isApiUnavailableError(error)) return API_UNAVAILABLE_MESSAGE

  return fallback
}
