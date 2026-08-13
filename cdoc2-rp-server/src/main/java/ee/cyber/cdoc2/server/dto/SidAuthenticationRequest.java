package ee.cyber.cdoc2.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cdoc2-rp-server "POST /sid/authenticate" request body DTO (see the "SidAuthenticationRequestBody"
 * schema in the RP server's OpenAPI spec).
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SidAuthenticationRequest {

    /**
     * Semantics identifier of the authentication subject, e.g. "PNOEE-30303039914".
     */
    @JsonProperty("semanticsIdentifier")
    private String semanticsIdentifier;

    /**
     * Level of certificate requested. One of "ADVANCED", "QUALIFIED".
     */
    @JsonProperty("certificateLevel")
    private String certificateLevel;

    /**
     * Signature protocol. Currently, the only allowed value is "ACSP_V2".
     */
    @JsonProperty("signatureProtocol")
    private String signatureProtocol;

    @JsonProperty("signatureProtocolParameters")
    private SignatureProtocolParameters signatureProtocolParameters;

    /**
     * Base64 encoded representation of a JSON object listing the supported interaction types.
     */
    @JsonProperty("interactions")
    private String interactions;

    /**
     * The Verification Code (VC) type to use. Currently, the only allowed value is "numeric4".
     */
    @JsonProperty("vcType")
    private String vcType;

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignatureProtocolParameters {

        /**
         * Base64 encoded random value, freshly generated for each authentication, 32-64 bytes.
         */
        @JsonProperty("rpChallenge")
        private String rpChallenge;

        /**
         * Digital signature algorithm name, e.g. "rsassa-pss".
         */
        @JsonProperty("signatureAlgorithm")
        private String signatureAlgorithm;

        @JsonProperty("signatureAlgorithmParameters")
        private SignatureAlgorithmParameters signatureAlgorithmParameters;
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignatureAlgorithmParameters {

        /**
         * Hash algorithm, e.g. "SHA-256".
         */
        @JsonProperty("hashAlgorithm")
        private String hashAlgorithm;
    }
}
