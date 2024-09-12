#!/bin/bash

source variables.sh

#cp ../put-server/docker/application.properties put-server
mkdir -p put-server/keys
cp  ${CDOC2_CAPSULE_SERVER_DIR}/keys/servertruststore.jks ${CDOC2_CAPSULE_SERVER_DIR}/keys/cdoc2server.p12 put-server/keys

