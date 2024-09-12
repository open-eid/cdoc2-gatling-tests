#!/bin/bash

source variables.sh

#cp ../get-server/docker/application.properties get-server
mkdir -p get-server/keys
cp  ${CDOC2_CAPSULE_SERVER_DIR}/keys/servertruststore.jks ${CDOC2_CAPSULE_SERVER_DIR}/keys/cdoc2server.p12 get-server/keys

