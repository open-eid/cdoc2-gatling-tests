package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteSidSession;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/sid/session/{sessionID}" endpoint.
 */
@Slf4j
public class SidSessionScenarios extends ExecuteSidSession {

    // "sessionID" path parameter is a UUID (format: uuid); anything else fails Spring's
    // @PathVariable type conversion before any application logic runs.
    private static final String MALFORMED_SESSION_ID = "not-a-uuid";

    public SidSessionScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder getSidSession() {
        return this.getSidSessionCheckSuccess(ScenarioIdentifiers.POS_SID_SESSION_01 + " - Get SID session");
    }

    public ScenarioBuilder getSidSessionWithMissingSessionToken() {
        return scenario("Fail to get SID session without a session token").exec(
            this.getSidSessionWithMissingSessionToken(
                ScenarioIdentifiers.NEG_SID_SESSION_01,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder getSidSessionWithMalformedSessionToken() {
        return scenario("Fail to get SID session with a malformed session token").exec(
            this.getSidSessionWithMalformedSessionToken(
                ScenarioIdentifiers.NEG_SID_SESSION_02,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getSidSessionWithMissingSessionCert() {
        return scenario("Fail to get SID session without a session certificate").exec(
            this.getSidSessionWithMissingSessionCert(
                ScenarioIdentifiers.NEG_SID_SESSION_03,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder getSidSessionWithMalformedSessionCert() {
        return scenario("Fail to get SID session with a malformed session certificate").exec(
            this.getSidSessionWithMalformedSessionCert(
                ScenarioIdentifiers.NEG_SID_SESSION_04,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getSidSessionWithSessionSubCertMismatch() {
        return scenario("Fail to get SID session when session token subject doesn't match certificate").exec(
            this.getSidSessionWithSessionSubCertMismatch(
                ScenarioIdentifiers.NEG_SID_SESSION_05,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getSidSessionWithMissingSessionId() {
        return scenario("Fail to get SID session with missing sessionID").exec(
            this.getSidSessionWithMissingSessionId(
                ScenarioIdentifiers.NEG_SID_SESSION_06,
                HttpResponseStatus.NOT_FOUND
            )
        );
    }

    public ScenarioBuilder getSidSessionWithMalformedSessionId() {
        return scenario("Fail to get SID session with malformed sessionID").exec(
            this.getSidSessionWithId(
                ScenarioIdentifiers.NEG_SID_SESSION_07,
                MALFORMED_SESSION_ID,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }
}
