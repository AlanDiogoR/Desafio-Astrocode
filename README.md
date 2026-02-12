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

## 📋 Descrição

Sistema de controle financeiro pessoal desenvolvido com arquitetura limpa e boas práticas de segurança. Permite aos usuários gerenciar suas finanças de forma organizada, controlando contas bancárias, categorizando transações, definindo metas de economia e acompanhando o progresso financeiro.

### Funcionalidades Principais

- 🔐 **Autenticação Segura**: Cadastro e login com JWT e criptografia BCrypt
- 💳 **Gestão de Contas**: Múltiplas contas bancárias isoladas por usuário
- 📊 **Categorização**: Sistema de categorias para receitas e despesas
- 🎯 **Metas de Economia**: Definição e acompanhamento de objetivos financeiros
- 📈 **Transações**: Registro completo de movimentações financeiras

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
│   └── README.md
├── frontend/             # Interface web com Nuxt
│   ├── app/
│   │   ├── components/
│   │   ├── composables/
│   │   ├── layouts/
│   │   ├── pages/
│   │   ├── plugins/
│   │   └── stores/
│   ├── .env.example
│   ├── nuxt.config.ts
│   └── README.md
└── README.md
```

---

## 📚 Documentação

- [Backend](backend/README.md) - API REST, endpoints e configuração
- [Frontend](frontend/README.md) - Setup, estrutura e design tokens

---

## 🏃 Como Começar

### Pré-requisitos

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 18+

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
