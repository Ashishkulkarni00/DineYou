# Stage 1: Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (creates JAR file)
RUN mvn clean package -DskipTests -B

# Stage 2: Production Stage
FROM gcr.io/distroless/java21-debian12:nonroot

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar menucard_service.jar

# Use non-root user (distroless default)
USER nonroot:nonroot

# Expose application port
EXPOSE 8081

# Health check (if using Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD ["/usr/bin/wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8081/actuator/health"]

# Run application with JVM optimizations

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}", \
    "-jar", \
    "menucard_service.jar"]
