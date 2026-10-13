package es.gob.jmulticard.jse.provider.rsacipher;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/TlsRsaPremasterSecretParameterSpec.class */
final class TlsRsaPremasterSecretParameterSpec implements AlgorithmParameterSpec {
    private final int clientVersion;
    private final int serverVersion;

    TlsRsaPremasterSecretParameterSpec(int clientVer, int serverVer) {
        this.clientVersion = checkVersion(clientVer);
        this.serverVersion = checkVersion(serverVer);
    }

    int getClientVersion() {
        return this.clientVersion;
    }

    int getServerVersion() {
        return this.serverVersion;
    }

    private static int checkVersion(int version) {
        if (version < 0 || version > 65535) {
            throw new IllegalArgumentException("La version debe ser un numero entre 0 y 65,535");
        }
        return version;
    }
}
