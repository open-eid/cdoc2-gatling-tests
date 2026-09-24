package ee.cyber.cdoc2.server.conf;

import lombok.Value;

/**
 * Load test configuration
 */
@Value
public class ConstantLoadTestConfig {
    int concurrentUsers;
    Long concurrentUsersDuration;
    int rampDownUsers;
    Long rampDownDuration;
}
