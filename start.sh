#!/bin/bash

# Start ActiveMQ in the background
/opt/activemq/bin/activemq console &

# Wait for ActiveMQ to start properly
sleep 10

# Start your Spring Boot application
java -jar /opt/app/train.booking-0.0.1-SNAPSHOT.jar

# Keep the container running
wait

