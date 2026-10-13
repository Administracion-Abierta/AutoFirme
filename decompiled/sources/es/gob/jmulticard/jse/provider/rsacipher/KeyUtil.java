package es.gob.jmulticard.jse.provider.rsacipher;

import java.security.SecureRandom;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/KeyUtil.class */
final class KeyUtil {
    private KeyUtil() {
    }

    static byte[] checkTlsPreMasterSecretKey(int clientVersion, int serverVersion, SecureRandom random, byte[] encoded, boolean isFailOver) {
        byte[] replacer = new byte[48];
        (random != null ? random : new SecureRandom()).nextBytes(replacer);
        if (!isFailOver && encoded != null) {
            if (encoded.length != 48) {
                return replacer;
            }
            int encodedVersion = ((encoded[0] & 255) << 8) | (encoded[1] & 255);
            if (clientVersion != encodedVersion && (clientVersion > 769 || serverVersion != encodedVersion)) {
                return replacer;
            }
            return encoded;
        }
        return replacer;
    }
}
