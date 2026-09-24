package ee.cyber.cdoc2.server.scenarios;

/**
 * Test scenario identifiers, matching the ids used in the CDOC2 server test plan's
 * "Authentication Server API functionality tests" section
 */
public final class ScenarioIdentifiers {

    private ScenarioIdentifiers() {
    }

    public static final String POS_START_AUTH_01 = "POST_START_AUTH-POS-01-SID";
    public static final String POS_START_AUTH_02 = "POST_START_AUTH-POS-02-MID";
    public static final String POS_START_AUTH_03 = "POST_START_AUTH-POS-03-SID";
    public static final String POS_START_AUTH_04 = "POST_START_AUTH-POS-04-MID";

    public static final String NEG_START_AUTH_01 = "POST_START_AUTH-NEG-01-IDENTIFIER_MISSING";
    public static final String NEG_START_AUTH_02 = "POST_START_AUTH-NEG-02-IDENTIFIER_TOO_SHORT";
    public static final String NEG_START_AUTH_03 = "POST_START_AUTH-NEG-03-IDENTIFIER_TOO_LONG";
    public static final String NEG_START_AUTH_04 = "POST_START_AUTH-NEG-04-IDENTIFIER_FORMAT_INCORRECT";

    public static final String NEG_START_AUTH_05_MOBILE_NR_TOO_SHORT = "POST_START_AUTH-NEG-05-MOBILE_NR_TOO_SHORT";
    public static final String NEG_START_AUTH_06_MOBILE_NR_TOO_LONG = "POST_START_AUTH-NEG-06-MOBILE_NR_TOO_LONG";
    public static final String NEG_START_AUTH_06 = "POST_START_AUTH-NEG-06-LANGUAGE_UNKNOWN";

    public static final String POS_AUTH_STATUS_01 = "GET_AUTH_STATUS-POS-01-COMPLETED-ONCE";
    public static final String POS_AUTH_STATUS_02 = "GET_AUTH_STATUS-POS-02-COMPLETED-REPEATEDLY";
    public static final String POS_AUTH_STATUS_03 = "GET_AUTH_STATUS-POS-03-RUNNING";
    public static final String POS_AUTH_STATUS_04 = "GET_AUTH_STATUS-POS-04-FAILED";

    public static final String NEG_AUTH_STATUS_01 = "GET_AUTH_STATUS-NEG-01-UUID_TOO_SHORT";
    public static final String NEG_AUTH_STATUS_02 = "GET_AUTH_STATUS-NEG-02-UUID_TOO_LONG";
    public static final String NEG_AUTH_STATUS_03 = "GET_AUTH_STATUS-NEG-03-UUID_MALFORMED";
    public static final String NEG_AUTH_STATUS_04 = "GET_AUTH_STATUS-NEG-04-AUTH_PROCESS_NOT_FOUND";

    public static final String POS_INFO_01 = "GET_INFO-POS-01";
}
