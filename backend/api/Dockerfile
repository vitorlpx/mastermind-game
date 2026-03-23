# Estágio 1 — Build
# Usa a imagem completa do JDK 17 para compilar o projeto
FROM eclipse-temurin:17-jdk-alpine AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o Maven wrapper e o pom.xml primeiro
# Isso aproveita o cache do Docker — se o pom.xml não mudou,
# não precisa baixar as dependências novamente
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Baixa as dependências
RUN ./mvnw dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila e gera o .jar (sem rodar os testes aqui)
RUN ./mvnw package -DskipTests

# Estágio 2 — Runtime
# Usa só o JRE — imagem muito menor que o JDK completo
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia só o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]