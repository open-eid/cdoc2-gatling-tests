package ee.cyber.cdoc2.server.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

import ee.cyber.cdoc2.server.dto.AuthIdentityRequest;

/**
 * Generates test data
 */
@Slf4j
public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    // "identifier" field length bounds, as defined in the cdoc2-auth-server OpenAPI spec
    public static final int AUTH_IDENTIFIER_MIN_LENGTH = 12;
    public static final int AUTH_IDENTIFIER_MAX_LENGTH = 32;

    // "mobileNr" field length bounds, as defined in the cdoc2-auth-server OpenAPI spec
    public static final int MOBILE_NR_MIN_LENGTH = 6;
    public static final int MOBILE_NR_MAX_LENGTH = 32;

    /**
     * SK Smart-ID demo environment test identity that always completes authentication
     * successfully. Sourced from cdoc2-auth-server's own AbstractAuthServerTest.
     */
    public static final String SID_IDENTIFIER_OK = "etsi/PNOEE-40504040001";

    /**
     * SK Mobile-ID demo environment test identity (and matching phone number) that always
     * completes authentication successfully. Sourced from cdoc2-auth-server's own
     * AbstractAuthServerTest.
     */
    public static final String MID_IDENTIFIER_OK = "etsi/PNOEE-51307149560";
    public static final String MID_PHONE_NUMBER_OK = "+37269930366";

    public static final String LANGUAGE_ET = "ET";

    /**
     * SK Smart-ID demo environment test identity that always has the user refuse the auth
     * request. Sourced from cdoc2-auth-server's own AbstractAuthServerTest. The mock
     * SID/MID server (mock-sid-mid-server) also recognizes this identity and reports a
     * refused/failed auth process for it.
     */
    public static final String SID_IDENTIFIER_USER_REFUSED = "etsi/PNOEE-30403039917";

    private static final String ALPHANUMERIC_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Random RANDOM = new Random();

    @SneakyThrows
    public static String toJson(Object dto) {
        return JSON.writeValueAsString(dto);
    }

    /**
     * Generates a random alphanumeric string with exactly the given character length, for
     * precise boundary testing of "identifier"/"mobileNr" length constraints.
     *
     * @param length the exact length of the string
     * @return a random string with exactly the given length
     */
    public static String randomString(int length) {
        var builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC_CHARS.charAt(RANDOM.nextInt(ALPHANUMERIC_CHARS.length())));
        }
        return builder.toString();
    }

    /**
     * Builds a request that starts a Smart-ID auth process for the "always refused" SID demo
     * test identity.
     */
    public static AuthIdentityRequest createSidAuthRequestForRefusal() {
        return new AuthIdentityRequest(SID_IDENTIFIER_USER_REFUSED, null, null);
    }
}
