package ee.cyber.cdoc2.server.mock;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Small helpers shared by the mock SID/MID HTTP handlers.
 */
final class MockHttpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private MockHttpUtil() {
    }

    /**
     * Drains the request body (required so the connection can be reused) and writes a JSON
     * response with the given status code.
     */
    static void respondJson(HttpExchange exchange, int statusCode, String jsonBody) throws IOException {
        exchange.getRequestBody().readAllBytes();

        byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream body = exchange.getResponseBody()) {
            body.write(bytes);
        }
    }

    /**
     * Generates a random base64 string, for filling in response fields whose exact content
     * does not matter (not cryptographically validated by the caller).
     */
    static String randomBase64(int byteLength) {
        byte[] bytes = new byte[byteLength];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
