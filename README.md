<div align="center">

# 💰 Grivy Sistema de Controle Financeiro

**Sistema completo de gestão financeira pessoal com controle de contas bancárias, transações, categorias e metas de economia**

[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Vue.js](https://img.shields.io/badge/Vue.js-Em%20Breve-4FC08D?style=for-the-badge&logo=vue.js)](https://vuejs.org/)

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

### Linguagens

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-yellow?style=for-the-badge&logo=javascript)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue?style=for-the-badge&logo=typescript)

### Back-end

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-brightgreen?style=for-the-badge&logo=spring-security)
![JWT](https://img.shields.io/badge/JWT-0.13.0-black?style=for-the-badge&logo=jsonwebtokens)
![Flyway](https://img.shields.io/badge/Flyway-10.x-red?style=for-the-badge&logo=flyway)

### Front-end

![Vue.js](https://img.shields.io/badge/Vue.js-Em%20Breve-4FC08D?style=for-the-badge&logo=vue.js)
![Vite](https://img.shields.io/badge/Vite-Em%20Breve-646CFF?style=for-the-badge&logo=vite)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-Em%20Breve-38B2AC?style=for-the-badge&logo=tailwind-css)

### Banco de Dados

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)

### DevOps & Ferramentas

![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)

---

## 📁 Estrutura do Repositório

```
Desafio-Astrocode/
├── backend/          # API REST com Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/         # Interface web (Em desenvolvimento)
│   └── README.md
└── README.md         # Este arquivo
```

---

## 📚 Documentação

- [📖 README do Backend](backend/README.md) - Documentação completa da API
- [🎨 README do Frontend](frontend/README.md) - Documentação do frontend (Em breve)

---

## 🏃 Como Começar

### Pré-requisitos

- Java 25 ou superior
- Maven 3.6+
- PostgreSQL 12+
- Node.js 18+ (para o frontend, quando disponível)

### Instalação Rápida

1. Clone o repositório:
```bash
git clone https://github.com/AlanDiogoR/Desafio-Astrocode.git
cd Desafio-Astrocode
```

2. Configure o backend seguindo as instruções em [backend/README.md](backend/README.md)

3. Execute a aplicação backend:
```bash
cd backend
mvn spring-boot:run
```

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

---

<div align="center">

**Desenvolvido com ❤️ usando Spring Boot e Vue.js**

</div>
