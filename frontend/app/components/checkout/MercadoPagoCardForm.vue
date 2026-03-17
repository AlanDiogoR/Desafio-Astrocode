<script setup lang="ts">
/**
 * Checkout Transparente - CardForm do Mercado Pago.
 * Formulário customizado que mantém o pagamento no ambiente da loja.
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

const formRef = ref<HTMLFormElement | null>(null)
const isLoading = ref(true)
const loadError = ref<string | null>(null)
const isSubmitting = ref(false)

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

interface CardFormApi {
  getCardFormData: () => {
    token: string
    paymentMethodId: string
    issuerId: string | number
    cardholderEmail: string
    amount: string
    installments: number
    identificationNumber: string
    identificationType: string
  }
}

onMounted(async () => {
  if (!formRef.value || !props.publicKey) {
    loadError.value = 'Chave pública não configurada.'
    isLoading.value = false
    return
  }

  try {
    const MercadoPago = await loadMercadoPago()
    const mp = new MercadoPago(props.publicKey, { locale: 'pt-BR' })

    const initEmail = useAuthStore().getUser?.email ?? ''

    const cardForm = mp.cardForm({
      amount: String(props.amount),
      iframe: true,
      form: {
        id: 'form-checkout',
        cardNumber: {
          id: 'form-checkout__cardNumber',
          placeholder: 'Número do cartão',
        },
        expirationDate: {
          id: 'form-checkout__expirationDate',
          placeholder: 'MM/AA',
        },
        securityCode: {
          id: 'form-checkout__securityCode',
          placeholder: 'Código de segurança',
        },
        cardholderName: {
          id: 'form-checkout__cardholderName',
          placeholder: 'Nome no cartão',
        },
        issuer: {
          id: 'form-checkout__issuer',
          placeholder: 'Banco emissor',
        },
        installments: {
          id: 'form-checkout__installments',
          placeholder: 'Parcelas',
        },
        identificationType: {
          id: 'form-checkout__identificationType',
          placeholder: 'Tipo de documento',
        },
        identificationNumber: {
          id: 'form-checkout__identificationNumber',
          placeholder: 'Número do documento',
        },
        cardholderEmail: {
          id: 'form-checkout__cardholderEmail',
          placeholder: 'E-mail',
        },
      },
      callbacks: {
        onFormMounted: (error: unknown) => {
          if (error) {
            loadError.value = error instanceof Error ? error.message : 'Erro ao montar formulário.'
          } else {
            const emailEl = document.getElementById('form-checkout__cardholderEmail') as HTMLInputElement | null
            if (emailEl && initEmail) emailEl.value = initEmail
          }
          isLoading.value = false
        },
        onSubmit: (event: Event) => {
          event.preventDefault()
          if (isSubmitting.value) return
          isSubmitting.value = true

          const formData = (cardForm as CardFormApi).getCardFormData()

          if (!formData.token || !formData.cardholderEmail) {
            emit('error', 'Dados do cartão ou e-mail incompletos.')
            isSubmitting.value = false
            return
          }

          const { $api } = useNuxtApp()
          import('~/services/subscription/checkout')
            .then(({ checkout }) =>
              checkout($api, {
                planType: props.planType,
                token: formData.token,
                paymentMethodId: formData.paymentMethodId,
                installments: formData.installments,
                payerEmail: formData.cardholderEmail,
                payerIdentificationType: formData.identificationType || undefined,
                payerIdentificationNumber: formData.identificationNumber || undefined,
                issuerId: formData.issuerId != null ? String(formData.issuerId) : undefined,
              })
            )
            .then((result) => {
              emit('success', result)
            })
            .catch((e: unknown) => {
              const err = e as { response?: { data?: { message?: string } } }
              const msg = err?.response?.data?.message ?? 'Erro ao processar pagamento.'
              emit('error', msg)
            })
            .finally(() => {
              isSubmitting.value = false
            })
        },
        onFetching: () => {
          const bar = formRef.value?.querySelector('.cardform-progress') as HTMLProgressElement | null
          if (bar) bar.removeAttribute('value')
          return () => {
            if (bar) bar.setAttribute('value', '0')
          }
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
  <div class="mp-cardform">
    <form ref="formRef" id="form-checkout" class="mp-cardform__form">
      <div class="mp-cardform__fields">
        <div id="form-checkout__cardNumber" class="mp-cardform__field" />
        <div id="form-checkout__expirationDate" class="mp-cardform__field" />
        <div id="form-checkout__securityCode" class="mp-cardform__field" />
        <input
          type="text"
          id="form-checkout__cardholderName"
          placeholder="Nome no cartão"
          class="mp-cardform__native-input"
        />
        <select id="form-checkout__issuer" class="mp-cardform__select" />
        <select id="form-checkout__installments" class="mp-cardform__select" />
        <select id="form-checkout__identificationType" class="mp-cardform__select" />
        <input
          type="text"
          id="form-checkout__identificationNumber"
          placeholder="CPF"
          class="mp-cardform__native-input"
        />
        <input
          type="email"
          id="form-checkout__cardholderEmail"
          placeholder="E-mail"
          class="mp-cardform__native-input"
        />
      </div>
      <progress value="0" class="cardform-progress mp-cardform__progress" />
      <v-btn
        type="submit"
        id="form-checkout__submit"
        color="primary"
        size="large"
        block
        :loading="isSubmitting"
        class="mt-4"
      >
        Pagar {{ new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(props.amount) }}
      </v-btn>
    </form>
    <div v-if="isLoading" class="mp-cardform__loading">
      <v-progress-circular indeterminate color="primary" size="40" />
      <span>Carregando formulário...</span>
    </div>
    <v-alert v-if="loadError" type="error" class="mt-4">
      {{ loadError }}
    </v-alert>
  </div>
</template>

<style scoped>
.mp-cardform {
  position: relative;
  min-height: 200px;
}

.mp-cardform__form {
  display: flex;
  flex-direction: column;
  max-width: 100%;
}

.mp-cardform__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.mp-cardform__field {
  min-height: 40px;
  border: 1px solid rgba(0, 0, 0, 0.38);
  border-radius: 4px;
  padding: 8px 12px;
}

.mp-cardform__field :deep(iframe) {
  border: none;
  width: 100%;
  min-height: 24px;
}

.mp-cardform__native-input,
.mp-cardform__select {
  width: 100%;
  min-height: 40px;
  padding: 8px 12px;
  border: 1px solid rgba(0, 0, 0, 0.38);
  border-radius: 4px;
  font-size: 14px;
}

.mp-cardform__select {
  background: white;
  cursor: pointer;
}

.mp-cardform__progress {
  width: 100%;
  height: 4px;
  margin-top: 8px;
  accent-color: var(--v-theme-primary);
}

.mp-cardform__loading {
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
