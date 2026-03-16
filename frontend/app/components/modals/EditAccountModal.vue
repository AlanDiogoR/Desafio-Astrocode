<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useEditAccountModalController } from '~/composables/useEditAccountModalController'

const {
  accountId,
  balance,
  name,
  type,
  color,
  errors,
  markTouched,
  shouldShowError,
  isLoading,
  accountTypeOptions,
  loadAccount,
  resetForm,
  handleSubmit,
  openDeleteConfirm,
} = useEditAccountModalController()

const { isEditAccountModalOpen, closeEditAccountModal, accountBeingEdited } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeEditAccountModal()
}

watch(isEditAccountModalOpen, (open: boolean) => {
  if (open) {
    loadAccount(accountBeingEdited.value)
  } else {
    resetForm()
  }
})
</script>

<template>
  <AppModal
    :open="isEditAccountModalOpen"
    title="Editar conta"
    @update:open="handleOpenChange"
  >
    <template #rightAction>
      <button
        type="button"
        class="edit-account-delete-btn"
        aria-label="Excluir conta"
        :disabled="!accountId"
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
    <v-form class="edit-account-form" @submit.prevent="handleSubmit">
      <div class="edit-account-form__fields">
        <InputCurrency
          v-model="balance"
          label="Saldo atual"
          placeholder="0,00"
          :error-text="shouldShowError('balance') ? errors.balance : ''"
          @blur="markTouched('balance')"
        />
        <AppInput
          v-model="name"
          label="Nome da conta"
          :error-text="shouldShowError('name') ? errors.name : ''"
          @blur="markTouched('name')"
        />
        <AppSelect
          v-model="type"
          label="Tipo de Conta"
          :options="accountTypeOptions"
          placeholder="Selecione o tipo"
          :error-text="shouldShowError('type') ? errors.type : ''"
          @update:model-value="markTouched('type')"
        />
        <AppColorDropdown
          v-model="color"
          label="Cor"
          :error-text="shouldShowError('color') ? errors.color : ''"
          @update:model-value="markTouched('color')"
        />
      </div>
      <div class="edit-account-form__footer">
        <AppButton
          type="submit"
          class="edit-account-form__submit"
          :loading="isLoading"
          :disabled="isLoading"
        >
          Salvar alterações
        </AppButton>
        <v-btn
          variant="outlined"
          color="error"
          class="edit-account-form__delete"
          :disabled="isLoading"
          @click="openDeleteConfirm"
        >
          Excluir conta
        </v-btn>
      </div>
    </v-form>
  </AppModal>
</template>

<style scoped>
.edit-account-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-account-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-account-form__footer {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-account-form__submit {
  width: 100%;
}

.edit-account-form__delete {
  width: 100%;
}

.edit-account-delete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  min-width: 40px;
}

.edit-account-delete-btn:not(:disabled) {
  cursor: pointer;
}

.trash-icon {
  pointer-events: none;
  filter: brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%);
}
</style>
