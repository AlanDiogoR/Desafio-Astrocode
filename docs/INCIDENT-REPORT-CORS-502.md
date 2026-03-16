# Relatório de Incident Response — CORS + 502

**Data:** 16/03/2025  
**Status:** Correções implementadas | 502 pendente de validação em produção

---

## 1. CAUSA RAIZ EXATA

1. **502 "Application failed to respond"** — O gateway Railway retorna 502 quando o backend **não responde dentro do timeout**. Evidência: `Server: railway-edge`, `X-Railway-Fallback: true`. Possíveis motivos: crash na inicialização (ex.: `JWT_SECRET` inválido/ausente), timeout de DB (Neon), cold start longo ou health check falhando.

2. **CORS ausente em respostas de erro** — O Spring Security aplicava CORS só após o chain. Quando auth ou exceções retornavam 4xx/5xx via `sendError`/`HttpStatusEntryPoint`, os headers CORS não eram aplicados, gerando "No 'Access-Control-Allow-Origin' header" no browser.

3. **Ordem do filtro CORS** — CORS precisa rodar **antes** do Spring Security e de outros filtros. Um `CorsFilter` dentro do Security chain só processava requisições que passavam por ele; preflight e erros podiam não receber headers.

---

## 2. ARQUIVOS ALTERADOS

| Arquivo | Alteração |
|---------|-----------|
| `backend/src/main/java/.../config/CorsConfig.java` | **Novo** — `FilterRegistrationBean<CorsFilter>` com `Ordered.HIGHEST_PRECEDENCE`. CorsFilter na cadeia do Servlet, antes do Spring Security. |
| `backend/src/main/java/.../config/SecurityConfig.java` | `cors(AbstractHttpConfigurer::disable)` — desativa CORS duplicado do Security |
| `backend/src/main/resources/application.properties` | `management.endpoint.health.show-details=always` — facilitar debug sem auth |
| `backend/src/test/java/.../api/controllers/AuthControllerIntegrationTest.java` | Teste de preflight OPTIONS com verificação de headers CORS |

---

## 3. CONFIGURAÇÃO DE AMBIENTE (ENV)

### Railway (backend)

| Variável | Valor | Obrigatório |
|----------|-------|-------------|
| `JWT_SECRET` | Base64, mínimo 32 bytes. Ex: `openssl rand -base64 64` | **Sim** |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | Credenciais PostgreSQL (Neon) | **Sim** |
| `APP_CORS_ORIGINS` | `https://grivy.netlify.app,https://www.grivy.netlify.app` | Recomendado (default já inclui) |
| `PORT` | Injetada pelo Railway | Auto |

### Netlify (frontend)

| Variável | Valor | Obrigatório |
|----------|-------|-------------|
| `NUXT_PUBLIC_API_BASE` | `https://desafio-astrocode-production.up.railway.app/api` | **Sim** |

---

## 4. EVIDÊNCIAS DOS TESTES

### Comandos de verificação (executar após deploy)

```bash
# Preflight em /api/auth/login
curl -v -X OPTIONS "https://desafio-astrocode-production.up.railway.app/api/auth/login" \
  -H "Origin: https://grivy.netlify.app" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"

# Resultado esperado:
# HTTP 200 ou 204
# Access-Control-Allow-Origin: https://grivy.netlify.app
# Access-Control-Allow-Credentials: true

# Health
curl -s "https://desafio-astrocode-production.up.railway.app/actuator/health"

# GET /api/users/me (sem token → 401, mas com CORS)
curl -v "https://desafio-astrocode-production.up.railway.app/api/users/me" \
  -H "Origin: https://grivy.netlify.app"
# Esperado: 401 + headers CORS (não 502)
```

### Teste automatizado (backend)

```bash
mvn test -Dtest=AuthControllerIntegrationTest#optionsLogin_returns200WithCorsHeaders
```

---

## 5. CHECKLIST DE DEPLOY SEGURO E ROLLBACK

### Pré-deploy

- [ ] `JWT_SECRET` configurado no Railway (mín. 32 caracteres)
- [ ] Variáveis de DB válidas e migrações Flyway ok
- [ ] `APP_CORS_ORIGINS` com `https://grivy.netlify.app` (ou default)

### Pós-deploy

- [ ] OPTIONS `/api/auth/login` retorna 200/204 com `Access-Control-Allow-Origin: https://grivy.netlify.app`
- [ ] GET `/api/users/me` não retorna 502 (401 sem token é esperado)
- [ ] Browser não exibe mais erro CORS
- [ ] Login funciona no Netlify
- [ ] Health endpoint estável, sem restart loop

### Rollback

1. Reverter commits no repositório
2. Redeploy no Railway
3. Conferir variáveis de ambiente (DB, JWT_SECRET)

---

## 6. CHECKLIST DE ACEITE

- [ ] OPTIONS `/api/auth/login` retorna 200/204 com `Access-Control-Allow-Origin: https://grivy.netlify.app`
- [ ] GET `/api/users/me` não retorna 502
- [ ] Browser não mostra mais erro CORS
- [ ] Login funciona no Netlify
- [ ] Health endpoint estável sem restart loop
