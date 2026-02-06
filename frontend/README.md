# 🎨 Frontend - Interface Web

<div align="center">

![Vue.js](https://img.shields.io/badge/Vue.js-Em%20Breve-4FC08D?style=for-the-badge&logo=vue.js)
![Vite](https://img.shields.io/badge/Vite-Em%20Breve-646CFF?style=for-the-badge&logo=vite)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

</div>

---

## 🚧 Status do Projeto

**Frontend em Desenvolvimento**

A interface web do sistema de controle financeiro está sendo desenvolvida e em breve estará disponível.

---

## 🎯 Tech Stack Planejado

### Framework e Build Tool

![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D?style=for-the-badge&logo=vue.js)
![Vite](https://img.shields.io/badge/Vite-5.x-646CFF?style=for-the-badge&logo=vite)

### Linguagens

![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue?style=for-the-badge&logo=typescript)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-yellow?style=for-the-badge&logo=javascript)

---

## 📋 Funcionalidades Planejadas

### 🔐 Autenticação
- [ ] Tela de login
- [ ] Tela de registro
- [ ] Gerenciamento de sessão com JWT
- [ ] Proteção de rotas autenticadas

### 💳 Contas Bancárias
- [ ] Listagem de contas
- [ ] Criação de nova conta
- [ ] Edição de conta existente
- [ ] Exclusão de conta
- [ ] Visualização de saldo

### 📊 Transações
- [ ] Listagem de transações
- [ ] Criação de transação (receita/despesa)
- [ ] Filtros por categoria, conta e período
- [ ] Gráficos e relatórios

### 📂 Categorias
- [ ] Visualização de categorias
- [ ] Criação de categorias personalizadas
- [ ] Edição e exclusão de categorias

### 🎯 Metas de Economia
- [ ] Criação de metas
- [ ] Acompanhamento de progresso
- [ ] Visualização de metas ativas e concluídas

### 📈 Dashboard
- [ ] Visão geral das finanças
- [ ] Gráficos de receitas vs despesas
- [ ] Resumo mensal
- [ ] Indicadores financeiros

---

## 🏗️ Estrutura Planejada

```
frontend/
├── src/
│   ├── components/      # Componentes Vue reutilizáveis
│   ├── views/           # Páginas/Vistas
│   ├── router/          # Configuração de rotas
│   ├── store/           # Gerenciamento de estado (Pinia)
│   ├── services/        # Serviços de API
│   ├── utils/           # Utilitários
│   └── assets/          # Recursos estáticos
├── public/              # Arquivos públicos
└── package.json         # Dependências
```

---


## 🔌 Integração com Backend

O frontend se comunicará com a API REST do backend através de:

- **Base URL**: `http://localhost:8080/api`
- **Autenticação**: JWT Token no header `Authorization: Bearer <token>`
- **CORS**: Configurado no backend para aceitar requisições do frontend

### Endpoints que serão utilizados:

- `POST /api/auth/login` - Login
- `POST /api/users` - Registro
- `GET /api/accounts` - Listar contas
- `POST /api/accounts` - Criar conta
- `PUT /api/accounts/{id}` - Atualizar conta
- `DELETE /api/accounts/{id}` - Excluir conta
- `GET /api/categories` - Listar categorias

---

## 🎨 Design e UX

O frontend será desenvolvido com foco em:

- ✅ Interface moderna e intuitiva
- ✅ Design responsivo (mobile-first)
- ✅ Acessibilidade (WCAG 2.1)
- ✅ Performance otimizada
- ✅ Experiência de usuário fluida

---

## 📝 Notas

Este README será atualizado conforme o desenvolvimento do frontend progride. Fique atento às atualizações!

---

<div align="center">

**Em breve disponível! 🚀**

</div>
