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
  date,
  isRecurring,
  errors,
  isLoading,
  categories,
  accounts,
  title,
  valueColor,
  valueLabel,
  accountLabel,
  accountPlaceholder,
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
          <AppSelect
            v-model="account"
            :label="accountLabel"
            :options="accounts"
            :placeholder="accountPlaceholder"
            :error-text="errors.account"
          />
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

