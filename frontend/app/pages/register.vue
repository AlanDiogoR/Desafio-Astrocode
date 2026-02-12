<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const {
  name,
  email,
  password,
  isLoading,
  nameError,
  emailError,
  passwordError,
  handleRegister,
  clearFieldError,
} = useAuthForm()

const { validateField } = useFieldValidation()

const showPassword = ref(false)
const hasAttemptedSubmit = ref(false)

const nameRules: Array<(v: string) => boolean | string> = [
  (v: string) => !!v || 'Nome é obrigatório',
  (v: string) => !v || v.length >= 3 || 'Nome deve ter no mínimo 3 caracteres',
]

const emailRules: Array<(v: string) => boolean | string> = [
  (v: string) => !!v || 'E-mail é obrigatório',
  (v: string) => /.+@.+\..+/.test(v || '') || 'E-mail inválido',
]

const passwordRules: Array<(v: string) => boolean | string> = [
  (v: string) => !!v || 'Senha é obrigatória',
  (v: string) => !v || v.length >= 8 || 'Senha deve ter no mínimo 8 caracteres',
]

const nameFieldError = computed(() => {
  if (!hasAttemptedSubmit.value) return nameError.value
  return validateField(name.value, nameRules) || nameError.value
})

const emailFieldError = computed(() => {
  if (!hasAttemptedSubmit.value) return emailError.value
  return validateField(email.value, emailRules) || emailError.value
})

const passwordFieldError = computed(() => {
  if (!hasAttemptedSubmit.value) return passwordError.value
  return validateField(password.value, passwordRules) || passwordError.value
})

async function onSubmit() {
  hasAttemptedSubmit.value = true
  const nameValidation = validateField(name.value, nameRules)
  const emailValidation = validateField(email.value, emailRules)
  const passwordValidation = validateField(password.value, passwordRules)
  if (nameValidation || emailValidation || passwordValidation) return
  await handleRegister()
}
</script>

<template>
  <div
    class="mx-auto text-center"
    style="max-width: 440px; width: 100%;"
  >
    <div class="d-flex justify-center mb-2">
      <AppLogo color="#868E96" :size="28" />
    </div>
    <h1 class="page-title text-h4 font-weight-bold text-center mb-1 mt-0">Crie sua conta</h1>
    <p class="auth-subtitle text-body-1 text-center mb-8">Acesse sua plataforma de controle financeiro</p>

    <v-form class="w-100" @submit.prevent="onSubmit">
      <AppInput
        v-model="name"
        label="Nome"
        type="text"
        :rules="nameRules"
        :field-error="nameFieldError"
        :disabled="isLoading"
        class="mb-4"
        @clear-error="clearFieldError('name')"
      />

      <AppInput
        v-model="email"
        label="E-mail"
        type="email"
        :rules="emailRules"
        :field-error="emailFieldError"
        :disabled="isLoading"
        class="mb-4"
        @clear-error="clearFieldError('email')"
      />

      <AppInput
        v-model="password"
        label="Senha"
        :type="showPassword ? 'text' : 'password'"
        :rules="passwordRules"
        :field-error="passwordFieldError"
        :disabled="isLoading"
        class="mb-6"
        @clear-error="clearFieldError('password')"
      >
        <template #append-inner>
          <v-icon
            :icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            class="cursor-pointer"
            @click="showPassword = !showPassword"
          />
        </template>
      </AppInput>

      <AppButton type="submit" :loading="isLoading" block>
        Criar conta
      </AppButton>
    </v-form>

    <p class="page-footer mt-8 text-center">
      Já possui uma conta?
      <NuxtLink to="/login" class="footer-link">Fazer login</NuxtLink>
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
  color: #868E96; /* Gray-7 - cinza suave */
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

@media (max-width: 599px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
