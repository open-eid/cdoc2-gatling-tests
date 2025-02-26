package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.auth.AuthTokenSigner;
import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.KeyShareRequest;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.bodyLength;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.doIfOrElse;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for getting key shares.
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
@Slf4j
public abstract class ExecuteGetKeyShares {

    // context path of the API (
    protected static final String API_ENDPOINT = "/key-shares";

    protected final TestConfig testConf;

    // holds latest sent data for each user (used in verifying that the same data was received back)
    protected final ConcurrentHashMap<Long, KeyShareRequest> sentData = new ConcurrentHashMap<>();

    /**
     * Gets the key share and verifies successful response.
     */
    protected ChainBuilder getKeyShareCheckSuccess(String testId) {
        String serverBaseUrl = this.testConf.getServerBaseUrl();

        return exec(
            http(testId)
                .get(session -> {
                    String shareUrl = session.getString(SessionVariables.LOCATION);
                    log.info("Request \"{}\". Share ID location is {}", testId, shareUrl);
                    return serverBaseUrl + shareUrl;
                })
                .header("x-cdoc2-auth-ticket", session -> {
                    String shareUrl = session.getString(SessionVariables.LOCATION);
                    String[] shareIdLocation = shareUrl.split("/");
                    String shareId = shareIdLocation[shareIdLocation.length - 1];
                    log.info("Request \"{}\". Share ID is {}", testId, shareId);

                    String nonce = session.get(SessionVariables.NONCE);
                    log.info("Request \"{}\". Nonce is {}", testId, nonce);
                    String xAuthTicket = generateAuthTicket(serverBaseUrl, shareId, nonce);
                    return xAuthTicket;
                })
                .header("x-cdoc2-auth-x5c", TestDataGenerator.TEST_CERT_PEM)
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    bodyString().saveAs(SessionVariables.KEY_SHARE_RESPONSE),
                    bodyString().is(session -> {
                        String response = session.getString(SessionVariables.KEY_SHARE_RESPONSE);
                        log.info("Request \"{}\". Response {}", testId, response);
                        return response;
                    })
                )
            ).exitHereIfFailed();
    }

    // sends a request with the invalid share id or authentication ticket
    protected ChainBuilder checkInvalidInput(
        String testId,
        String shareId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId + " - with shareId '" + shareId + "'")
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + shareId)
                .header("x-cdoc2-auth-ticket", TestDataGenerator.RANDOM_X_AUTH_TICKET)
                .header("x-cdoc2-auth-x5c", TestDataGenerator.TEST_CERT_PEM)
                .check(
                    status().is(expectedResponse.code()),
                    bodyLength().is(0)
                )
        );
    }

    protected ChainBuilder checkEmptyShareId(
        String testId,
        String shareId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId + " - with empty shareId '" + shareId + "'")
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT + '/' + shareId)
                .check(
                    status().is(expectedResponse.code())
                )
        );
    }

    protected ChainBuilder checkMissingShareIdAndUriSlash(
        String testId,
        HttpResponseStatus expectedResponse
    ) {
        return exec(
            http(testId + " - with missing shareId")
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .check(
                    status().is(expectedResponse.code())
                )
        );
    }

    protected KeyShareRequest getSentData(Long userId) {
        return Optional.ofNullable(this.sentData.get(userId))
            .orElseThrow(() -> new RuntimeException("No sent data for user " + userId));
    }

    private String generateAuthTicket(
        String serverBaseUrl,
        String shareId,
        String nonce
    ) {
        return AuthTokenSigner.signAuthToken(serverBaseUrl, shareId, nonce);
    }
}
