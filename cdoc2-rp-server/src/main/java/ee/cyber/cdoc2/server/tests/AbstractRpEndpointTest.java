package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.auth.SessionTokenSigner;
import ee.cyber.cdoc2.server.conf.TestConfig;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Session;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Common request building blocks shared by all cdoc2-rp-server endpoint test classes: every
 * endpoint requires the same "x-cdoc2-session-token"/"x-cdoc2-session-x5c" pair (see the
 * "SessionTokenHeader"/"SessionX5cHeader" OpenAPI parameters, shared by all four RP endpoints).
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRpEndpointTest {

    protected final TestConfig testConf;

    /**
     * Fetches a fresh, single-use session nonce ("POST /session_nonce") and stores it in the
     * session (see {@link SessionVariables#SESSION_NONCE}), so that a valid session token can be
     * signed for it. Not a scenario of its own - the RP server test plan doesn't cover
     * "/session_nonce" directly, only its role in constructing the "x-cdoc2-session-token"
     * header for the endpoints the plan does cover.
     */
    protected ChainBuilder fetchSessionNonce() {
        return exec(
            http("Create session nonce")
                .post(this.testConf.getServerBaseUrl() + "/session_nonce")
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.nonce").exists().saveAs(SessionVariables.SESSION_NONCE)
                )
        ).exitHereIfFailed();
    }

    /**
     * Signs a session token for the given subject, disclosing the session nonce fetched by a
     * preceding {@link #fetchSessionNonce()} step as its audience.
     */
    protected String signSessionToken(Session session, String subject) {
        String nonce = session.getString(SessionVariables.SESSION_NONCE);
        String nonceUrl = this.testConf.getServerBaseUrl() + "/session_nonce/" + nonce;
        return SessionTokenSigner.signSessionToken(nonceUrl, subject);
    }
}
