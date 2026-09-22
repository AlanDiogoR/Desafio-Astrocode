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

## ⚙️ Configuração

### Variáveis de Ambiente

| Variável | Obrigatória | Descrição |
|----------|-------------|-----------|
| `NUXT_PUBLIC_API_BASE` | Sim | URL base da API REST (ex: `http://localhost:8080/api`) |

Crie `.env` na raiz do frontend baseado no `.env.example`:

```env
NUXT_PUBLIC_API_BASE=http://localhost:8080/api
```

**Produção (Netlify)**: em Site settings > Environment variables, defina `NUXT_PUBLIC_API_BASE` para a URL da API em produção.

---

## Checklist com a API desligada

Use isto quando a API (Railway) estiver fora. Não há sucesso de cadastro ou login nesse estado.

- [ ] `GET /` responde **200** e mostra os CTAs **Criar conta** e **Entrar**
- [ ] `GET /planos` responde **200** e mostra os CTAs **Criar conta** e **Entrar**
- [ ] Login, cadastro e recuperação de senha **não** exibem sucesso; o formulário fica desabilitado com **Em breve**
- [ ] `GET /dashboard` e as rotas `/dashboard/*` **redirecionam** para `/login` (sem sessão)
- [ ] O hero não tem link morto: sem WhatsApp fictício e sem atalho para `/dashboard` antes do login

Conferir no preview:

```bash
curl -s -o /dev/null -w "%{http_code}\n" https://<preview>/ 
curl -s -o /dev/null -w "%{http_code}\n" https://<preview>/planos
curl -s -o /dev/null -w "%{http_code} %{redirect_url}\n" https://<preview>/dashboard
curl -s -o /dev/null -w "%{http_code} %{redirect_url}\n" https://<preview>/dashboard/planos
```

---

## 🔄 Fluxo de Dados

- **Pinia** (`stores/auth.ts`): Estado de autenticação (token, user). O cookie `auth_token` persiste o JWT.
- **Vue Query**: Cache de dados (transações, contas, metas, categorias, resumo mensal). Queries principais:
  - `['transactions']` — lista de transações
  - `['dashboard']` — resumo consolidado (saldo total, receitas/despesas do mês)
  - `['monthly-summary', year, month]` — resumo de gastos (SpendingAlert)
  - `['monthly-summary-modal', year, month]` — resumo no modal
  - `['categories']`, `['bankAccounts']`, `['goals']`, `['dashboard']` — listas e resumos por domínio
- **Invalidação**: Ao criar/editar/deletar transação ou contribuir/sacar em meta, as queries `transactions`, `monthly-summary` e `monthly-summary-modal` são invalidadas para refletir mudanças imediatas.

---

## 📦 Composables Principais

| Composable | Responsabilidade |
|------------|------------------|
| `useDashboard` | Estado global dos modais (transações, metas, contas, perfil) e ações de abertura/fechamento |
| `useAuth` / `useAuthStore` | Autenticação: login, logout, token, usuário |
| `useTransactions` | Lista de transações com filtros, paginação e query key |
| `useCategories` | Lista de categorias filtrada por tipo (INCOME/EXPENSE) |
| `useBankAccounts` | Contas bancárias e invalidação |
| `useDashboardData` | Saldo total e totais do mês (GET /api/dashboard) |
| `useGoals` | Metas de poupança e invalidação |
| `useInsightsController` | Resumo mensal e SpendingAlert (alerta de categoria com alta % de gastos) |
| `useNewTransactionModalController` | Formulário e criação de transação |
| `useEditTransactionModalController` | Edição de transação |
| `useGoalInteractionController` | Contribuir/sacar em metas |
| `useConfirmDelete` | Exclusão genérica (conta, meta, transação) |

---

## 🧩 Convenções de Componentes

- **`App*`**: Componentes base de UI (AppButton, AppInput, AppModal, AppSelect, AppDropdown).
- **Modais**: Em `modals/`, acionados via `useDashboard` (ex: `openNewTransactionModal`, `openEditProfileModal`).
- **Slot `append-inner`**: AppInput suporta ícone à direita (ex: olho para senha).
- **Design tokens**: Em `assets/design-tokens.scss` e `main.scss`.

---

## 🚀 Desenvolvimento

```bash
npm install
npm run dev
```

Aplicação em `http://localhost:3000`.

**Comandos úteis:**
- `npm run dev` — servidor de desenvolvimento
- `npm run build` — build de produção
- `npm run preview` — prévia do build
- `npx nuxi typecheck` — checagem de tipos
- `npx nuxi analyze` — análise de bundle (se configurado)

**Dicas de debug:**
- Vue Query Devtools: inspecione cache e invalidações.
- Pinia Devtools: veja estado de auth.
- Axios: requisições em `plugins/axios.client.ts`; interceptors para token e erros.

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
│   │   │                    # EditGoalModal, NewAccountModal, GoalInteractionModal, ConfirmDeleteModal,
│   │   │                    # EditProfileModal, MonthlySummaryModal
│   │   ├── transactions/    # TransactionCard, TransactionListHeader, TransactionEmptyState,
│   │   │                    # TransactionFiltersModal, MonthSelector, TransactionsFab, SpendingAlert
│   │   ├── ui/              # AppButton, AppModal, AppDropdown, AppInput, AppLogo, AppSelect,
│   │   │                    # AppColorDropdown, InputCurrency, AppDatePicker
│   │   ├── global/          # AppLaunchScreen
│   │   └── LogoIcon.vue
│   ├── composables/
│   │   ├── useAuthForm.ts, useFieldValidation.ts, useUser.ts
│   │   ├── useAppLoading.ts, useCarousel.ts, useMonthSelector.ts
│   │   ├── useTransactions.ts, useGoals.ts, useBankAccounts.ts
│   │   ├── useDashboard.ts, useCategories.ts, useDashboardController.ts
│   │   ├── useInsightsController.ts
│   │   ├── useNewTransactionModalController.ts, useEditTransactionModalController.ts
│   │   ├── useNewGoalModalController.ts, useEditGoalModalController.ts, useGoalInteractionController.ts
│   │   ├── useNewAccountModalController.ts, useEditProfileModalController.ts, useConfirmDelete.ts
│   │   └── ...
│   ├── constants/
│   ├── layouts/
│   ├── middleware/
│   ├── pages/
│   ├── plugins/
│   ├── services/
│   ├── stores/
│   ├── types/
│   ├── utils/
│   └── app.vue
├── public/
├── .env.example
├── nuxt.config.ts
└── package.json
```

---

## 📚 Documentação

- [Nuxt](https://nuxt.com/docs)
- [Vuetify](https://vuetifyjs.com/)
- [Radix Vue](https://www.radix-vue.com/)
- [Backend API](../backend/README.md) - API REST, endpoints e configuração
- [Swagger UI](http://localhost:8080/swagger-ui.html) - Documentação interativa da API (requer backend rodando)
