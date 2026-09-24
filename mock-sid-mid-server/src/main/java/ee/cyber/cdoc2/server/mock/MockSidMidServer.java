package ee.cyber.cdoc2.server.mock;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * A minimal stand-in for the Smart-ID and Mobile-ID demo REST APIs, so that
 * cdoc2-auth-server can be load tested without depending on (or placing load on) the real SK
 * demo environment.
 * <p>
 * Point cdoc2-auth-server's own configuration at this process:
 * {@code app.smartid.client.hostUrl=http://localhost:PORT} and
 * {@code app.mobileid.client.hostUrl=http://localhost:PORT} (no TLS - see README.md for why
 * that is fine, and for the coverage/scope of what is and isn't mocked).
 */
@Slf4j
public final class MockSidMidServer {

    private static final int DEFAULT_PORT = 9500;
    private static final int SOCKET_BACKLOG = 0;

    private MockSidMidServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.getInteger("mock-server.port", DEFAULT_PORT);
        boolean completeSessionImmediate = Boolean.getBoolean("mock-server.sessionImmediate");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), SOCKET_BACKLOG);

        // sessionId -> endResult ("OK"/"USER_REFUSED"), shared between the two SID handlers
        ConcurrentHashMap<String, String> midSidSessionStates = new ConcurrentHashMap<>();

        // Smart-ID: POST /authentication/notification/etsi/{semanticsIdentifier}, GET /session/{sessionId}
        server.createContext("/authentication/notification/etsi", new SidAuthenticationHandler(midSidSessionStates));
        server.createContext("/session", new SidSessionStatusHandler(
            midSidSessionStates,
            completeSessionImmediate
        ));

        // Mobile-ID: POST /authentication, GET /authentication/session/{sessionId}
        // ("/authentication/session" is registered separately so it wins over "/authentication"
        // for that more specific path, per HttpServer's longest-prefix context matching)
        server.createContext("/authentication", new MidAuthenticationHandler(midSidSessionStates));
        server.createContext("/authentication/session", new MidSessionStatusHandler(
            midSidSessionStates,
            completeSessionImmediate
        ));

        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();

        log.info("Mock SID/MID server listening on http://localhost:{}", port);
    }
}
