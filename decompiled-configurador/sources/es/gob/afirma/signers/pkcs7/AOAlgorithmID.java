package es.gob.afirma.signers.pkcs7;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.Dictionary;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/pkcs7/AOAlgorithmID.class */
public final class AOAlgorithmID {
    public static final String OID_SHA256 = "2.16.840.1.101.3.4.2.1";
    private static final String OID_SHA1 = "1.3.14.3.2.26";
    private static final String OID_SHA224 = "2.16.840.1.101.3.4.2.4";
    private static final String OID_SHA512 = "2.16.840.1.101.3.4.2.3";
    private static final String OID_SHA384 = "2.16.840.1.101.3.4.2.2";
    private static final String OID_RSA = "1.2.840.113549.1.1.1";
    private static final String OID_RSA_SHA1 = "1.2.840.113549.1.1.5";
    private static final String OID_RSA_SHA224 = "1.2.840.113549.1.1.14";
    private static final String OID_RSA_SHA256 = "1.2.840.113549.1.1.11";
    private static final String OID_RSA_SHA384 = "1.2.840.113549.1.1.12";
    private static final String OID_RSA_SHA512 = "1.2.840.113549.1.1.13";
    private static final String OID_ECDSA_SHA1 = "1.2.840.10045.4.1";
    private static final String OID_ECDSA_SHA224 = "1.2.840.10045.4.3.1";
    private static final String OID_ECDSA_SHA256 = "1.2.840.10045.4.3.2";
    private static final String OID_ECDSA_SHA384 = "1.2.840.10045.4.3.3";
    private static final String OID_ECDSA_SHA512 = "1.2.840.10045.4.3.4";
    private static final String OID_RSA_SHA3_224 = "2.16.840.1.101.3.4.3.13";
    private static final String OID_RSA_SHA3_256 = "2.16.840.1.101.3.4.3.14";
    private static final String OID_RSA_SHA3_384 = "2.16.840.1.101.3.4.3.15";
    private static final String OID_RSA_SHA3_512 = "2.16.840.1.101.3.4.3.16";
    private static final String OID_ECDSA_SHA3_224 = "2.16.840.1.101.3.4.3.9";
    private static final String OID_ECDSA_SHA3_256 = "2.16.840.1.101.3.4.3.10";
    private static final String OID_ECDSA_SHA3_384 = "2.16.840.1.101.3.4.3.11";
    private static final String OID_ECDSA_SHA3_512 = "2.16.840.1.101.3.4.3.12";
    private static final String OID_DSA = "1.2.840.10040.4.1";
    private static final String OID_DSA_SHA1 = "1.2.840.10040.4.3";
    private static final String OID_DSA_SHA224 = "2.16.840.1.101.3.4.3.1";
    private static final String OID_DSA_SHA256 = "2.16.840.1.101.3.4.3.2";
    private static final String OID_DSA_SHA384 = "2.16.840.1.101.3.4.3.3";
    private static final String OID_DSA_SHA512 = "2.16.840.1.101.3.4.3.4";
    private static final String OID_DSA_SHA3_224 = "2.16.840.1.101.3.4.3.5";
    private static final String OID_DSA_SHA3_256 = "2.16.840.1.101.3.4.3.6";
    private static final String OID_DSA_SHA3_384 = "2.16.840.1.101.3.4.3.7";
    private static final String OID_DSA_SHA3_512 = "2.16.840.1.101.3.4.3.8";
    private static final Dictionary<String, String> OIDS = new Hashtable();

    private AOAlgorithmID() {
    }

    static {
        OIDS.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, OID_SHA1);
        OIDS.put("SHA-1", OID_SHA1);
        OIDS.put("SHA", OID_SHA1);
        OIDS.put(OID_SHA1, OID_SHA1);
        OIDS.put("SHA-512", OID_SHA512);
        OIDS.put("SHA512", OID_SHA512);
        OIDS.put(OID_SHA512, OID_SHA512);
        OIDS.put("SHA-256", OID_SHA256);
        OIDS.put("SHA256", OID_SHA256);
        OIDS.put(OID_SHA256, OID_SHA256);
        OIDS.put("SHA-384", OID_SHA384);
        OIDS.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, OID_SHA384);
        OIDS.put(OID_SHA384, OID_SHA384);
        OIDS.put("SHA-224", OID_SHA224);
        OIDS.put("SHA224", OID_SHA224);
        OIDS.put(OID_SHA224, OID_SHA224);
        OIDS.put("RSA", OID_RSA);
        OIDS.put(OID_RSA, OID_RSA);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA, OID_RSA_SHA1);
        OIDS.put("SHA-1withRSA", OID_RSA_SHA1);
        OIDS.put("SHA224withRSA", OID_RSA_SHA224);
        OIDS.put("SHA-224withRSA", OID_RSA_SHA224);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA, OID_RSA_SHA256);
        OIDS.put("SHA-256withRSA", OID_RSA_SHA256);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA, OID_RSA_SHA384);
        OIDS.put("SHA-384withRSA", OID_RSA_SHA384);
        OIDS.put("SHA512withRSA", OID_RSA_SHA512);
        OIDS.put("SHA-512withRSA", OID_RSA_SHA512);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA, OID_ECDSA_SHA1);
        OIDS.put("SHA-1withECDSA", OID_ECDSA_SHA1);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA, OID_ECDSA_SHA224);
        OIDS.put("SHA-224withECDSA", OID_ECDSA_SHA224);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA, OID_ECDSA_SHA256);
        OIDS.put("SHA-256withECDSA", OID_ECDSA_SHA256);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA, OID_ECDSA_SHA384);
        OIDS.put("SHA-384withECDSA", OID_ECDSA_SHA384);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA, OID_ECDSA_SHA512);
        OIDS.put("SHA-512withECDSA", OID_ECDSA_SHA512);
        OIDS.put("DSA", OID_DSA);
        OIDS.put(OID_DSA, OID_DSA);
        OIDS.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHDSA, OID_DSA_SHA1);
        OIDS.put("SHA-1withDSA", OID_DSA_SHA1);
        OIDS.put("SHA224withDSA", OID_DSA_SHA224);
        OIDS.put("SHA-224withDSA", OID_DSA_SHA224);
        OIDS.put("SHA256withDSA", OID_DSA_SHA256);
        OIDS.put("SHA-256withDSA", OID_DSA_SHA256);
        OIDS.put("SHA384withDSA", OID_DSA_SHA384);
        OIDS.put("SHA-384withDSA", OID_DSA_SHA384);
        OIDS.put("SHA512withDSA", OID_DSA_SHA512);
        OIDS.put("SHA-512withDSA", OID_DSA_SHA512);
        OIDS.put("SHA3-224withRSA", OID_RSA_SHA3_224);
        OIDS.put("SHA3-256withRSA", OID_RSA_SHA3_256);
        OIDS.put("SHA3-384withRSA", OID_RSA_SHA3_384);
        OIDS.put("SHA3-512withRSA", OID_RSA_SHA3_512);
        OIDS.put("SHA3-224withECDSA", OID_ECDSA_SHA3_224);
        OIDS.put("SHA3-256withECDSA", OID_ECDSA_SHA3_256);
        OIDS.put("SHA3-384withECDSA", OID_ECDSA_SHA3_384);
        OIDS.put("SHA3-512withECDSA", OID_ECDSA_SHA3_512);
        OIDS.put("SHA3-224withDSA", OID_DSA_SHA3_224);
        OIDS.put("SHA3-256withDSA", OID_DSA_SHA3_256);
        OIDS.put("SHA3-384withDSA", OID_DSA_SHA3_384);
        OIDS.put("SHA3-512withDSA", OID_DSA_SHA3_512);
    }

    public static String getOID(String str) {
        if (str == null) {
            return null;
        }
        String str2 = OIDS.get(str);
        if (str2 == null) {
            throw new IllegalArgumentException("Se desconoce el algoritmo '" + str + "'");
        }
        return str2;
    }

    public static boolean isRSAOID(String str) {
        boolean z = false;
        if (OID_RSA.equals(str) || OID_RSA_SHA1.equals(str) || OID_RSA_SHA224.equals(str) || OID_RSA_SHA256.equals(str) || OID_RSA_SHA384.equals(str) || OID_RSA_SHA512.equals(str) || OID_RSA_SHA3_224.equals(str) || OID_RSA_SHA3_256.equals(str) || OID_RSA_SHA3_384.equals(str) || OID_RSA_SHA3_512.equals(str)) {
            z = true;
        }
        return z;
    }

    public static boolean isECDSAOID(String str) {
        boolean z = false;
        if (OID_ECDSA_SHA1.equals(str) || OID_ECDSA_SHA224.equals(str) || OID_ECDSA_SHA256.equals(str) || OID_ECDSA_SHA384.equals(str) || OID_ECDSA_SHA512.equals(str) || OID_ECDSA_SHA3_224.equals(str) || OID_ECDSA_SHA3_256.equals(str) || OID_ECDSA_SHA3_384.equals(str) || OID_ECDSA_SHA3_512.equals(str)) {
            z = true;
        }
        return z;
    }

    public static boolean isDSAOID(String str) {
        boolean z = false;
        if (OID_DSA.equals(str) || OID_DSA_SHA1.equals(str) || OID_DSA_SHA224.equals(str) || OID_DSA_SHA256.equals(str) || OID_DSA_SHA384.equals(str) || OID_DSA_SHA512.equals(str) || OID_DSA_SHA3_224.equals(str) || OID_DSA_SHA3_256.equals(str) || OID_DSA_SHA3_384.equals(str) || OID_DSA_SHA3_512.equals(str)) {
            z = true;
        }
        return z;
    }
}
