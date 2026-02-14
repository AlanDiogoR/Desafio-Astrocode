<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppDatePicker from '~/components/ui/AppDatePicker.vue'
import AppColorDropdown from '~/components/ui/AppColorDropdown.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useEditGoalModalController } from '~/composables/useEditGoalModalController'

const {
  goalId,
  name,
  targetAmount,
  deadline,
  color,
  errors,
  markTouched,
  shouldShowError,
  isLoading,
  goalOptions,
  resetForm,
  handleSubmit,
  handleDelete,
} = useEditGoalModalController()

const { isEditGoalModalOpen, closeEditGoalModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeEditGoalModal()
}

watch(isEditGoalModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isEditGoalModalOpen"
    title="Editar meta"
    @update:open="handleOpenChange"
  >
    <v-form class="edit-goal-form" @submit.prevent="handleSubmit">
      <div class="edit-goal-form__fields">
        <AppSelect
          v-model="goalId"
          label="Meta"
          :options="goalOptions"
          placeholder="Selecione a meta"
          :error-text="shouldShowError('goalId') ? errors.goalId : ''"
          @update:model-value="markTouched('goalId')"
        />
        <AppInput
          v-model="name"
          label="Nome da meta"
          placeholder="Ex: Comprar Carro"
          :error-text="shouldShowError('name') ? errors.name : ''"
          @blur="markTouched('name')"
        />
        <InputCurrency
          v-model="targetAmount"
          label="Valor do Objetivo"
          placeholder="0,00"
          :error-text="shouldShowError('targetAmount') ? errors.targetAmount : ''"
          @blur="markTouched('targetAmount')"
        />
        <AppDatePicker
          v-model="deadline"
          placeholder="Data limite"
        />
        <AppColorDropdown
          v-model="color"
          label="Cor"
          :error-text="shouldShowError('color') ? errors.color : ''"
          @update:model-value="markTouched('color')"
        />
      </div>
      <div class="edit-goal-form__footer">
        <AppButton
          type="submit"
          class="edit-goal-form__submit"
          :loading="isLoading"
          :disabled="isLoading"
        >
          Salvar alterações
        </AppButton>
        <button
          type="button"
          class="edit-goal-form__delete"
          :disabled="isLoading || !goalId"
          @click="handleDelete"
        >
          Excluir meta
        </button>
      </div>
    </v-form>
  </AppModal>
</template>

<style scoped>
.edit-goal-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-goal-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-goal-form__footer {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-goal-form__submit {
  width: 100%;
}

.edit-goal-form__delete {
  background: none;
  border: none;
  padding: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #dc2626;
  cursor: pointer;
  transition: opacity 0.2s;
}

.edit-goal-form__delete:hover:not(:disabled) {
  opacity: 0.9;
}

.edit-goal-form__delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
