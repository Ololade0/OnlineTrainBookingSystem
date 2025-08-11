# Stage 1: Build your Spring Boot app
FROM maven:3.8.6-openjdk-21 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests to speed up)
RUN mvn clean package -DskipTests

# Stage 2: Base ActiveMQ image + your app
FROM rmohr/activemq:latest

# Create app directory
RUN mkdir -p /opt/app

# Copy the jar from builder stage
COPY --from=builder /app/target/train.booking-0.0.1-SNAPSHOT.jar /opt/app/train.booking-0.0.1-SNAPSHOT.jar

# Copy start script and make it executable
COPY start.sh /opt/start.sh
RUN chmod +x /opt/start.sh

# Expose ActiveMQ ports and app port
EXPOSE 61616 8161 8080

# Start ActiveMQ and your Spring Boot app
CMD ["/opt/start.sh"]
