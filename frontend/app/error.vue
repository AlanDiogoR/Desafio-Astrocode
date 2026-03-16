<template>
  <v-app>
    <v-main class="d-flex align-center justify-center" style="min-height: 100vh">
      <div class="text-center pa-8">
        <v-icon size="80" color="grey-lighten-1">mdi-alert-circle-outline</v-icon>
        <h1 class="text-h4 mt-4 mb-2">
          {{ error?.statusCode === 404 ? 'Página não encontrada' : 'Algo deu errado' }}
        </h1>
        <p class="text-body-1 text-medium-emphasis mb-6">
          {{ error?.statusCode === 404
            ? 'A página que você procura não existe.'
            : 'Ocorreu um erro inesperado. Tente novamente.' }}
        </p>
        <v-btn color="primary" @click="clearError({ redirect: '/dashboard' })">
          Voltar ao início
        </v-btn>
      </div>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
const props = defineProps<{
  error: { statusCode?: number }
}>()

const { clearError } = useError()
</script>
