<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import AppInput from '~/components/ui/AppInput.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppDatePicker from '~/components/ui/AppDatePicker.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useNewGoalModalController } from '~/composables/useNewGoalModalController'

const {
  name,
  targetAmount,
  deadline,
  color,
  errors,
  markTouched,
  shouldShowError,
  isLoading,
  resetForm,
  handleSubmit,
} = useNewGoalModalController()

const { isNewGoalModalOpen, closeNewGoalModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeNewGoalModal()
}

watch(isNewGoalModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isNewGoalModalOpen"
    title="Nova meta"
    @update:open="handleOpenChange"
  >
    <v-form class="new-goal-form" @submit.prevent="handleSubmit">
      <div class="new-goal-form__fields">
        <InputCurrency
          v-model="targetAmount"
          label="Valor do Objetivo"
          placeholder="0,00"
          :error-text="shouldShowError('targetAmount') ? errors.targetAmount : ''"
          @blur="markTouched('targetAmount')"
        />
        <AppInput
          v-model="name"
          label="Nome meta"
          :error-text="shouldShowError('name') ? errors.name : ''"
          @blur="markTouched('name')"
        />
        <AppDatePicker
          v-model="deadline"
          placeholder="Data final"
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
        class="new-goal-form__submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        Criar meta
      </AppButton>
    </v-form>
  </AppModal>
</template>

<style scoped>
.new-goal-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.new-goal-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.new-goal-form__submit {
  width: 100%;
}
</style>
