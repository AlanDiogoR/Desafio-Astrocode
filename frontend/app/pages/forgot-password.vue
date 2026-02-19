<script setup lang="ts">
import { z } from 'zod'
import { getErrorMessage } from '~/utils/errorHandler'
import { requestPasswordReset } from '~/services/auth/requestPasswordReset'
import { resetPassword as resetPasswordApi } from '~/services/auth/resetPassword'

definePageMeta({
  layout: 'auth',
})

const emailSchema = z.string().min(1, 'Campo obrigatório').email('E-mail inválido')
const codeSchema = z.string().min(6, 'Código deve ter 6 caracteres').max(6, 'Código deve ter 6 caracteres')
const passwordSchema = z.string().min(8, 'A senha deve ter pelo menos 8 caracteres')

const step = ref<1 | 2>(1)
const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const emailError = ref('')
const codeError = ref('')
const newPasswordError = ref('')
const confirmPasswordError = ref('')

const isRequestingCode = ref(false)
const isResetting = ref(false)

const authStore = useAuthStore()
const toast = useNuxtApp().$toast as typeof import('vue3-hot-toast').default

const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

async function handleRequestCode() {
  emailError.value = ''
  const result = emailSchema.safeParse(email.value.trim())
  if (!result.success) {
    emailError.value = result.error.issues[0]?.message ?? 'E-mail inválido'
    return
  }
  isRequestingCode.value = true
  try {
    await requestPasswordReset(result.data)
    toast.success('Código enviado para o seu e-mail.')
    step.value = 2
  } catch (err: unknown) {
    toast.error(getErrorMessage(err, 'Falha ao enviar código. Tente novamente.'))
  } finally {
    isRequestingCode.value = false
  }
}

function validateStep2(): boolean {
  codeError.value = ''
  newPasswordError.value = ''
  confirmPasswordError.value = ''

  const codeResult = codeSchema.safeParse(code.value.trim())
  if (!codeResult.success) {
    codeError.value = codeResult.error.issues[0]?.message ?? 'Código inválido'
    return false
  }

  const passResult = passwordSchema.safeParse(newPassword.value)
  if (!passResult.success) {
    newPasswordError.value = passResult.error.issues[0]?.message ?? 'Senha inválida'
    return false
  }

  if (newPassword.value !== confirmPassword.value) {
    confirmPasswordError.value = 'As senhas não coincidem'
    return false
  }
  return true
}

async function handleResetPassword() {
  if (!validateStep2()) return
  isResetting.value = true
  try {
    const response = await resetPasswordApi({
      email: email.value.trim().toLowerCase(),
      code: code.value.trim(),
      newPassword: newPassword.value,
    })
    authStore.setToken(response.token)
    authStore.setUser({
      id: response.id,
      name: response.name,
      email: response.email,
    })
    toast.success('Senha alterada com sucesso!')
    toast.success('Entrando...', { duration: 1000 })
    await navigateTo('/dashboard')
  } catch (err: unknown) {
    toast.error(getErrorMessage(err, 'Código inválido ou expirado. Solicite um novo.'))
  } finally {
    isResetting.value = false
  }
}

function goBack() {
  step.value = 1
  code.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  codeError.value = ''
  newPasswordError.value = ''
  confirmPasswordError.value = ''
}
</script>

<template>
  <div
    class="mx-auto text-center"
    style="max-width: 440px; width: 100%;"
  >
    <div class="d-flex justify-center mb-2">
      <AppLogo
        color="#868E96"
        :size="28"
      />
    </div>
    <h1 class="page-title text-h4 font-weight-bold text-center mb-1 mt-0">
      {{ step === 1 ? 'Recuperar senha' : 'Nova senha' }}
    </h1>
    <p class="auth-subtitle text-body-1 text-center mb-8">
      {{ step === 1 ? 'Informe seu e-mail para receber o código de recuperação' : 'Digite o código e defina uma nova senha' }}
    </p>

    <template v-if="step === 1">
      <v-form
        class="w-100"
        @submit.prevent="handleRequestCode"
      >
        <ClientOnly>
          <AppInput
            v-model="email"
            label="E-mail"
            type="email"
            :field-error="emailError"
            :disabled="isRequestingCode"
            class="mb-4"
          />
        </ClientOnly>
        <AppButton
          type="submit"
          :loading="isRequestingCode"
          :disabled="isRequestingCode"
          block
        >
          Enviar código
        </AppButton>
      </v-form>
    </template>

    <template v-else>
      <v-form
        class="w-100"
        @submit.prevent="handleResetPassword"
      >
        <ClientOnly>
          <AppInput
            v-model="code"
            label="Código (6 caracteres)"
            type="text"
            inputmode="text"
            autocomplete="one-time-code"
            :field-error="codeError"
            :disabled="isResetting"
            class="mb-4"
            maxlength="6"
            placeholder="Ex: A1B2C3"
          />
          <AppInput
            v-model="newPassword"
            label="Nova senha"
            :type="showNewPassword ? 'text' : 'password'"
            :field-error="newPasswordError"
            :disabled="isResetting"
            class="mb-4"
          >
            <template #append-inner>
              <v-icon
                :icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="cursor-pointer"
                @click="showNewPassword = !showNewPassword"
              />
            </template>
          </AppInput>
          <AppInput
            v-model="confirmPassword"
            label="Confirmar senha"
            :type="showConfirmPassword ? 'text' : 'password'"
            :field-error="confirmPasswordError"
            :disabled="isResetting"
            class="mb-6"
          >
            <template #append-inner>
              <v-icon
                :icon="showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'"
                class="cursor-pointer"
                @click="showConfirmPassword = !showConfirmPassword"
              />
            </template>
          </AppInput>
        </ClientOnly>
        <AppButton
          type="submit"
          :loading="isResetting"
          :disabled="isResetting"
          block
        >
          Redefinir senha
        </AppButton>
      </v-form>
    </template>

    <p class="page-footer mt-8 text-center">
      <template v-if="step === 1">
        Lembrou a senha?
        <NuxtLink
          to="/login"
          class="footer-link"
        >Entrar</NuxtLink>
      </template>
      <template v-else>
        <button
          type="button"
          class="footer-link-btn"
          :disabled="isResetting"
          @click="goBack"
        >
          Voltar
        </button>
      </template>
    </p>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #212529;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.auth-subtitle {
  color: #868E96;
  font-weight: 400;
  line-height: 1.5;
}

.page-footer {
  font-size: 14px;
  color: #495057;
  margin: 0;
}

.footer-link {
  color: #087F5B;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}

.footer-link:hover {
  text-decoration: underline;
}

.footer-link-btn {
  background: none;
  border: none;
  color: #087F5B;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
}

.footer-link-btn:hover:not(:disabled) {
  text-decoration: underline;
}

.footer-link-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 599px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
