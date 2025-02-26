package ee.cyber.cdoc2.server.scenarios;

/**
 * Test scenario identifiers
 */
public final class ScenarioIdentifiers {

    private ScenarioIdentifiers() {
    }

    public static final String POS_PUT_SHARE_01 = "PUT_SHARE-POS-01";
    public static final String POS_PUT_SHARE_02 = "PUT_SHARE-POS-02";
    public static final String POS_PUT_SHARE_03 = "PUT_SHARE-POS-03";

    public static final String POS_PUT_NONCE_01 = "PUT_NONCE-POS-01";

    public static final String POS_GET_SHARE_01 = "GET_SHARE-POS-01";

    public static final String NEG_GET_SHARE_01 = "GET_SHARE-NEG-01";
    public static final String NEG_GET_SHARE_02 = "GET_SHARE-NEG-02-EMPTY_STRING_SHARE_ID";
    public static final String NEG_GET_SHARE_03 = "GET_SHARE-NEG-03-MISSING_SHARE_ID";
    public static final String NEG_GET_SHARE_04 = "GET_SHARE-NEG-04-MISSING_SHARE_ID_AND_URI_SLASH";
    public static final String NEG_GET_SHARE_05 = "GET_SHARE-NEG-05-TOO_LONG_RANDOM_SHARE_ID";
    public static final String NEG_GET_SHARE_06 = "GET_SHARE-NEG-06-RANDOM_AUTH_TICKET";

    public static final String NEG_PUT_SHARE_01 = "PUT_SHARE-NEG-01-SHARE_TOO_BIG";
}
