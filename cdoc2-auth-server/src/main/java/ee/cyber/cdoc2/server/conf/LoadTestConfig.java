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
    Long requestStartDelay;
    int atOnceUsers;
}
