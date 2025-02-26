package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.CreateKeySharesScenarios;
import ee.cyber.cdoc2.server.scenarios.GetKeySharesScenarios;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.http.HttpDsl.http;


/**
 * Functional tests for the key-capsules API
 */
@Slf4j
@SuppressWarnings("squid:S2187") //SonarQube: TestCases should contain tests
public final class KeyShareFunctionalTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final CreateKeySharesScenarios createScenarios = new CreateKeySharesScenarios(this.config);
    private final GetKeySharesScenarios getScenarios = new GetKeySharesScenarios(this.config);

    HttpProtocolBuilder client = http
        .acceptHeader("application/json")
        .disableWarmUp();

    {
        setUp(
            this.createScenarios.sendKeyShareRepeatedly()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.createScenarios.sendKeyShareRandomKeyMaterial()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.createScenarios.sendKeyShareTooBigKeyMaterial()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getScenarios.getWithRandomAuthTicket()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getScenarios.getWithInvalidShareIds()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client)
        )
        .assertions(global().successfulRequests().percent().is(100.0));
    }
}
