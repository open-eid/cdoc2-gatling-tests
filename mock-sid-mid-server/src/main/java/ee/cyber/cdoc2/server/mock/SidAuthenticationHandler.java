package ee.cyber.cdoc2.server.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mocks Smart-ID's "POST /authentication/notification/etsi/{semanticsIdentifier}", which
 * cdoc2-auth-server calls to start a Smart-ID auth session. The verification code shown to
 * the user is computed by cdoc2-auth-server itself from data it never sends us, so the only
 * thing this response needs to provide is a session ID to poll later.
 * <p>
 * The requested semantics identifier is inspected for {@link #USER_REFUSED_IDENTIFIER_MARKER}
 * (mirroring the real SK demo environment's "always refused" test identity convention) so that
 * {@link SidSessionStatusHandler} can later report that specific session as refused rather
 * than completed - see mock-sid-mid-server/README.md.
 */
@Slf4j
@RequiredArgsConstructor
final class SidAuthenticationHandler implements HttpHandler {

    static final String USER_REFUSED_IDENTIFIER_MARKER = "30403039917";

    private static final ObjectMapper JSON = new ObjectMapper();

    private final ConcurrentHashMap<String, String> sessionEndResults;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String sessionId = UUID.randomUUID().toString();
        String endResult = path.contains(USER_REFUSED_IDENTIFIER_MARKER) ? "USER_REFUSED" : "OK";
        this.sessionEndResults.put(sessionId, endResult);

        log.info("SID authentication started ({}), session {} will report {}", path, sessionId, endResult);

        MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(Map.of("sessionID", sessionId)));
    }
}
