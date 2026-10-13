package org.apache.xml.security.encryption.params;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/params/KeyDerivationParameters.class */
public abstract class KeyDerivationParameters {
    private final String algorithm;
    private final int keyBitLength;

    protected KeyDerivationParameters(String algorithm, int keyLength) {
        this.algorithm = algorithm;
        this.keyBitLength = keyLength;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public int getKeyBitLength() {
        return this.keyBitLength;
    }

    public int getKeyLength() {
        return (this.keyBitLength / 8) + (this.keyBitLength % 8 == 0 ? 0 : 1);
    }
}
