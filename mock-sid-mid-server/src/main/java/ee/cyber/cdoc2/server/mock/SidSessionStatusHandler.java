package ee.cyber.cdoc2.server.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mocks Smart-ID's "GET /session/{sessionId}". Always reports the session as COMPLETE on the
 * first poll, with the end result ({@code OK} or {@code USER_REFUSED}) recorded by
 * {@link SidAuthenticationHandler} for that session ID. cdoc2-auth-server embeds the
 * "signature" object verbatim into the issued session token without cryptographically
 * verifying it, so its exact content does not matter here, only its presence for the OK case.
 * <p>
 * {@code sessionEndResults} is never evicted, so it grows for the lifetime of the process -
 * fine for a finite test/load run, restart the mock between runs if that matters.
 */
@Slf4j
@RequiredArgsConstructor
final class SidSessionStatusHandler implements HttpHandler {

    private static final String END_RESULT_OK = "OK";

    private static final ObjectMapper JSON = new ObjectMapper();

    private final ConcurrentHashMap<String, String> sessionEndResults;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        String sessionId = lastPathSegment(exchange.getRequestURI().getPath());
        String endResult = this.sessionEndResults.getOrDefault(sessionId, END_RESULT_OK);

        log.info("SID session status requested ({}) -> COMPLETE/{}", exchange.getRequestURI(), endResult);

        MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(completeSessionStatus(endResult)));
    }

    private static String lastPathSegment(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private static Map<String, Object> completeSessionStatus(String endResult) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("state", "COMPLETE");
        body.put("result", Map.of("endResult", endResult));
        body.put("signatureProtocol", "ACSP_V2");

        // a refused session has no signature/certificate to report - matches the real API,
        // and cdoc2-auth-server never reads these fields for a non-OK result anyway
        if (END_RESULT_OK.equals(endResult)) {
            body.put("signature", Map.of(
                "value", MockHttpUtil.randomBase64(256),
                "serverRandom", MockHttpUtil.randomBase64(24),
                "userChallenge", MockHttpUtil.randomBase64(24),
                "flowType", "Notification",
                "signatureAlgorithm", "rsassa-pss",
                "signatureAlgorithmParameters", Map.of(
                    "hashAlgorithm", "SHA-256",
                    "maskGenAlgorithm", Map.of(
                        "algorithm", "id-mgf1",
                        "parameters", Map.of("hashAlgorithm", "SHA-256")
                    ),
                    "saltLength", 32,
                    "trailerField", "0xbc"
                )
            ));
            body.put("cert", Map.of(
                "value", MockHttpUtil.randomBase64(512),
                "certificateLevel", "QUALIFIED"
            ));
            body.put("interactionTypeUsed", "confirmationMessageAndVerificationCodeChoice");
        }

        return body;
    }
}
