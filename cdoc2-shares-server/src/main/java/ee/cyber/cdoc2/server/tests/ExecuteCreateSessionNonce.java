package ee.cyber.cdoc2.server.tests;


import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Execution class for creating session nonce.
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
@Slf4j
public abstract class ExecuteCreateSessionNonce {
    protected final TestConfig testConf;

    protected ChainBuilder sendSessionNonceCheckSuccess(String requestName) {
        return exec(
            http(requestName)
                .post(session -> {
                    log.info("Request \"{}\".", requestName);
                    return this.testConf.getServerBaseUrl() + "/session_nonce";
                })
                .check(
                    status().is(HttpResponseStatus.OK.code()),
                    jsonPath("$.nonce").saveAs(SessionVariables.NONCE)
                )
        ).exitHereIfFailed();
    }
}
