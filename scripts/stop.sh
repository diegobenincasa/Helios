#!/bin/bash
echo "Stopping existing application..."
pkill -f 'java -jar' || true
