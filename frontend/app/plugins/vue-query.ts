import type { DehydratedState } from '@tanstack/vue-query'
import { VueQueryPlugin, QueryClient, hydrate, dehydrate } from '@tanstack/vue-query'
import { useState } from '#imports'

const STALE_TIME_MS = 5 * 60 * 1000

export default defineNuxtPlugin({
  name: 'vue-query',
  enforce: 'pre',
  setup(nuxtApp) {
    const vueQueryState = useState<DehydratedState | null>('vue-query', () => null)

    const queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          staleTime: STALE_TIME_MS,
          refetchOnWindowFocus: false,
        },
      },
    })

    nuxtApp.vueApp.use(VueQueryPlugin, { queryClient })

    if (import.meta.server) {
      nuxtApp.hooks.hook('app:rendered', () => {
        vueQueryState.value = dehydrate(queryClient)
      })
    }

    if (import.meta.client) {
      hydrate(queryClient, vueQueryState.value)
    }
  },
})
