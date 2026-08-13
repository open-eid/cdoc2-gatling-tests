package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.MidAuthenticationRequest;
import ee.cyber.cdoc2.server.tests.ExecuteMidAuthenticate;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/mid/authenticate" endpoint.
 */
@Slf4j
public class MidAuthenticateScenarios extends ExecuteMidAuthenticate {

    public MidAuthenticateScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder startMidAuthenticate() {
        return this.sendMidAuthenticateCheckSuccess(ScenarioIdentifiers.POS_MID_AUTHENTICATE_01 + " - Authenticate");
    }

    public ScenarioBuilder authenticateWithMissingSessionToken() {
        return scenario("Fail to authenticate via MID without a session token").exec(
            this.sendMidAuthenticateWithMissingSessionToken(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_01,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMalformedSessionToken() {
        return scenario("Fail to authenticate via MID with a malformed session token").exec(
            this.sendMidAuthenticateWithMalformedSessionToken(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_02,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingSessionCert() {
        return scenario("Fail to authenticate via MID without a session certificate").exec(
            this.sendMidAuthenticateWithMissingSessionCert(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_03,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMalformedSessionCert() {
        return scenario("Fail to authenticate via MID with a malformed session certificate").exec(
            this.sendMidAuthenticateWithMalformedSessionCert(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_04,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithSessionSubCertMismatch() {
        return scenario("Fail to authenticate via MID when session token subject doesn't match certificate").exec(
            this.sendMidAuthenticateWithSessionSubCertMismatch(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_05,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingBody() {
        return scenario("Fail to authenticate via MID without a request body").exec(
            this.sendMidAuthenticateWithMissingBody(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_06,
                // no body means no Content-Type header either, which Spring rejects as
                // unsupported media type rather than treating it as a body validation failure
                HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingPhoneNumber() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            null,
            valid.getNationalIdentityNumber(),
            valid.getHash(),
            valid.getHashType(),
            valid.getLanguage(),
            valid.getDisplayText(),
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing phone number").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_07,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingNationalIdentityNumber() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            null,
            valid.getHash(),
            valid.getHashType(),
            valid.getLanguage(),
            valid.getDisplayText(),
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing national identity number").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_08,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingHash() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            valid.getNationalIdentityNumber(),
            null,
            valid.getHashType(),
            valid.getLanguage(),
            valid.getDisplayText(),
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing hash").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_09,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingHashType() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            valid.getNationalIdentityNumber(),
            valid.getHash(),
            null,
            valid.getLanguage(),
            valid.getDisplayText(),
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing hash type").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_10,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingLanguage() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            valid.getNationalIdentityNumber(),
            valid.getHash(),
            valid.getHashType(),
            null,
            valid.getDisplayText(),
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing language").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_11,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingDisplayText() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            valid.getNationalIdentityNumber(),
            valid.getHash(),
            valid.getHashType(),
            valid.getLanguage(),
            null,
            valid.getDisplayTextFormat()
        );

        return scenario("Fail to authenticate via MID with missing display text").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_MID_AUTHENTICATE_12,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingDisplayTextFormat() {
        var valid = TestDataGenerator.createMidAuthenticationRequest();
        var payload = new MidAuthenticationRequest(
            valid.getPhoneNumber(),
            valid.getNationalIdentityNumber(),
            valid.getHash(),
            valid.getHashType(),
            valid.getLanguage(),
            valid.getDisplayText(),
            null
        );

        return scenario("Authenticate via MID with missing display text format uses default").exec(
            this.sendMidAuthenticateWithPayload(
                ScenarioIdentifiers.POS_MID_AUTHENTICATE_02,
                payload,
                HttpResponseStatus.OK
            )
        );
    }
}
