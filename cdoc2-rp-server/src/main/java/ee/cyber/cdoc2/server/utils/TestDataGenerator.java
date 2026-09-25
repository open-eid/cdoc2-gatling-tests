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

    @SuppressWarnings("LineLength")
    public static final String SID_SESSION_CERT_BASE64URL = "MIIGpzCCBi6gAwIBAgIQGcJUbe6JHI6jJyV-42vjnTAKBggqhkjOPQQDAzBxMSwwKgYDVQQDDCNURVNUIG9mIFNLIElEIFNvbHV0aW9ucyBFSUQtUSAyMDI0RTEXMBUGA1UEYQwOTlRSRUUtMTA3NDcwMTMxGzAZBgNVBAoMElNLIElEIFNvbHV0aW9ucyBBUzELMAkGA1UEBhMCRUUwHhcNMjYwMTA2MTQyNTAxWhcNMjkwMTA1MTQyNTAwWjBXMQswCQYDVQQGEwJFRTEQMA4GA1UEAwwHVEVTVCxPSzENMAsGA1UEBAwEVEVTVDELMAkGA1UEKgwCT0sxGjAYBgNVBAUTEVBOT0VFLTQwNTA0MDQwMDAxMIIDIjANBgkqhkiG9w0BAQEFAAOCAw8AMIIDCgKCAwEAkI98VzyaeSueyaUQYIXMMf-1VY10Gw-b8Q13Rb9N62ROZY97wMIB__f8_PuOIoqkAPM6Tn_t4lp1R_rHrbuqs0hl2dgLlOcR5wmWmp7YfKPDvRndVLl_doIHruxY8O60rFGskSnqt4coHN4xGcmCyPkJoB8Rfm8-Y9poVKAreS0Ta32p5OSME0HjSs7-ahB2erWfb2GulFw1vyeH42d3XDpCCfd6CByvSsi4oByUqs5G-kjSrGUglflgWXK3MxBYto0swgsbD1nrW5doU_cMCfRoFURun4XguX8dTt9VeyqeJitxRfub2Hj18RbsKuoFNHQNOxAxRK4oTVCtUrYbVqBHDmoOm8r3CsSuqjuZ2njQybiUhBofpTVMCZ6lB6VgoLphmEwSEOQXIumpmpb2qJZqbZaBoyyWb4f5AQjw3Q5lwPSao5215hIgSuuENRezpP9rTzIwyOMbnV2nMSMInAuaXIXskB2NdpMsROsvOqBC0h5azTj9naCS-5EW-9eI7GGK03Du5JoKD5wYajJxfcxFwBAl8Ko71OvhGFtYiu-hqzz-CyG6NswB87KvzDYUCQ-0qOfgRBNCgYnbjnuYVJb3CGLp_cP5GmKtUC3wHX1WnPGyK4bD19Rcy-FhG6mD_ZrAPcmZ3s4FLLErpRJ3ui-fiMPLQl2bpCKTWoaEZoPg6Grnhr3bE2ZiKWmqdVwf30bG3-GnvTBTuF0T1lzt6NeBlB23SJsffCmzSFSNcFJHHYI1FYdZu2p0gL6KAabEmnE8GrTrCn93DFNBtoKu9vG30QrRzyh-itPvtn9w-9t-nDkhaVHmNCjWD1xcMeXsyK8ek0rbz5aVe_RPvCifhIpgjqNsDHh9q1QT9KIFsd6RD2XPMlekL9c6YiVY9H7uRyIQWqJwtrvNvBKj4ZT9745zTfkhCJTPvnLy-4iKeINVZ2f98BblsGAEHKGol8YA-3SRkPh9BVnVhSdI3lxCDEbmHuk21GIPE9689efSvbcDEHpqeYoxo3tXjl_hqfzPAgMBAAGjggH1MIIB8TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFLAkFxmI42b4zShYZXtNFNiSZk9rMHAGCCsGAQUFBwEBBGQwYjAzBggrBgEFBQcwAoYnaHR0cDovL2Muc2suZWUvVEVTVF9FSUQtUV8yMDI0RS5kZXIuY3J0MCsGCCsGAQUFBzABhh9odHRwOi8vYWlhLmRlbW8uc2suZWUvZWlkcTIwMjRlMDAGA1UdEQQpMCekJTAjMSEwHwYDVQQDDBhQTk9FRS00MDUwNDA0MDAwMS1ERU0wLVEweAYDVR0gBHEwbzBjBgkrBgEEAc4fEQIwVjBUBggrBgEFBQcCARZIaHR0cHM6Ly93d3cuc2tpZHNvbHV0aW9ucy5ldS9yZXNvdXJjZXMvY2VydGlmaWNhdGlvbi1wcmFjdGljZS1zdGF0ZW1lbnQvMAgGBgQAj3oBAjAoBgNVHQkEITAfMB0GCCsGAQUFBwkBMREYDzE5MDUwNDA0MTIwMDAwWjAWBgNVHSUEDzANBgsrBgEEAYPmYgUHADA0BgNVHR8ELTArMCmgJ6AlhiNodHRwOi8vYy5zay5lZS90ZXN0X2VpZC1xXzIwMjRlLmNybDAdBgNVHQ4EFgQUX9YaVGlPdUOO2J6rzNc4sljBQBAwDgYDVR0PAQH_BAQDAgeAMAoGCCqGSM49BAMDA2cAMGQCMHhYJCeKceJv_m0xcFRssS4WVFnnCryDiuSEpjDZu0irJ_XurXXIFDr-9hhl2x7GMwIwbiD5GALRtwzUaEh-SV9jigT9Oc336f6QYf8YaSA0-Un8eRQPa9wTK0cSQrM_CUIu";
    @SuppressWarnings("LineLength")
    public static final String MID_SESSION_CERT_BASE64URL = "MIIDqDCCAy6gAwIBAgIQB9W11BzBABj-0d_AZx6UHzAKBggqhkjOPQQDAjBxMQswCQYDVQQGEwJFRTEbMBkGA1UECgwSU0sgSUQgU29sdXRpb25zIEFTMRcwFQYDVQRhDA5OVFJFRS0xMDc0NzAxMzEsMCoGA1UEAwwjVEVTVCBvZiBTSyBJRCBTb2x1dGlvbnMgRUlELVEgMjAyMUUwHhcNMjQwNjEyMDY0NTI4WhcNMjkwNjE2MDY0NTI3WjCBlTELMAkGA1UEBhMCRUUxLzAtBgNVBAMMJk1BUlkgw4ROTixPJ0NPTk5Fxb0txaBVU0xJSyBURVNUTlVNQkVSMSUwIwYDVQQEDBxPJ0NPTk5Fxb0txaBVU0xJSyBURVNUTlVNQkVSMRIwEAYDVQQqDAlNQVJZIMOETk4xGjAYBgNVBAUTEVBOT0VFLTUxMzA3MTQ5NTYwMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEWlV1aVSXw6WhagWmFmXE_oe-0R1xZzrHyoiVlgKpGiJ8cwIQLogRGQnWY7NwgQvRHCBmsl99bj57h7SWnd03m6OCAYEwggF9MAkGA1UdEwQCMAAwHwYDVR0jBBgwFoAUScfc7QYUosdtnKbP11L9aOXoBBQwcAYIKwYBBQUHAQEEZDBiMDMGCCsGAQUFBzAChidodHRwOi8vYy5zay5lZS9URVNUX0VJRC1RXzIwMjFFLmRlci5jcnQwKwYIKwYBBQUHMAGGH2h0dHA6Ly9haWEuZGVtby5zay5lZS9laWRxMjAyMWUweAYDVR0gBHEwbzAIBgYEAI96AQIwYwYJKwYBBAHOHxIBMFYwVAYIKwYBBQUHAgEWSGh0dHBzOi8vd3d3LnNraWRzb2x1dGlvbnMuZXUvcmVzb3VyY2VzL2NlcnRpZmljYXRpb24tcHJhY3RpY2Utc3RhdGVtZW50LzA0BgNVHR8ELTArMCmgJ6AlhiNodHRwOi8vYy5zay5lZS90ZXN0X2VpZC1xXzIwMjFlLmNybDAdBgNVHQ4EFgQUj8KjnXvGQJCRYOd5LVfPku7QsZwwDgYDVR0PAQH_BAQDAgeAMAoGCCqGSM49BAMCA2gAMGUCMQCocXWDbBnkM3WEyBdv9Vm0A1MNRv08WrR192dRBcX42Kz5oiH0SdHRJv2ffeuEeSwCMEw2tSA3ClJv233Dl7rIYU_T6UG2NQhvDD5FhnP0umZRmVfAUQ6eVcmU8AhFtNJjwg==";

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
