FROM rmohr/activemq:latest

# Copy your Spring Boot jar (use actual filename!)
COPY target/train.booking-0.0.1-SNAPSHOT.jar /opt/app/train.booking-0.0.1-SNAPSHOT.jar

# Copy start script
COPY start.sh /opt/start.sh
RUN chmod +x /opt/start.sh

# Expose ActiveMQ ports and app port
EXPOSE 61616 8161 8080

# Run the startup script
CMD ["/opt/start.sh"]
