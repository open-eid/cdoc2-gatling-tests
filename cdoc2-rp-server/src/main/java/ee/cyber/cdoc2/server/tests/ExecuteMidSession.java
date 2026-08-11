package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for cdoc2-rp-server's "GET /mid/session/{sessionID}" endpoint.
 */
@Slf4j
public abstract class ExecuteMidSession extends AbstractRpEndpointTest {

    protected static final String API_ENDPOINT = "/mid/session";

    protected ExecuteMidSession(TestConfig testConf) {
        super(testConf);
    }

    /**
     * Polls the status of the MID session created by a previous "/mid/authenticate" request in
     * the same scenario (its sessionID must already be saved in the session) and verifies a
     * successful response.
     */
    protected ChainBuilder getMidSessionCheckSuccess(String testId) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .get(session -> this.testConf.getServerBaseUrl() + API_ENDPOINT + '/'
                    + session.getString(SessionVariables.MID_SESSION_ID))
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(HttpResponseStatus.OK.code()))
        ).exitHereIfFailed();
    }

    /**
     * Requests the session status for a fixed, syntactically valid but unrelated sessionID, with
     * otherwise fully valid session auth headers, and verifies that the given response status
     * was received. Used for the sessionID-focused scenarios, where the session auth headers
     * themselves aren't under test.
     */
    protected ChainBuilder getMidSessionWithId(String testId, String sessionId, HttpResponseStatus expectedResponse) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId + " - with sessionID '" + sessionId + "'")
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + sessionId)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Requests the session status at the "/mid/session" endpoint with the sessionID path segment
     * omitted entirely.
     */
    protected ChainBuilder getMidSessionWithMissingSessionId(String testId, HttpResponseStatus expectedResponse) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Requests the session status for a random, syntactically valid sessionID, without the
     * "x-cdoc2-session-token" header.
     */
    protected ChainBuilder getMidSessionWithMissingSessionToken(String testId, HttpResponseStatus expectedResponse) {
        return exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + UUID.randomUUID())
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    protected ChainBuilder getMidSessionWithMalformedSessionToken(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + UUID.randomUUID())
                .header("x-cdoc2-session-token", TestDataGenerator.randomString(64))
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    protected ChainBuilder getMidSessionWithMissingSessionCert(String testId, HttpResponseStatus expectedResponse) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + UUID.randomUUID())
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    protected ChainBuilder getMidSessionWithMalformedSessionCert(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + UUID.randomUUID())
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.randomString(64))
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    protected ChainBuilder getMidSessionWithSessionSubCertMismatch(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + UUID.randomUUID())
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MISMATCH_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }
}
