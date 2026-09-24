package ee.cyber.cdoc2.server.mock;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
final class SessionStateHelper {
    private SessionStateHelper() {
        // utility class
    }

    static final String SESSION_RUNNING_MARKER = "RUNNING.";

    static String calculateSessionEndResult(
        URI requestUri,
        ConcurrentHashMap<String, String> sessionStates,
        boolean completeSessionImmediate) {
        String sessionId = lastPathSegment(requestUri.getPath());
        log.info("sessionState: {}, {}", sessionStates.get(sessionId), requestUri);

        String currentState = sessionStates.get(sessionId);

        if (currentState == null) {
            return "NOT_FOUND";
        }

        if (currentState.startsWith(SESSION_RUNNING_MARKER)) {
            String finalResult = currentState.substring(SESSION_RUNNING_MARKER.length());

            sessionStates.put(
                sessionId,
                finalResult
            );
            return completeSessionImmediate ? finalResult : null;
        }

        return currentState;
    }

    private static String lastPathSegment(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
