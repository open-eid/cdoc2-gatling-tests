package ee.cyber.cdoc2.server;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.MidAuthenticateScenarios;
import ee.cyber.cdoc2.server.scenarios.MidSessionScenarios;
import ee.cyber.cdoc2.server.scenarios.SidAuthenticateScenarios;
import ee.cyber.cdoc2.server.scenarios.SidSessionScenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load tests for the "/sid/authenticate" and "/sid/session/{sessionID}" endpoints.
 */
@Slf4j
public final class RpServerConstantLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final SidAuthenticateScenarios sidAuthenticateScenarios = new SidAuthenticateScenarios(this.config);
    private final MidAuthenticateScenarios midAuthenticateScenarios =
        new MidAuthenticateScenarios(this.config);
    private final SidSessionScenarios sidSessionScenarios = new SidSessionScenarios(this.config);
    private final MidSessionScenarios midSessionScenarios = new MidSessionScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();

    {
        var loadTestConfig = this.config.getConstantLoadTestConfig();

        ScenarioBuilder sidScenarioBuilder = scenario("Authenticate via SID and get its session status")
            .exec(this.sidAuthenticateScenarios.startSidAuthenticate())
            .exec(this.sidSessionScenarios.getSidSession());

        ScenarioBuilder midScenarioBuilder = scenario("Authenticate via MID and get its session status")
            .exec(this.midAuthenticateScenarios.startMidAuthenticate())
            .exec(this.midSessionScenarios.getMidSession());

        int concurrentUsers = loadTestConfig.concurrentUsers() / 2;
        Long concurrentUsersDuration = loadTestConfig.concurrentUsersDuration();
        int rampUsers = loadTestConfig.rampUsers() / 2;
        Long rampDuration = loadTestConfig.rampDuration();

        setUp(
            sidScenarioBuilder.injectClosed(
                constantConcurrentUsers(concurrentUsers)
                    .during(concurrentUsersDuration), // 1
                rampConcurrentUsers(concurrentUsers).to(rampUsers).during(rampDuration)
            ),
            midScenarioBuilder.injectClosed(
                constantConcurrentUsers(concurrentUsers)
                    .during(concurrentUsersDuration), // 1
                rampConcurrentUsers(concurrentUsers).to(rampUsers).during(rampDuration)
            )
        ).protocols(this.httpConf)
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
