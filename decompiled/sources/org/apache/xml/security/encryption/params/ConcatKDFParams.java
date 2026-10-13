package org.apache.xml.security.encryption.params;

import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/params/ConcatKDFParams.class */
public class ConcatKDFParams extends KeyDerivationParameters {
    private String digestAlgorithm;
    private String algorithmID;
    private String partyUInfo;
    private String partyVInfo;
    private String suppPubInfo;
    private String suppPrivInfo;

    protected ConcatKDFParams(int keyBitLength, String digestAlgorithm) {
        super(EncryptionConstants.ALGO_ID_KEYDERIVATION_CONCATKDF, keyBitLength);
        this.digestAlgorithm = digestAlgorithm;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm == null ? "http://www.w3.org/2001/04/xmlenc#sha256" : this.digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public String getAlgorithmID() {
        return this.algorithmID;
    }

    public void setAlgorithmID(String algorithmID) {
        this.algorithmID = algorithmID;
    }

    public String getPartyUInfo() {
        return this.partyUInfo;
    }

    public void setPartyUInfo(String partyUInfo) {
        this.partyUInfo = partyUInfo;
    }

    public String getPartyVInfo() {
        return this.partyVInfo;
    }

    public void setPartyVInfo(String partyVInfo) {
        this.partyVInfo = partyVInfo;
    }

    public String getSuppPubInfo() {
        return this.suppPubInfo;
    }

    public void setSuppPubInfo(String suppPubInfo) {
        this.suppPubInfo = suppPubInfo;
    }

    public String getSuppPrivInfo() {
        return this.suppPrivInfo;
    }

    public void setSuppPrivInfo(String suppPrivInfo) {
        this.suppPrivInfo = suppPrivInfo;
    }

    public static Builder createBuilder(int keyBitLength, String digestAlgorithm) {
        return new Builder(keyBitLength, digestAlgorithm);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/params/ConcatKDFParams$Builder.class */
    public static class Builder {
        private int keyBitLength;
        private String digestAlgorithm;
        private String algorithmID;
        private String partyUInfo;
        private String partyVInfo;
        private String suppPubInfo;
        private String suppPrivInfo;

        protected Builder(int keyBitLength, String digestAlgorithm) {
            this.keyBitLength = keyBitLength;
            this.digestAlgorithm = digestAlgorithm;
        }

        public Builder algorithmID(String algorithmID) {
            this.algorithmID = algorithmID;
            return this;
        }

        public Builder partyUInfo(String partyUInfo) {
            this.partyUInfo = partyUInfo;
            return this;
        }

        public Builder partyVInfo(String partyVInfo) {
            this.partyVInfo = partyVInfo;
            return this;
        }

        public Builder suppPubInfo(String suppPubInfo) {
            this.suppPubInfo = suppPubInfo;
            return this;
        }

        public Builder suppPrivInfo(String suppPrivInfo) {
            this.suppPrivInfo = suppPrivInfo;
            return this;
        }

        public ConcatKDFParams build() {
            ConcatKDFParams params = new ConcatKDFParams(this.keyBitLength, this.digestAlgorithm);
            params.setAlgorithmID(this.algorithmID);
            params.setPartyUInfo(this.partyUInfo);
            params.setPartyVInfo(this.partyVInfo);
            params.setSuppPubInfo(this.suppPubInfo);
            params.setSuppPrivInfo(this.suppPrivInfo);
            return params;
        }
    }
}
