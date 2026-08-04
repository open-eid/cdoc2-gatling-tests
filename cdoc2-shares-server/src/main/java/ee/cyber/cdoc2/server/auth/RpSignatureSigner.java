package ee.cyber.cdoc2.server.auth;

import ee.cyber.cdoc2.server.utils.TestDataGenerator;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.authlete.hms.ComponentIdentifier;
import com.authlete.hms.ComponentValueProvider;
import com.authlete.hms.SignatureBase;
import com.authlete.hms.SignatureBaseBuilder;
import com.authlete.hms.SignatureMetadata;
import com.authlete.hms.SignatureMetadataParameters;
import com.authlete.hms.impl.JoseHttpSigner;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;

/**
 * Creates the RFC 9421 HTTP message signature ("RP countersignature") that a relying party
 * attaches to "GET /key-shares/{shareId}" requests, so that load tests don't depend on a
 * running cdoc2-rp-server instance.
 * <p>
 * The signature is created with a fixed EC key ({@link TestDataGenerator#AUTH_TOKEN_SIGNING_KEY}),
 * whose public part must be served by whatever cdoc2-shares-server under test uses as its
 * "rp server well-known" JWK set, under the key ID computed by {@link #keyId(ECKey)}.
 */
@Slf4j
public final class RpSignatureSigner {

    private static final String SIGNATURE_LABEL = "rp-sig";
    private static final String RP_NAME = "DEMO";
    private static final String HASH_ALGORITHM = "SHA-256";

    private RpSignatureSigner() {
        // utility class
    }

    public record RpSignatureHeaders(
        String rpSignedHash,
        String rpName,
        String signatureInput,
        String signature
    ) {
    }

    public static RpSignatureHeaders sign() {
        try {
            ECKey pemKey = (ECKey) JWK.parseFromPEMEncodedObjects(TestDataGenerator.AUTH_TOKEN_SIGNING_KEY);
            ECKey ecKey = new ECKey.Builder(pemKey)
                .algorithm(JWSAlgorithm.ES256)
                .build();

            String rpSignedHash = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance(HASH_ALGORITHM).digest(TestDataGenerator.randomBytes(32))
            );

            ComponentValueProvider context = new ComponentValueProvider()
                .setHeaders(Map.of(
                    "x-rp-signed-hash", List.of(rpSignedHash),
                    "x-rp-name", List.of(RP_NAME)
                ));

            SignatureMetadataParameters params = new SignatureMetadataParameters()
                .setCreated(Instant.now())
                .setKeyid(keyId(ecKey));

            SignatureMetadata metadata = new SignatureMetadata(
                List.of(
                    new ComponentIdentifier("x-rp-signed-hash"),
                    new ComponentIdentifier("x-rp-name")
                ),
                params
            );

            SignatureBase base = new SignatureBaseBuilder(context).build(metadata);
            byte[] signatureBytes = base.sign(new JoseHttpSigner(ecKey));

            String signatureInput = SIGNATURE_LABEL + "=" + metadata.serialize();
            String signature = SIGNATURE_LABEL + "=:" + Base64.getEncoder().encodeToString(signatureBytes) + ":";

            return new RpSignatureHeaders(rpSignedHash, RP_NAME, signatureInput, signature);
        } catch (Exception ex) {
            log.error("Failed to create RP HTTP message signature.", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Key id under which the RP server's well-known endpoint must publish the public part of
     * {@link TestDataGenerator#AUTH_TOKEN_SIGNING_KEY} (RFC 7638 JWK thumbprint), matching how
     * cdoc2-rp-server derives key ids for its published JWK set.
     */
    private static String keyId(ECKey ecKey) throws Exception {
        return ecKey.computeThumbprint().toString();
    }
}
