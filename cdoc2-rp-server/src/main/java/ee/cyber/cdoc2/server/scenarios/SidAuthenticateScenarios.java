package ee.cyber.cdoc2.server.scenarios;

import ee.cyber.cdoc2.server.conf.TestConfig;
import ee.cyber.cdoc2.server.dto.SidAuthenticationRequest;
import ee.cyber.cdoc2.server.tests.ExecuteSidAuthenticate;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Test scenarios for the "/sid/authenticate" endpoint.
 */
@Slf4j
public class SidAuthenticateScenarios extends ExecuteSidAuthenticate {

    public SidAuthenticateScenarios(TestConfig conf) {
        super(conf);
    }

    public ChainBuilder startSidAuthenticate() {
        return this.sendSidAuthenticateCheckSuccess(ScenarioIdentifiers.POS_SID_AUTHENTICATE_01 + " - Authenticate");
    }

    public ScenarioBuilder authenticateWithMissingSessionToken() {
        return scenario("Fail to authenticate via SID without a session token").exec(
            this.sendSidAuthenticateWithMissingSessionToken(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_01,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMalformedSessionToken() {
        return scenario("Fail to authenticate via SID with a malformed session token").exec(
            this.sendSidAuthenticateWithMalformedSessionToken(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_02,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingSessionCert() {
        return scenario("Fail to authenticate via SID without a session certificate").exec(
            this.sendSidAuthenticateWithMissingSessionCert(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_03,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMalformedSessionCert() {
        // as of cdoc2-auth-token 0.7.1, a non-base64url/DER "x-cdoc2-session-x5c" crashes
        // CertVerifier#checkCertificate with an unhandled NullPointerException (HTTP 500)
        // instead of being rejected cleanly - this assertion documents the intended behavior
        // and is expected to fail until that's fixed.
        return scenario("Fail to authenticate via SID with a malformed session certificate").exec(
            this.sendSidAuthenticateWithMalformedSessionCert(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_04,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithSessionSubCertMismatch() {
        return scenario("Fail to authenticate via SID when session token subject doesn't match certificate").exec(
            this.sendSidAuthenticateWithSessionSubCertMismatch(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_05,
                HttpResponseStatus.UNAUTHORIZED
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingBody() {
        return scenario("Fail to authenticate via SID without a request body").exec(
            this.sendSidAuthenticateWithMissingBody(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_06,
                // no body means no Content-Type header either, which Spring rejects as
                // unsupported media type rather than treating it as a body validation failure
                HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingSemanticsIdentifier() {
        var valid = TestDataGenerator.createSidAuthenticationRequest();
        var payload = new SidAuthenticationRequest(
            null,
            valid.getCertificateLevel(),
            valid.getSignatureProtocol(),
            valid.getSignatureProtocolParameters(),
            valid.getInteractions(),
            valid.getVcType()
        );

        return scenario("Fail to authenticate via SID with missing semantics identifier").exec(
            this.sendSidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_07,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingSignatureProtocol() {
        var valid = TestDataGenerator.createSidAuthenticationRequest();
        var payload = new SidAuthenticationRequest(
            valid.getSemanticsIdentifier(),
            valid.getCertificateLevel(),
            null,
            valid.getSignatureProtocolParameters(),
            valid.getInteractions(),
            valid.getVcType()
        );

        return scenario("Fail to authenticate via SID with missing signature protocol").exec(
            this.sendSidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_08,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingSignatureProtocolParameters() {
        var valid = TestDataGenerator.createSidAuthenticationRequest();
        var payload = new SidAuthenticationRequest(
            valid.getSemanticsIdentifier(),
            valid.getCertificateLevel(),
            valid.getSignatureProtocol(),
            null,
            valid.getInteractions(),
            valid.getVcType()
        );

        return scenario("Fail to authenticate via SID with missing signature protocol parameters").exec(
            this.sendSidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_09,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingInteractions() {
        var valid = TestDataGenerator.createSidAuthenticationRequest();
        var payload = new SidAuthenticationRequest(
            valid.getSemanticsIdentifier(),
            valid.getCertificateLevel(),
            valid.getSignatureProtocol(),
            valid.getSignatureProtocolParameters(),
            null,
            valid.getVcType()
        );

        return scenario("Fail to authenticate via SID with missing interactions").exec(
            this.sendSidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_10,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }

    public ScenarioBuilder authenticateWithMissingVcType() {
        var valid = TestDataGenerator.createSidAuthenticationRequest();
        var payload = new SidAuthenticationRequest(
            valid.getSemanticsIdentifier(),
            valid.getCertificateLevel(),
            valid.getSignatureProtocol(),
            valid.getSignatureProtocolParameters(),
            valid.getInteractions(),
            null
        );

        return scenario("Fail to authenticate via SID with missing verification code type").exec(
            this.sendSidAuthenticateWithPayload(
                ScenarioIdentifiers.NEG_SID_AUTHENTICATE_11,
                payload,
                HttpResponseStatus.BAD_REQUEST
            )
        );
    }
}
