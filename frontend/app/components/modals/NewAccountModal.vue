<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useNewAccountModalController } from '~/composables/useNewAccountModalController'

const {
  balance,
  name,
  type,
  color,
  errors,
  markTouched,
  shouldShowError,
  isLoading,
  accountTypeOptions,
  resetForm,
  handleSubmit,
} = useNewAccountModalController()

const { isNewAccountModalOpen, closeNewAccountModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeNewAccountModal()
}

watch(isNewAccountModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isNewAccountModalOpen"
    title="Nova conta"
    @update:open="handleOpenChange"
  >
    <v-form class="new-account-form" @submit.prevent="handleSubmit">
      <div class="new-account-form__fields">
        <InputCurrency
          v-model="balance"
          label="Saldo Inicial"
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
      <AppButton
        type="submit"
        class="new-account-form__submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        Criar conta
      </AppButton>
    </v-form>
  </AppModal>
</template>

<style scoped>
.new-account-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.new-account-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.new-account-form__submit {
  width: 100%;
}
</style>
