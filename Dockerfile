# syntax=docker/dockerfile:1.7

# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -q -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -B -DskipTests package && \
    mkdir -p /workspace/app && \
    cp target/*.jar /workspace/app/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /workspace/app/app.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
