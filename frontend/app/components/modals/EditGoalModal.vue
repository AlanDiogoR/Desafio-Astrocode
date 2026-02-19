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
} = useEditGoalModalController()

const { isEditGoalModalOpen, closeEditGoalModal, openConfirmDeleteModal } = useDashboard()

function handleOpenChange(v: boolean) {
  if (!v) closeEditGoalModal()
}

watch(isEditGoalModalOpen, (open: boolean) => {
  if (!open) resetForm()
})

function handleTrashClick() {
  if (goalId.value) openConfirmDeleteModal('GOAL', goalId.value)
}
</script>

<template>
  <AppModal
    :open="isEditGoalModalOpen"
    title="Editar meta"
    @update:open="handleOpenChange"
  >
    <template #rightAction>
      <button
        type="button"
        class="edit-goal-trash-btn"
        aria-label="Excluir meta"
        :disabled="!goalId"
        @click="handleTrashClick"
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

.edit-goal-trash-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  min-width: 40px;
}

.edit-goal-trash-btn:not(:disabled) {
  cursor: pointer;
}

.trash-icon {
  pointer-events: none;
  filter: brightness(0) saturate(100%) invert(18%) sepia(74%) saturate(5865%) hue-rotate(356deg) brightness(101%) contrast(115%);
}
</style>
