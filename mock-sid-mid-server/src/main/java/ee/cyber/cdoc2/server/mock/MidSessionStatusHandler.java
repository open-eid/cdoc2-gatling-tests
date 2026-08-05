package ee.cyber.cdoc2.server.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

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
final class MidSessionStatusHandler implements HttpHandler {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            MockHttpUtil.respondJson(exchange, 405, "{}");
            return;
        }

        log.info(
            "MID session status requested ({}) -> RUNNING (MID completion is not mocked)",
            exchange.getRequestURI()
        );

        MockHttpUtil.respondJson(exchange, 200, JSON.writeValueAsString(Map.of("state", "RUNNING")));
    }
}
