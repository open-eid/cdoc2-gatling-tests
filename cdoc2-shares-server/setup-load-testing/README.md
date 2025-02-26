Contains semi-automated setup for load-testing. 

Requires Linux host with Docker installed and Postgres DB where empty DB or existing cdoc2-shares-server
exists.

### GitHub

Create GitHub Personal Access Token with registry read rights and login to docker
* `docker login ghcr.io -u $GH_USERNAME`

Alternatively 

* Create .gh.env file with following contents:
```
cat .gh.env
GH_USERNAME=<github username>
CR_PAT=<github personal access token with registry read access>

```
* Load environment variables: `source .gh.env`
* Login to ghcr.io: `echo $CR_PAT |docker login ghcr.io -u $GH_USERNAME --password-stdin`

### Checkout cdoc2-shares-server

Checkout https://github.com/open-eid/cdoc2-shares-server

### Edit variables.sh
```bash
cp variables.sh.sample variables.sh
vim variables.h
```

### Copy server keys and certificates
View `scp.sh` that copies development keys and configuration files from `cdoc2-shares-server` 
to `$DEST_HOST`
```bash
./scp.sh
```


#### cdoc2server.p12
This file contains server TLS certificate and private key.

#### test_sid_trusted_issuers.jks
Must contain `gatling-ca.pem` (development truststore already contains). 
Used by shares-server to authenticate clients (client certificate must be signed by cert in truststore).

#### logback.xml
Logging configuration


### Setup cdoc2-shares-server database

```bash
bash run-cdoc2-shares-server-liquibase.sh
```

### Start cdoc2-shares-server servers

On DEST_HOST run:
```
./run_cdoc2-shares-server.sh
```

### Start load tests

Run from `cdoc2-gatling-tests/cdoc2-shares-server` 

* Create and edit `src/test/resources/application.conf`:
  ```
  cp src/test/resources/application.conf.sample src/test/resources/application.conf
  vim src/test/resources/application.conf
  ```
* Run load tests:
  ```
  mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.KeySharesLoadTests
  ```

### Random notes:

#### Check that cdoc2-shares is up

`curl -k https://$DEST_HOST:18442/actuator/health`

#### logs for running container

`docker logs --follow cdoc2-shares-server`

