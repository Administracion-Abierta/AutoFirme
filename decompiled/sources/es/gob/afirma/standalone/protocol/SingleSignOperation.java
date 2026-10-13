package es.gob.afirma.standalone.protocol;

import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/SingleSignOperation.class */
public class SingleSignOperation {
    private String docId;
    private Operation cryptoOperation;
    private byte[] data;
    private String format;
    private String algorithm;
    private Properties extraParams;

    public String getDocId() {
        return this.docId;
    }

    public void setDocId(String str) {
        this.docId = str;
    }

    public Operation getCryptoOperation() {
        return this.cryptoOperation;
    }

    public void setCryptoOperation(Operation operation) {
        this.cryptoOperation = operation;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String str) {
        this.format = str;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String str) {
        this.algorithm = str;
    }

    public Properties getExtraParams() {
        if (this.extraParams != null) {
            return (Properties) this.extraParams.clone();
        }
        return null;
    }

    public void setExtraParams(Properties properties) {
        this.extraParams = properties != null ? (Properties) properties.clone() : null;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/SingleSignOperation$Operation.class */
    public enum Operation {
        SIGN,
        COSIGN,
        COUNTERSIGN;

        public static Operation getOperation(String str) {
            if ("SIGN".equalsIgnoreCase(str)) {
                return SIGN;
            }
            if ("COSIGN".equalsIgnoreCase(str)) {
                return COSIGN;
            }
            if ("COUNTERSIGN".equalsIgnoreCase(str)) {
                return COUNTERSIGN;
            }
            return null;
        }
    }
}
