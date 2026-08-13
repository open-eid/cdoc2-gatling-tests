package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
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
