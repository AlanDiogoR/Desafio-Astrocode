<script setup lang="ts">
import { ExitIcon } from '@radix-icons/vue'
import AppModal from '~/components/ui/AppModal.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppButton from '~/components/ui/AppButton.vue'
import AppDropdown from '~/components/ui/AppDropdown.vue'
import { useEditProfileModalController } from '~/composables/useEditProfileModalController'
import { useAuthStore } from '~/stores/auth'

const {
  name,
  currentPassword,
  newPassword,
  confirmPassword,
  errors,
  markTouched,
  shouldShowError,
  isLoading,
  initForm,
  handleSubmit,
} = useEditProfileModalController()

const {
  isEditProfileModalOpen,
  closeEditProfileModal,
} = useDashboard()

const authStore = useAuthStore()

const logoutItems = [
  {
    label: 'Sair',
    icon: ExitIcon,
    danger: true,
    action: () => {
      closeEditProfileModal()
      authStore.logout()
    },
  },
]

function handleOpenChange(v: boolean) {
  if (!v) closeEditProfileModal()
}

function submit() {
  handleSubmit(closeEditProfileModal)
}

watch(isEditProfileModalOpen, (open) => {
  if (open) initForm()
})
</script>

<template>
  <AppModal
    :open="isEditProfileModalOpen"
    title="Editar perfil"
    @update:open="handleOpenChange"
  >
    <template #rightAction>
      <AppDropdown
        :items="logoutItems"
        content-side="bottom"
        content-align="end"
        :content-z-index="2500"
      >
        <template #trigger>
          <button
            type="button"
            aria-label="Sair"
            class="edit-profile-logout-btn"
          >
            <ExitIcon width="24" height="24" />
          </button>
        </template>
      </AppDropdown>
    </template>

    <v-form class="edit-profile-form" @submit.prevent="submit">
      <div class="edit-profile-form__fields">
        <AppInput
          v-model="name"
          label="Nome"
          :field-error="shouldShowError('name') ? errors.name : ''"
          :disabled="isLoading"
          @blur="markTouched('name')"
        />
        <div class="edit-profile-form__section">
          <p class="edit-profile-form__section-label">Alterar senha (opcional)</p>
          <AppInput
            v-model="currentPassword"
            label="Senha atual"
            type="password"
            :field-error="shouldShowError('currentPassword') ? errors.currentPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('currentPassword')"
          />
          <AppInput
            v-model="newPassword"
            label="Nova senha"
            type="password"
            :field-error="shouldShowError('newPassword') ? errors.newPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('newPassword')"
          />
          <AppInput
            v-model="confirmPassword"
            label="Confirmar nova senha"
            type="password"
            :field-error="shouldShowError('confirmPassword') ? errors.confirmPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('confirmPassword')"
          />
        </div>
      </div>
      <AppButton
        type="submit"
        class="edit-profile-form__submit"
        :loading="isLoading"
        :disabled="isLoading"
      >
        Salvar
      </AppButton>
    </v-form>
  </AppModal>
</template>

<style scoped>
.edit-profile-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.edit-profile-form__fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-profile-form__section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-profile-form__section-label {
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.edit-profile-form__submit {
  width: 100%;
}

.edit-profile-logout-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 8px;
  color: #ef4444;
  transition: color 0.2s, background-color 0.2s;
}

.edit-profile-logout-btn:hover {
  color: #b91c1c;
  background-color: #fef2f2;
}
</style>
