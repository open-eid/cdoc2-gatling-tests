package ee.cyber.cdoc2.server;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.scenarios.MidAuthenticateScenarios;
import ee.cyber.cdoc2.server.scenarios.MidSessionScenarios;
import ee.cyber.cdoc2.server.scenarios.SidAuthenticateScenarios;
import ee.cyber.cdoc2.server.scenarios.SidSessionScenarios;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;


/**
 * Functional tests covering cdoc2-rp-server's "Relying Party Server API functionality tests"
 * as listed in the CDOC2 server test plan: "/sid/authenticate", "/sid/session/{sessionID}",
 * "/mid/authenticate" and "/mid/session/{sessionID}".
 */
@Slf4j
@SuppressWarnings("squid:S2187") //SonarQube: TestCases should contain tests
public final class RpServerFunctionalTests extends Simulation {

    private final TestConfig config = TestConfig.load();
    private final SidAuthenticateScenarios sidAuthenticateScenarios = new SidAuthenticateScenarios(this.config);
    private final SidSessionScenarios sidSessionScenarios = new SidSessionScenarios(this.config);
    private final MidAuthenticateScenarios midAuthenticateScenarios = new MidAuthenticateScenarios(this.config);
    private final MidSessionScenarios midSessionScenarios = new MidSessionScenarios(this.config);

    HttpProtocolBuilder client = http
        .acceptHeader("application/json")
        .disableWarmUp();

    {
        setUp(
            // POST /mid/authenticate, GET /mid/session/{sessionID}
            scenario("Authenticate via MID and get session status")
                .exec(this.midAuthenticateScenarios.startMidAuthenticate())
                .exec(this.midSessionScenarios.getMidSession())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            // POST /sid/authenticate, GET /sid/session/{sessionID}
            scenario("Authenticate via SID and get session status")
                .exec(this.sidAuthenticateScenarios.startSidAuthenticate())
                .exec(this.sidSessionScenarios.getSidSession())
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMalformedSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMalformedSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithSessionSubCertMismatch()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingBody()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingSemanticsIdentifier()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingSignatureProtocol()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingSignatureProtocolParameters()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingInteractions()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidAuthenticateScenarios.authenticateWithMissingVcType()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMissingSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMalformedSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMissingSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMalformedSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithSessionSubCertMismatch()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMissingSessionId()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.sidSessionScenarios.getSidSessionWithMalformedSessionId()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMalformedSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMalformedSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithSessionSubCertMismatch()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingBody()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingPhoneNumber()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingNationalIdentityNumber()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingHash()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingHashType()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingLanguage()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingDisplayText()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midAuthenticateScenarios.authenticateWithMissingDisplayTextFormat()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMissingSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMalformedSessionToken()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMissingSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMalformedSessionCert()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithSessionSubCertMismatch()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMissingSessionId()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client),
            this.midSessionScenarios.getMidSessionWithMalformedSessionId()
                .injectOpen(atOnceUsers(1))
                .protocols(this.client)
        )
            .assertions(global().successfulRequests().percent().is(100.0));
    }
}
