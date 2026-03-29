<script setup lang="ts">
definePageMeta({
  layout: 'default',
})
</script>

<template>
  <div class="cancelled-page">
    <v-container class="d-flex flex-column align-center justify-center" style="min-height: 80vh">
      <v-icon color="warning" size="80">mdi-close-circle</v-icon>
      <h1 class="mt-4">Pagamento não concluído</h1>
      <p class="text-medium-emphasis mt-2">
        Você cancelou o pagamento. Nenhuma cobrança foi feita.
      </p>
      <AppButton class="mt-6" variant="outlined" @click="navigateTo('/dashboard')">
        Voltar ao Dashboard
      </AppButton>
    </v-container>
  </div>
</template>
