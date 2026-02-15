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
      class="new-transaction-form"
      @submit.prevent="handleSubmit"
    >
      <div class="new-transaction-form__fields">
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
        <div class="new-transaction-form__row">
          <AppSelect
            v-model="category"
            label="Categoria"
            :options="categories"
            placeholder="Categoria"
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
      </div>
      <AppButton
        type="submit"
        class="new-transaction-form__submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        {{ newTransactionType === 'INCOME' ? 'Adicionar receita' : 'Adicionar despesa' }}
      </AppButton>
    </v-form>
  </AppModal>
</template>

<style scoped>
.new-transaction-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.new-transaction-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.new-transaction-form__row {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.new-transaction-form__submit {
  width: 100%;
}
</style>
