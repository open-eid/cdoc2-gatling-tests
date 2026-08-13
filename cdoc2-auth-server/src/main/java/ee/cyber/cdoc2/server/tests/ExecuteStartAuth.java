package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.AuthIdentityRequest;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for starting a cdoc2-auth-server auth process ("POST /auth/start").
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ExecuteStartAuth {

    // context path of the API
    protected static final String API_ENDPOINT = "/auth/start";

    protected final TestConfig testConf;

    /**
     * Starts an auth process and verifies successful response.
     */
    protected ChainBuilder sendStartAuthCheckSuccess(
        Function<Long, AuthIdentityRequest> requestGenerator,
        String requestName
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
                    status().is(HttpResponseStatus.CREATED.code()),
                    header(SessionVariables.LOCATION).exists().saveAs(SessionVariables.LOCATION),
                    header(SessionVariables.LOCATION).transformWithSession((location, session) -> {
                        log.info("Request \"{}\". Auth process location is {}", requestName, location);
                        return location;
                    }),
                    jsonPath("$.vc").exists().saveAs(SessionVariables.VERIFICATION_CODE),
                    jsonPath("$.vc").transformWithSession((vc, session) -> {
                        log.info("Request \"{}\". Verification code is {}", requestName, vc);
                        return vc;
                    })
                )
        ).exitHereIfFailed();
    }

    /**
     * Starts an auth process and verifies that an error response was received.
     */
    protected ChainBuilder sendStartAuthCheckError(
        Function<Long, AuthIdentityRequest> requestGenerator,
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
