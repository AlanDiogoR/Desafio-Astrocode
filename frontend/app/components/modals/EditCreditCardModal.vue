<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useEditCreditCardModalController } from '~/composables/useEditCreditCardModalController'

const {
  creditCardId,
  name,
  creditLimit,
  closingDay,
  dueDay,
  color,
  errors,
  isLoading,
  loadCreditCard,
  resetForm,
  handleSubmit,
  openDeleteConfirm,
} = useEditCreditCardModalController()

const { isEditCreditCardModalOpen, closeEditCreditCardModal, creditCardBeingEdited } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeEditCreditCardModal()
}

watch(isEditCreditCardModalOpen, (open: boolean) => {
  if (open) {
    loadCreditCard(creditCardBeingEdited.value)
  } else {
    resetForm()
  }
})
</script>

<template>
  <AppModal
    :open="isEditCreditCardModalOpen"
    title="Editar cartão de crédito"
    @update:open="handleOpenChange"
  >
    <template #rightAction>
      <button
        type="button"
        class="edit-credit-card-delete-btn"
        aria-label="Excluir cartão"
        :disabled="!creditCardId"
        @click="openDeleteConfirm"
      >
        <img
          src="/images/Nome=Deletar.svg"
          alt=""
          width="20"
          height="20"
          class="trash-icon"
        >
      </button>
    </template>
    <v-form class="d-flex flex-column ga-6" @submit.prevent="handleSubmit">
      <div class="d-flex flex-column ga-4">
        <AppInput
          v-model="name"
          label="Nome do cartão"
          :error-text="errors.name"
        />
        <InputCurrency
          v-model="creditLimit"
          label="Limite de crédito"
          placeholder="0,00"
          value-color="#087f5b"
          :error-text="errors.creditLimit"
        />
        <div class="d-flex ga-4">
          <AppInput
            v-model.number="closingDay"
            type="number"
            label="Dia do fechamento"
            placeholder="1-28"
            :error-text="errors.closingDay"
          />
          <AppInput
            v-model.number="dueDay"
            type="number"
            label="Dia do vencimento"
            placeholder="1-28"
            :error-text="errors.dueDay"
          />
        </div>
        <AppColorDropdown
          v-model="color"
          label="Cor"
          :error-text="errors.color"
        />
      </div>
      <div class="d-flex flex-column ga-3">
        <AppButton
          type="submit"
          class="w-100"
          :loading="isLoading"
          :disabled="isLoading"
        >
          Salvar alterações
        </AppButton>
        <v-btn
          variant="outlined"
          color="error"
          :disabled="isLoading"
          @click="openDeleteConfirm"
        >
          Excluir cartão
        </v-btn>
      </div>
    </v-form>
  </AppModal>
</template>

<style scoped>
.edit-credit-card-delete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  min-width: 40px;
}

.edit-credit-card-delete-btn:not(:disabled) {
  cursor: pointer;
}

.trash-icon {
  pointer-events: none;
  filter: brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%);
}
</style>
