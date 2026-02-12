import toast, { Toaster } from 'vue3-hot-toast'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.component('Toaster', Toaster)
  return {
    provide: {
      toast,
    },
  }
})
