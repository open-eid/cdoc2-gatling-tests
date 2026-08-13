package ee.cyber.cdoc2.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.X509CertUtils;
import ee.cyber.cdoc2.server.dto.MidAuthenticationRequest;
import ee.cyber.cdoc2.server.dto.SidAuthenticationRequest;
import java.nio.charset.StandardCharsets;
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

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Random RANDOM = new Random();

    private static final String ALPHANUMERIC_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static final String SEMANTICS_IDENTIFIER_OK = "PNOEE-40504040001";

    public static final String MID_NATIONAL_IDENTITY_NUMBER_OK = "51307149560";
    public static final String MID_PHONE_NUMBER_OK = "+37269930366";


    public static final String SID_SESSION_SUBJECT_OK = "etsi/" + SEMANTICS_IDENTIFIER_OK;
    public static final String MID_SESSION_SUBJECT_OK = "etsi/PNOEE-" + MID_NATIONAL_IDENTITY_NUMBER_OK;

    public static final String MISMATCH_CERT_PEM = """
        -----BEGIN CERTIFICATE-----
        MIICVjCCAdygAwIBAgIUVW/v3aNWSeOvcVModdxNuutAZQ4wCgYIKoZIzj0EAwIw
        ajELMAkGA1UEBhMCRUUxEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1RhbGxp
        bm4xFDASBgNVBAoTC0N5YmVybmV0aWNhMQwwCgYDVQQLEwNJVE8xEzARBgNVBAMT
        CkdhdGxpbmcgQ0EwHhcNMjYwODA2MTExOTQyWhcNMzYwODAzMTExOTQyWjBtMQsw
        CQYDVQQGEwJFRTEbMBkGA1UEAwwSUkVDSVBJRU5ULE1JU01BVENIMRIwEAYDVQQE
        DAlSRUNJUElFTlQxETAPBgNVBCoMCE1JU01BVENIMRowGAYDVQQFExFQTk9FRS00
        MDQwNDA0MDAwNDBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABLKVIe2D09/DP5ro
        s8u88VtJfqAEz41BXGk+g4xqncQ9fgmCDTNb9UURCJuZiwcg4VOCS5kqTBGdS3TE
        6WcK3jujXTBbMAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgWgMB0GA1UdDgQWBBSw
        l4/9PLCqXrFN2mlR5OtYn71tNTAfBgNVHSMEGDAWgBSOc+QzKfFtmItd8CphTRuF
        ZQPf+zAKBggqhkjOPQQDAgNoADBlAjEAs/+nS6W28RD3W1jtWY/gMJ3K9n2cUBmr
        YHn7WWU+YKGb3VDGM/Y7CCwwC3GskXMQAjBoLy3Dwn7EucDukzXBgu8oSr/XPPA6
        IH0wedsGoogx3DFHmiiEBxUbeSUgShVs4FQ=
        -----END CERTIFICATE-----""";
    public static final String MISMATCH_CERT_BASE64URL = toBase64Url(MISMATCH_CERT_PEM);


    public static final String SID_SESSION_CERT_PEM = """
        -----BEGIN CERTIFICATE-----
        MIICQzCCAcigAwIBAgIUIZ3kUcoDD+imVBMhzFdsXao+qR0wCgYIKoZIzj0EAwIw
        ajELMAkGA1UEBhMCRUUxEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1RhbGxp
        bm4xFDASBgNVBAoTC0N5YmVybmV0aWNhMQwwCgYDVQQLEwNJVE8xEzARBgNVBAMT
        CkdhdGxpbmcgQ0EwHhcNMjYwODA3MTIwNTI5WhcNMzYwODA0MTIwNTI5WjBZMQsw
        CQYDVQQGEwJFRTENMAsGA1UEBAwEREVNTzEMMAoGA1UEKgwDU0lEMREwDwYDVQQD
        DAhERU1PLFNJRDEaMBgGA1UEBRMRUE5PRUUtNDA1MDQwNDAwMDEwWTATBgcqhkjO
        PQIBBggqhkjOPQMBBwNCAAR0Jc2pbqCwdVGOERLHwsvFZqn5VDAUNThGleiye/9N
        WnlOXi/Mf2TClnZTgEpTVecFToI5LeJNKlnXWEb5JLppo10wWzAMBgNVHRMBAf8E
        AjAAMAsGA1UdDwQEAwIFoDAfBgNVHSMEGDAWgBSOc+QzKfFtmItd8CphTRuFZQPf
        +zAdBgNVHQ4EFgQUtadkQOQRsacH4q50zrQ5A5ND+VMwCgYIKoZIzj0EAwIDaQAw
        ZgIxAO56R6YFSBpPzTxMDF6ZOVS4sofi53o3zHBn7a7/zSi1D1pk569ZRxwHPn5I
        w3PRXwIxAKCN6NTFbyvpL7r/fKFnVbTk1maWJ3bzg8W9CMycTGV7LJavvWylmzf9
        74eHLCMW1g==
        -----END CERTIFICATE-----""";
    public static final String SID_SESSION_CERT_BASE64URL = toBase64Url(SID_SESSION_CERT_PEM);


    public static final String MID_SESSION_CERT_PEM = """
        -----BEGIN CERTIFICATE-----
        MIICQjCCAcigAwIBAgIUeZbe8c9KsYbJewHfoez5woBnQoEwCgYIKoZIzj0EAwIw
        ajELMAkGA1UEBhMCRUUxEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1RhbGxp
        bm4xFDASBgNVBAoTC0N5YmVybmV0aWNhMQwwCgYDVQQLEwNJVE8xEzARBgNVBAMT
        CkdhdGxpbmcgQ0EwHhcNMjYwODA3MTIwNTI5WhcNMzYwODA0MTIwNTI5WjBZMQsw
        CQYDVQQGEwJFRTENMAsGA1UEBAwEREVNTzEMMAoGA1UEKgwDTUlEMREwDwYDVQQD
        DAhERU1PLE1JRDEaMBgGA1UEBRMRUE5PRUUtNTEzMDcxNDk1NjAwWTATBgcqhkjO
        PQIBBggqhkjOPQMBBwNCAARYhsgc21hdNBD6ZOtR0RVBHn7F3hafeajpdwCbHZTi
        sgQuniO1D4WbNZO7s40npcUV5z+AoDNJIVBBETxY/spCo10wWzAMBgNVHRMBAf8E
        AjAAMAsGA1UdDwQEAwIFoDAfBgNVHSMEGDAWgBSOc+QzKfFtmItd8CphTRuFZQPf
        +zAdBgNVHQ4EFgQU6InQBnG9dqvNiw4EYfZQwILmuPAwCgYIKoZIzj0EAwIDaAAw
        ZQIwa0Vj0EXFDbMiSEsyPp/pETMMbMVz5qi+l5joea/FX0T0Pp1TR8fdf/2JhqLH
        TN2pAjEAgLQ44U9B78ZyYvFAwWkHwFStTnZVjsSdNjfr+1+N+yUYe/5W/koxNiq5
        SMTCGhUj
        -----END CERTIFICATE-----""";
    public static final String MID_SESSION_CERT_BASE64URL = toBase64Url(MID_SESSION_CERT_PEM);

    public static final String SESSION_TOKEN_SIGNING_KEY_KID = "L3RrY5YVqn7fCEg6hf_-lsGUnhPc9dcKuTeTvJHO9W8";
    public static final String SESSION_TOKEN_SIGNING_KEY = """
        -----BEGIN EC PRIVATE KEY-----
        MHcCAQEEIGzQ2m+aRsNRJEUMbaVqCwiiwrBoyFD3uWNQp8klNUJHoAoGCCqGSM49
        AwEHoUQDQgAEgQT1/Ud+qCJZL+9zm/HBb2v/L1+ermyIo5IohV4SvyxjQMvAfl8d
        lLHBM3s12ntWsTKzfKp0xD/lx+YBgU54SQ==
        -----END EC PRIVATE KEY-----""";

    @SneakyThrows
    public static String toJson(Object dto) {
        return JSON.writeValueAsString(dto);
    }

    /**
     * Generates a random alphanumeric string with exactly the given character length. Used both
     * for precise boundary testing and for standing in for a syntactically-invalid token/cert
     * value.
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
     * Builds a fully valid "POST /sid/authenticate" request for the SK Smart-ID demo test
     * identity that always completes successfully.
     */
    public static SidAuthenticationRequest createSidAuthenticationRequest() {
        return new SidAuthenticationRequest(
            SEMANTICS_IDENTIFIER_OK,
            "QUALIFIED",
            "ACSP_V2",
            new SidAuthenticationRequest.SignatureProtocolParameters(
                Base64.getEncoder().encodeToString(randomBytes(48)),
                "rsassa-pss",
                new SidAuthenticationRequest.SignatureAlgorithmParameters("SHA-256")
            ),
            interactions(),
            "numeric4"
        );
    }

    /**
     * Builds a fully valid "POST /mid/authenticate" request for the SK Mobile-ID demo test
     * identity that always completes successfully.
     */
    public static MidAuthenticationRequest createMidAuthenticationRequest() {
        return new MidAuthenticationRequest(
            MID_PHONE_NUMBER_OK,
            MID_NATIONAL_IDENTITY_NUMBER_OK,
            Base64.getEncoder().encodeToString(randomBytes(32)),
            "SHA256",
            "ENG",
            "Decrypting container file \"test.txt\"",
            "GSM-7"
        );
    }

    @SneakyThrows
    private static String interactions() {
        String json = JSON.writeValueAsString(java.util.List.of(
            java.util.Map.of(
                "type", "confirmationMessage",
                "displayText200", "Decrypting container file \"test.txt\""
            )
        ));
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private static String toBase64Url(String certPem) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(X509CertUtils.parseWithException(certPem).getEncoded());
    }
}
