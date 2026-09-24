package ee.cyber.cdoc2.server.tests;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for getting the status of a cdoc2-auth-server auth process
 * ("GET /auth/status/{authProcessUuid}").
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ExecuteGetAuthStatus {

    // context path of the API
    protected static final String API_ENDPOINT = "/auth/status";

    protected final TestConfig testConf;

    /**
     * Gets the status of the auth process created by a previous "/auth/start" request in the
     * same scenario (its Location header must already be saved in the session) and verifies
     * that the response reports the given expected status.
     */
    protected ChainBuilder getAuthStatusCheckStatusIs(String requestName, String expectedStatus) {
        return exec(
            http(requestName)
                .get(session -> {
                    String location = session.getString(SessionVariables.LOCATION);
                    log.info("Request \"{}\". Auth process location is {}", requestName, location);
                    return this.testConf.getServerBaseUrl() + location;
                })
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.status").is(expectedStatus).saveAs(SessionVariables.AUTH_STATUS)
                )
        ).exitHereIfFailed();
    }

    protected ChainBuilder pollAuthStatusCheckStatusIs(String requestName, String expectedStatus) {
        return pollAuthStatusCheckStatusIs(
            requestName,
            expectedStatus,
            Duration.ofSeconds(30),
            Duration.ofSeconds(1)
        );
    }

    /**
     *  Polls the for the expected auth status at pollInterval until the timeout is reached
     */
    protected ChainBuilder pollAuthStatusCheckStatusIs(String requestName,
                                                       String expectedStatus,
                                                       Duration timeout,
                                                       Duration pollInterval) {
        return exec(session -> session.remove(SessionVariables.AUTH_STATUS))
            .asLongAsDuring(
                session -> !expectedStatus.equals(session.getString(SessionVariables.AUTH_STATUS)),
                timeout
            ).on(
                exec(
                    authStatusRequest(requestName)
                        .check(
                            status().is(HttpResponseStatus.OK.code()),
                            jsonPath("$.status").saveAs(SessionVariables.AUTH_STATUS)
                        )
                )
                    .exitHereIfFailed()
                    .doIf(session -> !expectedStatus.equals(session.getString(SessionVariables.AUTH_STATUS)))
                    .then(pause(pollInterval))
            )
            // Timed out: send one last request with the strict check so it's recorded as KO
            .doIf(session -> !expectedStatus.equals(session.getString(SessionVariables.AUTH_STATUS)))
            .then(
                exec(
                    authStatusRequest("TIMEOUT - " + requestName)
                        .check(
                            status().is(HttpResponseStatus.OK.code()),
                            jsonPath("$.status").is(expectedStatus).saveAs(SessionVariables.AUTH_STATUS)
                        )
                )
            )
            .exitHereIfFailed();
    }

    private HttpRequestActionBuilder authStatusRequest(String requestName) {
        return http(requestName)
            .get(session -> {
                String location = session.getString(SessionVariables.LOCATION);
                log.info("Request \"{}\". Auth process location is {}", requestName, location);
                return this.testConf.getServerBaseUrl() + location;
            });
    }

    /**
     * Requests the auth process status for a fixed authProcessUuid (not derived from a prior
     * "/auth/start" call in the same scenario) and verifies that an error response was
     * received.
     */
    protected ChainBuilder checkInvalidAuthProcessUuid(
        String requestName,
        String authProcessUuid,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(requestName + " - with authProcessUuid '" + authProcessUuid + "'")
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + authProcessUuid)
                .check(status().is(expectedResponse.code()))
        );
    }
}
