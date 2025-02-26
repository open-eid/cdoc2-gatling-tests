package ee.cyber.cdoc2.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.cyber.cdoc2.server.dto.KeyShareRequest;
import java.util.Base64;
import java.util.Random;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Generates test data
 */
@Slf4j
public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    // key share shareId min length
    public static final int SHARE_ID_MIN_LENGTH = 18;
    // key share shareId max length
    public static final int SHARE_ID_MAX_LENGTH = 34;

    public static final int KEY_SHARE_MAX_LENGTH = 128;

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Random RANDOM = new Random();
    public static final String TEST_IDENTIFIER = "30303039914";
    private static final String RECIPIENT = "etsi/PNOEE-" + TEST_IDENTIFIER;

    public static final String RANDOM_X_AUTH_TICKET = "eyJ0eXAiOiJ2bmQuY2RvYzIuYXV0aC10b2tlbi" +
        "52MStzZC1qd3QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJldHNpL1BOT0VFLTMwMzAzMDM5OTE0I" +
        "iwiX3NkIjpbIk1CUVpJTnFEUy1GUU5mWlVacGgwTnE3SnhrQ2ROSVpuMlFUbklIbElIbTgiXSwiX" +
        "3NkX2FsZyI6InNoYS0yNTYifQ.fgOhF-hRHkEZBUwG2keDrZ-FUhIiUnmx6SjQVYOrzK2b-IhObL" +
        "V8kz8_472DtyIXXVJew0H93U13z-gCb8F5Mg~WyJUVkY0MGtJdklVd2pFeDEzNGQzdEpBIiwiYXV" +
        "kIixbeyIuLi4iOiJFdHZORUZnOVRaUzF1dFVHR2t5bm1RUlBoUkJndzdOZGJ5a2hVcjJsMml3In1" +
        "dXQ~WyJjTGxMbHUyNVFVY1pNaXE0NlhBWkZnIiwiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0My9rZXkt" +
        "c2hhcmVzLzY5Y2ZhOWE0YjdhNjMxZGE1YzQ5N2IzNDI1M2FjMGEzP25vbmNlXHUwMDNkS18zRlg4" +
        "Q0M4X1M1aHhsM1E3YUdtQSJd~";

    public static final String TEST_CERT_PEM = "-----BEGIN CERTIFICATE-----"
        + """
        MIICDzCCAbWgAwIBAgIUVn26RKn5tb6rOhO4sMrInr8UgxEwCgYIKoZIzj0EAwQw
        TTELMAkGA1UEBhMCRUUxEDAOBgNVBAcMB1RhbGxpbm4xDzANBgNVBAoMBi1sb2Nh
        bDEbMBkGA1UEAwwSY3liZXItY2EubG9jYWxob3N0MB4XDTI1MDExMzE2MDE1OFoX
        DTI2MDExMzE2MDE1OFowYzEaMBgGA1UEBRMRUE5PRUUtMzAzMDMwMzk5MTQxCzAJ
        BgNVBCoMAk9LMRMwEQYDVQQEDApURVNUTlVNQkVSMRYwFAYDVQQDDA1URVNUTlVN
        QkVSLE9LMQswCQYDVQQGEwJFRTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABAWe
        VJrrXvoxM0smdRMl6pfmRLeHFVl9cBu9V2tLyTPVbWGM9KTWMtTK+Z8cuJP/9Qwf
        VYbyildK3Ljh0e3DoDyjXTBbMB8GA1UdIwQYMBaAFMOo1Ks+YOgIJxdsDy4nChTP
        jAlvMAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgWgMB0GA1UdDgQWBBRLVYFQ5JNE
        dGE3HjPOHWGWNebXmTAKBggqhkjOPQQDBANIADBFAiEA32rCmKZd5uho96r3zhWb
        e6SuLRYHAsuUqj5IcMx8cJ0CIA8ntNP2P2oAQRf0wmypbvzyirYtu6Im1hf1vh/Y
        BX2H
        """.replaceAll("\\s", "")
        + "-----END CERTIFICATE-----";

    public static final String TEST_ECDSA_KEY = """
        -----BEGIN EC PRIVATE KEY-----
        MHcCAQEEIBcKDOTuvQgeXk/Ba+B63t1oz0bTJ8hzY7y1s9HSsavmoAoGCCqGSM49
        AwEHoUQDQgAEBZ5Umute+jEzSyZ1EyXql+ZEt4cVWX1wG71Xa0vJM9VtYYz0pNYy
        1Mr5nxy4k//1DB9VhvKKV0rcuOHR7cOgPA==
        -----END EC PRIVATE KEY-----""";

    @SneakyThrows
    public static String toJson(Object dto) {
        return JSON.writeValueAsString(dto);
    }

    /**
     * Generates random bytes
     * @param length the number of bytes to generate
     * @return random bytes
     */
    public static byte[] randomBytes(int length) {
        var bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    /**
     * Generates a random URL encoded string
     * @param length the length of the string
     * @return a random string with the given length
     */
    public static String randomString(int length) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes(length)); // make url safe
    }

    public static KeyShareRequest createKeyShareRequest(int length) {
        return new KeyShareRequest(randomBytes(length), RECIPIENT);
    }
}
