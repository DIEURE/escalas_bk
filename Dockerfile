# ETAPA 1: Compilação com Maven e Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Garante permissão de execução do wrapper no Linux
RUN chmod +x mvnw

# Baixa as dependências em cache
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte e gera o pacote .jar ignorando testes no build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ETAPA 2: Imagem final leve para execução (apenas o JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário sem privilégios de root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia o JAR compilado da primeira etapa
COPY --from=builder /app/target/*.jar app.jar

# Variáveis padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=America/Sao_Paulo

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
