FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copia o JAR
COPY voluntariado-1.0.0.jar app.jar
# Expõe a porta
EXPOSE 8080
# Roda a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]