package ee.cyber.cdoc2.server.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Gatling test configuration properties for RP server instances
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
     */
    public static TestConfig load() {
        var conf = ConfigFactory.load();

        var testConf = new TestConfig(
            conf.getString("rp-server.base-url"),
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
