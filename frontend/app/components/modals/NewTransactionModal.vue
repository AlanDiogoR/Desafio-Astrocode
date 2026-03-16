<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import AppDatePicker from '~/components/ui/AppDatePicker.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useNewTransactionModalController } from '~/composables/useNewTransactionModalController'

const {
  amount,
  name,
  category,
  account,
  creditCard,
  date,
  isRecurring,
  errors,
  isLoading,
  categories,
  accounts,
  creditCardsOptions,
  sourceMode,
  title,
  valueColor,
  valueLabel,
  accountLabel,
  accountPlaceholder,
  showAccountSelector,
  showCreditCardSelector,
  availableLimit,
  resetForm,
  handleSubmit,
} = useNewTransactionModalController()

const { isNewTransactionModalOpen, newTransactionType, closeNewTransactionModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeNewTransactionModal()
}

watch(isNewTransactionModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isNewTransactionModalOpen"
    :title="title"
    @update:open="handleOpenChange"
  >
    <v-form
      v-if="newTransactionType"
      class="d-flex flex-column ga-6"
      @submit.prevent="handleSubmit"
    >
      <div class="d-flex flex-column ga-4">
        <InputCurrency
          v-model="amount"
          :label="valueLabel"
          placeholder="0,00"
          :value-color="valueColor"
          :error-text="errors.amount"
        />
        <AppInput
          v-model="name"
          label="Nome"
          :error-text="errors.name"
        />
        <div class="d-flex flex-column ga-4">
          <AppSelect
            v-model="category"
            label="Categoria"
            :options="categories"
            placeholder="Selecione uma categoria"
            :error-text="errors.category"
            :scrollable="newTransactionType === 'EXPENSE'"
          />
          <v-radio-group
            v-if="newTransactionType === 'EXPENSE'"
            v-model="sourceMode"
            inline
            density="compact"
            hide-details
            class="mt-0 mb-0"
          >
            <v-radio label="Débito" value="DEBIT" />
            <v-radio label="Crédito" value="CREDIT" />
          </v-radio-group>
          <AppSelect
            v-if="showAccountSelector"
            v-model="account"
            :label="accountLabel"
            :options="accounts"
            :placeholder="accountPlaceholder"
            :error-text="errors.account"
          />
          <template v-if="showCreditCardSelector">
            <AppSelect
              v-model="creditCard"
              :label="accountLabel"
              :options="creditCardsOptions"
              :placeholder="accountPlaceholder"
              :error-text="errors.account"
            />
            <p v-if="availableLimit > 0" class="text-caption text-medium-emphasis mt-n2">
              Limite disponível: {{ new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(availableLimit) }}
            </p>
          </template>
        </div>
        <AppDatePicker
          v-model="date"
          placeholder="Data da transação"
          :error-text="errors.date"
        />
        <v-switch
          v-model="isRecurring"
          color="primary"
          hide-details
          density="compact"
          class="mt-2"
          label="Repetir mensalmente?"
        />
      </div>
      <AppButton
        type="submit"
        class="w-100"
        :loading="isLoading"
        :disabled="isLoading"
      >
        {{ newTransactionType === 'INCOME' ? 'Adicionar receita' : 'Adicionar despesa' }}
      </AppButton>
    </v-form>
  </AppModal>
</template>

