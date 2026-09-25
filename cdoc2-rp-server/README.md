# Gatling tests for CDOC2 RP server

Covers the `POST /sid/authenticate`, `GET /sid/session/{sessionID}`, `POST /mid/authenticate`
and `GET /mid/session/{sessionID}` endpoints.

## Preconditions for executing tests

* The latest cdoc2 java libraries are installed locally, if not already available from a Maven
  repository.

* A CDOC2 RP server instance is running (see the
  [cdoc2-rp-server](https://github.com/open-eid/cdoc2-rp-server) README for setup), configured
  against the real SK Smart-ID/Mobile-ID demo environment.
* Add the Gatling CA certificate (cdoc2-shares-server/src/test/resources/keys/gatling-ca.pem) to the
  rp-server-trust store.

  For load testing, point the RP server instance at [../mock-sid-mid-server](../mock-sid-mid-server)
  instead of the real SK demo environment - see that module's README for setup.

## Configuration

Create a configuration file using the sample file:

```
cp src/test/resources/application.conf.sample src/test/resources/application.conf
```

In the configuration file one can specify:

* Target server URL
* Load test configuration for starting SID/MID authentications and checking session status (user
  increment):
    * start-users-per-second - users per second addition rate at the start
    * increment-users-per-second - number of users added to concurrent amount of users per second
    * increment-cycles - how many times number of concurrent users is incremented
    * cycle-duration-seconds - duration of each cycle with the currently reached number of
      concurrent users
    * initial-delay-seconds - delay before executing requests
    * at-once-users - number of users added to the session at the start
* Load test configuration for starting SID/MID authentications and checking session status (constant
  load with final ramp):
    * concurrent-users - number of users in the session
    * concurrent-users-duration-seconds - duration of the constant user count period
    * ramp-to-users - user number to ramp to after the constant period
    * ramp-duration-seconds - duration of the user ramp

## Using the mock SID/MID server

Run [../mock-sid-mid-server](../mock-sid-mid-server) (see that module's README for build/run
instructions) and point the RP server instance under test at it instead of the real SK demo hosts:

```properties
app.smartid.client.hostUrl=http://localhost:9500
app.mobileid.client.hostUrl=http://localhost:9500
```

(Same two properties `cdoc2-auth-server`'s own tests already use to point at this mock.) Plain HTTP
is fine - neither client SDK requires an `https://` host URL, and the RP server's existing SID/MID
truststore SSL bundle properties can stay as they are; they're simply unused when no TLS handshake
happens.

The mock implements the exact REST paths the RP server's underlying `smart-id-java-client` and
`mid-rest-java-client` SDKs call, so:

* **SID works end-to-end against the mock**: `POST_SID_AUTHENTICATE-POS-01` and
  `GET_SID_SESSION-POS-01` both complete normally.
* **MID only works up to `POST /mid/authenticate`**. The mock's `GET
  /authentication/session/{sessionId}` always reports `RUNNING` and never completes. (Faking a
  genuine MID completion would require the mock to hold its own trusted CA/leaf certificate and
  produce a real signature over the exact hash the RP server expects - out of scope for this simple
  mock.) The scenario `GET_MID_SESSION-POS-01` only checks that response returns http 200.

This is not something the `cdoc2-gatling-tests` project itself configures - it's a property on
whatever RP server instance you point these tests at.

## Running functional tests

A CDOC2 RP server must be running on the host:port as configured in the configuration file specified
above.

From `cdoc2-rp-server` directory run:

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.RpServerFunctionalTests
```

## Running load tests

For running load tests first execution profile should be designed and configured. Load test
execution models and configuring options are described in more detail here
https://gatling.io/docs/gatling/reference/current/core/injection/#incrementuserspersec

Open Model is implemented for CDOC2 server load tests, meaning that continuously growing load is
applied to the server.

Each virtual user runs the full flow: half start a Smart-ID, half a Mobile-ID authentication 
(`/sid/authenticate`, `/mid/authenticate`), then fetch its session status (`/sid/session/
{sessionID}`).

For executing load tests run from gatling-tests directory:

#### User increment load tests

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.RpServerLoadTests
```

#### Constant user load tests

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.RpServerConstantLoadTests
```

## Docker

### Build image

From `cdoc2-rp-server` directory:

````
mvn clean install
docker build -t cdoc2-rp-server-gatling .
````

### Run

Create results directory

````
mkdir -p results
````

The docker commands expect an `application.conf` file in the working directory

replace `RpServerFunctionalTests` with `RpServerLoadTests` or `RpServerConstantLoadTests` to
run load tests

````
docker run --rm --network host \
  --user "$(id -u):$(id -g)" \
  -v "$(pwd)/results:/gatling/results" \
  -v "$(pwd)/application.conf:/gatling/application.conf:ro" \
  -e MAIN_CLASS=io.gatling.app.Gatling \
  -e MAIN_CLASS_ARGS="-s ee.cyber.cdoc2.server.RpServerFunctionalTests -rf /gatling/results" \
  -e JAVA_OPTS="-Xmx2g \
  -Dconfig.file=application.conf \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -Dlogback.configurationFile=/gatling/logback.xml" \
  cdoc2-rp-server-gatling
````
