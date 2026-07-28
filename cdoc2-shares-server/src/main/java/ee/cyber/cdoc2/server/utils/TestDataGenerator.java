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

    public static final String RANDOM_X_AUTH_TOKEN = "eyJ0eXAiOiJ2bmQuY2RvYzIuYXV0aC10b2tlbi" +
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

    public static final String RANDOM_X_SESSION_TOKEN = "eyJraWQiOiJlYy1rZXktMjAyNiIsInR5cCI" +
        "6InZuZC5jZG9jMi5zZXNzaW9uLXRva2VuLnYyK3NkLWp3dCIsImFsZyI6IkVTMjU2In0.eyJycENo" +
        "YWxsZW5nZSI6InJrZ2s0cTE2eTYxbFJoRVJOcVZVdzBpdEhXZ3MzbWZMS3Y5cEQ2Z2xCdDl0ZDJR" +
        "bmVhd1lLM0ZGOHFkMXBSakdDbnlyZ3NWTmprSXJ3T3NuZXkzOXl3PT0iLCJzdWIiOiJldHNpL1BO" +
        "T0VFLTQwNTA0MDQwMDAxIiwic2lnbmF0dXJlIjp7InZhbHVlIjoiSlhtVmgwWlZqdFRUZHFFaHlE" +
        "T1NCejNMLyt4UHZPTGF1WXVmbmUydS8wRkVuZ0loQ0g4WEl6ZW5zazhsa3BLZlNxYVBjSzZReDF5" +
        "bVZpdGM1YUNWY1N6bjRzUVV3SW5OODBXVEd1UTZtNTNESGdXWnFnS3NabHErSDcwamxxMXZSUE0v" +
        "S3UwVEJIK01GRkhOeWp5SWZWN0MwOVMyK2pCbm1kYWEzM0FaNCtnOS9FNWVnL1p6QktHQWNnQmFy" +
        "U01lYVpOdXNXQWNrdlJBQ3Y1WXlidUVYK0JSM3NYMXA0U09XNWdMT2lPOUwzaWtzVXNzVFp2K3My" +
        "VU9kbFVZTFpTVGVrWWIrYXFQRkdzODdjY1FQUmZuRlRWV1EwZ0pvVENWVG1lWWdXazA5MjZMOVRE" +
        "aUlQZlJ5cWxBbkNQNWRPZGlxRmNLSXFOZ3d4Z0RyZmlITmJ2R3hhQ3hGeVdJd2R2ekdmNEZPa3VM" +
        "aVdndXVPQVo3YlgxVzIwQnNlZjFHTnRTQUdBVlRMbmJsMUNiOC8rT2dwRU52WXM2UUtxdXdiRlZH" +
        "TDhzRDJEc1czNXZodllXdkJJN0tVK1NxODBnSDhURkFvdzNiN3QzOU80UEJmbVMzOUJrRTgxMW9m" +
        "K2MwSFpNMXhJd3NTajZVVkR3bkNLOHYwZ1BKOGZMMmo5NDIrVUVVRUpQUEJaNmo1TWcrRmxBb1ZF" +
        "R2pHakp1cDF3NVdCTFVBYTl1blRiNWp5UGtFUW84clVrS1Y0ZmQ0b21XTEpobGJXcmNYWlh5Zndr" +
        "RGhJR211Uzh4Z2w2NSs0anMyVTI4THRDQzJYSjhlNWJhWm9rNWQ0Q2VVbFJvUjErbmIvZTg0cmth" +
        "OUtPOUV6MGZHVmRlSmc0MFZhYTBWb2xBeDkxYVVmUS9mV0tpRXMxbHBOR0EyMG55cytJSTU3QWZq" +
        "RDdkdGJxTzZaNzBXbEpUenMrREE0SHJEcXFQQ0ZRbVRmc2VhWFFOOVBxK3RnRFZqdFc1TlVRMTg4" +
        "UlhSZ2pvalp4ZitCT2piVDJ6b0xxMS96VmdETkZTaW1kSElLQitJYUlwaDB5LzZHWWFGb0p2eERs" +
        "Rm9YSzhwUnU4My8vdGNnZmFuN1gzUWZKMnF1WTN3T2VDUHN5dmM1TklNdHRJdnhHRFlTdWI3QW9T" +
        "a3Z4eGFsdHg3Vy9FWEJWVmNDaWROZE1YM1ErZ2VNQlMzeS9NbEE3M0pqMXQxb1N6UmZxdFFpY2tY" +
        "d0w3bmYrNXB5RzY2eUVFb1ZaZ1dVdE0zMUptSE9LVUF6UGVFOSthd2xRUjd3NXJSeitFemJ1MitL" +
        "dlY2N1V4NFdXVG5JY0pOUG4rRkhOKzE1V1ZlTVhuRlhxVHZOSiIsInNlcnZlclJhbmRvbSI6InNW" +
        "OXdsS3RaZTV0cjBnTjlpZXRQU0ovVCIsInVzZXJDaGFsbGVuZ2UiOiJmeWtaTHJmU2tsMW9uMXBI" +
        "TlBrZEFZUS1pekd0N1ZGeWN6eUNDM2x4cmlrIiwic2lnbmF0dXJlQWxnb3JpdGhtIjoicnNhc3Nh" +
        "LXBzcyIsImZsb3dUeXBlIjoiTm90aWZpY2F0aW9uIiwic2lnbmF0dXJlQWxnb3JpdGhtUGFyYW1l" +
        "dGVycyI6eyJoYXNoQWxnb3JpdGhtIjoiU0hBLTI1NiIsIm1hc2tHZW5BbGdvcml0aG0iOnsiYWxn" +
        "b3JpdGhtIjoiaWQtbWdmMSIsInBhcmFtZXRlcnMiOnsiaGFzaEFsZ29yaXRobSI6IlNIQS0yNTYi" +
        "fX0sInNhbHRMZW5ndGgiOjMyLCJ0cmFpbGVyRmllbGQiOiIweGJjIn19LCJpc3MiOiJodHRwczov" +
        "L2Nkb2MyLWF1dGgtc2VydmVyLmVlIiwic2NoZW1lTmFtZSI6InNtYXJ0LWlkLWRlbW8iLCJzaWdu" +
        "YXR1cmVQcm90b2NvbCI6IlJTQVNTQS1QU1MrQUNTUF9WMiIsIl9zZCI6WyJuTEpHdS05X3lKMmlE" +
        "OGhrRXU5Ym5yc0EzUHJ5Y3UwVVE1WXQ5UENTNV8wIl0sImludGVyYWN0aW9uc0RpZ2VzdCI6Im9s" +
        "Sk43T1hVdmZ5MWJVUE51NzEyWDNBN01PbTFCWGlXdGxBbXYrdWJJejA9IiwiX3NkX2FsZyI6InNo" +
        "YS0yNTYiLCJleHAiOjE3NzY4NzI1MjksImlhdCI6MTc3Njc4NjEyOSwiaW50ZXJhY3Rpb25UeXBl" +
        "VXNlZCI6ImNvbmZpcm1hdGlvbk1lc3NhZ2VBbmRWZXJpZmljYXRpb25Db2RlQ2hvaWNlIiwicnBO" +
        "YW1lIjoiREVNTyJ9.5ORVwgy0tMpX5tdwZmhnnQK_H4ngB-duofWj2OYCrJU5kL5dUvJRSeiC5QL" +
        "buzH-8gk08b5asqIW9lWNEErAuw~WyJONGFScHVxNTVRZzh6LTVxS3dlRURBIiwiYXVkIixbeyIu" +
        "Li4iOiIxM19rVmNGcXF3M0tycllRaUpnUEJ4Qm1zOG1rY0puMmtnNWZBRGc4aUlFIn0seyIuLi4i" +
        "OiJkaG9VbVZod0c2TEJIbkwyMHJxbDZFTkVFdjlfdHBPOFo4aUVFbmVESmhjIn1dXQ~WyJMb2dqR" +
        "24xc21ZNmxpWllEZnh4OGhnIiwiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3Nlc3Npb25fbm9uY2VfM" +
        "i9uclZjU0VjSHVXdDJTS2Zqa01tNlJRIl0~";

    public static final String SID_SIGNING_CERTIFICATE_BASE64URL =
        "MIIGpzCCBi6gAwIBAgIQGcJUbe6JHI6jJyV-42vjnTAKBggqhkjOPQQDAzBxMSwwKgYDVQQDDCNURVNUIG9mIFNLIElEIFNvbHV0aW9ucyBFSUQtUSAyMDI0RTEXMBUGA1UEYQwOTlRSRUUtMTA3NDcwMTMxGzAZBgNVBAoMElNLIElEIFNvbHV0aW9ucyBBUzELMAkGA1UEBhMCRUUwHhcNMjYwMTA2MTQyNTAxWhcNMjkwMTA1MTQyNTAwWjBXMQswCQYDVQQGEwJFRTEQMA4GA1UEAwwHVEVTVCxPSzENMAsGA1UEBAwEVEVTVDELMAkGA1UEKgwCT0sxGjAYBgNVBAUTEVBOT0VFLTQwNTA0MDQwMDAxMIIDIjANBgkqhkiG9w0BAQEFAAOCAw8AMIIDCgKCAwEAkI98VzyaeSueyaUQYIXMMf-1VY10Gw-b8Q13Rb9N62ROZY97wMIB__f8_PuOIoqkAPM6Tn_t4lp1R_rHrbuqs0hl2dgLlOcR5wmWmp7YfKPDvRndVLl_doIHruxY8O60rFGskSnqt4coHN4xGcmCyPkJoB8Rfm8-Y9poVKAreS0Ta32p5OSME0HjSs7-ahB2erWfb2GulFw1vyeH42d3XDpCCfd6CByvSsi4oByUqs5G-kjSrGUglflgWXK3MxBYto0swgsbD1nrW5doU_cMCfRoFURun4XguX8dTt9VeyqeJitxRfub2Hj18RbsKuoFNHQNOxAxRK4oTVCtUrYbVqBHDmoOm8r3CsSuqjuZ2njQybiUhBofpTVMCZ6lB6VgoLphmEwSEOQXIumpmpb2qJZqbZaBoyyWb4f5AQjw3Q5lwPSao5215hIgSuuENRezpP9rTzIwyOMbnV2nMSMInAuaXIXskB2NdpMsROsvOqBC0h5azTj9naCS-5EW-9eI7GGK03Du5JoKD5wYajJxfcxFwBAl8Ko71OvhGFtYiu-hqzz-CyG6NswB87KvzDYUCQ-0qOfgRBNCgYnbjnuYVJb3CGLp_cP5GmKtUC3wHX1WnPGyK4bD19Rcy-FhG6mD_ZrAPcmZ3s4FLLErpRJ3ui-fiMPLQl2bpCKTWoaEZoPg6Grnhr3bE2ZiKWmqdVwf30bG3-GnvTBTuF0T1lzt6NeBlB23SJsffCmzSFSNcFJHHYI1FYdZu2p0gL6KAabEmnE8GrTrCn93DFNBtoKu9vG30QrRzyh-itPvtn9w-9t-nDkhaVHmNCjWD1xcMeXsyK8ek0rbz5aVe_RPvCifhIpgjqNsDHh9q1QT9KIFsd6RD2XPMlekL9c6YiVY9H7uRyIQWqJwtrvNvBKj4ZT9745zTfkhCJTPvnLy-4iKeINVZ2f98BblsGAEHKGol8YA-3SRkPh9BVnVhSdI3lxCDEbmHuk21GIPE9689efSvbcDEHpqeYoxo3tXjl_hqfzPAgMBAAGjggH1MIIB8TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFLAkFxmI42b4zShYZXtNFNiSZk9rMHAGCCsGAQUFBwEBBGQwYjAzBggrBgEFBQcwAoYnaHR0cDovL2Muc2suZWUvVEVTVF9FSUQtUV8yMDI0RS5kZXIuY3J0MCsGCCsGAQUFBzABhh9odHRwOi8vYWlhLmRlbW8uc2suZWUvZWlkcTIwMjRlMDAGA1UdEQQpMCekJTAjMSEwHwYDVQQDDBhQTk9FRS00MDUwNDA0MDAwMS1ERU0wLVEweAYDVR0gBHEwbzBjBgkrBgEEAc4fEQIwVjBUBggrBgEFBQcCARZIaHR0cHM6Ly93d3cuc2tpZHNvbHV0aW9ucy5ldS9yZXNvdXJjZXMvY2VydGlmaWNhdGlvbi1wcmFjdGljZS1zdGF0ZW1lbnQvMAgGBgQAj3oBAjAoBgNVHQkEITAfMB0GCCsGAQUFBwkBMREYDzE5MDUwNDA0MTIwMDAwWjAWBgNVHSUEDzANBgsrBgEEAYPmYgUHADA0BgNVHR8ELTArMCmgJ6AlhiNodHRwOi8vYy5zay5lZS90ZXN0X2VpZC1xXzIwMjRlLmNybDAdBgNVHQ4EFgQUX9YaVGlPdUOO2J6rzNc4sljBQBAwDgYDVR0PAQH_BAQDAgeAMAoGCCqGSM49BAMDA2cAMGQCMHhYJCeKceJv_m0xcFRssS4WVFnnCryDiuSEpjDZu0irJ_XurXXIFDr-9hhl2x7GMwIwbiD5GALRtwzUaEh-SV9jigT9Oc336f6QYf8YaSA0-Un8eRQPa9wTK0cSQrM_CUIu";

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
