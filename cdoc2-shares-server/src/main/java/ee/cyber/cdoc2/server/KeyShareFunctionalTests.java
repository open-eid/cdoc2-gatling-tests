package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.CreateKeySharesScenarios;
import ee.cyber.cdoc2.server.scenarios.CreateNonceScenarios;
import ee.cyber.cdoc2.server.scenarios.CreateSessionNonceScenarios;
import ee.cyber.cdoc2.server.scenarios.GetKeySharesScenarios;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;


/**
 * Functional tests for the key-capsules API
 */
@Slf4j
@SuppressWarnings("squid:S2187") //SonarQube: TestCases should contain tests
public final class KeyShareFunctionalTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final CreateKeySharesScenarios createScenarios = new CreateKeySharesScenarios(this.config);
    private final CreateSessionNonceScenarios sessionNonceScenarios = new CreateSessionNonceScenarios(this.config);
    private final CreateNonceScenarios nonceScenarios = new CreateNonceScenarios(this.config);
    private final GetKeySharesScenarios getScenarios = new GetKeySharesScenarios(this.config);

    HttpProtocolBuilder client = http
        .acceptHeader("application/json")
        .disableWarmUp();

    // POST_NONCE-NEG-01: nonce requested for a share ID that was never created
    ScenarioBuilder nonceForNonExistingShare = scenario("Request nonce for non-existing key share")
        .exec(this.sessionNonceScenarios.createSessionNonce())
        .exec(this.nonceScenarios.createNonceForNonExistingShare())
        .exitHereIfFailed();

    // GET_KEYSHARE-NEG-06: key share requested for a share ID that was never created
    ScenarioBuilder getKeyShareWithRandomShareId = scenario("Request key share for non-existing shareId")
        .exec(this.sessionNonceScenarios.createSessionNonce())
        .exec(this.getScenarios.getKeyShareWithRandomShareId())
        .exitHereIfFailed();

    // GET_KEYSHARE-NEG-07: key share exists, but the authenticated identity isn't its recipient
    ScenarioBuilder getKeyShareWithMismatchedRecipient =
        scenario("Request key share with recipient not matching authenticated identity")
            .exec(this.createScenarios.sendKeyShareMismatchedRecipient())
            .exec(this.sessionNonceScenarios.createSessionNonce())
            .exec(this.nonceScenarios.createNonceForMismatchedRecipientShare())
            .exec(this.getScenarios.getKeyShareWithMismatchedRecipient())
            .exitHereIfFailed();

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
                .protocols(this.client),
            this.getScenarios.getWithMissingAuthHeaders()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.nonceForNonExistingShare
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getKeyShareWithRandomShareId
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getKeyShareWithMismatchedRecipient
                .injectOpen(atOnceUsers(1))
                .protocols(this.client)
        )
        .assertions(global().successfulRequests().percent().is(100.0));
    }
}
