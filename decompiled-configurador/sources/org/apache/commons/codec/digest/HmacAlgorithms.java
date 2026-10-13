package org.apache.commons.codec.digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/digest/HmacAlgorithms.class */
public enum HmacAlgorithms {
    HMAC_MD5("HmacMD5"),
    HMAC_SHA_1("HmacSHA1"),
    HMAC_SHA_224("HmacSHA224"),
    HMAC_SHA_256("HmacSHA256"),
    HMAC_SHA_384("HmacSHA384"),
    HMAC_SHA_512("HmacSHA512");

    private final String name;

    HmacAlgorithms(String algorithm) {
        this.name = algorithm;
    }

    public String getName() {
        return this.name;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }
}
