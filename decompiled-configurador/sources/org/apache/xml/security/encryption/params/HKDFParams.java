package org.apache.xml.security.encryption.params;

import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/params/HKDFParams.class */
public class HKDFParams extends KeyDerivationParameters {
    private String hmacHashAlgorithm;
    private byte[] salt;
    private byte[] info;

    protected HKDFParams(int keyBitLength, String hmacHashAlgorithm) {
        super(EncryptionConstants.ALGO_ID_KEYDERIVATION_HKDF, keyBitLength);
        this.hmacHashAlgorithm = hmacHashAlgorithm;
    }

    public String getHmacHashAlgorithm() {
        return this.hmacHashAlgorithm == null ? "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256" : this.hmacHashAlgorithm;
    }

    public void setHmacHashAlgorithm(String hmacHashAlgorithm) {
        this.hmacHashAlgorithm = hmacHashAlgorithm;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getInfo() {
        return this.info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }

    public static Builder createBuilder(int keyBitLength, String hmacHashAlgorithm) {
        return new Builder(keyBitLength, hmacHashAlgorithm);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/params/HKDFParams$Builder.class */
    public static class Builder {
        private final int keyBitLength;
        private final String hmacHashAlgorithm;
        private byte[] salt;
        private byte[] info;

        protected Builder(int keyBitLength, String hmacHashAlgorithm) {
            this.keyBitLength = keyBitLength;
            this.hmacHashAlgorithm = hmacHashAlgorithm;
        }

        public Builder salt(byte[] salt) {
            this.salt = salt;
            return this;
        }

        public Builder info(byte[] info) {
            this.info = info;
            return this;
        }

        public HKDFParams build() {
            HKDFParams params = new HKDFParams(this.keyBitLength, this.hmacHashAlgorithm);
            params.setSalt(this.salt);
            params.setInfo(this.info);
            return params;
        }
    }
}
