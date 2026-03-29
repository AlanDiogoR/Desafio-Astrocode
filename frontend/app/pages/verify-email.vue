<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const route = useRoute()
const status = ref<'idle' | 'loading' | 'ok' | 'err'>('idle')
const message = ref('')

onMounted(async () => {
  const token = typeof route.query.token === 'string' ? route.query.token : ''
  if (!token) {
    status.value = 'err'
    message.value = 'Link inválido: token ausente.'
    return
  }
  status.value = 'loading'
  try {
    const { $api } = useNuxtApp()
    await $api.get('/users/verify-email', { params: { token } })
    status.value = 'ok'
    message.value = 'E-mail confirmado com sucesso. Você já pode entrar na sua conta.'
  } catch {
    status.value = 'err'
    message.value = 'Não foi possível confirmar o e-mail. O link pode ter expirado ou ser inválido.'
  }
})
</script>

<template>
  <div class="mx-auto text-center verify-email-container">
    <div class="d-flex justify-center mb-2">
      <AppLogo color="#868E96" :size="28" />
    </div>
    <h1 class="page-title text-h4 font-weight-bold mb-2">Confirmação de e-mail</h1>
    <p v-if="status === 'loading'" class="text-body-1">Validando...</p>
    <p v-else class="text-body-1" :class="status === 'ok' ? 'text-success' : 'text-error'">
      {{ message }}
    </p>
    <NuxtLink v-if="status !== 'loading'" to="/login" class="footer-link d-inline-block mt-6">
      Ir para o login
    </NuxtLink>
  </div>
</template>

<style scoped>
.verify-email-container {
  max-width: 440px;
  width: 100%;
}

.page-title {
  color: var(--color-text-primary);
}

.text-success {
  color: #087f5b;
}

.text-error {
  color: #b91c1c;
}

.footer-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 600;
}

.footer-link:hover {
  text-decoration: underline;
}
</style>
