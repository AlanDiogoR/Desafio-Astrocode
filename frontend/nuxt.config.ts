// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // Módulos
  modules: ['@pinia/nuxt'],

  // Vuetify styles são importados via plugin (plugins/vuetify.client.ts)
  // Não há necessidade de CSS customizado - Vuetify gerencia tudo via tema

  // Configuração de SSR
  ssr: true,
})
