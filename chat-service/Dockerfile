FROM gradle:8-jdk17-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle* .
COPY build.gradle* .

RUN chmod +x ./gradlew && sed -i 's/\r$//' ./gradlew

RUN ./gradlew dependencies --no-daemon || return 0

COPY chat-service chat-service
COPY common common

RUN ./gradlew :chat-service:bootJar -x test --no-daemon



FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/chat-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

