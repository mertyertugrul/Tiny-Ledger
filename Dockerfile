# syntax=docker/dockerfile:1

# ---- Stage 1: Build the application ----
FROM gradle:8-jdk17 AS build
WORKDIR /app

# Copy Gradle wrapper files (if present) and build.gradle, settings.gradle first for caching
COPY build.gradle /app/
COPY settings.gradle /app/

# Copy the entire source code
COPY . /app

# Build the application without running tests (optional: remove `-x test` to include tests)
RUN gradle clean build -x test

# ---- Stage 2: Create a minimal runtime image ----
FROM openjdk:17-jdk-slim
EXPOSE 8080

# Copy the built jar from Stage 1 to Stage 2
COPY --from=build /app/build/libs/*.jar /app/tiny-ledger.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/tiny-ledger.jar"]
