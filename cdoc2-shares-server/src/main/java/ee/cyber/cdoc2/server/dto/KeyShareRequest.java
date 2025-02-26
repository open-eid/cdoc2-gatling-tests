package ee.cyber.cdoc2.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CDOC2 key share request DTO
 */
@Getter
@AllArgsConstructor
public class KeyShareRequest {

    /**
     * Key Share in binary format.
     */
    @JsonProperty("share")
    private byte[] share;

    /**
     * ETSI identifier as recipient. Example \"etsi/PNOEE-30303039914\"
     */
    @JsonProperty("recipient")
    private String recipient;
}
