<div align="center">

# 💰 Grivy Sistema de Controle Financeiro

**Sistema completo de gestão financeira pessoal com controle de contas bancárias, transações, categorias e metas de economia**

### Frontend

[![Nuxt](https://img.shields.io/badge/Nuxt-4.3-00DC82?style=for-the-badge&logo=nuxt.js)](https://nuxt.com/)
[![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?style=for-the-badge&logo=vue.js)](https://vuejs.org/)
[![Vuetify](https://img.shields.io/badge/Vuetify-3.11-1867C0?style=for-the-badge&logo=vuetify)](https://vuetifyjs.com/)
[![Pinia](https://img.shields.io/badge/Pinia-3.0-FFD859?style=for-the-badge&logo=pinia)](https://pinia.vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript)](https://www.typescriptlang.org/)
[![Radix Icons](https://img.shields.io/badge/Radix_UI-Vue-000000?style=for-the-badge&logo=radix-ui)](https://www.radix-ui.com/)

### Backend

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=spring-security)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)

</div>

---

## 🎬 Demonstração do Projeto

Confira o vídeo de demonstração do **Grivy** em funcionamento:

[![Assistir no YouTube](https://img.shields.io/badge/YouTube-Demo_Grivy-FF0000?style=for-the-badge&logo=youtube)](https://youtu.be/ZRDuhOfDv9A)

---

## 📋 Descrição

Sistema de controle financeiro pessoal desenvolvido com arquitetura limpa e boas práticas de segurança. Permite aos usuários gerenciar suas finanças de forma organizada, controlando contas bancárias, categorizando transações, definindo metas de economia e acompanhando o progresso financeiro.

### Funcionalidades Principais

- 🔐 **Autenticação Segura**: Cadastro, login e recuperação de senha com JWT e criptografia BCrypt
- 💳 **Gestão de Contas**: Múltiplas contas bancárias isoladas por usuário
- 📊 **Categorização**: Sistema de categorias para receitas e despesas
- 🎯 **Metas de Economia**: Definição e acompanhamento de objetivos financeiros
- 📈 **Transações**: Registro completo de movimentações financeiras

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                        Cliente (Browser)                         │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│  Frontend (Nuxt 4)                                                │
│  • Vue 3 + Composition API + TypeScript                          │
│  • Vuetify 3 (UI) + Radix Vue (acessibilidade)                    │
│  • Pinia (estado global) + Vue Query (cache/dados)                │
│  • Porta: 3000                                                    │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    │  HTTP / REST
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│  Backend (Spring Boot 4)                                          │
│  • API REST + Spring Security + JWT                               │
│  • Camadas: Controller → Service → Repository                     │
│  • Porta: 8080                                                    │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│  PostgreSQL 16                                                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Fluxo de Desenvolvimento

1. **Backend primeiro**: Suba o banco PostgreSQL e a API antes do frontend.
2. **Configure `.env`** em `backend/` e `frontend/` (veja `.env.example` em cada pasta).
3. **Frontend**: O Nuxt consome a API via `NUXT_PUBLIC_API_BASE`. Em dev, use `http://localhost:8080/api`.

Com a API desligada, o checklist do site Netlify está em [`frontend/README.md`](frontend/README.md#checklist-com-a-api-desligada): `/` e `/planos` em 200 com CTA, auth em “em breve” sem sucesso falso, e `/dashboard*` redirecionando para o login.

---

## 📐 Convenções do Projeto

- **Nomenclatura**: camelCase em TS/JS; kebab-case em componentes Vue; PascalCase em componentes; snake_case em DB.
- **Estrutura**: `composables/` para lógica reutilizável; `services/` para chamadas HTTP; `stores/` para estado global (auth).
- **Componentes**: prefixo `App` para UI base; modais em `modals/`; páginas em `pages/` com layout por rota.

---

## 🚨 Troubleshooting

| Problema | Solução |
|----------|---------|
| **Porta em uso** | Backend: 8080. Frontend: 3000. Use `netstat` ou altere em `application.properties` / `nuxt.config.ts`. |
| **401 / JWT inválido** | Faça login novamente. Token expira em 14 dias. Verifique `JWT_SECRET` no backend. |
| **CORS** | Backend permite `localhost:3000` e `localhost:5173`. Em produção, adicione a origem em `SecurityConfig`. |
| **Variáveis de ambiente** | Sem `.env` o app falha. Copie `.env.example` para `.env` e preencha. |
| **Banco não conecta** | Verifique `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`. PostgreSQL deve estar rodando. |

---

## 🚀 Tech Stacks

### Frontend

| Tecnologia | Descrição |
|------------|-----------|
| Nuxt 4 | Framework full-stack baseado em Vue |
| Vue 3 | Framework progressivo para interfaces |
| Vuetify 3 | Component library Material Design |
| Pinia | Gerenciamento de estado |
| TypeScript | Tipagem estática |
| Radix Icons | Biblioteca de ícones |

### Backend

| Tecnologia | Descrição |
|------------|-----------|
| Java 21 | Linguagem de programação |
| Spring Boot 4 | Framework para APIs REST |
| Spring Security 6 | Autenticação e autorização |
| SpringDoc OpenAPI | Documentação da API via Swagger UI |
| PostgreSQL 16 | Banco de dados relacional |
| JWT 0.13 | Autenticação stateless |
| Flyway | Migrações de banco de dados |

---

## 📁 Estrutura do Repositório

```
Desafio-Astrocode/
├── backend/              # API REST com Spring Boot
│   ├── src/
│   ├── pom.xml
│   ├── .env.example
│   └── README.md
├── frontend/             # Interface web com Nuxt
│   ├── app/
│   │   ├── components/   # ui, transactions, goals, accounts, modals, global
│   │   ├── composables/
│   │   ├── layouts/
│   │   ├── pages/
│   │   ├── plugins/
│   │   ├── services/
│   │   ├── stores/
│   │   ├── constants/
│   │   ├── types/
│   │   └── utils/
│   ├── .env.example
│   ├── nuxt.config.ts
│   └── README.md
└── README.md
```

---

## 📚 Documentação

- [Backend](backend/README.md) - API REST, endpoints e configuração
- [Frontend](frontend/README.md) - Setup, estrutura e design tokens
- **Documentação interativa da API** (Swagger UI): quando o backend estiver rodando, acesse [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🏃 Como Começar

### Pré-requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 20+

### Backend

```bash
cd backend
# Configure .env (veja backend/.env.example)
mvn spring-boot:run
```

API disponível em `http://localhost:8080`

### Frontend

```bash
cd frontend
cp .env.example .env
# Ajuste NUXT_PUBLIC_API_BASE se necessário
npm install
npm run dev
```

Interface em `http://localhost:3000`

### Documentação da API (opcional)

Com o backend rodando, acesse a documentação interativa em: **http://localhost:8080/swagger-ui.html**

---

## 🔒 Segurança

- ✅ Senhas criptografadas com BCrypt
- ✅ Autenticação JWT com expiração de 14 dias
- ✅ Isolamento de dados por usuário
- ✅ Validação de entrada em todos os endpoints
- ✅ Tratamento global de exceções

---

## 📝 Licença

Este projeto está sob a licença especificada no arquivo [LICENSE](LICENSE).
