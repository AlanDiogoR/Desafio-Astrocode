# Checklist: Pronto para Produção

## Resumo das Correções por Risco

### Crítico (Risco de Vazamento/Segurança)
| Item | Correção | Rollback |
|------|----------|----------|
| Log de segredos no startup | Removido log de valores de env; apenas contagem | Reverter BackendApplication.java |
| Webhook sem assinatura | Assinatura obrigatória quando MP_WEBHOOK_SECRET configurado | Reverter WebhookController.java |
| Webhook sem idempotência | Tabela webhook_processed_events + verificação | Reverter migration V16 e WebhookController |
| Replay no webhook | Janela de 5 min para timestamp | Remover validação de ts em validateSignature |
| CSRF com cookie | OriginValidationFilter para POST/PUT/DELETE/PATCH | Remover OriginValidationFilter da SecurityConfig |

### Alto (Estabilidade)
| Item | Correção | Rollback |
|------|----------|----------|
| Loop de autenticação frontend | Bootstrap /users/me antes de redirecionar | Reverter auth.global.ts |
| Paginação inválida (500) | Validação page>=0, size 1-100 com 400 | Reverter TransactionController |
| LoginRateLimiter memória | TTL 2h + evicção em getBucketForIp | Reverter LoginRateLimiter |

### Médio (Observabilidade/Lógica)
| Item | Correção | Rollback |
|------|----------|----------|
| GlobalExceptionHandler 5xx | Log estruturado com correlationId | Reverter GlobalExceptionHandler |
| Subscription planType | externalRef inclui planType; fallback para legacy | Migration reversa complexa |
| Health check | Actuator health exposto | Remover actuator dependency |

## Lista de Arquivos Alterados

### Backend
- `BackendApplication.java` - Remoção de log de env
- `WebhookController.java` - Assinatura obrigatória, idempotência, replay
- `WebhookProcessedEvent.java` (novo)
- `WebhookProcessedEventRepository.java` (novo)
- `V16__webhook_processed_events.sql` (novo)
- `SubscriptionService.java` - externalRef com planType
- `SecurityConfig.java` - CorrelationIdFilter, OriginValidationFilter, actuator/health
- `CorrelationIdFilter.java` (novo)
- `OriginValidationFilter.java` (novo)
- `GlobalExceptionHandler.java` - correlationId, log 5xx, IllegalArgumentException
- `TransactionController.java` - Validação paginação
- `LoginRateLimiter.java` - TTL/evicção
- `application.properties` - actuator, app.cors.allowed-origins
- `pom.xml` - spring-boot-starter-actuator

### Backend Testes
- `WebhookControllerIntegrationTest.java` (novo)
- `SubscriptionControllerIntegrationTest.java` (novo)
- `TransactionControllerIntegrationTest.java` - teste page negativa

### Frontend
- `auth.global.ts` - Bootstrap /users/me
- `DashboardHeader.vue` - CTA Upgrade Pro
- `AccountOverview.vue` - Hierarquia Minhas contas
- `GoalsFab.vue` - FAB fixo mobile
- `GoalsList.vue` - Ajustes
- `planos.vue` (novo)
- `planos/checkout.vue` (novo)
- `services/subscription/listPlans.ts` (novo)
- `services/subscription/me.ts` (novo)
- `services/subscription/checkout.ts` (novo)

### Frontend Testes
- `__tests__/stores/auth.spec.ts` (novo)

## Plano de Deploy Seguro

1. **Pré-deploy**
   - [ ] Rodar `mvn test` no backend
   - [ ] Rodar `npm run test` no frontend
   - [ ] Aplicar migration V16 em staging
   - [ ] Verificar MP_WEBHOOK_SECRET em produção
   - [ ] Verificar APP_CORS_ORIGINS inclui domínios corretos

2. **Deploy Canary (recomendado)**
   - [ ] Deploy backend em 1 instância; monitorar health `/actuator/health`
   - [ ] Validar webhook com assinatura em staging
   - [ ] Deploy frontend; validar bootstrap de sessão e fluxo /planos

3. **Rollback**
   - Backend: reverter para commit anterior; migration V16 é additive (tabela vazia ok)
   - Frontend: reverter auth.global.ts se loops persistirem

## Evidências de Teste

### Backend
- `WebhookControllerIntegrationTest`: assinatura ausente → 401, request-id ausente → 400, assinatura inválida → 401
- `SubscriptionControllerIntegrationTest`: listPlans público, getMe autenticado
- `TransactionControllerIntegrationTest`: page=-1 → 400

### Frontend
- `auth.spec.ts`: hasToken, setUser, clearAuth, planLabel

## Variáveis de Ambiente

| Variável | Obrigatório | Descrição |
|----------|-------------|-----------|
| MP_WEBHOOK_SECRET | Produção | Para validar webhooks MP |
| APP_CORS_ORIGINS | Opcional | Default: localhost + grivy.netlify.app |

## Validação Pós-Deploy

- [ ] Login → Dashboard sem redirecionamento indevido
- [ ] Refresh na página protegida mantém sessão
- [ ] /planos carrega planos
- [ ] CTA "Upgrade Pro" visível para usuário FREE
- [ ] /actuator/health retorna 200
- [ ] Webhook MP com assinatura válida processa
- [ ] Webhook MP sem assinatura retorna 401 (quando secret configurado)
