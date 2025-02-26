package ee.cyber.cdoc2.server.conf;

import lombok.Value;

/**
 * Load test configuration
 */
@Value
public class LoadTestConfig {

    Long incrementUsersPerSec;
    int incrementCycles;
    Long cycleDurationSec;
    Long startingUsersPerSec;
    /**
     * Delay before executing requests to allow for some shares and nonces to be created
     * first so that there is input data for getShare tests available.
     */
    Long requestStartDelay;

}
