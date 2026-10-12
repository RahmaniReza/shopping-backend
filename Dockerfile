# Stage 1: Build the fat JAR
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install bash for the gradle wrapper
RUN apk add --no-cache bash

# Copy Gradle files for dependency caching
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src ./src

# Build the fat JAR
RUN ./gradlew shadowJar --no-daemon

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for production security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/build/libs/shopping-backend-all.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
