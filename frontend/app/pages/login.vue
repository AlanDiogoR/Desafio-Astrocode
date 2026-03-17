<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const {
  email,
  password,
  loginMutation,
  emailErrorDisplay,
  passwordErrorDisplay,
  handleLogin,
  clearFieldError,
  markAsTouched,
} = useAuthForm()

const isPending = computed(() => Boolean(unref(loginMutation.isPending)))
const showPassword = ref(false)

async function onSubmit() {
  await handleLogin()
}
</script>

<template>
  <div class="mx-auto text-center login-container">
    <div class="d-flex justify-center mb-2">
      <AppLogo color="#868E96" :size="28" />
    </div>
    <h1 class="page-title text-h4 font-weight-bold text-center mb-1 mt-0">Entrar na sua conta</h1>
    <p class="auth-subtitle text-body-1 text-center mb-8">Acesse sua plataforma de controle financeiro</p>

    <v-form class="w-100" @submit.prevent="onSubmit">
      <AppInput
        v-model="email"
        label="E-mail"
        type="email"
        autocomplete="email"
        :field-error="emailErrorDisplay"
        :disabled="isPending"
        class="mb-4"
        @clear-error="clearFieldError('email')"
        @blur="markAsTouched('email')"
      />

      <AppInput
        v-model="password"
        label="Senha"
        :type="showPassword ? 'text' : 'password'"
        autocomplete="current-password"
        :field-error="passwordErrorDisplay"
        :disabled="isPending"
        class="mb-2"
        @clear-error="clearFieldError('password')"
        @blur="markAsTouched('password')"
      >
        <template #append-inner>
          <v-icon
            :icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            class="cursor-pointer"
            :aria-label="showPassword ? 'Ocultar senha' : 'Mostrar senha'"
            @click="showPassword = !showPassword"
          />
        </template>
      </AppInput>
      <div class="text-right mb-6">
        <NuxtLink
          to="/forgot-password"
          class="forgot-password-link"
        >
          Esqueceu a senha?
        </NuxtLink>
      </div>

      <AppButton
        type="submit"
        :loading="isPending"
        :disabled="isPending"
        :class="{ 'app-button--loading': isPending }"
        block
      >
        Entrar
      </AppButton>
    </v-form>

    <p class="page-footer mt-8 text-center">
      Ainda não tem conta?
      <NuxtLink to="/register" class="footer-link">Criar conta</NuxtLink>
    </p>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 440px;
  width: 100%;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.auth-subtitle {
  color: var(--color-text-muted);
  font-weight: 400;
  line-height: 1.5;
}

.page-footer {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.footer-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
}

.footer-link:hover {
  text-decoration: underline;
}

.forgot-password-link {
  font-size: 14px;
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

.forgot-password-link:hover {
  text-decoration: underline;
}

.app-button--loading {
  opacity: 0.8;
}

@media (max-width: 599px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
