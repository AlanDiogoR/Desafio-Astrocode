<script setup lang="ts">
definePageMeta({
  layout: 'default',
})
</script>

<template>
  <div class="pending-page">
    <v-container class="d-flex flex-column align-center justify-center" style="min-height: 80vh">
      <v-icon color="info" size="80">mdi-clock-outline</v-icon>
      <h1 class="mt-4">Pagamento em processamento</h1>
      <p class="text-medium-emphasis mt-2">
        Seu pagamento está sendo processado. Você receberá uma confirmação por e-mail em breve.
      </p>
      <AppButton class="mt-6" variant="outlined" @click="navigateTo('/dashboard')">
        Voltar ao Dashboard
      </AppButton>
    </v-container>
  </div>
</template>
