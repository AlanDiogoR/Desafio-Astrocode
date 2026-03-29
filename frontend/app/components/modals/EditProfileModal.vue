<script setup lang="ts">
import { ExitIcon } from '@radix-icons/vue'
import AppModal from '~/components/ui/AppModal.vue'
import AppInput from '~/components/ui/AppInput.vue'
import AppButton from '~/components/ui/AppButton.vue'
import AppDropdown from '~/components/ui/AppDropdown.vue'
import { useEditProfileModalController } from '~/composables/useEditProfileModalController'
import { useLogout } from '~/composables/useLogout'
import { deleteAccount } from '~/services/user/deleteAccount'

const { performLogout } = useLogout()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

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

const logoutItems = [
  {
    label: 'Sair',
    icon: ExitIcon,
    danger: true,
    action: () => {
      performLogout()
    },
  },
]

function handleOpenChange(v: boolean) {
  if (!v) closeEditProfileModal()
}

const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const deleteDialogOpen = ref(false)
const deletePassword = ref('')
const deleteLoading = ref(false)

function togglePasswordVisibility(field: 'current' | 'new' | 'confirm') {
  if (field === 'current') showCurrentPassword.value = !showCurrentPassword.value
  else if (field === 'new') showNewPassword.value = !showNewPassword.value
  else showConfirmPassword.value = !showConfirmPassword.value
}

function submit() {
  handleSubmit(closeEditProfileModal)
}

watch(isEditProfileModalOpen, (open) => {
  if (open) initForm()
})

async function confirmDeleteAccount() {
  if (!deletePassword.value.trim()) {
    toast.error('Informe sua senha para excluir a conta.')
    return
  }
  deleteLoading.value = true
  try {
    await deleteAccount(deletePassword.value)
    deleteDialogOpen.value = false
    deletePassword.value = ''
    closeEditProfileModal()
    toast.success('Conta excluída.')
    await performLogout()
  } catch {
    toast.error('Não foi possível excluir. Verifique a senha.')
  } finally {
    deleteLoading.value = false
  }
}
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
            :type="showCurrentPassword ? 'text' : 'password'"
            :field-error="shouldShowError('currentPassword') ? errors.currentPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('currentPassword')"
          >
            <template #append-inner>
              <v-icon
                :icon="showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="password-toggle-icon"
                @click="togglePasswordVisibility('current')"
              />
            </template>
          </AppInput>
          <AppInput
            v-model="newPassword"
            label="Nova senha"
            :type="showNewPassword ? 'text' : 'password'"
            :field-error="shouldShowError('newPassword') ? errors.newPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('newPassword')"
          >
            <template #append-inner>
              <v-icon
                :icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="password-toggle-icon"
                @click="togglePasswordVisibility('new')"
              />
            </template>
          </AppInput>
          <AppInput
            v-model="confirmPassword"
            label="Confirmar nova senha"
            :type="showConfirmPassword ? 'text' : 'password'"
            :field-error="shouldShowError('confirmPassword') ? errors.confirmPassword : ''"
            :disabled="isLoading"
            @blur="markTouched('confirmPassword')"
          >
            <template #append-inner>
              <v-icon
                :icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="password-toggle-icon"
                @click="togglePasswordVisibility('confirm')"
              />
            </template>
          </AppInput>
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

      <div class="edit-profile-danger-zone">
        <p class="edit-profile-danger-zone__label">Zona de risco</p>
        <button
          type="button"
          class="edit-profile-delete-btn"
          @click="deleteDialogOpen = true"
        >
          Excluir minha conta
        </button>
      </div>
    </v-form>

    <v-dialog v-model="deleteDialogOpen" max-width="420" persistent>
      <v-card rounded="xl" class="pa-6">
        <v-card-title class="text-h6 pa-0 mb-2">Excluir conta</v-card-title>
        <p class="text-body-2 text-medium-emphasis mb-4">
          Esta ação é permanente e apaga transações, metas, contas e dados vinculados. Digite sua senha para confirmar.
        </p>
        <AppInput
          v-model="deletePassword"
          label="Senha atual"
          type="password"
          autocomplete="current-password"
          class="mb-4"
        />
        <div class="d-flex gap-2 justify-end">
          <v-btn variant="text" @click="deleteDialogOpen = false">Cancelar</v-btn>
          <v-btn color="error" :loading="deleteLoading" @click="confirmDeleteAccount">
            Excluir definitivamente
          </v-btn>
        </div>
      </v-card>
    </v-dialog>
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

.password-toggle-icon {
  cursor: pointer;
  color: #6b7280;
  transition: color 0.2s;
}

.password-toggle-icon:hover {
  color: #087f5b;
}

.edit-profile-danger-zone {
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.edit-profile-danger-zone__label {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.edit-profile-delete-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  color: #dc2626;
  font-size: 14px;
  font-weight: 600;
  text-decoration: underline;
}

.edit-profile-delete-btn:hover {
  color: #991b1b;
}
</style>
