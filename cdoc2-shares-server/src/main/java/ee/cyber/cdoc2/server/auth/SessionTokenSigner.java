package ee.cyber.cdoc2.server.auth;

import ee.cyber.cdoc2.auth.Constants;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.authlete.sd.Disclosure;
import com.authlete.sd.SDJWT;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Creates cdoc2-auth-server session tokens (SD-JWT) for load testing, so that load tests don't
 * depend on a running cdoc2-auth-server instance.
 * <p>
 * The token is signed with a fixed EC key ({@link TestDataGenerator#SESSION_TOKEN_SIGNING_KEY}),
 * whose public part must be served by whatever cdoc2-shares-server under test uses as its
 * "auth server well-known" JWK set (see {@link TestDataGenerator#SESSION_TOKEN_SIGNING_KEY_KID}).
 */
@Slf4j
public final class SessionTokenSigner {

    private static final String ISSUER = "https://cdoc2-auth-server.ee";
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(1);

    private SessionTokenSigner() {
        // utility class
    }

    public static String signSessionToken(String nonceUrl) {
        return signSessionToken(nonceUrl, TestDataGenerator.RECIPIENT);
    }

    /**
     * Creates a session token with a caller-provided subject, so that tests can present an
     * identity other than the default {@link TestDataGenerator#RECIPIENT}. The subject's
     * matching certificate must be passed separately as the "x-cdoc2-session-x5c" header, since
     * the server cross-checks the token subject against that certificate's identity.
     */
    public static String signSessionToken(String nonceUrl, String subject) {
        try {
            JWK jwk = JWK.parseFromPEMEncodedObjects(TestDataGenerator.SESSION_TOKEN_SIGNING_KEY);
            ECKey privateKey = jwk.toECKey();

            Disclosure audDisclosure = new Disclosure("aud", List.of(nonceUrl));

            Instant now = Instant.now();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(ISSUER)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(TOKEN_VALIDITY)))
                .claim("_sd_alg", "sha-256")
                .claim("_sd", List.of(audDisclosure.digest()))
                .build();

            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .type(new JOSEObjectType(Constants.TYPE_SESSION_TOKEN))
                    .keyID(TestDataGenerator.SESSION_TOKEN_SIGNING_KEY_KID)
                    .build(),
                claimsSet
            );
            signedJWT.sign(new ECDSASigner(privateKey));

            return new SDJWT(signedJWT.serialize(), List.of(audDisclosure)).toString();
        } catch (Exception ex) {
            log.error("Failed to sign session token.", ex);
            throw new RuntimeException(ex);
        }
    }
}
