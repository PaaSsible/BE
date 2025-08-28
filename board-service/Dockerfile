FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

ARG JAR_FILE=/workspace/output/board/board-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]