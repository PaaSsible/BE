FROM gradle:8-jdk17-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle* .
COPY build.gradle* .

COPY meet-service meet-service
COPY common common

RUN chmod +x ./gradlew && sed -i 's/\r$//' ./gradlew

RUN ./gradlew :meet-service:bootJar -x test


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/meet-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

