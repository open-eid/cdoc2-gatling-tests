package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.auth.SessionTokenSigner;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for creating key shares nonce.
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
@Slf4j
public abstract class ExecuteCreateNonce {

    protected final TestConfig testConf;

    /**
     * Creates key share nonce and verifies successful response.
     */
    protected ChainBuilder sendNonceCheckSuccess(String requestName) {
        return sendNonceCheckSuccess(
            requestName, TestDataGenerator.RECIPIENT, TestDataGenerator.TEST_CERT_BASE64URL
        );
    }

    /**
     * Creates key share nonce, authenticating as the given identity, and verifies successful
     * response. Used to obtain a nonce for a key share whose recipient is not the default
     * identity (see {@link TestDataGenerator#MISMATCH_RECIPIENT}).
     *
     * @param subject   identity presented in the session token, must match the share's recipient
     * @param sessionX5c signing certificate matching {@code subject}
     */
    protected ChainBuilder sendNonceCheckSuccess(String requestName, String subject, String sessionX5c) {
        return exec(
            http(requestName)
                .post(session -> {
                    String shareUrl = session.getString(SessionVariables.LOCATION);
                    log.info("Request \"{}\". Share ID location for the nonce request is {}",
                        requestName,
                        shareUrl);
                    return this.testConf.getServerBaseUrl() + shareUrl + "/nonce";
                })
                .header("x-cdoc2-session-token", session -> {
                    String sessionNonce = session.getString(SessionVariables.SESSION_NONCE);
                    String nonceUrl = this.testConf.getServerBaseUrl() + "/session_nonce/" + sessionNonce;
                    return SessionTokenSigner.signSessionToken(nonceUrl, subject);
                })
                .header("x-cdoc2-session-x5c", sessionX5c)
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.nonce").saveAs(SessionVariables.NONCE)
                )
        ).exitHereIfFailed();
    }

    /**
     * Requests a nonce for a share ID that was never created and verifies that an error
     * response was received.
     */
    protected ChainBuilder sendNonceForNonExistingShareCheckError(
        String requestName,
        HttpResponseStatus expectedResponse
    ) {
        String shareId = TestDataGenerator.randomString(TestDataGenerator.SHARE_ID_MIN_LENGTH);

        return exec(
            http(requestName + " - with shareId '" + shareId + "'")
                .post(this.testConf.getServerBaseUrl() + "/key-shares/" + shareId + "/nonce")
                .header("x-cdoc2-session-token", session -> {
                    String sessionNonce = session.getString(SessionVariables.SESSION_NONCE);
                    String nonceUrl = this.testConf.getServerBaseUrl() + "/session_nonce/" + sessionNonce;
                    return SessionTokenSigner.signSessionToken(nonceUrl);
                })
                .header("x-cdoc2-session-x5c", TestDataGenerator.TEST_CERT_BASE64URL)
                .check(
                    status().is(expectedResponse.code())
                )
        );
    }
}
