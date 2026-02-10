import { defineStore } from 'pinia'

interface User {
  id: string
  name: string
  email: string
  // Adicione outros campos do usuário conforme necessário
}

interface AuthState {
  user: User | null
}

// Cookie compartilhado para o token (criado uma única vez)
const tokenCookie = useCookie<string | null>('auth_token', {
  default: () => null,
  secure: true,
  sameSite: 'strict',
  maxAge: 60 * 60 * 24 * 14, // 14 dias (mesmo que o backend)
})

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
  }),

  getters: {
    // Token vem do cookie reativo
    token: (): string | null => {
      return tokenCookie.value
    },

    isLoggedIn: (state): boolean => {
      return !!tokenCookie.value && !!state.user
    },

    isAuthenticated(): boolean {
      return this.isLoggedIn
    },

    getUser: (state): User | null => {
      return state.user
    },

    getToken(): string | null {
      return this.token
    },
  },

  actions: {
    setToken(token: string) {
      tokenCookie.value = token
    },

    setUser(user: User) {
      this.user = user
    },

    clearAuth() {
      this.user = null
      tokenCookie.value = null
    },
  },
})
