FROM maven:3.9.6-eclipse-temurin-17 AS build

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="rag-backend" \
      org.opencontainers.image.description="Backend API service handling authentication, business logic, and RAG orchestration"

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
