package ee.cyber.cdoc2.server.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Gatling test configuration properties for shares server instances
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@ToString
public class TestConfig {

    private final String serverBaseUrl;
    private final LoadTestConfig loadTestConfig;

    /**
     * Loads the configuration from file
     *
     * @param isLoadTest boolean indicating whether to read load test configuration
     *
     * see @link https://github.com/lightbend/config#standard-behavior for file names and formats
     */
    public static TestConfig load() {
        var conf = ConfigFactory.load();

        var testConf = new TestConfig(
            conf.getString("shares-server.base-url"),
            readLoadTestConfig(conf)
        );

        log.info("Loaded test configuration: {}", testConf);

        return testConf;
    }

    private static LoadTestConfig readLoadTestConfig(Config config) {
        var request = config.getConfig("load-test.request");

        return new LoadTestConfig(
            request.getLong("increment-users-per-second"),
            request.getInt("increment-cycles"),
            request.getLong("cycle-duration-seconds"),
            request.getLong("start-users-per-second"),
            request.getLong("initial-delay-seconds")
        );
    }
}
