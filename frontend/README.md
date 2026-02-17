# 🎨 Grivy Frontend

<div align="center">

[![Nuxt](https://img.shields.io/badge/Nuxt-4.3-00DC82?style=for-the-badge&logo=nuxt.js)](https://nuxt.com/)
[![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?style=for-the-badge&logo=vue.js)](https://vuejs.org/)
[![Vuetify](https://img.shields.io/badge/Vuetify-3.11-1867C0?style=for-the-badge&logo=vuetify)](https://vuetifyjs.com/)
[![Radix Vue](https://img.shields.io/badge/Radix_Vue-1.9-000000?style=for-the-badge&logo=radix-ui)](https://www.radix-vue.com/)
[![Pinia](https://img.shields.io/badge/Pinia-3.0-FFD859?style=for-the-badge&logo=pinia)](https://pinia.vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript)](https://www.typescriptlang.org/)
[![Radix Icons](https://img.shields.io/badge/Radix_Icons-Vue-000000?style=for-the-badge&logo=radix-ui)](https://www.radix-ui.com/)

</div>

---

## 📋 Descrição

Interface web do **Grivy**, sistema de controle financeiro pessoal. Desenvolvida com Nuxt 4, Vue 3, Vuetify 3 e Radix Vue, oferecendo um dashboard completo com gestão de contas e transações.

### Funcionalidades

- 🔐 **Autenticação**: Login e cadastro com validação em tempo real
- 📱 **Layout responsivo**: Layout auth (split-screen) e default para dashboard
- ♿ **Acessibilidade**: Componentes com estados de erro e feedback visual

- 📊 **Dashboard Interativo**: Visão geral de saldo com modo de privacidade (olho) e carrossel de contas bancárias
- 💸 **Gestão de Transações**: Listagem com scroll infinito, agrupamento por data, ícones inteligentes e filtros por mês/conta/tipo
- 🎯 **Metas de Poupança**: CRUD de metas com contribuição, saque e acompanhamento de progresso
- ⚡ **Ações Rápidas**: Botões FAB (Speed Dial) para novas receitas, despesas, contas e metas
- 🎯 **u/UX Moderna**: Modais acessíveis e Dropdowns animados (via Radix UI) para filtros e menus

---

## 🛠️ Tech Stack

| Categoria | Tecnologias |
|----------|-------------|
| **Framework** | Nuxt 4, Vue 3 |
| **UI Library** | Vuetify 3, Radix Vue, @radix-icons/vue, @headlessui/vue |
| **Estilização** | CSS Scoped, SCSS, Vuetify, Design Tokens |
| **Estado** | Pinia |
| **Dados/Servidor** | TanStack Vue Query (Vue Query 5) |
| **Validação** | Zod |
| **HTTP** | Axios |
| **Extras** | vue3-hot-toast, vue-number-format, @vuepic/vue-datepicker |
| **Linguagem** | TypeScript |

---

## 📁 Estrutura do Projeto

```
frontend/
├── app/
│   ├── assets/              # Design tokens e estilos globais
│   │   ├── design-tokens.scss
│   │   └── main.scss
│   ├── components/
│   │   ├── accounts/         # AccountsEmpty
│   │   ├── goals/           # GoalsList, GoalCard, GoalsFab
│   │   ├── modals/          # NewTransactionModal, EditTransactionModal, NewGoalModal,
│   │   │                    # EditGoalModal, NewAccountModal, GoalInteractionModal, ConfirmDeleteModal
│   │   ├── transactions/    # TransactionCard, TransactionListHeader, TransactionEmptyState,
│   │   │                    # TransactionFiltersModal, MonthSelector, TransactionsFab
│   │   ├── ui/              # AppButton, AppModal, AppDropdown, AppInput, AppLogo, AppSelect,
│   │   │                    # AppColorDropdown, InputCurrency, AppDatePicker
│   │   ├── global/          # AppLaunchScreen
│   │   └── LogoIcon.vue
│   ├── composables/
│   │   ├── useAuthForm.ts, useFieldValidation.ts, useUser.ts
│   │   ├── useAppLoading.ts, useCarousel.ts, useMonthSelector.ts
│   │   ├── useTransactions.ts, useGoals.ts, useBankAccounts.ts, useAccounts.ts
│   │   ├── useDashboard.ts, useCategories.ts, useDashboardController.ts
│   │   ├── useNewTransactionModalController.ts, useEditTransactionModalController.ts
│   │   ├── useNewGoalModalController.ts, useEditGoalModalController.ts, useGoalInteractionController.ts
│   │   ├── useNewAccountModalController.ts, useConfirmDelete.ts
│   │   └── ...
│   ├── constants/
│   │   ├── carousel.ts
│   │   ├── transactions.ts
│   │   └── ...
│   ├── layouts/
│   │   ├── auth.vue
│   │   ├── dashboard.vue
│   │   └── default.vue
│   ├── middleware/
│   │   └── auth.global.ts
│   ├── pages/
│   │   ├── dashboard/       # index.vue (Protegido)
│   │   │   ├── components/  # AccountOverview, AccountCard, TransactionList, DashboardHeader
│   │   │   └── utils/       # categoryIcon
│   │   ├── index.vue
│   │   ├── login.vue
│   │   └── register.vue
│   ├── plugins/
│   │   ├── axios.client.ts
│   │   ├── toast.client.ts
│   │   ├── vue-query.ts
│   │   ├── vue-number-format.client.ts
│   │   └── vuetify.ts
│   ├── services/
│   │   ├── auth/            # login.ts, register.ts
│   │   ├── bankAccounts/    # list, create, update, delete
│   │   ├── categories/      # list.ts
│   │   ├── goals/           # list, create, update, delete, contribute, withdraw
│   │   └── transactions/    # list, create, update, delete
│   ├── stores/
│   │   └── auth.ts
│   ├── types/
│   │   └── confirmDelete.ts
│   ├── utils/
│   │   ├── format.ts, currency.ts, colors.ts, capitalize.ts
│   │   ├── errorHandler.ts, transactionIcons.ts
│   │   └── ...
│   └── app.vue
├── public/
│   └── images/              # Assets estáticos (ícones de categorias, etc.)
├── .env.example             # Exemplo de variáveis de ambiente
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

Para produção (Netlify), configure em Site settings > Environment variables:
- `NUXT_PUBLIC_API_BASE` = `https://desafio-astrocode-production.up.railway.app/api`

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

## 📚 Documentação

- [Nuxt](https://nuxt.com/docs)
- [Vuetify](https://vuetifyjs.com/)
- [Radix Vue](https://www.radix-vue.com/)
- [Backend API](../backend/README.md)
