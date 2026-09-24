package ee.cyber.cdoc2.server.mock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import static ee.cyber.cdoc2.server.mock.SessionStateHelper.SESSION_RUNNING_MARKER;

/**
 * Mocks Mobile-ID's "POST /authentication", which cdoc2-auth-server calls to start a
 * Mobile-ID auth session.
 */
@Slf4j
@RequiredArgsConstructor
final class MidAuthenticationHandler implements HttpHandler {
    private static final String USER_CANCELLED_IDENTIFIER_MARKER = "60001019950";

    private static final ObjectMapper JSON = new ObjectMapper();

    private final ConcurrentHashMap<String, String> sessionStates;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        String sessionId = UUID.randomUUID().toString();
        String requestBody = new String(exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8);
        String sessionState = requestBody.contains(USER_CANCELLED_IDENTIFIER_MARKER)
            ? SESSION_RUNNING_MARKER + "USER_CANCELLED"
            : SESSION_RUNNING_MARKER + "OK";
        this.sessionStates.put(sessionId, sessionState);
        log.info("MID authentication started ({}), session {}, state {}", exchange.getRequestURI(),
            sessionId, sessionState);

        MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(Map.of("sessionID", sessionId)));
    }
}
