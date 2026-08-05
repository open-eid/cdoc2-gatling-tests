package ee.cyber.cdoc2.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CDOC2 auth server "/auth/start" request body DTO (AuthIdentity in the auth server's OpenAPI spec).
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthIdentityRequest {

    /**
     * ETSI319412-1 semantics identifier. Example "etsi/PNOEE-30303039914".
     */
    @JsonProperty("identifier")
    private String identifier;

    /**
     * Mobile phone number. When present, authentication is routed through Mobile-ID,
     * otherwise through Smart-ID.
     */
    @JsonProperty("mobileNr")
    private String mobileNr;

    /**
     * Language for user-facing messages. One of "et", "ru", "en".
     */
    @JsonProperty("language")
    private String language;
}
