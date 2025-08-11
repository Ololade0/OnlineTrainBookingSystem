# Stage 1: Build your Spring Boot app with Java 21 + Maven installed
FROM eclipse-temurin:21 AS builder

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: ActiveMQ base image + your app
FROM rmohr/activemq:latest

RUN mkdir -p /opt/app

COPY --from=builder /app/target/train.booking-0.0.1-SNAPSHOT.jar /opt/app/train.booking-0.0.1-SNAPSHOT.jar

COPY start.sh /opt/start.sh
RUN chmod +x /opt/start.sh

EXPOSE 61616 8161 8080

CMD ["/opt/start.sh"]
