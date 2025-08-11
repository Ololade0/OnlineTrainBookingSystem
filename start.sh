#!/bin/bash

# Start ActiveMQ in background
/opt/activemq/bin/activemq console &

# Wait 10 seconds for ActiveMQ to fully start
sleep 10

# Start your Spring Boot app
java -jar /opt/app/train.booking-0.0.1-SNAPSHOT.jar

# Keep the container running (optional)
wait
