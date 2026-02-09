# ============================================================================
# Stage 1: Build
# ============================================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar apenas o pom.xml primeiro para aproveitar cache do Docker
# Isso permite reutilizar a camada de dependências quando apenas o código muda
COPY pom.xml .

# Baixar dependências (esta camada será cacheada se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar e gerar o JAR (pulando testes para acelerar o build)
RUN mvn clean package -DskipTests -B

# ============================================================================
# Stage 2: Runtime
# ============================================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Criar usuário não-root para segurança
RUN groupadd -r springboot && useradd -r -g springboot springboot

# Copiar JAR do stage de build
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Mudar ownership para o usuário não-root
RUN chown springboot:springboot app.jar

# Mudar para usuário não-root
USER springboot

# Expor porta da aplicação
EXPOSE 8080

# Variáveis de ambiente (podem ser sobrescritas no docker run)
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=backend_db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=
ENV JWT_SECRET=

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
