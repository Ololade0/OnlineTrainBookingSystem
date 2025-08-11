FROM rmohr/activemq:latest

# Use /tmp/app instead of /opt/app
RUN mkdir -p /tmp/app

COPY --from=builder /app/target/train.booking-0.0.1-SNAPSHOT.jar /tmp/app/train.booking-0.0.1-SNAPSHOT.jar

COPY start.sh /tmp/start.sh
RUN chmod +x /tmp/start.sh

EXPOSE 61616 8161 8080

CMD ["/tmp/start.sh"]
