<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const {
  name,
  email,
  password,
  registerMutation,
  nameErrorDisplay,
  emailErrorDisplay,
  passwordErrorDisplay,
  handleRegister,
  clearFieldError,
  markAsTouched,
} = useAuthForm()

const isPending = computed(() => Boolean(unref(registerMutation.isPending)))
const showPassword = ref(false)

async function onSubmit() {
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
      <ClientOnly>
        <AppInput
          v-model="name"
          label="Nome"
          type="text"
          :field-error="nameErrorDisplay"
          :disabled="isPending"
          class="mb-4"
          @clear-error="clearFieldError('name')"
          @blur="markAsTouched('name')"
        />

        <AppInput
          v-model="email"
          label="E-mail"
          type="email"
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
          :field-error="passwordErrorDisplay"
          :disabled="isPending"
          class="mb-6"
          @clear-error="clearFieldError('password')"
          @blur="markAsTouched('password')"
        >
        <template #append-inner>
          <v-icon
            :icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            class="cursor-pointer"
            @click="showPassword = !showPassword"
          />
        </template>
        </AppInput>
      </ClientOnly>

      <AppButton
        type="submit"
        :loading="isPending"
        :disabled="isPending"
        :class="{ 'app-button--loading': isPending }"
        block
      >
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

.app-button--loading {
  opacity: 0.8;
}

@media (max-width: 599px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
