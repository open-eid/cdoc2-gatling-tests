# Mock Smart-ID / Mobile-ID server

A minimal stand-in for the Smart-ID and Mobile-ID demo REST APIs, so that
[cdoc2-auth-server](../cdoc2-auth-server) can be load tested without depending on (or placing load
on) the real SK demo environment.

It is a single plain HTTP server (JDK's built-in `com.sun.net.httpserver.HttpServer`, no framework,
no TLS) that implements just enough of both APIs' wire format for cdoc2-auth-server's own client
SDKs (`smart-id-java-client`, `mid-rest-java-client`) to parse the responses successfully.

## Scope

* **Smart-ID** `POST /authentication/notification/etsi/{semanticsIdentifier}` returns a session ID
* **Smart-ID** `GET /session/{sessionId}` reports the session as `RUNNING` on the first poll and
  `COMPLETE` on the second. If run with the command line option
  `-Dmock-server.sessionImmediate=true`
  will report `COMPLETE` on the first poll. cdoc2-auth-server embeds the returned "signature" object
  verbatim into the session token it issues, without cryptographically verifying it - so a
  self-consistent-looking fake is enough. Both
  `/auth/start` and `/auth/status/{authProcessUuid}` work end-to-end for the SID flow against this
  mock.

* **Smart-ID** By default the reported end result is `OK`. To exercise the "auth process failed"
  path instead, start the process with a semantics identifier containing `30403039917` (mirroring
  the real SK demo environment's "always refused" test identity, e.g.
  `etsi/PNOEE-30403039917`) - that session will report `COMPLETE`/`USER_REFUSED` instead, with no
  signature/certificate (matching the real API, and never read by cdoc2-auth-server for a non-OK
  result anyway). The mapping from session ID to end result is held in memory for the lifetime of
  the mock process; restart it between test runs if that matters.

* **Mobile-ID** `POST /authentication` returns a session ID
* **Mobile-ID** `GET /authentication/session/{sessionId}` reports the session as `RUNNING` on the
  first poll and
  `COMPLETE` on the second. If run with the command line option
  `-Dmock-server.sessionImmediate=true`
  will report `COMPLETE` on the first poll. By default, cdoc2-auth-server does not verify the
  contents of authentication signature response. oth
  `/auth/start` and `/auth/status/{authProcessUuid}` work end-to-end for the MID flow against this
  mock.

* **Mobile-ID** By default the reported end result is `OK`. To exercise the "auth process failed"
  path instead, start the process with a semantics identifier containing `60001019950` (mirroring
  the real SK demo environment's "user cancelled" test identity, e.g.
  `etsi/PNOEE-60001019950`) - that session will report `COMPLETE`/`USER_CANCELLED` instead, with no
  signature/certificate (matching the real API, and never read by cdoc2-auth-server for a non-OK
  result anyway). The mapping from session ID to end result is held in memory for the lifetime of
  the mock process; restart it between test runs if that matters.

## Building and running

From the `gatling-tests` directory:

```
mvn -pl mock-sid-mid-server -am package
java -jar mock-sid-mid-server/target/cdoc2-auth-server-sid-mid-mock-1.0.0-SNAPSHOT.jar
```

The listen port defaults to `9500`; override with `-Dmock-server.port=<port>`.
Use the option `-Dmock-server.sessionImmediate=true` to force all session to report `COMPLETE` 
on the first poll - this can be useful for performance testing.

## Pointing cdoc2-auth-server at it

Run the mock server, then configure the cdoc2-auth-server instance under test (not this
gatling-tests project) to use it instead of the real SK demo hosts:

```properties
app.smartid.client.hostUrl=http://localhost:9500
app.mobileid.client.hostUrl=http://localhost:9500
```

Plain HTTP is fine - neither client SDK requires an `https://` host URL. The existing
`spring.ssl.bundle.jks.sid-server.*` / `spring.ssl.bundle.jks.mid-server.*` truststore properties
can stay as they are; they're simply unused when no TLS handshake happens.
