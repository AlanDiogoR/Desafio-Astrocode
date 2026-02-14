export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  spaLoadingTemplate: false,

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE,
    },
  },

  srcDir: 'app/',

  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],

  build: {
    transpile: ['vuetify', '@radix-icons/vue', '@headlessui/vue', 'vue-number-format', '@vuepic/vue-datepicker'],
  },

  vite: {
    optimizeDeps: {
      include: ['@vuepic/vue-datepicker'],
    },
  },

  modules: ['@pinia/nuxt'],

  css: [
    '~/assets/design-tokens.scss',
    '~/assets/main.scss',
  ],

  app: {
    head: {
      title: 'Grivy - Controle Financeiro',
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap' },
      ],
    },
  },

  ssr: true,
})
