package ee.cyber.cdoc2.server.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Mocks Mobile-ID's "POST /authentication", which cdoc2-auth-server calls to start a
 * Mobile-ID auth session. Only "/auth/start" is covered for MID (see
 * {@link MidSessionStatusHandler} for why "/auth/status" is not).
 */
@Slf4j
final class MidAuthenticationHandler implements HttpHandler {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        String sessionId = UUID.randomUUID().toString();
        log.info("MID authentication started ({}), session {}", exchange.getRequestURI(), sessionId);

        MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(Map.of("sessionID", sessionId)));
    }
}
