<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useNewCreditCardModalController } from '~/composables/useNewCreditCardModalController'

const {
  name,
  creditLimit,
  closingDay,
  dueDay,
  color,
  errors,
  isLoading,
  resetForm,
  handleSubmit,
} = useNewCreditCardModalController()

const { isNewCreditCardModalOpen, closeNewCreditCardModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeNewCreditCardModal()
}

watch(isNewCreditCardModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isNewCreditCardModalOpen"
    title="Novo cartão de crédito"
    @update:open="handleOpenChange"
  >
    <v-form class="d-flex flex-column ga-6" @submit.prevent="handleSubmit">
      <div class="d-flex flex-column ga-4">
        <AppInput
          v-model="name"
          label="Nome do cartão"
          placeholder="Ex: Nubank, Itaú"
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
      <AppButton
        type="submit"
        class="w-100"
        :loading="isLoading"
        :disabled="isLoading"
      >
        Criar cartão
      </AppButton>
    </v-form>
  </AppModal>
</template>
