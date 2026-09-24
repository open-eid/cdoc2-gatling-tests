package ee.cyber.cdoc2.server.conf;

/**
 * Load test configuration
 */
public record ConstantLoadTestConfig(
    int concurrentUsers,
    Long concurrentUsersDuration,
    int rampDownUsers,
    Long rampDownDuration
) {
}
