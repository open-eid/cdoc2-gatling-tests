# Gatling tests for CDOC2 shares server

## Preconditions for executing tests

* Update client keystore `cdoc2client.p12` only if it was recently updated in directory `keys`.
  Remote repository and branches must be set up manually in `remote_repository.sh` before 
  running following script:

```bash
source src/test/resources/remote_repository.sh
sh src/test/resources/copy_client_keystore.sh
```

* The latest cdoc2 java libraries are installed locally. From cdoc2-shares-server directory run:

```
mvn clean install
```

* CDOC key shares servers two instances are running
  * Can be started inside cdoc2-shares-server catalog with commands:
```
cd shares-server
java -Dspring.config.location=config/application-local.properties \
-jar target/cdoc2-shares-server-VER.jar
```

```
cd shares-server
java -Dspring.config.location=config/application-local.properties \
-Dserver.port=8442 -Dmanagement.server.port=18442 \
-jar target/cdoc2-shares-server-VER.jar
```

Or run servers with `-Dlogging.config=target/test-classes/logback.xml` if you need to see logs.


## Configuration

In configuration file one can specify following parameters
* Target server URL
* Load test configuration for uploading shares to the server, creating nonces and getting shares:
  * start-users-per-second - initial number of users added right after test start (e.g 5)
  * increment-users-per-second - number of users added to concurrent amount of users per second (5)
  * increment-cycles - how many times number of concurrent users is incremented (3 -> 5>10>15>20)
  * cycle-duration-seconds duration of each cycle with currently reached number of concurrent 
    users (10 -> 10s with 5 concurrent users; 10s with 10 concurrent users; 10s with 15 
    concurrent users; 10s with 20 concurrent users - total duration time = increment cycles x cycle-duration-time)
  * initial-delay-seconds - in addition to previous parameters delay is used for allowing 
    generating and uploading shares and nonces

Create a configuration file using the sample file:

```
cp src/test/resources/application.conf.sample src/test/resources/application.conf
```


## Running functional tests

The following functional tests exist for testing shares server functionality
* upload shares to server few times (sendKeyShareRepeatedly)
* upload shares to server with random key material (sendKeyShareRandomKeyMaterial)
* fail to upload shares to server with too big key material (sendKeyShareTooBigKeyMaterial)
* Get share with invalid authentication ticket (getWithRandomAuthTicket)
* Get share with invalid shareId (getWithInvalidShareIds)

A CDOC server must be running on the host:port as configured in the configuration file specified above.

From gatling-tests directory run:

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.KeyShareFunctionalTests
```

Or using a compiled jar:

```
export JAVA_OPTS="-Dgatling.ssl.useOpenSsl=false -Dconfig.file=src/test/resources/application.conf -Dlogback.configurationFile=src/test/resources/logback-test.xml"
java $JAVA_OPTS --add-opens java.base/java.lang=ALL-UNNAMED -cp target/cdoc2-shares-server-test-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ee.cyber.cdoc2.server.KeyShareFunctionalTests -rf /tmp/
```


## Running load tests

For running load tests first execution profile should be designed and configured. Load test execution models and configuring options are described in more detail here https://gatling.io/docs/gatling/reference/current/core/injection/#incrementuserspersec

Open Model is implemented for CDOC2 server load tests, meaning that continuously growing load is applied to the server.

* Sample configuration for continuously growing load example:
  - start-users-per-second = 10
  - increment-users-per-second = 5
  - increment-cycles = 10
  - cycle-duration-seconds = 60


Test lasts 10 cycles x 60 seconds = 600 seconds = 10 minutes.
Each next test cycle has 5 concurrent users more. Last cycle will have 60 concurrent users for 1 minute.


* Sample configuration for relatively stable slowly growing load:

  - start-users-per-second = 100
  - increment-users-per-second = 2
  - increment-cycles = 10
  - cycle-duration-seconds = 60

Test lasts 10 cycles x 60 seconds = 600 seconds = 10 minutes.
Initial load - 100 concurrent users will be applied and each next test cycle has 2 concurrent users more. Last cycle will have 120 concurrent users for 1 minute.

For executing load tests run from gatling-tests directory:

```
mvn gatling:test -Dgatling.simulationClass=ee.cyber.cdoc2.server.KeySharesLoadTests
```

Or using a compiled jar:

```
`export JAVA_OPTS="-Dgatling.ssl.useOpenSsl=false -Dconfig.file=src/test/resources/application.conf -Dlogback.configurationFile=src/test/resources/logback-test.xml"`
java $JAVA_OPTS --add-opens java.base/java.lang=ALL-UNNAMED -cp target/cdoc2-shares-server-test-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ee.cyber.cdoc2.server.KeySharesLoadTests -rf /tmp
```


## Server Keystore configuration

Note: Certificates are already generated in development phase and added to Server trust store.
Following commands are useful once new trust chain needs to be added to the server.

The generated certificates are signed with a test CA cert that was created with:

```
keytool -genkeypair -alias gatling-ca -keyalg ec -groupname secp384r1 -sigalg SHA512withECDSA \
 -keystore gatling-ca.p12 -storepass secret -ext KeyUsage=digitalSignature,keyCertSign \
 -ext BasicConstraints=ca:true,PathLen:3 -validity 365
```

To export the test CA certificate from the test CA keystore:

```
keytool -exportcert -keystore gatling-ca.p12 -alias gatling-ca -storepass secret -rfc -file gatling-ca.pem
```

To add the test CA certificate to the server's truststore:

```
keytool -import -trustcacerts -file gatling-ca.pem -alias gatling-ca -storepass passwd \
 -keystore path/to/servertruststore.jks
```
