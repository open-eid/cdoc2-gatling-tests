#!/usr/bin/env bash


source variables.sh


echo "Coping to $DEST_HOST:$DEST_DIR"

ssh $DEST_HOST "mkdir -p $DEST_DIR"
ssh $DEST_HOST "mkdir -p $DEST_CONF_DIR"

scp $CDOC2_SHARES_SERVER_DIR/shares-server/config/application.properties.docker $DEST_HOST:$DEST_CONF_DIR
scp -r $CDOC2_SHARES_SERVER_DIR/shares-server/src/test/resources/sid-trusted-issuers $DEST_HOST:$DEST_CONF_DIR/
scp -r $CDOC2_SHARES_SERVER_DIR/shares-server/src/test/resources/keystore $DEST_HOST:$DEST_CONF_DIR/
scp $CDOC2_SHARES_SERVER_DIR/shares-server/src/test/resources/logback.xml $DEST_HOST:$DEST_CONF_DIR/

scp variables.sh $DEST_HOST:$DEST_DIR/
scp run-cdoc2-shares-server.sh $DEST_HOST:$DEST_DIR/
scp run-cdoc2-shares-server-liquibase.sh $DEST_HOST:$DEST_DIR/