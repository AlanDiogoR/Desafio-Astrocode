<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import AppDatePicker from '~/components/ui/AppDatePicker.client.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { formatCurrency } from '~/utils/format'
import { toDateString } from '~/utils/format'
import type { CreditCard } from '~/types/creditCard'

const { creditCardForBillModal, closeCreditCardBillModal } = useDashboard()
const { accounts } = useBankAccounts()
const creditCardId = computed(() => creditCardForBillModal.value?.id ?? null)

const {
  currentBill,
  isBillPending,
  isPayLoading,
  payBill,
  refetchBill,
} = useCreditCardBillController({
  creditCardId: computed(() => creditCardForBillModal.value?.id ?? null),
})

const isPayFormVisible = ref(false)
const payAccountId = ref<string | null>(null)
const payDate = ref<Date>(new Date())
const payAmount = ref<number | null>(null)
const payErrors = reactive<Record<string, string>>({})

const accountOptions = computed(() =>
  accounts.value.map((a) => ({ label: a.name, value: a.id }))
)

const modalTitle = computed(() => {
  const name = creditCardForBillModal.value?.name ?? 'Cartão'
  return `Fatura - ${name}`
})

function handleOpenChange(v: boolean) {
  if (!v) {
    closeCreditCardBillModal()
    resetPayForm()
  }
}

function resetPayForm() {
  isPayFormVisible.value = false
  payAccountId.value = null
  payDate.value = new Date()
  payAmount.value = null
  Object.keys(payErrors).forEach((key) => delete payErrors[key])
}

function openPayForm() {
  if (currentBill.value) {
    payAmount.value = currentBill.value.totalAmount
    payDate.value = new Date()
    payAccountId.value = accountOptions.value[0]?.value ?? null
    isPayFormVisible.value = true
    Object.keys(payErrors).forEach((key) => delete payErrors[key])
  }
}

async function handlePaySubmit() {
  const bill = currentBill.value
  if (!bill) return

  Object.keys(payErrors).forEach((key) => delete payErrors[key])
  if (!payAccountId.value) {
    payErrors.account = 'Selecione a conta'
    return
  }
  if (!payAmount.value || payAmount.value <= 0) {
    payErrors.amount = 'Informe o valor'
    return
  }

  const result = await payBill(bill.id, {
    bankAccountId: payAccountId.value,
    payDate: toDateString(payDate.value),
    amount: payAmount.value,
  })

  if (result) {
    resetPayForm()
    if (result.status === 'PAID') {
      closeCreditCardBillModal()
    } else {
      await refetchBill()
    }
  }
}
</script>

<template>
  <AppModal
    :open="!!creditCardForBillModal"
    :title="modalTitle"
    @update:open="handleOpenChange"
  >
    <div v-if="creditCardForBillModal" class="d-flex flex-column ga-4">
      <div v-if="isBillPending" class="d-flex justify-center py-4">
        <v-progress-circular indeterminate color="primary" />
      </div>
      <template v-else-if="currentBill">
        <div class="bill-summary pa-4 rounded-lg bg-grey-lighten-4">
          <p class="text-body-2 text-medium-emphasis mb-2">
            Fatura de {{ currentBill.month }}/{{ currentBill.year }}
          </p>
          <p class="text-h6 font-weight-bold text-primary">
            {{ formatCurrency(currentBill.totalAmount) }}
          </p>
          <p class="text-caption text-medium-emphasis mt-2">
            Vencimento: {{ currentBill.dueDate }}
          </p>
          <p class="text-caption text-medium-emphasis">
            Fechamento: {{ currentBill.closingDate }}
          </p>
          <v-chip
            :color="currentBill.status === 'PAID' ? 'success' : currentBill.status === 'CLOSED' ? 'warning' : 'primary'"
            size="small"
          >
            {{ currentBill.status === 'PAID' ? 'Paga' : currentBill.status === 'CLOSED' ? 'Fechada' : 'Aberta' }}
          </v-chip>
        </div>

        <template v-if="!isPayFormVisible && currentBill.status !== 'PAID'">
          <AppButton
            variant="outlined"
            color="primary"
            class="w-100"
            @click="openPayForm"
          >
            Pagar fatura
          </AppButton>
        </template>
        <v-form v-else-if="isPayFormVisible" class="d-flex flex-column ga-4" @submit.prevent="handlePaySubmit">
          <AppSelect
            v-model="payAccountId"
            label="Conta de origem"
            :options="accountOptions"
            placeholder="Selecione a conta"
            :error-text="payErrors.account"
          />
          <AppDatePicker
            v-model="payDate"
            placeholder="Data do pagamento"
          />
          <InputCurrency
            v-model="payAmount"
            label="Valor"
            placeholder="0,00"
            value-color="#087f5b"
            :error-text="payErrors.amount"
          />
          <div class="d-flex ga-2">
            <AppButton
              type="submit"
              class="flex-grow-1"
              :loading="isPayLoading"
              :disabled="isPayLoading"
            >
              Confirmar pagamento
            </AppButton>
            <v-btn
              variant="text"
              :disabled="isPayLoading"
              @click="resetPayForm"
            >
              Cancelar
            </v-btn>
          </div>
        </v-form>
      </template>
      <p v-else class="text-body-2 text-medium-emphasis">
        Nenhuma fatura em aberto.
      </p>
    </div>
  </AppModal>
</template>
