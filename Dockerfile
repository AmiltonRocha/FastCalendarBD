# Estágio 1: Build com Maven
# Usa uma imagem com Maven e Java 17 para compilar o projeto
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Roda o comando de build para gerar o arquivo .jar
RUN mvn clean package -DskipTests

# Estágio 2: Execução
# Usa uma imagem leve, apenas com o necessário para rodar Java
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copia apenas o .jar gerado no estágio anterior
COPY --from=build /app/target/fastcalendar-0.0.1-SNAPSHOT.jar app.jar
# Expõe a porta que a aplicação usa
EXPOSE 8080
# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]