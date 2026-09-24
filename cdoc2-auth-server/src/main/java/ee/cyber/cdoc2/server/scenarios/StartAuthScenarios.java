package ee.cyber.cdoc2.server.scenarios;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.AuthIdentityRequest;
import ee.cyber.cdoc2.server.tests.ExecuteStartAuth;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;

import static ee.cyber.cdoc2.server.utils.TestDataGenerator.*;
import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/auth/start" endpoint
 */
@Slf4j
public class StartAuthScenarios extends ExecuteStartAuth {

    public StartAuthScenarios(TestConfig conf) {
        super(conf);
    }

    /**
     * Starts a Smart-ID auth process for the SID demo test identity. Performs a live call
     * to the SK Smart-ID demo environment through the auth server under test.
     */
    public ChainBuilder startSidAuth() {
        var payload = new AuthIdentityRequest(SID_IDENTIFIER_OK, null, LANGUAGE_ET);

        return this.sendStartAuthCheckSuccess(
            x -> payload,
            ScenarioIdentifiers.POS_START_AUTH_01 + " - Start SID auth"
        );
    }

    /**
     * Starts a Smart-ID auth process for the SID demo test identity without specifying language
     */
    public ChainBuilder startSidAuthNoLanguage() {
        var payload = new AuthIdentityRequest(SID_IDENTIFIER_OK, null, null);

        return this.sendStartAuthCheckSuccess(
            x -> payload,
            ScenarioIdentifiers.POS_START_AUTH_03 + " - Start SID auth with no language"
        );
    }

    /**
     * Starts a Mobile-ID auth process for the MID demo test identity. Performs a live call
     * to the SK Mobile-ID demo environment through the auth server under test.
     */
    public ChainBuilder startMidAuth() {
        var payload = new AuthIdentityRequest(MID_IDENTIFIER_OK, MID_PHONE_NUMBER_OK, LANGUAGE_ET);

        return this.sendStartAuthCheckSuccess(
            x -> payload,
            ScenarioIdentifiers.POS_START_AUTH_02 + " - Start MID auth"
        );
    }

    /**
     * Starts a Mobile-ID auth process for the MID demo test identity without specifying language
     */
    public ChainBuilder startMidAuthNoLanguage() {
        var payload = new AuthIdentityRequest(MID_IDENTIFIER_OK, MID_PHONE_NUMBER_OK, null);

        return this.sendStartAuthCheckSuccess(
            x -> payload,
            ScenarioIdentifiers.POS_START_AUTH_04 + " - Start MID auth with no language"
        );
    }

    /**
     * Starts a Smart-ID auth process for the "always refused" SID demo test identity - used
     * as setup for the GET_AUTH_STATUS-POS-04-FAILED scenario, not a scenario id of its own.
     */
    public ChainBuilder startSidAuthForRefusal() {
        var payload = TestDataGenerator.createSidAuthRequestForRefusal();

        return this.sendStartAuthCheckSuccess(x -> payload, "Start SID auth (will be refused)");
    }

    public ScenarioBuilder startSidAuthScenario() {
        return scenario("Start SID auth").exec(this.startSidAuth());
    }

    public ScenarioBuilder startSidAuthNoLanguageScenario() {
        return scenario("Start SID auth with no language").exec(this.startSidAuthNoLanguage());
    }

    public ScenarioBuilder startMidAuthScenario() {
        return scenario("Start MID auth").exec(this.startMidAuth());
    }

    /**
     * Starts a Smart-ID auth process for the "user cancelled" MID demo test identity - used
     * as setup for the GET_AUTH_STATUS-POS-04-FAILED scenario, not a scenario id of its own.
     */
    public ChainBuilder startMidAuthForRefusal() {
        var payload = TestDataGenerator.createMidAuthRequestForRefusal();

        return this.sendStartAuthCheckSuccess(x -> payload, "Start MID auth (will be refused)");
    }

    public ScenarioBuilder startMidAuthNoLanguageScenario() {
        return scenario("Start MID auth with no language").exec(this.startMidAuthNoLanguage());
    }

    public ScenarioBuilder startAuthWithMissingIdentifier() {
        var payload = new AuthIdentityRequest(null, null, null);

        return scenario("Fail to start auth with missing identifier").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_01,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithIdentifierTooShort() {
        var payload = new AuthIdentityRequest(
            TestDataGenerator.randomString(TestDataGenerator.AUTH_IDENTIFIER_MIN_LENGTH - 1),
            null,
            null
        );

        return scenario("Fail to start auth with too short identifier").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_02,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithIdentifierTooLong() {
        var payload = new AuthIdentityRequest(
            TestDataGenerator.randomString(TestDataGenerator.AUTH_IDENTIFIER_MAX_LENGTH + 1),
            null,
            null
        );

        return scenario("Fail to start auth with too long identifier").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_03,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithInvalidEtsiFormat() {
        // within the valid length bounds, but not a "etsi/COUNTRY-identifier" formatted value
        var payload = new AuthIdentityRequest(
            TestDataGenerator.randomString(TestDataGenerator.AUTH_IDENTIFIER_MIN_LENGTH + 4),
            null,
            null
        );

        return scenario("Fail to start auth with invalid ETSI identifier format").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_04,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithMobileNrTooShort() {
        var payload = new AuthIdentityRequest(
            SID_IDENTIFIER_OK,
            TestDataGenerator.randomString(TestDataGenerator.MOBILE_NR_MIN_LENGTH - 1),
            null
        );

        return scenario("Fail to start auth with too short mobile number").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_05_MOBILE_NR_TOO_SHORT,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithMobileNrTooLong() {
        var payload = new AuthIdentityRequest(
            SID_IDENTIFIER_OK,
            TestDataGenerator.randomString(TestDataGenerator.MOBILE_NR_MAX_LENGTH + 1),
            null
        );

        return scenario("Fail to start auth with too long mobile number").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_06_MOBILE_NR_TOO_LONG,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder startAuthWithUnknownLanguage() {
        var payload = new AuthIdentityRequest(
            SID_IDENTIFIER_OK,
            null,
            "zz"
        );

        return scenario("Fail to start auth with unknown language").exec(
            this.sendStartAuthCheckError(
                x -> payload,
                ScenarioIdentifiers.NEG_START_AUTH_06 + " - accepted, falls back to default language",
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }
}
