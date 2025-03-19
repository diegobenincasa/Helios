#!/bin/bash
echo "Starting new application..."
nohup java -jar /home/ec2-user/helios-1.0.war > /home/ec2-user/app.log 2>&1 &
