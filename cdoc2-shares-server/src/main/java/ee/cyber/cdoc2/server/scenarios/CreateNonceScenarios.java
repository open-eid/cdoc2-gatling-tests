package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteCreateNonce;
import io.gatling.javaapi.core.ChainBuilder;
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
}
