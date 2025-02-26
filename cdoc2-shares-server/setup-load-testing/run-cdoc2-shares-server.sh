#!/usr/bin/env bash

source variables.sh

DOCKER_IMAGE=${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}/cdoc2-shares-server:$SHARES_SERVER_VERSION

#current directory, outside of container
WORKDIR=$(pwd)

#cdoc2 conf dir inside container
CDOC2_CONF_DIR=/config

echo CDOC2_DB_URL=$POSTGRES_URL

CONTAINER_NAME=cdoc2-shares-server

docker pull $DOCKER_IMAGE

docker stop $CONTAINER_NAME
docker rm -f $CONTAINER_NAME


docker run -d --restart on-failure:3 --name $CONTAINER_NAME --user "$(id -u):$(id -g)" \
    -p 8442:8442 \
    -p 18442:18442 \
    -v $WORKDIR/config/sid-trusted-issuers/test_sid_trusted_issuers.jks:/config/sid_trusted_issuers.jks \
    -v $WORKDIR/config/application.properties.docker:/config/application.properties \
    -v $WORKDIR/config/keystore/cdoc2server.p12:/config/cdoc2server.p12 \
    --env BPL_JVM_THREAD_COUNT=250 \
    --env JAVA_OPTS="-Dspring.config.location=$CDOC2_CONF_DIR/application.properties" \
    --env POSTGRES_URL=${POSTGRES_URL} \
    --env POSTGRES_DB=${POSTGRES_DB} \
    --env POSTGRES_USER=${POSTGRES_USER} \
    --env POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
    --env management.endpoints.metrics.username=$CDOC2_ACTUATOR_USERNAME \
    --env management.endpoints.metrics.password=$CDOC2_ACTUATOR_PASSWORD \
    --env server.port=8442 \
    --env management.server.port=18442 \
    --log-opt mode=non-blocking \
    --cpus 2 \
    --memory 3072m \
    $DOCKER_IMAGE

