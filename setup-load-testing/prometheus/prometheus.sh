#!/bin/bash
# https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/

NETWORK_NAME=docker

docker stop prometheus
docker rm -f prometheus
docker run -d --name=prometheus \
    -p 9090:9090 \
    -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
    --cpu-shares 512 \
    --add-host=host.docker.internal:host-gateway \
    --cpus 1 \
    prom/prometheus \
    --config.file=/etc/prometheus/prometheus.yml


