package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.MidAuthenticationRequest;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for cdoc2-rp-server's "POST /mid/authenticate" endpoint.
 */
@Slf4j
public abstract class ExecuteMidAuthenticate extends AbstractRpEndpointTest {

    protected static final String API_ENDPOINT = "/mid/authenticate";

    protected ExecuteMidAuthenticate(TestConfig testConf) {
        super(testConf);
    }

    /**
     * Sends a fully valid request and verifies a successful response.
     */
    protected ChainBuilder sendMidAuthenticateCheckSuccess(String testId) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.sessionID").exists().saveAs(SessionVariables.MID_SESSION_ID)
                )
        ).exitHereIfFailed();
    }

    /**
     * Sends a request with a given body and otherwise fully valid session auth headers, and
     * verifies that the given response status was received. Used for the field-missing/invalid
     * body scenarios, where the session auth headers themselves aren't under test.
     */
    protected ChainBuilder sendMidAuthenticateWithPayload(
        String testId,
        MidAuthenticationRequest payload,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .body(StringBody(TestDataGenerator.toJson(payload)))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a request without a body, with otherwise fully valid session auth headers.
     */
    protected ChainBuilder sendMidAuthenticateWithMissingBody(String testId, HttpResponseStatus expectedResponse) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a fully valid request but without the "x-cdoc2-session-token" header.
     */
    protected ChainBuilder sendMidAuthenticateWithMissingSessionToken(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a fully valid request but with a syntactically invalid "x-cdoc2-session-token".
     */
    protected ChainBuilder sendMidAuthenticateWithMalformedSessionToken(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header("x-cdoc2-session-token", TestDataGenerator.randomString(64))
                .header("x-cdoc2-session-x5c", TestDataGenerator.MID_SESSION_CERT_BASE64URL)
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a fully valid request, with a valid session token, but without the
     * "x-cdoc2-session-x5c" header.
     */
    protected ChainBuilder sendMidAuthenticateWithMissingSessionCert(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a fully valid request, with a valid session token, but with a syntactically invalid
     * "x-cdoc2-session-x5c" certificate.
     */
    protected ChainBuilder sendMidAuthenticateWithMalformedSessionCert(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.randomString(64))
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }

    /**
     * Sends a fully valid request, with an otherwise valid session token and certificate, but
     * where the token's subject doesn't match the certificate's identity.
     */
    protected ChainBuilder sendMidAuthenticateWithSessionSubCertMismatch(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(this.fetchSessionNonce()).exec(
            http(testId)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .header(
                    "x-cdoc2-session-token",
                    session -> this.signSessionToken(session, TestDataGenerator.MID_SESSION_SUBJECT_OK)
                )
                .header("x-cdoc2-session-x5c", TestDataGenerator.MISMATCH_CERT_BASE64URL)
                .body(StringBody(TestDataGenerator.toJson(TestDataGenerator.createMidAuthenticationRequest())))
                .asJson()
                .check(status().is(expectedResponse.code()))
        ).exitHereIfFailed();
    }
}
