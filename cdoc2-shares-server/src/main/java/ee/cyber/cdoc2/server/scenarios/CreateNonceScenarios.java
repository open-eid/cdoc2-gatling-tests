package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteCreateNonce;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;


/**
 * Test scenarios for key share nonce
 */
@Slf4j
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
public class CreateNonceScenarios extends ExecuteCreateNonce {

    public CreateNonceScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder createNonceForKeyShare() {
        return this.sendNonceCheckSuccess(
            ScenarioIdentifiers.POS_NONCE_01 + " - Create nonce"
        );
    }

    public ChainBuilder createNonceForMismatchedRecipientShare() {
        return this.sendNonceCheckSuccess(
            "Create nonce for mismatched-recipient key share",
            TestDataGenerator.MISMATCH_RECIPIENT,
            TestDataGenerator.MISMATCH_CERT_BASE64URL
        );
    }

    public ChainBuilder createNonceForNonExistingShare() {
        return this.sendNonceForNonExistingShareCheckError(
            ScenarioIdentifiers.NEG_POST_NONCE_01, HttpResponseStatus.NOT_FOUND
        );
    }
}
