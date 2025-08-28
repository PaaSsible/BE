FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

ARG JAR_FILE=board-service-0.0.1-SNAPSHOT.jar
COPY /workspace/output/board/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]