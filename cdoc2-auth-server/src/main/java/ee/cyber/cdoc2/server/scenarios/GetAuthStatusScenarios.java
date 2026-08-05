package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteGetAuthStatus;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/auth/status/{authProcessUuid}" endpoint
 */
@Slf4j
public class GetAuthStatusScenarios extends ExecuteGetAuthStatus {

    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_COMPLETE = "COMPLETE";
    private static final String STATUS_FAILED = "FAILED";

    private static final int AUTH_PROCESS_UUID_MIN_LENGTH = 18;
    private static final int AUTH_PROCESS_UUID_MAX_LENGTH = 36;

    public GetAuthStatusScenarios(TestConfig conf) {
        super(conf);
    }

    /**
     * Checks the status of an auth process started earlier in the same scenario. For use as
     * a load test chain step - not tied to a specific test plan scenario id.
     */
    public ChainBuilder getAuthStatus() {
        return this.getAuthStatusCheckStatusIs("Get auth process status", STATUS_COMPLETE);
    }

    /**
     * Expects the process (started via SID against the mock SID/MID server, which completes
     * on its first status poll - see mock-sid-mid-server/README.md) to already be COMPLETE.
     */
    public ChainBuilder getAuthStatusCompletedOnce() {
        return this.getAuthStatusCheckStatusIs(
            ScenarioIdentifiers.POS_AUTH_STATUS_01 + " - Get completed auth process status",
            STATUS_COMPLETE
        );
    }

    public ChainBuilder getAuthStatusCompletedFirstPoll() {
        return this.getAuthStatusCheckStatusIs(ScenarioIdentifiers.POS_AUTH_STATUS_02 + " - 1st", STATUS_COMPLETE);
    }

    public ChainBuilder getAuthStatusCompletedSecondPoll() {
        return this.getAuthStatusCheckStatusIs(ScenarioIdentifiers.POS_AUTH_STATUS_02 + " - 2nd", STATUS_COMPLETE);
    }

    /**
     * Expects the process to still be running. Only reachable via a Mobile-ID auth process:
     * the mock SID/MID server always reports MID sessions as running, while SID sessions
     * complete on the first poll - see mock-sid-mid-server/README.md.
     */
    public ChainBuilder getAuthStatusRunning() {
        return this.getAuthStatusCheckStatusIs(
            ScenarioIdentifiers.POS_AUTH_STATUS_03 + " - Get running auth process status",
            STATUS_STARTED
        );
    }

    /**
     * Expects the process (started via SID for the "always refused" test identity - see
     * StartAuthScenarios.startSidAuthForRefusal()) to have failed.
     */
    public ChainBuilder getAuthStatusFailed() {
        return this.getAuthStatusCheckStatusIs(
            ScenarioIdentifiers.POS_AUTH_STATUS_04 + " - Get failed auth process status",
            STATUS_FAILED
        );
    }

    public ScenarioBuilder checkStatusForTooShortUuid() {
        return scenario("Request auth process status with too short UUID").exec(
            this.checkInvalidAuthProcessUuid(
                ScenarioIdentifiers.NEG_AUTH_STATUS_01,
                TestDataGenerator.randomString(AUTH_PROCESS_UUID_MIN_LENGTH - 1),
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder checkStatusForTooLongUuid() {
        return scenario("Request auth process status with too long UUID").exec(
            this.checkInvalidAuthProcessUuid(
                ScenarioIdentifiers.NEG_AUTH_STATUS_02,
                TestDataGenerator.randomString(AUTH_PROCESS_UUID_MAX_LENGTH + 1),
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder checkStatusForMalformedUuid() {
        return scenario("Request auth process status with malformed UUID").exec(
            this.checkInvalidAuthProcessUuid(
                ScenarioIdentifiers.NEG_AUTH_STATUS_03,
                // within the valid length bounds, but not a valid UUID string
                TestDataGenerator.randomString(AUTH_PROCESS_UUID_MIN_LENGTH + 5),
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder checkStatusForUnknownAuthProcess() {
        return scenario("Request auth process status for a process not present in the database").exec(
            this.checkInvalidAuthProcessUuid(
                ScenarioIdentifiers.NEG_AUTH_STATUS_04,
                UUID.randomUUID().toString(),
                HttpResponseStatus.NOT_FOUND
            )
        );
    }
}
