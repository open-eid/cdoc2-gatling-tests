#!/bin/bash
# https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/

docker stop prometheus
docker rm -f prometheus
docker run -d --name=prometheus \
    -p 9090:9090 \
    -v "$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml" \
    --cpu-shares 512 \
    --cpus 1 \
    prom/prometheus \
    --config.file=/etc/prometheus/prometheus.yml

