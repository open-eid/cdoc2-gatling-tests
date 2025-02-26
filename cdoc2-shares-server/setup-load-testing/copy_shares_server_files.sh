#!/bin/bash

source variables.sh

#cp ../shares-server/config/application.properties.docker shares-server
mkdir -p keys
cp ${CDOC2_SHARES_SERVER_DIR}/keys/servertruststore.jks ${CDOC2_SHARES_SERVER_DIR}/keys/cdoc2server.p12 shares-server/keys
cp ${CDOC2_SHARES_SERVER_DIR}/shares-server/src/test/resources/sid-trusted-issuers/test_sid_trusted_issuers.jks shares-server/sid-trusted-issuers
