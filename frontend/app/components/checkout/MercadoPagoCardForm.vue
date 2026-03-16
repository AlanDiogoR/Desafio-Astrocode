<script setup lang="ts">
/**
 * Componente de pagamento com cartão usando Mercado Pago Card Payment Brick.
 * Carrega o SDK dinamicamente e renderiza o formulário.
 */
const props = defineProps<{
  publicKey: string
  planType: string
  amount: number
}>()

const emit = defineEmits<{
  success: [result: unknown]
  error: [message: string]
}>()

const containerRef = ref<HTMLElement | null>(null)
const isLoading = ref(true)
const loadError = ref<string | null>(null)

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
  if (typeof window !== 'undefined' && (window as unknown as { MercadoPago?: typeof window.MercadoPago }).MercadoPago) {
    return Promise.resolve((window as unknown as { MercadoPago: typeof window.MercadoPago }).MercadoPago)
  }
  return loadScript('https://sdk.mercadopago.com/js/v2').then(() => {
    return (window as unknown as { MercadoPago: typeof window.MercadoPago }).MercadoPago
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
    containerRef.value.id = 'mp-card-payment-container'
    const bricksBuilder = mp.bricks()
    await bricksBuilder.create(
      'cardPayment',
      'mp-card-payment-container',
      {
        initialization: {
          amount: props.amount,
          payer: {
            email: initEmail,
          },
        },
        customization: {
          visual: {
            style: {
              theme: 'default',
            },
          },
        },
        callbacks: {
          onReady: () => {
            isLoading.value = false
          },
          onSubmit: (formData: {
            token?: string
            payment_method_id?: string
            installments?: number
            payer?: { email?: string; identification?: { type?: string; number?: string } }
          }) => {
            const payerEmail = formData.payer?.email ?? useAuthStore().getUser?.email ?? ''
            if (!formData.token || !payerEmail) {
              emit('error', 'Dados do cartão ou e-mail incompletos.')
              return Promise.reject(new Error('Dados incompletos'))
            }
            const { $api } = useNuxtApp()
            return import('~/services/subscription/checkout').then(({ checkout }) =>
              checkout($api, {
                planType: props.planType,
                token: formData.token!,
                installments: formData.installments ?? 1,
                payerEmail,
                payerIdentificationType: formData.payer?.identification?.type,
                payerIdentificationNumber: formData.payer?.identification?.number,
              })
            ).then((result) => {
              emit('success', result)
            }).catch((e: unknown) => {
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
      }
    )
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
