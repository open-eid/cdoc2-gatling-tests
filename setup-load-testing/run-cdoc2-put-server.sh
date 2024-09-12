#!/usr/bin/env bash

source variables.sh

DOCKER_IMAGE=ghcr.io/open-eid/cdoc2-put-server:latest

#current directory, outside of container
WORKDIR=$(pwd)

#cdoc2 conf dir inside container
CDOC2_CONF_DIR=/conf

CONTAINER_NAME=cdoc2-put-server
#NETWORK_NAME=cdoc2-network

docker pull $DOCKER_IMAGE

docker stop $CONTAINER_NAME
docker rm -f $CONTAINER_NAME

# decrease thread count (default 250) to run on machine with 1 GB RAM
docker run -d --restart on-failure:3 --name $CONTAINER_NAME --user "$(id -u):$(id -g)" \
    -p 8443:8443 \
    -p 18443:18443 \
    -v $WORKDIR/put-server:/conf \
    --env BPL_JVM_THREAD_COUNT=250 \
    --env JAVA_OPTS="-Dspring.config.location=$CDOC2_CONF_DIR/application.properties" \
    --env CDOC2_CONF_DIR=$CDOC2_CONF_DIR \
    --env CDOC2_DB_URL=$CDOC2_DB_URL \
    --env CDOC2_DB_USERNAME=$CDOC2_DB_USERNAME \
    --env CDOC2_DB_PASSWORD=$CDOC2_DB_PASSWORD \
    --env CDOC2_ACTUATOR_USERNAME=$CDOC2_ACTUATOR_USERNAME \
    --env CDOC2_ACTUATOR_PASSWORD=$CDOC2_ACTUATOR_PASSWORD \
    --log-opt mode=non-blocking \
   --cpu-shares 410 \
    --cpus 2 \
    --cpuset-cpus 0-1\
    $DOCKER_IMAGE
#    --network $NETWORK_NAME \
#    -v /var/log/cdoc2:/var/log/cdoc2 \
#    -v /opt/cdoc2/cdoc2-server/get-server-conf/logback.xml:/workspace/logback.xml \


