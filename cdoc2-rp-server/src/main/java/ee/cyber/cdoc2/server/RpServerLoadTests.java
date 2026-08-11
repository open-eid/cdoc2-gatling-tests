package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.SidAuthenticateScenarios;
import ee.cyber.cdoc2.server.scenarios.SidSessionScenarios;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load tests for the "/sid/authenticate" and "/sid/session/{sessionID}" endpoints.
 */
@Slf4j
public final class RpServerLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final SidAuthenticateScenarios sidAuthenticateScenarios = new SidAuthenticateScenarios(this.config);
    private final SidSessionScenarios sidSessionScenarios = new SidSessionScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();

    {
        var loadTestConfig = this.config.getLoadTestConfig();

        ScenarioBuilder scenarioBuilder = scenario("Authenticate via SID and get its session status")
            .exec(this.sidAuthenticateScenarios.startSidAuthenticate())
            .exec(this.sidSessionScenarios.getSidSession());

        setUp(
            scenarioBuilder.injectOpen(
                    nothingFor(loadTestConfig.requestStartDelay()),
                    incrementUsersPerSec(loadTestConfig.incrementUsersPerSec())
                        .times(loadTestConfig.incrementCycles())
                        .eachLevelLasting(loadTestConfig.cycleDurationSec())
                        .startingFrom(loadTestConfig.startingUsersPerSec())
                )
        ).protocols(this.httpConf)
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
