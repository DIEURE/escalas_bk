# ETAPA 1: Compilação com Maven e Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Instala dos2unix para higienizar arquivos de texto
RUN apk add --no-cache dos2unix

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src

# Remove BOM e converte quebras de linha CRLF para LF
RUN find src/main/resources -name "*.properties" -exec dos2unix {} +

# Compila ignorando testes
RUN ./mvnw clean package -DskipTests

# ETAPA 2: Imagem final de execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=America/Sao_Paulo

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
