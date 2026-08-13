package ee.cyber.cdoc2.server.tests;

import ee.cyber.cdoc2.server.conf.TestConfig;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for getting cdoc2-auth-server's server info ("GET /info").
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ExecuteGetInfo {

    // context path of the API
    protected static final String API_ENDPOINT = "/info";

    protected final TestConfig testConf;

    /**
     * Gets the server info and verifies a successful response.
     */
    protected ChainBuilder getInfoCheckSuccess(String requestName) {
        return exec(
            http(requestName)
                .get(this.testConf.getServerBaseUrl() + API_ENDPOINT)
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    bodyString().exists()
                )
        ).exitHereIfFailed();
    }
}
