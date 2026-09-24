package ee.cyber.cdoc2.server;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.GetAuthStatusScenarios;
import ee.cyber.cdoc2.server.scenarios.StartAuthScenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load tests for the "/auth/start" and "/auth/status/{authProcessUuid}" endpoints.
 * <p>
 * Each virtual user starts a real Smart-ID auth process against the SK Smart-ID demo
 * environment (through the auth server under test), reusing the fixed SID demo test identity
 * for every user, then immediately checks that process's status using the Location header
 * returned by the start request. Checking the status also calls out to the SK Smart-ID demo
 * environment whenever the process has not completed yet. Keep the load profile in the
 * configuration file modest: the demo environment is a shared resource and may reject or
 * rate-limit a large number of concurrent sessions for the same identity.
 */
@Slf4j
public final class StartAuthLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final StartAuthScenarios startAuthScenarios = new StartAuthScenarios(this.config);
    private final GetAuthStatusScenarios getAuthStatusScenarios = new GetAuthStatusScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();

    {
        var loadTestConfig = this.config.getLoadTestConfig();

        ScenarioBuilder sidAuthScenarioBuilder = scenario("Start SID auth and get its status")
            .exec(this.startAuthScenarios.startSidAuth())
            .exec(this.getAuthStatusScenarios.getAuthStatus());

        ScenarioBuilder midAuthScenarioBuilder = scenario("Start MID auth and get its status")
            .exec(this.startAuthScenarios.startMidAuth())
            .exec(this.getAuthStatusScenarios.getAuthStatus());


        setUp(
            sidAuthScenarioBuilder.injectOpen(
                    nothingFor(loadTestConfig.getRequestStartDelay()),
                    incrementUsersPerSec(loadTestConfig.getIncrementUsersPerSec())
                        .times(loadTestConfig.getIncrementCycles())
                        .eachLevelLasting(loadTestConfig.getCycleDurationSec())
                        .startingFrom(loadTestConfig.getStartingUsersPerSec())
                ),
            midAuthScenarioBuilder.injectOpen(
                nothingFor(loadTestConfig.getRequestStartDelay()),
                incrementUsersPerSec(loadTestConfig.getIncrementUsersPerSec())
                    .times(loadTestConfig.getIncrementCycles())
                    .eachLevelLasting(loadTestConfig.getCycleDurationSec())
                    .startingFrom(loadTestConfig.getStartingUsersPerSec())
            )
        ).protocols(this.httpConf)
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
