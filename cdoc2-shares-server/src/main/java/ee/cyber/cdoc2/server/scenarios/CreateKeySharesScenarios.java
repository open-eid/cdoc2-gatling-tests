package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteCreateKeyShares;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for creating key shares
 */
@Slf4j
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
public class CreateKeySharesScenarios extends ExecuteCreateKeyShares {

    public CreateKeySharesScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder sendKeyShare() {
        var payload = TestDataGenerator.createKeyShareRequest(TestDataGenerator.KEY_SHARE_MAX_LENGTH);

        return this.sendKeyShareCheckSuccess(
            x -> payload,
            ScenarioIdentifiers.POS_KEYSHARE_01 + " - Create key share"
        );
    }

    public ScenarioBuilder sendKeyShareRandomKeyMaterial() {
        var payload = TestDataGenerator.createKeyShareRequest(TestDataGenerator.KEY_SHARE_MAX_LENGTH);

        return scenario("Send key share with random material").exec(
            this.sendKeyShareCheckSuccess(x -> payload, ScenarioIdentifiers.POS_KEYSHARE_03)
        );
    }

    public ScenarioBuilder sendKeyShareRepeatedly() {
        var payload = TestDataGenerator.createKeyShareRequest(TestDataGenerator.KEY_SHARE_MAX_LENGTH);

        return scenario("Send same key share twice").exec(
            this.sendKeyShareCheckSuccess(
                x -> payload, ScenarioIdentifiers.POS_KEYSHARE_02 + " - 1st"
            ),
            this.sendKeyShareCheckSuccess(
                x -> payload, ScenarioIdentifiers.POS_KEYSHARE_02 + " - 2nd"
            )
        );
    }

    public ChainBuilder sendKeyShareMismatchedRecipient() {
        var payload = TestDataGenerator.createKeyShareRequest(
            TestDataGenerator.KEY_SHARE_MAX_LENGTH, TestDataGenerator.MISMATCH_RECIPIENT
        );

        return this.sendKeyShareCheckSuccess(
            x -> payload, "Create key share for mismatched recipient"
        );
    }

    public ScenarioBuilder sendKeyShareTooBigKeyMaterial() {
        var payload = TestDataGenerator.createKeyShareRequest(TestDataGenerator.KEY_SHARE_MAX_LENGTH + 1);

        return scenario("Fail to create key share with too big key material").exec(
            this.sendKeyShareCheckError(x -> payload, ScenarioIdentifiers.NEG_POST_KEYSHARE_01,
                HttpResponseStatus.BAD_REQUEST)
        );
    }
}
