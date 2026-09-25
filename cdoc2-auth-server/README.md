# Gatling tests for CDOC2 auth server

Currently, covers the `POST /auth/start`, `GET /auth/status/{authProcessUuid}` and `/info`
endpoints.

## Preconditions for executing tests

* The latest cdoc2 java libraries are installed locally, if not already available from a Maven
  repository.

* A CDOC2 auth server instance is running (see the
  [cdoc2-auth-server](https://github.com/open-eid/cdoc2-auth-server) README for setup).

  For load testing, point the auth server instance at
  [../mock-sid-mid-server](../mock-sid-mid-server) instead of the real SK demo environment - see
  that module's README for setup.

## Configuration

Create a configuration file using the sample file:

```
cp src/test/resources/application.conf.sample src/test/resources/application.conf
```

In the configuration file one can specify:

* Target server URL
* Load test configuration for starting auth processes (user increment):
    * start-users-per-second - users per second addition rate at the start
    * increment-users-per-second - number of users added to concurrent amount of users per second
    * increment-cycles - how many times number of concurrent users is incremented
    * cycle-duration-seconds - duration of each cycle with the currently reached number of
      concurrent users
    * initial-delay-seconds - delay before executing requests
    * at-once-users - number of users added to the session at the start
* Load test configuration for starting auth processes (constant load with final ramp):
    * concurrent-users - number of users in the session
    * concurrent-users-duration-seconds - duration of the constant user count period
    * ramp-to-users - user number to ramp to after the constant period
    * ramp-duration-seconds - duration of the user ramp

**Note:** if the auth server under test is configured against the real SK demo environment (rather
than [../mock-sid-mid-server](../mock-sid-mid-server)), keep the load level modest - every
successful request starts a real Smart-ID/Mobile-ID auth process using a single fixed demo test
identity for every user, and a large number of concurrent sessions for the same identity may be
rejected or rate-limited by the demo environment, showing up as failures unrelated to the auth
server itself.

## Running functional tests

A CDOC2 auth server must be running on the host:port as configured in the configuration file
specified above.

From `cdoc2-auth-server` directory run:

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.StartAuthFunctionalTests
```

## Running load tests

For running load tests first execution profile should be designed and configured. Load test
execution models and configuring options are described in more detail here
https://gatling.io/docs/gatling/reference/current/core/injection/

Open Model is implemented for CDOC2 server load tests, meaning that continuously growing load is
applied to the server.

Each virtual user runs the full flow: half start a Smart-ID, half a Mobile-ID auth process
(`/auth/start`), then fetch its status (`/auth/status/{authProcessUuid}`) using the Location header
returned by the start request.

For executing load tests run from gatling-tests directory:

#### User increment load tests

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.StartAuthLoadTests
```

#### Constant user load tests

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.StartAuthConstantLoadTests
```

## Docker

### Build image

From `cdoc2-auth-server` directory:

````
mvn clean install
docker build -t cdoc2-auth-server-gatling .
````

### Run

Create results directory

````
mkdir -p results
````

The docker commands expect an `application.conf` file in the working directory

replace `StartAuthFunctionalTests` with `StartAuthLoadTests` or `StartAuthConstantLoadTests` to 
run load tests

````
docker run --rm --network host \
  --user "$(id -u):$(id -g)" \
  -v "$(pwd)/results:/gatling/results" \
  -v "$(pwd)/application.conf:/gatling/application.conf:ro" \
  -e MAIN_CLASS=io.gatling.app.Gatling \
  -e MAIN_CLASS_ARGS="-s ee.cyber.cdoc2.server.StartAuthFunctionalTests -rf /gatling/results" \
  -e JAVA_OPTS="-Xmx2g \
  -Dconfig.file=application.conf \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -Dlogback.configurationFile=/gatling/logback.xml" \
  cdoc2-auth-server-gatling
````
