<script setup lang="ts">
import { subscriptionService, type PaymentResponse } from '~/services/subscription/subscriptionService'

const props = defineProps<{
  publicKey: string
  /** Ex.: PRO_MONTHLY (Mercado Pago checkout plan id) */
  planId: string
  amount: number
}>()

const emit = defineEmits<{
  success: [result: PaymentResponse]
  pending: [result: PaymentResponse]
  error: [message: string]
}>()

const containerRef = ref<HTMLElement | null>(null)
const isLoading = ref(true)
const loadError = ref<string | null>(null)

interface BrickFormData {
  token?: string
  payment_method_id?: string
  paymentMethodId?: string
  issuer_id?: string | number
  issuerId?: string | number
  installments?: number
  payer?: {
    email?: string
    identification?: { type?: string; number?: string }
  }
}

async function loadScript(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve()
      return
    }
    const script = document.createElement('script')
    script.src = src
    script.async = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Falha ao carregar SDK Mercado Pago'))
    document.head.appendChild(script)
  })
}

function loadMercadoPago(): Promise<typeof window.MercadoPago> {
  if (typeof window !== 'undefined' && (window as { MercadoPago?: typeof window.MercadoPago }).MercadoPago) {
    return Promise.resolve((window as { MercadoPago: typeof window.MercadoPago }).MercadoPago)
  }
  return loadScript('https://sdk.mercadopago.com/js/v2').then(() => {
    return (window as { MercadoPago: typeof window.MercadoPago }).MercadoPago
  })
}

onMounted(async () => {
  if (!containerRef.value || !props.publicKey) {
    loadError.value = 'Chave pública não configurada.'
    isLoading.value = false
    return
  }

  try {
    const MercadoPago = await loadMercadoPago()
    const mp = new MercadoPago(props.publicKey, { locale: 'pt-BR' })

    const initEmail = useAuthStore().getUser?.email ?? ''
    const containerId = 'mp-card-payment-' + Math.random().toString(36).slice(2, 11)
    containerRef.value.id = containerId

    const bricksBuilder = mp.bricks()
    await bricksBuilder.create('cardPayment', containerId, {
      initialization: {
        amount: props.amount,
        payer: { email: initEmail },
      },
      customization: {
        visual: { style: { theme: 'default' } },
      },
      callbacks: {
        onReady: () => {
          isLoading.value = false
        },
        onSubmit: (formData: BrickFormData) => {
          const payerEmail = formData.payer?.email ?? useAuthStore().getUser?.email ?? ''
          const token = formData.token
          const paymentMethodId = formData.payment_method_id ?? formData.paymentMethodId
          const rawIssuer = formData.issuer_id ?? formData.issuerId
          const issuerId = rawIssuer != null && String(rawIssuer).trim() !== '' ? String(rawIssuer) : undefined

          if (!token || !payerEmail) {
            emit('error', 'Dados do cartão ou e-mail incompletos.')
            return Promise.reject(new Error('Dados incompletos'))
          }

          if (!paymentMethodId) {
            emit('error', 'Método de pagamento não identificado. Tente novamente.')
            return Promise.reject(new Error('payment_method_id ausente'))
          }

          return subscriptionService
            .processPayment({
              token,
              transactionAmount: props.amount,
              installments: formData.installments ?? 1,
              paymentMethodId,
              planId: props.planId,
              payer: {
                email: payerEmail,
                identification:
                  formData.payer?.identification?.type && formData.payer?.identification?.number
                    ? {
                        type: formData.payer.identification.type,
                        number: formData.payer.identification.number,
                      }
                    : undefined,
              },
              issuerId,
            })
            .then((result) => {
              if (result.status === 'approved') {
                emit('success', result)
              } else if (result.status === 'pending' || result.status === 'in_process') {
                emit('pending', result)
              } else {
                emit('error', result.message)
              }
            })
            .catch((e: unknown) => {
              const err = e as { response?: { data?: { message?: string } } }
              const msg = err?.response?.data?.message ?? 'Erro ao processar pagamento.'
              emit('error', msg)
              throw new Error(msg)
            })
        },
        onError: (err: unknown) => {
          const msg = err instanceof Error ? err.message : 'Erro no formulário de pagamento.'
          emit('error', msg)
        },
      },
    })
  } catch (e) {
    loadError.value = e instanceof Error ? e.message : 'Erro ao carregar formulário de pagamento.'
    isLoading.value = false
  }
})
</script>

<template>
  <div class="mp-card-form">
    <div ref="containerRef" class="mp-card-form__container" />
    <div v-if="isLoading" class="mp-card-form__loading">
      <v-progress-circular indeterminate color="primary" size="40" />
      <span>Carregando formulário...</span>
    </div>
    <v-alert v-if="loadError" type="error" class="mt-4">
      {{ loadError }}
    </v-alert>
  </div>
</template>

<style scoped>
.mp-card-form {
  position: relative;
  min-height: 200px;
}

.mp-card-form__container {
  min-height: 200px;
}

.mp-card-form__loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #64748b;
  font-size: 14px;
}
</style>
