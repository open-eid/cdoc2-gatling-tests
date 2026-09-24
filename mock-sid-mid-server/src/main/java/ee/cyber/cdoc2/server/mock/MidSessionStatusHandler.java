package ee.cyber.cdoc2.server.mock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import static ee.cyber.cdoc2.server.mock.SessionStateHelper.calculateSessionEndResult;

/**
 * Mocks Mobile-ID's "GET /authentication/session/{sessionId}".
 * <p>
 * Unlike the Smart-ID flow, cdoc2-auth-server cryptographically verifies the MID signature
 * (against a certificate chained to its configured MID truststore) before it will mark a MID
 * auth process COMPLETE. Faking that would require this mock to hold its own trusted CA/leaf
 * certificate and produce a genuine signature over the exact hash cdoc2-auth-server expects,
 * which this simple mock does not attempt. Instead, every poll reports RUNNING, which
 * cdoc2-auth-server accepts without attempting signature validation - so "/auth/start" for
 * MID works against this mock, but "/auth/status" for a MID auth process will never
 * progress past STARTED here.
 */
@Slf4j
@RequiredArgsConstructor
final class MidSessionStatusHandler implements HttpHandler {
    private static final String END_RESULT_OK = "OK";
    private static final ObjectMapper JSON = new ObjectMapper();

    private final ConcurrentHashMap<String, String> sessionStates;
    private final boolean completeSessionImmediate;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        String sessionEndResult = calculateSessionEndResult(
            exchange.getRequestURI(),
            this.sessionStates,
            completeSessionImmediate
        );

        if (sessionEndResult == null) {
            log.info("MID session status requested ({}) -> RUNNING", exchange.getRequestURI());
            MockHttpUtil.respondJson(exchange, 200, runningSessionStatus());
        } else {
            log.info("MID session status requested ({}) -> COMPLETE/{}", exchange.getRequestURI(), sessionEndResult);

            MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(completeSessionStatus(sessionEndResult)));
        }
    }

    private static String runningSessionStatus() throws JsonProcessingException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("state", "RUNNING");
        return JSON.writeValueAsString(body);
    }

    private static Map<String, Object> completeSessionStatus(String endResult) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("state", "COMPLETE");
        body.put("result", endResult);

        // a refused session has no signature/certificate to report - matches the real API,
        // and cdoc2-auth-server does not validate these fields by default
        if (END_RESULT_OK.equals(endResult)) {
            body.put("signature", Map.of(
                "value", MockHttpUtil.randomBase64(256)
            ));
            body.put("cert", MockHttpUtil.randomBase64(512));
        }

        return body;
    }
}
