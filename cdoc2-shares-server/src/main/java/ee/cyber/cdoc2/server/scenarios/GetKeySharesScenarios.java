package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.SessionVariables;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import ee.cyber.cdoc2.server.tests.ExecuteGetKeyShares;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for getting key shares
 */
@Slf4j
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
public class GetKeySharesScenarios extends ExecuteGetKeyShares {

    public GetKeySharesScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder getKeyShare() {
        return this.getKeyShareCheckSuccess(
            ScenarioIdentifiers.POS_GET_KEYSHARE_01 + " - Get key share"
        );
    }

    public ScenarioBuilder getWithInvalidShareIds() {
        return scenario("Request key share with invalid shareId values")
            .exec(
                this.checkEmptyShareId(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_02,
                    "",
                    HttpResponseStatus.NOT_FOUND
                ),
                this.checkInvalidInput(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_01 + " - Invalid share ID",
                    null,
                    HttpResponseStatus.BAD_REQUEST
                ),
                this.checkMissingShareIdAndUriSlash(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_04,
                    HttpResponseStatus.METHOD_NOT_ALLOWED
                ),
                this.checkInvalidInput(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_03 + " - Invalid share ID",
                    TestDataGenerator.randomString(TestDataGenerator.SHARE_ID_MAX_LENGTH + 1),
                    HttpResponseStatus.BAD_REQUEST
                )
            )
            .exitHereIfFailed();
    }

    public ScenarioBuilder getWithRandomAuthTicket() {
        return scenario("Request Key Share with random authentication ticket")
            .exec(
                this.checkInvalidInput(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_05 + " - Random authentication ticket",
                    TestDataGenerator.randomString(TestDataGenerator.SHARE_ID_MIN_LENGTH),
                    HttpResponseStatus.UNAUTHORIZED
                )
            ).exitHereIfFailed();
    }

    /**
     * Requests a key share for a share ID that was never created. Depends on a session nonce
     * already being present in the session (see {@link SessionVariables#SESSION_NONCE}).
     */
    public ChainBuilder getKeyShareWithRandomShareId() {
        return this.checkRandomShareIdNotFound(
            ScenarioIdentifiers.NEG_GET_KEYSHARE_06 + " - Random share ID",
            HttpResponseStatus.NOT_FOUND
        );
    }

    /**
     * Requests an existing key share, authenticating as an identity other than the share's
     * recipient. Depends on {@link SessionVariables#LOCATION} and {@link SessionVariables#NONCE}
     * already referring to a key share created for {@link TestDataGenerator#MISMATCH_RECIPIENT}.
     */
    public ChainBuilder getKeyShareWithMismatchedRecipient() {
        return this.getKeyShareCheckError(
            ScenarioIdentifiers.NEG_GET_KEYSHARE_07 + " - Recipient not matching",
            HttpResponseStatus.NOT_FOUND
        );
    }

    public ScenarioBuilder getWithMissingAuthHeaders() {
        return scenario("Request key share without authentication headers")
            .exec(
                this.checkMissingAuthHeaders(
                    ScenarioIdentifiers.NEG_GET_KEYSHARE_08 + " - Missing authentication headers",
                    HttpResponseStatus.BAD_REQUEST
                )
            ).exitHereIfFailed();
    }
}
