package es.gob.jmulticard;

import es.gob.afirma.core.signers.AOSignConstants;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/DigestAlgorithm.class */
public enum DigestAlgorithm {
    SHA1(AOSignConstants.DIGEST_ALGORITHM_SHA1, 20),
    SHA256("SHA-256", 32),
    SHA384("SHA-384", 48),
    SHA512("SHA-512", 64);

    private final String name;
    private final int length;

    DigestAlgorithm(String n, int l) {
        this.name = n;
        this.length = l;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }

    public int getDigestLength() {
        return this.length;
    }

    public static DigestAlgorithm getDigestAlgorithm(String name) {
        if (AOSignConstants.DIGEST_ALGORITHM_SHA1.equals(name) || "SHA-1".equals(name)) {
            return SHA1;
        }
        if ("SHA256".equals(name) || "SHA-256".equals(name)) {
            return SHA256;
        }
        if (AOSignConstants.DIGEST_ALGORITHM_SHA384.equals(name) || "SHA-384".equals(name)) {
            return SHA384;
        }
        if ("SHA512".equals(name) || "SHA-512".equals(name)) {
            return SHA512;
        }
        throw new IllegalArgumentException("Algoritmo de huella no soportado: " + name);
    }
}
