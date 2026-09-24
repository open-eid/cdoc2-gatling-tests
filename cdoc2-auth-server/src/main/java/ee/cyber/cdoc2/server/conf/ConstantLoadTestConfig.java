package ee.cyber.cdoc2.server.conf;

/**
 * Constant load test configuration
 */
public record ConstantLoadTestConfig(
    int concurrentUsers,
    Long concurrentUsersDuration,
    int rampUsers,
    Long rampDuration
) {
}
