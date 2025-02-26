package ee.cyber.cdoc2.server.scenarios;

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
            ScenarioIdentifiers.POS_GET_SHARE_01 + " - Get key share"
        );
    }

    public ScenarioBuilder getWithInvalidShareIds() {
        return scenario("Request key share with invalid shareId values")
            .exec(
                this.checkEmptyShareId(
                    ScenarioIdentifiers.NEG_GET_SHARE_02,
                    "",
                    HttpResponseStatus.NOT_FOUND
                ),
                this.checkInvalidInput(
                    ScenarioIdentifiers.NEG_GET_SHARE_03 + " - Invalid share ID",
                    null,
                    HttpResponseStatus.BAD_REQUEST
                ),
                this.checkMissingShareIdAndUriSlash(
                    ScenarioIdentifiers.NEG_GET_SHARE_04,
                    HttpResponseStatus.METHOD_NOT_ALLOWED
                ),
                this.checkInvalidInput(
                    ScenarioIdentifiers.NEG_GET_SHARE_05 + " - Invalid share ID",
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
                    ScenarioIdentifiers.NEG_GET_SHARE_06 + " - Random authentication ticket",
                    TestDataGenerator.randomString(TestDataGenerator.SHARE_ID_MIN_LENGTH),
                    HttpResponseStatus.UNAUTHORIZED
                )
            ).exitHereIfFailed();
    }
}
