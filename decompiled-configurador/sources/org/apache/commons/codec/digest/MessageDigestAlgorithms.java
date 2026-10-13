package org.apache.commons.codec.digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/digest/MessageDigestAlgorithms.class */
public class MessageDigestAlgorithms {
    public static final String MD2 = "MD2";
    public static final String MD5 = "MD5";
    public static final String SHA_1 = "SHA-1";
    public static final String SHA_224 = "SHA-224";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_384 = "SHA-384";
    public static final String SHA_512 = "SHA-512";
    public static final String SHA_512_224 = "SHA-512/224";
    public static final String SHA_512_256 = "SHA-512/256";
    public static final String SHA3_224 = "SHA3-224";
    public static final String SHA3_256 = "SHA3-256";
    public static final String SHA3_384 = "SHA3-384";
    public static final String SHA3_512 = "SHA3-512";

    public static String[] values() {
        return new String[]{MD2, MD5, "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512", SHA_512_224, SHA_512_256, SHA3_224, "SHA3-256", SHA3_384, SHA3_512};
    }

    private MessageDigestAlgorithms() {
    }
}
