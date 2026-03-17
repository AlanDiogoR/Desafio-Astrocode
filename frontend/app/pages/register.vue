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

const passwordStrength = computed(() => {
  const p = password.value
  let score = 0
  if (p.length >= 8) score++
  if (/[A-Z]/.test(p)) score++
  if (/[0-9]/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  return Math.min(score, 4)
})
const passwordStrengthPercent = computed(() => (passwordStrength.value / 4) * 100)
const passwordStrengthColor = computed(() => {
  const colors = ['#dc2626', '#f59e0b', '#22c55e', '#087f5b']
  return colors[passwordStrength.value]
})
const passwordStrengthLabel = computed(() => {
  const labels = ['Fraca', 'Razoável', 'Boa', 'Forte']
  return labels[passwordStrength.value]
})

async function onSubmit() {
  await handleRegister()
}
</script>

<template>
  <div class="mx-auto text-center register-container">
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
          autocomplete="name"
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
          autocomplete="new-password"
          :field-error="passwordErrorDisplay"
          :disabled="isPending"
          :class="password.length > 0 ? 'mb-4' : 'mb-6'"
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
        <div v-if="password.length > 0" class="password-strength mb-6">
          <div
            class="password-strength__bar"
            role="progressbar"
            :aria-valuenow="passwordStrength"
            aria-valuemin="0"
            aria-valuemax="4"
            :aria-label="`Força da senha: ${passwordStrengthLabel}`"
          >
            <div
              class="password-strength__fill"
              :style="{ width: passwordStrengthPercent + '%', backgroundColor: passwordStrengthColor }"
            />
          </div>
          <span class="password-strength__label" :style="{ color: passwordStrengthColor }">
            {{ passwordStrengthLabel }}
          </span>
        </div>
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
.register-container {
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

.app-button--loading {
  opacity: 0.8;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
}

.password-strength__bar {
  flex: 1;
  height: 4px;
  background: #e5e7eb;
  border-radius: 2px;
  overflow: hidden;
}

.password-strength__fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s, background-color 0.3s;
}

.password-strength__label {
  font-size: 12px;
  font-weight: 500;
  min-width: 80px;
}

@media (max-width: 599px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
