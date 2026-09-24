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

@Slf4j
@RequiredArgsConstructor
final class SidSessionStatusHandler implements HttpHandler {
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
            log.info("SID session status requested ({}) -> RUNNING", exchange.getRequestURI());
            MockHttpUtil.respondJson(exchange, 200, runningSessionStatus());
        } else {
            log.info("SID session status requested ({}) -> COMPLETE/{}", exchange.getRequestURI(), sessionEndResult);

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
