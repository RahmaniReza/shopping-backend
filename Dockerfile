# Stage 1: Build the fat JAR
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Configure Gradle JVM options to prevent Out Of Memory (OOM) errors during build
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx512m -XX:+UseParallelGC"

# Copy Gradle files for dependency caching
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Pre-fetch Gradle dependencies to leverage Docker layer caching
RUN ./gradlew dependencies --no-daemon --no-watch-fs || true

# Copy source code
COPY src ./src

# Build the fat JAR
RUN ./gradlew shadowJar --no-daemon --no-watch-fs --stacktrace

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root user for production security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/build/libs/shopping-backend-all.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

