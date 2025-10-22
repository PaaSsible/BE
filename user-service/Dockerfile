FROM gradle:8-jdk17-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle* .
COPY build.gradle* .

COPY user-service user-service
COPY common common

RUN chmod +x ./gradlew && sed -i 's/\r$//' ./gradlew

RUN ./gradlew :user-service:bootJar -x test
RUN rm -rf /home/gradle/.gradle

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/user-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

