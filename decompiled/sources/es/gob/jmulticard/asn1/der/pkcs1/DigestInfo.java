package es.gob.jmulticard.asn1.der.pkcs1;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.der.OctectString;
import es.gob.jmulticard.asn1.der.Sequence;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/pkcs1/DigestInfo.class */
public final class DigestInfo extends Sequence {
    private static final String SHA1WITHRSA_NORMALIZED_ALGO_NAME = "SHA1withRSA";
    private static final String SHA256WITHRSA_NORMALIZED_ALGO_NAME = "SHA256withRSA";
    private static final String SHA384WITHRSA_NORMALIZED_ALGO_NAME = "SHA384withRSA";
    private static final String SHA512WITHRSA_NORMALIZED_ALGO_NAME = "SHA512withRSA";
    private static final byte[] SHA1_DIGESTINFO_HEADER = {48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20};
    private static final byte[] SHA256_DIGESTINFO_HEADER = {48, 49, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 1, 5, 0, 4, 32};
    private static final byte[] SHA384_DIGESTINFO_HEADER = {48, 65, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 2, 5, 0, 4, 48};
    private static final byte[] SHA512_DIGESTINFO_HEADER = {48, 81, 48, 13, 6, 9, 96, -122, 72, 1, 101, 3, 4, 2, 3, 5, 0, 4, 64};

    private static byte[] getSha1DigestinfoHeader() {
        byte[] out = new byte[SHA1_DIGESTINFO_HEADER.length];
        System.arraycopy(SHA1_DIGESTINFO_HEADER, 0, out, 0, SHA1_DIGESTINFO_HEADER.length);
        return out;
    }

    private static byte[] getSha256DigestinfoHeader() {
        byte[] out = new byte[SHA256_DIGESTINFO_HEADER.length];
        System.arraycopy(SHA256_DIGESTINFO_HEADER, 0, out, 0, SHA256_DIGESTINFO_HEADER.length);
        return out;
    }

    private static byte[] getSha384DigestinfoHeader() {
        byte[] out = new byte[SHA384_DIGESTINFO_HEADER.length];
        System.arraycopy(SHA384_DIGESTINFO_HEADER, 0, out, 0, SHA384_DIGESTINFO_HEADER.length);
        return out;
    }

    private static byte[] getSha512DigestinfoHeader() {
        byte[] out = new byte[SHA512_DIGESTINFO_HEADER.length];
        System.arraycopy(SHA512_DIGESTINFO_HEADER, 0, out, 0, SHA512_DIGESTINFO_HEADER.length);
        return out;
    }

    public DigestInfo() {
        super(new OptionalDecoderObjectElement(AlgorithmIdentifer.class, false), new OptionalDecoderObjectElement(OctectString.class, false));
    }

    public String toString() {
        return "DigestInfo:\n  Datos=" + HexUtils.hexify(((OctectString) getElementAt(1)).getOctectStringByteValue(), false) + "\n  Algoritmo=" + ((AlgorithmIdentifer) getElementAt(0)).toString();
    }

    public static byte[] encode(String signingAlgorithm, byte[] data, CryptoHelper cryptoHelper) throws IOException {
        String normalizedSignningAlgorithm = getNormalizedSigningAlgorithm(signingAlgorithm);
        DigestAlgorithm digestAlgorithm = getDigestAlgorithm(normalizedSignningAlgorithm);
        byte[] header = selectHeaderTemplate(digestAlgorithm);
        byte[] md = cryptoHelper.digest(digestAlgorithm, data);
        byte[] digestInfo = new byte[header.length + md.length];
        System.arraycopy(header, 0, digestInfo, 0, header.length);
        System.arraycopy(md, 0, digestInfo, header.length, md.length);
        return digestInfo;
    }

    private static String getNormalizedSigningAlgorithm(String algorithm) {
        if (AOSignConstants.DIGEST_ALGORITHM_SHA1.equalsIgnoreCase(algorithm) || "SHA-1".equalsIgnoreCase(algorithm) || "SHA".equalsIgnoreCase(algorithm) || "SHAwithRSA".equalsIgnoreCase(algorithm) || "SHA-1withRSA".equalsIgnoreCase(algorithm) || "SHA1withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA-1withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA1withRSA".equalsIgnoreCase(algorithm)) {
            return "SHA1withRSA";
        }
        if ("SHA256".equalsIgnoreCase(algorithm) || "SHA-256".equalsIgnoreCase(algorithm) || "SHA-256withRSA".equalsIgnoreCase(algorithm) || "SHA-256withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA256withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA256withRSA".equalsIgnoreCase(algorithm)) {
            return "SHA256withRSA";
        }
        if (AOSignConstants.DIGEST_ALGORITHM_SHA384.equalsIgnoreCase(algorithm) || "SHA-384".equalsIgnoreCase(algorithm) || "SHA-384withRSA".equalsIgnoreCase(algorithm) || "SHA-384withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA384withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA384withRSA".equalsIgnoreCase(algorithm)) {
            return "SHA384withRSA";
        }
        if ("SHA512".equalsIgnoreCase(algorithm) || "SHA-512".equalsIgnoreCase(algorithm) || "SHA-512withRSA".equalsIgnoreCase(algorithm) || "SHA-512withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA512withRSAEncryption".equalsIgnoreCase(algorithm) || "SHA512withRSA".equalsIgnoreCase(algorithm)) {
            return "SHA512withRSA";
        }
        return algorithm;
    }

    private static byte[] selectHeaderTemplate(DigestAlgorithm algorithm) {
        switch (algorithm) {
            case SHA1:
                return getSha1DigestinfoHeader();
            case SHA256:
                return getSha256DigestinfoHeader();
            case SHA384:
                return getSha384DigestinfoHeader();
            case SHA512:
                return getSha512DigestinfoHeader();
            default:
                throw new IllegalStateException("Algoritmo de huella digital no soportado: " + algorithm);
        }
    }

    private static DigestAlgorithm getDigestAlgorithm(String signatureAlgorithm) {
        if ("SHA1withRSA".equals(signatureAlgorithm)) {
            return DigestAlgorithm.SHA1;
        }
        if ("SHA256withRSA".equals(signatureAlgorithm)) {
            return DigestAlgorithm.SHA256;
        }
        if ("SHA384withRSA".equals(signatureAlgorithm)) {
            return DigestAlgorithm.SHA384;
        }
        if ("SHA512withRSA".equals(signatureAlgorithm)) {
            return DigestAlgorithm.SHA512;
        }
        throw new IllegalStateException("Algoritmo de huella digital no soportado para: " + signatureAlgorithm);
    }
}
