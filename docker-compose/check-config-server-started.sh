#!/bin/bash

echo "SCRIPT BASLADI"

apt-get update -y
yes | apt-get install curl

echo "CURL KURULDU"

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" \
http://config-server:8888/actuator/health)

while [[ ! $curlResult == "200" ]]; do
    echo "Config Server hazir degil. Bekleniyor..."
    sleep 2
    curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" \
    http://config-server:8888/actuator/health)
done

echo "CONFIG SERVER HAZIR"

echo "LAUNCHER CALISTIRILIYOR"

exec /cnb/process/web

echo "LAUNCHER BITTI"