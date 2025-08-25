FROM gradle:8-jdk17-alpine AS builder
WORKDIR /workspace

COPY gradle gradle
COPY gradlew gradlew
COPY settings.gradle* settings.gradle*
COPY build.gradle* build.gradle*

COPY user-service user-service

RUN chmod +x gradlew && sed -i 's/\r$//' gradlew
RUN ./gradlew :user-service:bootJar -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/user-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
