package ee.cyber.cdoc2.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.X509CertUtils;
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
    public static final String TEST_IDENTIFIER_MID = "30303039914";
    public static final String RECIPIENT = "etsi/PNOEE-" + TEST_IDENTIFIER_MID;

    // a second identity, unrelated to RECIPIENT/TEST_CERT_PEM, used to test that the shares
    // server rejects a key share request when the authenticated identity doesn't match the
    // share's recipient. Certificate is signed by the same test CA as TEST_CERT_PEM.
    public static final String MISMATCH_RECIPIENT = "etsi/PNOEE-40404040004";
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

    public static final String RANDOM_X_AUTH_TOKEN = "eyJ0eXAiOiJ2bmQuY2RvYzIuYXV0aC10b2tlbi"
        + "52MStzZC1qd3QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJldHNpL1BOT0VFLTMwMzAzMDM5OTE0I"
        + "iwiX3NkIjpbIk1CUVpJTnFEUy1GUU5mWlVacGgwTnE3SnhrQ2ROSVpuMlFUbklIbElIbTgiXSwiX"
        + "3NkX2FsZyI6InNoYS0yNTYifQ.fgOhF-hRHkEZBUwG2keDrZ-FUhIiUnmx6SjQVYOrzK2b-IhObL"
        + "V8kz8_472DtyIXXVJew0H93U13z-gCb8F5Mg~WyJUVkY0MGtJdklVd2pFeDEzNGQzdEpBIiwiYXV"
        + "kIixbeyIuLi4iOiJFdHZORUZnOVRaUzF1dFVHR2t5bm1RUlBoUkJndzdOZGJ5a2hVcjJsMml3In1"
        + "dXQ~WyJjTGxMbHUyNVFVY1pNaXE0NlhBWkZnIiwiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0My9rZXkt"
        + "c2hhcmVzLzY5Y2ZhOWE0YjdhNjMxZGE1YzQ5N2IzNDI1M2FjMGEzP25vbmNlXHUwMDNkS18zRlg4"
        + "Q0M4X1M1aHhsM1E3YUdtQSJd~";

    public static final String TEST_CERT_PEM = """
        -----BEGIN CERTIFICATE-----
        MIICTDCCAdKgAwIBAgIUZa9w87ztl6Lhjb7+xQjNiYXj5TEwCgYIKoZIzj0EAwIw
        ajELMAkGA1UEBhMCRUUxEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1RhbGxp
        bm4xFDASBgNVBAoTC0N5YmVybmV0aWNhMQwwCgYDVQQLEwNJVE8xEzARBgNVBAMT
        CkdhdGxpbmcgQ0EwHhcNMjYwODA0MDk1OTQ2WhcNMzYwODAxMDk1OTQ2WjBjMQsw
        CQYDVQQGEwJFRTEWMBQGA1UEAwwNVEVTVE5VTUJFUixPSzETMBEGA1UEBAwKVEVT
        VE5VTUJFUjELMAkGA1UEKgwCT0sxGjAYBgNVBAUTEVBOT0VFLTMwMzAzMDM5OTE0
        MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEAWxDUPDoAHaz0mBWh3xxHqibNZk7
        +0I6V/H7Q4q/fUmrtVYrgc3VHou0GPsDZFBgL6VdyOCo6HLCUkpfMUSC+qNdMFsw
        DAYDVR0TAQH/BAIwADALBgNVHQ8EBAMCBaAwHwYDVR0jBBgwFoAUjnPkMynxbZiL
        XfAqYU0bhWUD3/swHQYDVR0OBBYEFM3dTpGKpaHGeSDnJleGYFg2B0ebMAoGCCqG
        SM49BAMCA2gAMGUCMA0mE1HpreBF84xoZfL38pe37OlysvU31DYjvn0dgx5LhaoA
        4H2CM9hYylo3bqQjiQIxAM+Slc+kTO4lJf72wmKx9S/pPQawhtiv9HtwDS3N7+l9
        IP8yh+CXwHv3H+s4jVStJQ==
        -----END CERTIFICATE-----""";

    public static final String TEST_ECDSA_KEY = """
        -----BEGIN EC PRIVATE KEY-----
        MHcCAQEEIGwfHeS6vrQr7K/xljecU3MBOJYKdx7RMWsLB9bYSDaPoAoGCCqGSM49
        AwEHoUQDQgAEAWxDUPDoAHaz0mBWh3xxHqibNZk7+0I6V/H7Q4q/fUmrtVYrgc3V
        Hou0GPsDZFBgL6VdyOCo6HLCUkpfMUSC+g==
        -----END EC PRIVATE KEY-----""";

    public static final String TEST_CERT_BASE64URL = toBase64Url(TEST_CERT_PEM);

    public static final String SESSION_TOKEN_SIGNING_KEY_KID = "L3RrY5YVqn7fCEg6hf_-lsGUnhPc9dcKuTeTvJHO9W8";
    public static final String SESSION_TOKEN_SIGNING_KEY = """
        -----BEGIN EC PRIVATE KEY-----
        MHcCAQEEIGzQ2m+aRsNRJEUMbaVqCwiiwrBoyFD3uWNQp8klNUJHoAoGCCqGSM49
        AwEHoUQDQgAEgQT1/Ud+qCJZL+9zm/HBb2v/L1+ermyIo5IohV4SvyxjQMvAfl8d
        lLHBM3s12ntWsTKzfKp0xD/lx+YBgU54SQ==
        -----END EC PRIVATE KEY-----""";

    public static final String AUTH_TOKEN_SIGNING_KEY = """
        -----BEGIN EC PRIVATE KEY-----
        MHcCAQEEIAjwC1cDF0aXNC/u8wKRRFWi2Uhmv8/P6waDwDoNlF3OoAoGCCqGSM49
        AwEHoUQDQgAEH0VsHWVwImGA4uolFRROI5MWsEnVFrOKkFlRsFHRGKSQ5Kgvu6i6
        OqgiAIifzruWroT4t+WkzLoTx+wRgFxjEw==
        -----END EC PRIVATE KEY-----""";

    @SuppressWarnings("checkstyle:LineLength")
    public static final String RANDOM_X_SESSION_TOKEN = "eyJraWQiOiJlYy1rZXktMjAyNiIsInR5cCI6InZuZC5jZG9jMi5zZXNzaW9uLXRva2VuLnYyK3NkLWp3dCIsImFsZyI6IkVTMjU2In0.eyJycENoYWxsZW5nZSI6InJrZ2s0cTE2eTYxbFJoRVJOcVZVdzBpdEhXZ3MzbWZMS3Y5cEQ2Z2xCdDl0ZDJRbmVhd1lLM0ZGOHFkMXBSakdDbnlyZ3NWTmprSXJ3T3NuZXkzOXl3PT0iLCJzdWIiOiJldHNpL1BOT0VFLTQwNTA0MDQwMDAxIiwic2lnbmF0dXJlIjp7InZhbHVlIjoiSlhtVmgwWlZqdFRUZHFFaHlET1NCejNMLyt4UHZPTGF1WXVmbmUydS8wRkVuZ0loQ0g4WEl6ZW5zazhsa3BLZlNxYVBjSzZReDF5bVZpdGM1YUNWY1N6bjRzUVV3SW5OODBXVEd1UTZtNTNESGdXWnFnS3NabHErSDcwamxxMXZSUE0vS3UwVEJIK01GRkhOeWp5SWZWN0MwOVMyK2pCbm1kYWEzM0FaNCtnOS9FNWVnL1p6QktHQWNnQmFyU01lYVpOdXNXQWNrdlJBQ3Y1WXlidUVYK0JSM3NYMXA0U09XNWdMT2lPOUwzaWtzVXNzVFp2K3MyVU9kbFVZTFpTVGVrWWIrYXFQRkdzODdjY1FQUmZuRlRWV1EwZ0pvVENWVG1lWWdXazA5MjZMOVREaUlQZlJ5cWxBbkNQNWRPZGlxRmNLSXFOZ3d4Z0RyZmlITmJ2R3hhQ3hGeVdJd2R2ekdmNEZPa3VMaVdndXVPQVo3YlgxVzIwQnNlZjFHTnRTQUdBVlRMbmJsMUNiOC8rT2dwRU52WXM2UUtxdXdiRlZHTDhzRDJEc1czNXZodllXdkJJN0tVK1NxODBnSDhURkFvdzNiN3QzOU80UEJmbVMzOUJrRTgxMW9mK2MwSFpNMXhJd3NTajZVVkR3bkNLOHYwZ1BKOGZMMmo5NDIrVUVVRUpQUEJaNmo1TWcrRmxBb1ZFR2pHakp1cDF3NVdCTFVBYTl1blRiNWp5UGtFUW84clVrS1Y0ZmQ0b21XTEpobGJXcmNYWlh5ZndrRGhJR211Uzh4Z2w2NSs0anMyVTI4THRDQzJYSjhlNWJhWm9rNWQ0Q2VVbFJvUjErbmIvZTg0cmthOUtPOUV6MGZHVmRlSmc0MFZhYTBWb2xBeDkxYVVmUS9mV0tpRXMxbHBOR0EyMG55cytJSTU3QWZqRDdkdGJxTzZaNzBXbEpUenMrREE0SHJEcXFQQ0ZRbVRmc2VhWFFOOVBxK3RnRFZqdFc1TlVRMTg4UlhSZ2pvalp4ZitCT2piVDJ6b0xxMS96VmdETkZTaW1kSElLQitJYUlwaDB5LzZHWWFGb0p2eERsRm9YSzhwUnU4My8vdGNnZmFuN1gzUWZKMnF1WTN3T2VDUHN5dmM1TklNdHRJdnhHRFlTdWI3QW9Ta3Z4eGFsdHg3Vy9FWEJWVmNDaWROZE1YM1ErZ2VNQlMzeS9NbEE3M0pqMXQxb1N6UmZxdFFpY2tYd0w3bmYrNXB5RzY2eUVFb1ZaZ1dVdE0zMUptSE9LVUF6UGVFOSthd2xRUjd3NXJSeitFemJ1MitLdlY2N1V4NFdXVG5JY0pOUG4rRkhOKzE1V1ZlTVhuRlhxVHZOSiIsInNlcnZlclJhbmRvbSI6InNWOXdsS3RaZTV0cjBnTjlpZXRQU0ovVCIsInVzZXJDaGFsbGVuZ2UiOiJmeWtaTHJmU2tsMW9uMXBITlBrZEFZUS1pekd0N1ZGeWN6eUNDM2x4cmlrIiwic2lnbmF0dXJlQWxnb3JpdGhtIjoicnNhc3NhLXBzcyIsImZsb3dUeXBlIjoiTm90aWZpY2F0aW9uIiwic2lnbmF0dXJlQWxnb3JpdGhtUGFyYW1ldGVycyI6eyJoYXNoQWxnb3JpdGhtIjoiU0hBLTI1NiIsIm1hc2tHZW5BbGdvcml0aG0iOnsiYWxnb3JpdGhtIjoiaWQtbWdmMSIsInBhcmFtZXRlcnMiOnsiaGFzaEFsZ29yaXRobSI6IlNIQS0yNTYifX0sInNhbHRMZW5ndGgiOjMyLCJ0cmFpbGVyRmllbGQiOiIweGJjIn19LCJpc3MiOiJodHRwczovL2Nkb2MyLWF1dGgtc2VydmVyLmVlIiwic2NoZW1lTmFtZSI6InNtYXJ0LWlkLWRlbW8iLCJzaWduYXR1cmVQcm90b2NvbCI6IlJTQVNTQS1QU1MrQUNTUF9WMiIsIl9zZCI6WyJuTEpHdS05X3lKMmlEOGhrRXU5Ym5yc0EzUHJ5Y3UwVVE1WXQ5UENTNV8wIl0sImludGVyYWN0aW9uc0RpZ2VzdCI6Im9sSk43T1hVdmZ5MWJVUE51NzEyWDNBN01PbTFCWGlXdGxBbXYrdWJJejA9IiwiX3NkX2FsZyI6InNoYS0yNTYiLCJleHAiOjE3NzY4NzI1MjksImlhdCI6MTc3Njc4NjEyOSwiaW50ZXJhY3Rpb25UeXBlVXNlZCI6ImNvbmZpcm1hdGlvbk1lc3NhZ2VBbmRWZXJpZmljYXRpb25Db2RlQ2hvaWNlIiwicnBOYW1lIjoiREVNTyJ9.5ORVwgy0tMpX5tdwZmhnnQK_H4ngB-duofWj2OYCrJU5kL5dUvJRSeiC5QLbuzH-8gk08b5asqIW9lWNEErAuw~WyJONGFScHVxNTVRZzh6LTVxS3dlRURBIiwiYXVkIixbeyIuLi4iOiIxM19rVmNGcXF3M0tycllRaUpnUEJ4Qm1zOG1rY0puMmtnNWZBRGc4aUlFIn0seyIuLi4iOiJkaG9VbVZod0c2TEJIbkwyMHJxbDZFTkVFdjlfdHBPOFo4aUVFbmVESmhjIn1dXQ~WyJMb2dqR24xc21ZNmxpWllEZnh4OGhnIiwiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3Nlc3Npb25fbm9uY2VfMi9uclZjU0VjSHVXdDJTS2Zqa01tNlJRIl0~";

    @SuppressWarnings("checkstyle:LineLength")
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
        return createKeyShareRequest(length, RECIPIENT);
    }

    public static KeyShareRequest createKeyShareRequest(int length, String recipient) {
        return new KeyShareRequest(randomBytes(length), recipient);
    }

    @SneakyThrows
    private static String toBase64Url(String certPem) {
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(X509CertUtils.parseWithException(certPem).getEncoded());
    }
}
