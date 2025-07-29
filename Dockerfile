# Use a lightweight Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy everything into the image
COPY . .

# Build the project using Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the jar file (replace the jar name with your actual one if known)
CMD ["java", "-jar", "target/train.booking-0.0.1-SNAPSHOT.jar"]
