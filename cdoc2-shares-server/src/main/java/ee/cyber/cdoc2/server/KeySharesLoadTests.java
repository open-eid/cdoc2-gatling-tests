package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.CreateKeySharesScenarios;
import ee.cyber.cdoc2.server.scenarios.CreateNonceScenarios;
import ee.cyber.cdoc2.server.scenarios.GetKeySharesScenarios;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.incrementUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.nothingFor;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load tests for the key-shares API
 */
@Slf4j
public final class KeySharesLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final CreateKeySharesScenarios createSharesScenarios = new CreateKeySharesScenarios(this.config);
    private final CreateNonceScenarios createNonceScenarios = new CreateNonceScenarios(this.config);
    private final GetKeySharesScenarios getSharesScenarios = new GetKeySharesScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();
    {
        var loadTestConfig = this.config.getLoadTestConfig();

        ScenarioBuilder scenarioBuilder = scenario("Run full key share flow")
            .exec(this.createSharesScenarios.sendKeyShare())
            .exec(this.createNonceScenarios.createNonceForKeyShare())
            .exec(this.getSharesScenarios.getKeyShare());

        setUp(
            scenarioBuilder.injectOpen(
                    // wait for some shares and nonces to be created and their urls returned
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
