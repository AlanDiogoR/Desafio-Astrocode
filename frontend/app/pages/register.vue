<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const { name, email, password, isLoading, errorMessage, handleRegister } = useAuthForm()
const showPassword = ref(false)
</script>

<template>
  <div>
    <!-- Logo -->
    <div class="d-flex align-center mb-10">
      <img src="~/assets/images/Bank Card double 1.png" alt="Grivy" style="height: 32px; width: auto; margin-right: 8px;" />
      <span class="text-h6 font-weight-bold" style="color: #087F5B">Grivy</span>
    </div>

    <!-- Cabeçalho -->
    <h1 class="auth-title mb-2">
      Crie sua conta
    </h1>
    <p class="text-body-1 mb-8" style="color: #868E96">
      Já possui uma conta?
      <NuxtLink to="/login" class="text-decoration-none font-weight-medium" style="color: var(--auth-primary)">
        Faça login
      </NuxtLink>
    </p>

    <!-- Formulário -->
    <v-form @submit.prevent="handleRegister">
      <v-text-field
        v-model="name"
        label="Nome"
        variant="outlined"
        density="comfortable"
        :disabled="isLoading"
        class="mb-1 auth-input"
      />

      <v-text-field
        v-model="email"
        type="email"
        label="E-mail"
        variant="outlined"
        density="comfortable"
        :disabled="isLoading"
        class="mb-1 auth-input"
      />

      <v-text-field
        v-model="password"
        :type="showPassword ? 'text' : 'password'"
        label="Senha"
        variant="outlined"
        density="comfortable"
        :disabled="isLoading"
        class="mb-1 auth-input"
      >
        <template #append-inner>
          <v-icon
            :icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            @click="showPassword = !showPassword"
            style="cursor: pointer"
          />
        </template>
      </v-text-field>

      <v-alert
        v-if="errorMessage"
        type="error"
        variant="tonal"
        density="compact"
        class="mb-4"
      >
        {{ errorMessage }}
      </v-alert>

      <v-btn
        type="submit"
        color="primary"
        block
        :loading="isLoading"
        class="text-none auth-btn"
      >
        Criar conta
      </v-btn>
    </v-form>
  </div>
</template>
