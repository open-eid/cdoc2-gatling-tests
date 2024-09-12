Contains semi-automated setup for load-testing. Can be tested locally, but for real load-testing
needs separate Linux host with Docker and separate postgres database


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

### Checkout cdoc2-capsule-server

Checkout https://github.com/open-eid/cdoc2-capsule-server

### Edit variables.sh
```
cp variables.sh.sample variables.sh
vim variables.h
```

### Copy server keys
If using development keys from cdoc2-capsule-server repo:
```
./copy_put_server_files.sh
./copy_get_server_files.sh
```

Copies `cdoc2server.p12`  and `servertruststore.jks` file under `*-server/keys`. 

#### cdoc2server.p12
This file contains server TLS certificate and private key.

#### servertruststore.jks
Must contain `gatling-ca.pem` (development truststore already contains). 
Used by get-server to authenticate clients (client certificate must be signed by cert in truststore). 

### Setup cdoc2 database

* Edit `cdoc2-capsule-server/server-db/liquibase.properties`
* Run `mvn liquibase:update` inside `cdoc2-capsule-server/server-db`


### Start cdoc2-*-server servers

Copy configuration to LOAD_TEST_HOST:
```
scp.sh
```

Review `docker run` settings in `run_cdoc2-*-server.sh` files (options `--cpus` and `--memory`). 


On LOAD_TEST_HOST run:
```
./run_cdoc2-get-server.sh
./run_cdoc2-put-server.sh
```

### Install Prometheus/Grafana

* `cp prometheus-sample.yml prometheus.yml`
* Edit `prometheus.yml`, update `cdoc2-put-server.host`/`cdoc2-put-server.host` hostnames and `username` and `password` for `/actuator/prometheus` endpoint
* Run `prometheus/prometheus.sh` on load host or dedicated prometheus host and check http://<prometheus.host>:9090/targets
* Run Grafana `prometheus/grafana.sh`
  - Open http://<grafana.host>:3000 (admin:admin) in browser
  - [Configure Prometheus data source](https://grafana.com/docs/grafana/latest/datasources/prometheus/configure-prometheus-data-source/)
  - (Optional) Install https://grafana.com/grafana/dashboards/17360-spring-http-example/
  - (Optional) Install https://grafana.com/grafana/dashboards/12271-jvm-micrometer/

Note: `http_server_requests_*` metrics appear after you have made some requests against cdoc2 servers 

### Start load tests

Run from `cdoc2-gatling-tests` 

* Create test keys:`mvn clean compile exec:java -Damount=10`
* Create and edit `src/test/resources/application.conf`:
  ```
  cp src/test/resources/application.conf.sample src/test/resources/application.conf
  vim src/test/resources/application.conf
  ```
* Run load tests:
  ```
  mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.KeyCapsuleLoadTests
  ```

### Random notes:

#### bash access

running container: `docker exec -it cdoc2-put-server /bin/bash`
image: `docker run -it --entrypoint /bin/bash ghcr.io/open-eid/cdoc2-put-server:latest`

#### logs for running container

`docker logs --follow cdoc2-put-server`

