package ee.cyber.cdoc2.server.conf;

public record KeysConfig(
    String sessionTokenSigningKey,
    String rpCounterSigningKey
) {
}

