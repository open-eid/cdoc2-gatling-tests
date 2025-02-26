package ee.cyber.cdoc2.server.auth;

import ee.cyber.cdoc2.auth.AuthTokenCreator;
import ee.cyber.cdoc2.auth.EtsiIdentifier;
import ee.cyber.cdoc2.auth.ShareAccessData;
import ee.cyber.cdoc2.server.utils.TestDataGenerator;

import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.ECKey;

/**
 * Authentication token signing and ticket creation.
 */
@Slf4j
public class AuthTokenSigner {

    public static String signAuthToken(String serverBaseUrl, String shareId, String nonce) {
        try {
            JWK jwk = JWK.parseFromPEMEncodedObjects(TestDataGenerator.TEST_ECDSA_KEY);
            ECKey privateKey = jwk.toECKey();
            ECDSASigner jwsSigner = new ECDSASigner(privateKey);

            EtsiIdentifier etsi
                = new EtsiIdentifier("etsi/PNOEE-" + TestDataGenerator.TEST_IDENTIFIER);

            AuthTokenCreator tokenSigner = AuthTokenCreator.builder()
                .withEtsiIdentifier(etsi) // "iss" field etsi/PNOEE-30303039914
                .withShareAccessData(new ShareAccessData(
                    serverBaseUrl,
                    shareId,
                    nonce
                ))
                .build();

            tokenSigner.sign(jwsSigner, JWSAlgorithm.ES256);

            return tokenSigner.createTicketForShareId(shareId);
        } catch (Exception ex) {
            log.error("Failed to sign token.", ex);
            throw new RuntimeException(ex);
        }
    }
}
