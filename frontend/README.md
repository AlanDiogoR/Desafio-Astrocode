# 🎨 Grivy Frontend

<div align="center">

[![Nuxt](https://img.shields.io/badge/Nuxt-4.3-00DC82?style=for-the-badge&logo=nuxt.js)](https://nuxt.com/)
[![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?style=for-the-badge&logo=vue.js)](https://vuejs.org/)
[![Vuetify](https://img.shields.io/badge/Vuetify-3.11-1867C0?style=for-the-badge&logo=vuetify)](https://vuetifyjs.com/)
[![Pinia](https://img.shields.io/badge/Pinia-3.0-FFD859?style=for-the-badge&logo=pinia)](https://pinia.vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript)](https://www.typescriptlang.org/)
[![Radix Icons](https://img.shields.io/badge/Radix_Icons-Vue-000000?style=for-the-badge&logo=radix-ui)](https://www.radix-ui.com/)

</div>

---

## 📋 Descrição

Interface web do **Grivy**, sistema de controle financeiro pessoal. Desenvolvida com Nuxt 4, Vue 3 e Vuetify 3, seguindo design system baseado no Figma oficial.

### Funcionalidades

- 🔐 **Autenticação**: Login e cadastro com validação em tempo real
- 📱 **Layout responsivo**: Layout auth (split-screen) e default para dashboard
- 🎨 **Design system**: Tokens de design (cores, fontes, espaçamentos) alinhados ao Figma
- ♿ **Acessibilidade**: Componentes com estados de erro e feedback visual

---

## 🛠️ Tech Stack

| Categoria | Tecnologias |
|----------|-------------|
| **Framework** | Nuxt 4, Vue 3 |
| **UI** | Vuetify 3, Radix Icons |
| **Estado** | Pinia |
| **Validação** | Zod |
| **HTTP** | Axios |
| **Linguagem** | TypeScript |

---

## 📁 Estrutura do Projeto

```
frontend/
├── app/
│   ├── assets/           # Design tokens e estilos globais
│   ├── components/       # Componentes reutilizáveis
│   │   ├── ui/           # AppButton, AppInput, AppLogo
│   │   └── LogoIcon.vue
│   ├── composables/      # Lógica reutilizável
│   │   ├── useAuthForm.ts
│   │   └── useFieldValidation.ts
│   ├── layouts/          # Layouts (auth, default)
│   ├── middleware/       # Middleware global (auth)
│   ├── pages/            # Páginas (login, register, dashboard)
│   ├── plugins/          # Plugins (axios, vuetify)
│   └── stores/           # Pinia stores (auth)
├── public/
│   └── images/          # Assets estáticos
├── .env.example          # Exemplo de variáveis de ambiente
├── nuxt.config.ts
└── package.json
```

---

## ⚙️ Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do frontend baseado no `.env.example`:

```bash
NUXT_PUBLIC_API_BASE=http://localhost:8080/api
```

Para produção, defina a URL do backend (ex: `https://seu-backend.railway.app/api`).

### Instalação

```bash
npm install
```

---

## 🚀 Desenvolvimento

```bash
npm run dev
```

A aplicação estará em `http://localhost:3000`.

---

## 🏗️ Build

```bash
npm run build
```

Preview da build de produção:

```bash
npm run preview
```

---

## 📐 Design (Figma)

O frontend segue o design do [Figma Grivy](https://www.figma.com/design/DpG2mssrVypqAfVy8VDXWr/Grivy?node-id=229-8335).

### Tokens principais

| Token | Valor |
|-------|-------|
| Primary | `#087F5B` |
| Title | `#212529`, 32px, 700 |
| Subtitle | `#868E96`, 400 |
| Error | `#E03131` |
| Input Border | `#ADB5BD` / `#DEE2E6` |
| Hero Gradient | `#E6FCF5` → `#C3FAE8` → `#96F2D7` |

---

## 📚 Documentação

- [Nuxt](https://nuxt.com/docs)
- [Vuetify](https://vuetifyjs.com/)
- [Backend API](../backend/README.md)
