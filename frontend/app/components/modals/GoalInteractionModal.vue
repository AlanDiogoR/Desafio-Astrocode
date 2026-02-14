<script setup lang="ts">
import AppModal from '~/components/ui/AppModal.vue'
import AppSelect from '~/components/ui/AppSelect.vue'
import InputCurrency from '~/components/ui/InputCurrency.vue'
import AppButton from '~/components/ui/AppButton.vue'
import { useGoalInteractionController } from '~/composables/useGoalInteractionController'

const {
  mode,
  goalId,
  amount,
  bankAccountId,
  goalLockedFromCard,
  canAdd,
  canRemove,
  errors,
  touched,
  markTouched,
  shouldShowError,
  isLoading,
  goalOptions,
  accountOptions,
  resetForm,
  submit,
} = useGoalInteractionController()

const { isNewGoalValueModalOpen, closeNewGoalValueModal } = useDashboard()

const modalTitle = computed(() =>
  mode.value === 'ADD' ? 'Realizar Aporte' : 'Realizar Resgate',
)

const accountSelectLabel = computed(() =>
  mode.value === 'ADD' ? 'Retirar da conta' : 'Depositar na conta',
)

const submitLabel = computed(() =>
  mode.value === 'ADD' ? 'Confirmar Aporte' : 'Confirmar Resgate',
)

const inputValueColor = computed(() =>
  mode.value === 'ADD' ? '#12b886' : '#ff6b6b',
)

const isSubmitDisabled = computed(() => {
  if (mode.value === 'ADD' && !canAdd.value) return true
  if (mode.value === 'REMOVE' && !canRemove.value) return true
  return isLoading.value
})

function handleOpenChange(v: boolean) {
  if (!v) closeNewGoalValueModal()
}

watch(isNewGoalValueModalOpen, (open: boolean) => {
  if (!open) resetForm()
})
</script>

<template>
  <AppModal
    :open="isNewGoalValueModalOpen"
    :title="modalTitle"
    @update:open="handleOpenChange"
  >
    <div class="goal-interaction-modal">
      <v-form class="goal-interaction-modal__form" @submit.prevent="submit">
        <div class="goal-interaction-modal__fields">
          <InputCurrency
            v-model="amount"
            label="Valor"
            placeholder="0,00"
            :value-color="inputValueColor"
            :error-text="shouldShowError('amount') ? errors.amount : ''"
            @blur="markTouched('amount')"
          />
          <AppSelect
            v-if="!goalLockedFromCard"
            v-model="goalId"
            label="Meta"
            :options="goalOptions"
            placeholder="Selecione a meta"
            :error-text="shouldShowError('goalId') ? errors.goalId : ''"
            @update:model-value="markTouched('goalId')"
          />
          <AppSelect
            v-model="bankAccountId"
            :label="accountSelectLabel"
            :options="accountOptions"
            placeholder="Selecione a conta"
            :error-text="shouldShowError('bankAccountId') ? errors.bankAccountId : ''"
            @update:model-value="markTouched('bankAccountId')"
          />
        </div>
        <AppButton
          type="submit"
          class="goal-interaction-modal__submit"
          :loading="isLoading"
          :disabled="isSubmitDisabled"
          color="primary"
        >
          {{ submitLabel }}
        </AppButton>
      </v-form>
    </div>
  </AppModal>
</template>

<style scoped>
.goal-interaction-modal {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.goal-interaction-modal__form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.goal-interaction-modal__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.goal-interaction-modal__submit {
  width: 100%;
}
</style>
