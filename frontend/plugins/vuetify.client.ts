import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

const grivyTheme = {
  dark: true,
  colors: {
    primary: '#10B981', // Verde esmeralda do Figma
    background: '#0F172A', // Grafite profundo
    surface: '#1E293B', // Azul acinzentado dos cards
    error: '#F43F5E', // Vermelho para saídas/expenses
    'on-primary': '#ffffff',
    'on-background': '#ffffff',
    'on-surface': '#ffffff',
    'on-error': '#ffffff',
  },
}

export default defineNuxtPlugin((nuxtApp) => {
  const vuetify = createVuetify({
    components,
    directives,
    theme: {
      defaultTheme: 'grivyTheme',
      themes: {
        grivyTheme,
      },
    },
    defaults: {
      VBtn: {
        flat: true,
        rounded: 'lg',
      },
      VTextField: {
        variant: 'outlined',
        density: 'comfortable',
      },
      VSelect: {
        variant: 'outlined',
        density: 'comfortable',
      },
      VCard: {
        rounded: 'xl',
        elevation: 0,
      },
    },
  })

  nuxtApp.vueApp.use(vuetify)
})
