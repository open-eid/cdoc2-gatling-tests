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
                    return SessionTokenSigner.signSessionToken(nonceUrl);
                })
                .header("x-cdoc2-session-x5c", TestDataGenerator.TEST_CERT_BASE64URL)
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.nonce").saveAs(SessionVariables.NONCE)
                )
        ).exitHereIfFailed();
    }
}
