package es.gob.afirma.standalone.plugins;

import java.util.Map;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/SignOperation.class */
public class SignOperation {
    private Operation cryptoOperation;
    private byte[] data;
    private String format;
    private String algorithm;
    private Properties extraParams;
    private Map<String, String> anotherParams;

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

    public Map<String, String> getAnotherParams() {
        return this.anotherParams;
    }

    public void setAnotherParams(Map<String, String> map) {
        this.anotherParams = map;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/SignOperation$Operation.class */
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
