package ee.cyber.cdoc2.server;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.CreateKeySharesScenarios;
import ee.cyber.cdoc2.server.scenarios.CreateNonceScenarios;
import ee.cyber.cdoc2.server.scenarios.CreateSessionNonceScenarios;
import ee.cyber.cdoc2.server.scenarios.GetKeySharesScenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load tests for the key-shares API
 */
@Slf4j
public final class KeySharesConstantLoadTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final CreateKeySharesScenarios createSharesScenarios = new CreateKeySharesScenarios(this.config);
    private final CreateSessionNonceScenarios createSessionNonceScenarios =
        new CreateSessionNonceScenarios(this.config);
    private final CreateNonceScenarios createNonceScenarios = new CreateNonceScenarios(this.config);
    private final GetKeySharesScenarios getSharesScenarios = new GetKeySharesScenarios(this.config);

    HttpProtocolBuilder httpConf = http
        .baseUrl(this.config.getServerBaseUrl())
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .disableWarmUp();
    {
        var loadTestConfig = this.config.getConstantLoadTestConfig();

        ScenarioBuilder scenarioBuilder = scenario("Run full key share flow")
            .exec(this.createSharesScenarios.sendKeyShare())
            .exec(this.createSessionNonceScenarios.createSessionNonce())
            .exec(this.createNonceScenarios.createNonceForKeyShare())
            .exec(this.getSharesScenarios.getKeyShare());

        setUp(
            scenarioBuilder.injectClosed(
                constantConcurrentUsers(loadTestConfig.concurrentUsers())
                    .during(loadTestConfig.concurrentUsersDuration())
            )
        ).protocols(this.httpConf)
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
