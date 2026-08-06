package ee.cyber.cdoc2.server.scenarios;

/**
 * Test scenario identifiers.
 *
 * <p>Identifiers follow the naming used in the CDOC2 SmartID/MobileID server test plan
 * (test_plan_sidmid.md, section "Key Shares Server API functionality tests").
 */
public final class ScenarioIdentifiers {

    private ScenarioIdentifiers() {
    }

    public static final String POS_KEYSHARE_01 = "POST_KEYSHARE-POS-01-ONCE";
    public static final String POS_KEYSHARE_02 = "POST_KEYSHARE-POS-02-REPEATEDLY";
    public static final String POS_KEYSHARE_03 = "POST_KEYSHARE-POS-03-RANDOM_CONTENT";

    public static final String POS_NONCE_01 = "POST_NONCE-POS-01-CORRECT_SHARE_ID";
    public static final String POS_SESSION_NONCE_01 = "POST_SESSION_NONCE-POS-01";

    public static final String POS_GET_KEYSHARE_01 = "GET_KEYSHARE-POS-01-CORRECT_REQUEST";

    public static final String NEG_POST_KEYSHARE_01 = "POST_KEYSHARE-NEG-01-SHARE_TOO_BIG";

    public static final String NEG_GET_KEYSHARE_01 = "GET_KEYSHARE-NEG-01-TOO_SHORT_SHARE_ID";
    public static final String NEG_GET_KEYSHARE_02 = "GET_KEYSHARE-NEG-02-EMPTY_STRING_SHARE_ID";
    public static final String NEG_GET_KEYSHARE_03 = "GET_KEYSHARE-NEG-03-TOO_LONG_RANDOM_STRING_SHARE_ID";
    public static final String NEG_GET_KEYSHARE_04 = "GET_KEYSHARE-NEG-04-MISSING_SHARE_ID_AND_URI_SLASH";
    public static final String NEG_GET_KEYSHARE_05 = "GET_KEYSHARE-NEG-05-RANDOM_AUTH_TICKET";
    public static final String NEG_GET_KEYSHARE_06 = "GET_KEYSHARE-NEG-06-RANDOM_SHARE_ID";
    public static final String NEG_GET_KEYSHARE_07 = "GET_KEYSHARE-NEG-07-RECIPIENT_NOT_MATCHING";
    public static final String NEG_GET_KEYSHARE_08 = "GET_KEYSHARE-NEG-08-MISSING_AUTH_HEADERS";

    public static final String NEG_POST_NONCE_01 = "POST_NONCE-NEG-01-RANDOM_SHARE_ID";
}
