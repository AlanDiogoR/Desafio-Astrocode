<script setup lang="ts">
import { useQueryClient } from '@tanstack/vue-query'

definePageMeta({
  layout: 'default',
})

const queryClient = useQueryClient()

onMounted(() => {
  void queryClient.invalidateQueries({ queryKey: ['subscription-status'] })
})
</script>

<template>
  <div class="success-page">
    <v-container class="d-flex flex-column align-center justify-center" style="min-height: 80vh">
      <v-icon color="success" size="80">mdi-check-circle</v-icon>
      <h1 class="mt-4">Pagamento confirmado!</h1>
      <p class="text-medium-emphasis mt-2">
        Sua assinatura Grivy Pro será ativada em instantes. O webhook do Mercado Pago pode levar alguns segundos.
      </p>
      <AppButton class="mt-6" @click="navigateTo('/dashboard')">
        Ir para o Dashboard
      </AppButton>
    </v-container>
  </div>
</template>
