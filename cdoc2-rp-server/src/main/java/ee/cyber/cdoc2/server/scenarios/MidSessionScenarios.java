package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteMidSession;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/mid/session/{sessionID}" endpoint.
 */
@Slf4j
public class MidSessionScenarios extends ExecuteMidSession {

    private static final String MALFORMED_SESSION_ID = "not-a-uuid";

    public MidSessionScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder getMidSession() {
        return this.getMidSessionCheckSuccess(ScenarioIdentifiers.POS_MID_SESSION_01 + " - Get MID session");
    }

    public ScenarioBuilder getMidSessionWithMissingSessionToken() {
        return scenario("Fail to get MID session without a session token").exec(
            this.getMidSessionWithMissingSessionToken(
                ScenarioIdentifiers.NEG_MID_SESSION_01,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder getMidSessionWithMalformedSessionToken() {
        return scenario("Fail to get MID session with a malformed session token").exec(
            this.getMidSessionWithMalformedSessionToken(
                ScenarioIdentifiers.NEG_MID_SESSION_02,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getMidSessionWithMissingSessionCert() {
        return scenario("Fail to get MID session without a session certificate").exec(
            this.getMidSessionWithMissingSessionCert(
                ScenarioIdentifiers.NEG_MID_SESSION_03,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder getMidSessionWithMalformedSessionCert() {
        return scenario("Fail to get MID session with a malformed session certificate").exec(
            this.getMidSessionWithMalformedSessionCert(
                ScenarioIdentifiers.NEG_MID_SESSION_04,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getMidSessionWithSessionSubCertMismatch() {
        return scenario("Fail to get MID session when session token subject doesn't match certificate").exec(
            this.getMidSessionWithSessionSubCertMismatch(
                ScenarioIdentifiers.NEG_MID_SESSION_05,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder getMidSessionWithMissingSessionId() {
        return scenario("Fail to get MID session with missing sessionID").exec(
            this.getMidSessionWithMissingSessionId(
                ScenarioIdentifiers.NEG_MID_SESSION_06,
                HttpResponseStatus.NOT_FOUND
            )
        );
    }

    public ScenarioBuilder getMidSessionWithMalformedSessionId() {
        return scenario("Fail to get MID session with malformed sessionID").exec(
            this.getMidSessionWithId(
                ScenarioIdentifiers.NEG_MID_SESSION_07,
                MALFORMED_SESSION_ID,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }
}
