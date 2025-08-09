# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

# Give permission to run mvnw
RUN chmod +x mvnw

# Build the application JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
