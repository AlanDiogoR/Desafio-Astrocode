import VueNumberFormat from 'vue-number-format'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.use(VueNumberFormat, {
    prefix: '',
    suffix: '',
    decimal: ',',
    thousand: '.',
    precision: 2,
    acceptNegative: true,
    isInteger: false,
  })
})
