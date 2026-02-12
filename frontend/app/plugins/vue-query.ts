import { VueQueryPlugin, QueryClient } from '@tanstack/vue-query'

const STALE_TIME_MS = 5 * 60 * 1000

export default defineNuxtPlugin((nuxtApp) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        staleTime: STALE_TIME_MS,
      },
    },
  })

  nuxtApp.vueApp.use(VueQueryPlugin, { queryClient })
})
