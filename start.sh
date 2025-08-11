#!/bin/bash

/opt/activemq/bin/activemq console &

sleep 10

java -jar /tmp/app/train.booking-0.0.1-SNAPSHOT.jar

wait
