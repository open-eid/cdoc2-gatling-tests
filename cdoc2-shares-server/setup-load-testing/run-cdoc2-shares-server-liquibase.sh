#!/bin/bash

# initialize or update database for cdoc2-shares-server
source variables.sh

DOCKER_IMAGE=${DOCKER_REGISTRY}/${DOCKER_REPOSITORY}/cdoc2-shares-server-liquibase:$SHARES_SERVER_VERSION

docker run --rm \
--env DB_URL=$POSTGRES_URL/$POSTGRES_DB \
--env DB_PASSWORD=$POSTGRES_PASSWORD \
--env DB_USER=$POSTGRES_USER \
$DOCKER_IMAGE