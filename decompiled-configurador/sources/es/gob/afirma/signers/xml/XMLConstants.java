package es.gob.afirma.signers.xml;

import es.gob.afirma.core.signers.AOSignConstants;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/XMLConstants.class */
public final class XMLConstants {
    public static final String DSIGNNS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String URL_SHA1_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String URL_SHA256_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String URL_SHA384_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    private static final String URL_SHA512_RSA = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String URL_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    private static final String URL_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    private static final String URL_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    private static final String URL_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String URL_SHA1_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    private static final String URL_SHA224_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    private static final String URL_SHA256_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    private static final String URL_SHA384_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    private static final String URL_SHA512_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    public static final Map<String, String> SIGN_ALGOS_URI = new HashMap();
    public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final Map<String, String> MESSAGEDIGEST_ALGOS_URI;
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    public static final String OBJURI = "http://www.w3.org/2000/09/xmldsig#Object";
    public static final String TAG_SIGNATURE = "Signature";
    public static final String TAG_SIGNEDINFO = "SignedInfo";
    public static final String TAG_REFERENCE = "Reference";

    private XMLConstants() {
    }

    static {
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("RSA", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("SHA-1withRSA", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("SHA1withRSAEncryption", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("SHA-1withRSAEncryption", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("SHAwithRSAEncryption", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put("SHAwithRSA", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        SIGN_ALGOS_URI.put("SHA-256withRSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        SIGN_ALGOS_URI.put("SHA256withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        SIGN_ALGOS_URI.put("SHA-256withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        SIGN_ALGOS_URI.put("SHA-384withRSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        SIGN_ALGOS_URI.put("SHA384withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        SIGN_ALGOS_URI.put("SHA-384withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        SIGN_ALGOS_URI.put("SHA512withRSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        SIGN_ALGOS_URI.put("SHA-512withRSA", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        SIGN_ALGOS_URI.put("SHA512withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        SIGN_ALGOS_URI.put("SHA-512withRSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("ECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("SHA-1withECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("SHA1withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("SHA-1withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("SHAwithECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put("SHAwithECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224");
        SIGN_ALGOS_URI.put("SHA-224withECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224");
        SIGN_ALGOS_URI.put("SHA224withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224");
        SIGN_ALGOS_URI.put("SHA-224withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
        SIGN_ALGOS_URI.put("SHA-256withECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
        SIGN_ALGOS_URI.put("SHA256withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
        SIGN_ALGOS_URI.put("SHA-256withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384");
        SIGN_ALGOS_URI.put("SHA-384withECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384");
        SIGN_ALGOS_URI.put("SHA384withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384");
        SIGN_ALGOS_URI.put("SHA-384withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384");
        SIGN_ALGOS_URI.put(AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA, "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512");
        SIGN_ALGOS_URI.put("SHA-512withECDSA", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512");
        SIGN_ALGOS_URI.put("SHA512withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512");
        SIGN_ALGOS_URI.put("SHA-512withECDSAEncryption", "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512");
        MESSAGEDIGEST_ALGOS_URI = new HashMap();
        MESSAGEDIGEST_ALGOS_URI.put("sha1", "http://www.w3.org/2000/09/xmldsig#sha1");
        MESSAGEDIGEST_ALGOS_URI.put("sha-1", "http://www.w3.org/2000/09/xmldsig#sha1");
        MESSAGEDIGEST_ALGOS_URI.put("sha256", "http://www.w3.org/2001/04/xmlenc#sha256");
        MESSAGEDIGEST_ALGOS_URI.put("sha-256", "http://www.w3.org/2001/04/xmlenc#sha256");
        MESSAGEDIGEST_ALGOS_URI.put("sha384", "http://www.w3.org/2001/04/xmldsig-more#sha384");
        MESSAGEDIGEST_ALGOS_URI.put("sha-384", "http://www.w3.org/2001/04/xmldsig-more#sha384");
        MESSAGEDIGEST_ALGOS_URI.put("sha512", "http://www.w3.org/2001/04/xmlenc#sha512");
        MESSAGEDIGEST_ALGOS_URI.put("sha-512", "http://www.w3.org/2001/04/xmlenc#sha512");
    }
}
