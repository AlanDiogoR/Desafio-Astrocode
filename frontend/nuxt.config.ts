export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  spaLoadingTemplate: false,

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE,
      /** Public Key Mercado Pago (Checkout Pro / SDK no cliente, se necessário) */
      mpPublicKey: process.env.NUXT_PUBLIC_MP_PUBLIC_KEY ?? '',
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
    transpile: ['vuetify', '@radix-icons/vue', '@headlessui/vue', 'vue-number-format', '@vuepic/vue-datepicker', '@tanstack/vue-query'],
  },

  vite: {
    build: {
      chunkSizeWarningLimit: 600,
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes('node_modules/vuetify')) return 'vuetify'
            if (id.includes('node_modules/@radix-icons') || id.includes('node_modules/radix-vue')) return 'radix'
            if (id.includes('node_modules/@tanstack/vue-query')) return 'vue-query'
          },
        },
      },
    },
    optimizeDeps: {
      include: ['@vuepic/vue-datepicker', '@tanstack/vue-query'],
    },
  },


  modules: ['@pinia/nuxt', '@netlify/nuxt'],

  css: [
    '~/assets/design-tokens.scss',
    '~/assets/main.scss',
    '~/assets/legal.scss',
  ],

  app: {
    head: {
      title: 'Grivy - Controle Financeiro',
      meta: [
        {
          name: 'description',
          content:
            'Grivy organiza suas finanças em minutos: contas, transações, metas e cartões. Teste grátis, sem cartão. Web app responsivo.',
        },
        { property: 'og:title', content: 'Grivy — Controle financeiro pessoal' },
        {
          property: 'og:description',
          content:
            'Saiba para onde vai seu dinheiro. Painel claro, metas e categorias — sem planilhas complicadas.',
        },
        { property: 'og:type', content: 'website' },
        { property: 'og:url', content: 'https://grivy.netlify.app' },
      ],
      viewport: 'width=device-width, initial-scale=1, viewport-fit=cover',
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' },
        { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap', fetchpriority: 'high' },
      ],
    },
  },

  ssr: true,
})
