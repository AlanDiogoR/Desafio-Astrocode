<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import AppDatePicker from '~/components/ui/AppDatePicker.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useEditTransactionModalController } from '~/composables/useEditTransactionModalController'

const { isEditTransactionModalOpen, transactionBeingEdited, closeEditTransactionModal } = useDashboard()

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
  valueColor,
  save,
  handleDelete,
} = useEditTransactionModalController(transactionBeingEdited)

function handleOpenChange(v: boolean) {
  if (!v) closeEditTransactionModal()
}
</script>

<template>
  <AppModal
    title="Editar transação"
    :open="isEditTransactionModalOpen"
    @update:open="handleOpenChange"
  >
    <template #rightAction>
      <button
        type="button"
        aria-label="Excluir transação"
        @click="handleDelete"
      >
        <v-icon icon="mdi-delete-outline" size="24" />
      </button>
    </template>
    <v-form class="edit-transaction-form" @submit.prevent="save">
      <div class="edit-transaction-form__fields">
        <InputCurrency
          v-model="amount"
          label="Valor"
          placeholder="0,00"
          :value-color="valueColor"
          :error-text="errors.amount"
        />
        <AppInput
          v-model="name"
          label="Nome"
          :error-text="errors.name"
        />
        <div class="edit-transaction-form__row">
          <AppSelect
            v-model="category"
            label="Categoria"
            :options="categories"
            placeholder="Categoria"
            :error-text="errors.category"
          />
          <AppSelect
            v-model="account"
            label="Conta"
            :options="accounts"
            placeholder="Conta"
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
        class="edit-transaction-form__submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        Salvar alterações
      </AppButton>
    </v-form>
  </AppModal>
</template>

<style scoped>
.edit-transaction-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-transaction-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-transaction-form__row {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-transaction-form__submit {
  width: 100%;
}
</style>
