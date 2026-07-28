package ee.cyber.cdoc2.server.scenarios;

import io.gatling.javaapi.core.ChainBuilder;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteCreateSessionNonce;

/**
 * Test scenarios for session nonce
 */
@Slf4j
@SuppressWarnings("unchecked") // compiler warning come from usage of Gatling API
public class CreateSessionNonceScenarios extends ExecuteCreateSessionNonce {

    public CreateSessionNonceScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder createSessionNonce() {
        return this.sendSessionNonceCheckSuccess(
            ScenarioIdentifiers.POS_PUT_NONCE_02 + " - Create session nonce"
        );
    }
}
