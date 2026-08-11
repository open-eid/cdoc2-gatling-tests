package ee.cyber.cdoc2.server.conf;

/**
 * Load test configuration
 */
public record LoadTestConfig(
    Long incrementUsersPerSec,
    int incrementCycles,
    Long cycleDurationSec,
    Long startingUsersPerSec,
    Long requestStartDelay
) {

}
