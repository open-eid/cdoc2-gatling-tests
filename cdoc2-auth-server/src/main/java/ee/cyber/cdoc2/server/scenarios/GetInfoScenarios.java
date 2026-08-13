package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.tests.ExecuteGetInfo;
import io.gatling.javaapi.core.ChainBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Test scenarios for the "/info" endpoint
 */
@Slf4j
public class GetInfoScenarios extends ExecuteGetInfo {

    public GetInfoScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder getInfo() {
        return this.getInfoCheckSuccess(ScenarioIdentifiers.POS_INFO_01 + " - Get server info");
    }
}
