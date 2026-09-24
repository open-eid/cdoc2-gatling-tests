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


@Slf4j
public final class StartAuthConstantLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final StartAuthScenarios startAuthScenarios = new StartAuthScenarios(this.config);
    private final GetAuthStatusScenarios getAuthStatusScenarios = new GetAuthStatusScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();

    {
        var loadTestConfig = this.config.getConstantLoadTestConfig();

        ScenarioBuilder sidAuthScenarioBuilder = scenario("Start SID auth and get its status")
            .exec(this.startAuthScenarios.startSidAuth())
            .exec(this.getAuthStatusScenarios.getAuthStatus());

        ScenarioBuilder midAuthScenarioBuilder = scenario("Start MID auth and get its status")
            .exec(this.startAuthScenarios.startMidAuth())
            .exec(this.getAuthStatusScenarios.getAuthStatus());

        int concurrentUsers = loadTestConfig.getConcurrentUsers() / 2;
        Long concurrentUsersDuration = loadTestConfig.getConcurrentUsersDuration();
        int rampDownUsers = loadTestConfig.getRampDownUsers() / 2;
        Long rampDownDuration = loadTestConfig.getRampDownDuration();

        setUp(
            sidAuthScenarioBuilder.injectClosed(
                constantConcurrentUsers(concurrentUsers)
                    .during(concurrentUsersDuration), // 1
                rampConcurrentUsers(concurrentUsers).to(rampDownUsers).during(rampDownDuration)
            ),
            midAuthScenarioBuilder.injectClosed(
                constantConcurrentUsers(concurrentUsers)
                    .during(concurrentUsersDuration), // 1
                rampConcurrentUsers(concurrentUsers).to(rampDownUsers).during(rampDownDuration)
            )
        ).protocols(this.httpConf)
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
