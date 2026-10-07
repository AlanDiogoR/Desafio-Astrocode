<div align="center">

# Grivy — Controle Financeiro Pessoal

Aplicação full-stack de finanças pessoais: contas bancárias, transações, cartões de crédito, metas de economia e assinaturas, com frontend em Nuxt e API REST em Spring Boot.

[Frontend publicado (Netlify)](https://grivy.netlify.app) · [Vídeo de demonstração (YouTube)](https://youtu.be/ZRDuhOfDv9A)

</div>

> **Status do deploy:** o frontend está publicado em https://grivy.netlify.app, mas a API (hospedada no Railway) está **offline** no momento. Por isso, cadastro, login e telas do dashboard não funcionam na versão publicada. Para ver o sistema completo, assista ao vídeo ou rode o projeto localmente.

---

## Sobre o projeto

O Grivy nasceu como solução para o **Desafio Astrocode** e evoluiu para um produto completo de controle financeiro. O repositório é um monorepo com:

- `frontend/`: Nuxt 4 (Vue 3 + TypeScript), Vuetify, Pinia e TanStack Vue Query
- `backend/`: API REST em Java 21 com Spring Boot 4, Spring Security (JWT), JPA e Flyway sobre PostgreSQL

## Stack

| Camada | Tecnologias |
|---|---|
| **Frontend** | Nuxt 4, Vue 3, TypeScript, Vuetify 3, Pinia, TanStack Vue Query, Axios, Zod, Chart.js / vue-chartjs, Radix Vue, Sass |
| **Backend** | Java 21, Spring Boot 4 (Web MVC, Data JPA, Security, Validation, AOP, Actuator), JJWT 0.13, Flyway, Bucket4j, SpringDoc OpenAPI (Swagger UI), Lombok |
| **Banco de dados** | PostgreSQL com schema versionado por migrações Flyway |
| **Integrações** | Mercado Pago (pagamentos/assinaturas), Pluggy (Open Finance), WhatsApp Cloud API (Meta), OpenAI, Resend / Brevo (e-mail) |
| **Testes e CI** | Vitest + Vue Test Utils (frontend), JUnit/Spring Boot Test com PostgreSQL (backend), GitHub Actions |
| **Infra** | Dockerfile multi-stage para o backend; frontend com preset Netlify |

## Funcionalidades

Todas as funcionalidades abaixo estão implementadas no código (controllers em `backend/.../api/controllers` e páginas em `frontend/app/pages`).

**Autenticação e conta**
- Cadastro com verificação de e-mail, login, refresh de token e logout (JWT, senhas com BCrypt)
- Recuperação de senha por código enviado por e-mail
- Edição de perfil e exclusão da própria conta
- Rate limiting de login por IP (Bucket4j) e validação de `Origin` como mitigação de CSRF

**Finanças**
- Contas bancárias: CRUD com saldo atual e limites por plano
- Transações de receita e despesa: CRUD paginado, filtros por mês, conta e tipo, categorias e **transações recorrentes** (geradas por job agendado)
- Cartões de crédito: CRUD, fatura atual, histórico de faturas e pagamento de fatura
- Metas de economia: CRUD, aportes e resgates, com expiração automática por job agendado
- Dashboard com visão geral das contas, resumo mensal, gráfico de despesas por categoria, maiores gastos e alertas de gastos
- Tour guiado e "primeiros passos" para novos usuários

**Planos e integrações**
- Planos (gratuito, mensal, semestral e anual) com recursos restritos ao plano pago via anotação `@RequiresPro` (AOP)
- Checkout com formulário de cartão do Mercado Pago e webhook de confirmação de pagamento
- Open Finance via Pluggy (token de conexão, sincronização de contas) e lista de espera
- Bot de WhatsApp: recebe mensagens pelo webhook da Meta (com verificação de assinatura), extrai valor e categoria e registra a transação do usuário
- E-mails transacionais e de marketing (boas-vindas, onboarding, reativação) com fila agendada
- Páginas legais (termos, privacidade) e endpoint de exclusão de dados exigido pela Meta

## Arquitetura

```
Navegador ──► Frontend (Nuxt 4, porta 3000)
                 │  HTTP/REST (NUXT_PUBLIC_API_BASE)
                 ▼
              Backend (Spring Boot 4, porta 8080)
              Controller → Service → Repository
                 │
                 ▼
              PostgreSQL (migrações Flyway)
```

Convenções do frontend: `services/` concentra as chamadas HTTP, `composables/` a lógica reutilizável (controllers de modais e telas), `stores/` o estado global (auth) e `components/ui/` os componentes base com prefixo `App`.

## Como rodar localmente

### Pré-requisitos

- Java 21+ e Maven 3.9+
- Node.js 20+
- PostgreSQL rodando localmente

### 1. Backend

```bash
cd backend
cp .env.example .env   # preencha as variáveis abaixo
mvn spring-boot:run
```

API em `http://localhost:8080`. Fora do perfil `prod` (onde o Swagger é desativado), a documentação interativa fica em `http://localhost:8080/swagger-ui.html`.

Variáveis de ambiente (ver `backend/.env.example` e `.env.example` na raiz):

- **Obrigatórias:** `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `APP_CORS_ORIGINS`, `APP_FRONTEND_URL`
- **Opcionais / integrações:** `SPRING_PROFILES_ACTIVE`, `APP_URL`, `APP_WEBHOOK_BASE_URL`, `APP_REQUIRE_EMAIL_VERIFICATION`, `RESEND_API_KEY`, `BREVO_API_KEY`, `MAIL_FROM`, `MAIL_PASSWORD`, `MP_ACCESS_TOKEN`, `MP_PUBLIC_KEY`, `MP_WEBHOOK_SECRET`, `PLUGGY_CLIENT_ID`, `PLUGGY_CLIENT_SECRET`, `WHATSAPP_ACCESS_TOKEN`, `WHATSAPP_PHONE_NUMBER_ID`, `WHATSAPP_VERIFY_TOKEN`, `WHATSAPP_APP_SECRET`, `OPENAI_API_KEY`

### 2. Frontend

```bash
cd frontend
cp .env.example .env   # defina NUXT_PUBLIC_API_BASE (ex.: http://localhost:8080/api)
npm install
npm run dev
```

Interface em `http://localhost:3000`. Variáveis: `NUXT_PUBLIC_API_BASE` e, para o checkout, `NUXT_PUBLIC_MP_PUBLIC_KEY`.

### Testes

```bash
# frontend
cd frontend && npm test            # ou npm run test:coverage

# backend (requer PostgreSQL acessível com as variáveis DB_*)
cd backend && mvn test
```

O workflow `.github/workflows/test.yml` roda as duas suítes a cada push e pull request.

## Solução de problemas

| Problema | Solução |
|---|---|
| Porta em uso | Backend usa 8080 e frontend 3000. Altere em `application.properties` / `nuxt.config.ts`. |
| 401 / JWT inválido | Faça login novamente (o token expira em 14 dias) e confira o `JWT_SECRET` do backend. |
| Erro de CORS | Inclua a origem do frontend em `APP_CORS_ORIGINS` (várias origens separadas por vírgula). |
| Banco não conecta | Confira `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD` e se o PostgreSQL está rodando. |

## Documentação complementar

- [Backend](backend/README.md): endpoints, exemplos de requisição e configuração
- [Frontend](frontend/README.md): setup, estrutura e design tokens

## Licença

Distribuído sob a licença descrita em [LICENSE](LICENSE).
