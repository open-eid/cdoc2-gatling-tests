package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.KeyShareRequest;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyLength;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.doIfOrElse;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for creating key shares.
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
@Slf4j
public abstract class ExecuteCreateKeyShares {

    // context path of the API (
    protected static final String API_ENDPOINT = "/key-shares";

    protected final TestConfig testConf;

    /**
     * Sends the key share and verifies successful response.
     */
    protected ChainBuilder sendKeyShareCheckSuccess(
        Function<Long, KeyShareRequest> sharesRequest,
        String requestName
    ) {
        return exec(
            http(requestName)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .body(StringBody(session -> {
                    var userId = session.userId();
                    var request = sharesRequest.apply(userId);
                    return TestDataGenerator.toJson(request);
                })).asJson()
                .check(
                    status().is(HttpResponseStatus.CREATED.code()),
                    header(SessionVariables.LOCATION).exists().saveAs(SessionVariables.LOCATION),
                    header(SessionVariables.LOCATION).transformWithSession((shareUrl, session) -> {
                    log.info("Request \"{}\". Share ID location is {}", requestName, shareUrl);
                    return shareUrl;
                    })
                )
            ).exitHereIfFailed();
    }

    /**
     * Sends key share and verifies that an error response was received.
     */
    protected ChainBuilder sendKeyShareCheckError(
        Function<Long, KeyShareRequest> requestGenerator,
        String requestName,
        HttpResponseStatus errorResponse
    ) {
        return exec(
            http(requestName)
                .post(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .body(StringBody(session -> {
                    var userId = session.userId();
                    var request = requestGenerator.apply(userId);
                    return TestDataGenerator.toJson(request);
                })).asJson()
                .check(
                    status().not(HttpResponseStatus.OK.code()),
                    status().not(HttpResponseStatus.CREATED.code()),
                    status().is(errorResponse.code()),
                    header(SessionVariables.LOCATION).notExists() // location header must not exist
                )
            ).exitHereIfFailed();
    }
}
