package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.GetAuthStatusScenarios;
import ee.cyber.cdoc2.server.scenarios.GetInfoScenarios;
import ee.cyber.cdoc2.server.scenarios.StartAuthScenarios;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;


/**
 * Functional tests covering cdoc2-auth-server's "Authentication Server API functionality
 * tests" as listed in the CDOC2 server test plan: "/auth/start", "/auth/status/{uuid}" and
 * "/info".
 * <p>
 * Scenarios that start a real Smart-ID or Mobile-ID auth process (startSidAuth, startMidAuth
 * and their derivatives) perform a live call through the auth server under test - normally to
 * the mock SID/MID server (see ../../mock-sid-mid-server/README.md for what that mock does
 * and does not fake), or to the real SK demo environment if the auth server under test is
 * configured that way. The remaining negative scenarios fail request validation before any
 * such call is made, so they carry no such dependency.
 */
@Slf4j
@SuppressWarnings("squid:S2187") //SonarQube: TestCases should contain tests
public final class StartAuthFunctionalTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final StartAuthScenarios startAuthScenarios = new StartAuthScenarios(this.config);
    private final GetAuthStatusScenarios getAuthStatusScenarios = new GetAuthStatusScenarios(this.config);
    private final GetInfoScenarios getInfoScenarios = new GetInfoScenarios(this.config);

    HttpProtocolBuilder client = http
        .acceptHeader("application/json")
        .disableWarmUp();

    {
        setUp(
            // POST /auth/start
            this.startAuthScenarios.startSidAuthScenario()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startMidAuthScenario()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startSidAuthNoLanguageScenario()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startMidAuthNoLanguageScenario()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithMissingIdentifier()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithIdentifierTooShort()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithIdentifierTooLong()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithInvalidEtsiFormat()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithMobileNrTooShort()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithMobileNrTooLong()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.startAuthScenarios.startAuthWithUnknownLanguage()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),

            // GET /auth/status/{authProcessUuid}
            scenario("Get completed auth process status once")
                .exec(this.startAuthScenarios.startSidAuth())
                .exec(this.getAuthStatusScenarios.pollAuthStatusCompleted())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            scenario("Get completed auth process status repeatedly")
                .exec(this.startAuthScenarios.startSidAuth())
                .exec(this.getAuthStatusScenarios.pollAuthStatusCompletedFirst())
                .exec(this.getAuthStatusScenarios.getAuthStatusCompletedSecond())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            scenario("Get running auth process status for SID")
                .exec(this.startAuthScenarios.startSidAuth())
                .exec(this.getAuthStatusScenarios.getAuthStatusRunning())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            scenario("Get running auth process status for MID")
                .exec(this.startAuthScenarios.startMidAuth())
                .exec(this.getAuthStatusScenarios.getAuthStatusRunning())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            scenario("Get failed auth process status for SID")
                .exec(this.startAuthScenarios.startSidAuthForRefusal())
                .exec(this.getAuthStatusScenarios.getAuthStatusFailed())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            scenario("Get failed auth process status for MID")
                .exec(this.startAuthScenarios.startMidAuthForRefusal())
                .exec(this.getAuthStatusScenarios.getAuthStatusFailed())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getAuthStatusScenarios.checkStatusForTooShortUuid()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getAuthStatusScenarios.checkStatusForTooLongUuid()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getAuthStatusScenarios.checkStatusForMalformedUuid()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.getAuthStatusScenarios.checkStatusForUnknownAuthProcess()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),

            // GET /info
            scenario("Get server info")
                .exec(this.getInfoScenarios.getInfo())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client)
        )
        .assertions(global().successfulRequests().percent().is(100.0));
    }
}
