package ee.cyber.cdoc2.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cdoc2-rp-server "POST /mid/authenticate" request body DTO (see the "MidAuthenticationRequestBody"
 * schema in the RP server's OpenAPI spec).
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MidAuthenticationRequest {

    /**
     * Phone number of the signer with country code prefix, e.g. "+3726234566".
     */
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    /**
     * National identity number of the signer (without country prefix).
     */
    @JsonProperty("nationalIdentityNumber")
    private String nationalIdentityNumber;

    /**
     * Base64-encoded hash value that is signed by the user's Mobile-ID SIM card.
     */
    @JsonProperty("hash")
    private String hash;

    /**
     * Hash algorithm used to compute "hash". One of "SHA256", "SHA384", "SHA512".
     */
    @JsonProperty("hashType")
    private String hashType;

    /**
     * Language for the user dialog in the Mobile-ID app. One of "EST", "ENG", "RUS", "LIT".
     */
    @JsonProperty("language")
    private String language;

    /**
     * Text displayed to the user on the Mobile-ID app.
     */
    @JsonProperty("displayText")
    private String displayText;

    /**
     * Encoding of "displayText". One of "GSM-7", "UCS-2".
     */
    @JsonProperty("displayTextFormat")
    private String displayTextFormat;
}
