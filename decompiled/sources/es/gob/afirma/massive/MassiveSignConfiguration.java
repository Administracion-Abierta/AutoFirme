package es.gob.afirma.massive;

import java.security.KeyStore;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/massive/MassiveSignConfiguration.class */
public final class MassiveSignConfiguration {
    private final KeyStore.PrivateKeyEntry keyEntry;
    private MassiveType massiveOperation = MassiveType.SIGN;
    private String algorithm = "SHA512withRSA";
    private String mode = "explicit";
    private String defaultFormat = "CAdES";
    private String signatureFormat = "CAdES";
    private boolean originalFormat = true;
    private Properties extraParams = new Properties();

    public MassiveSignConfiguration(KeyStore.PrivateKeyEntry privateKeyEntry) {
        this.keyEntry = privateKeyEntry;
    }

    public MassiveType getMassiveOperation() {
        return this.massiveOperation;
    }

    public void setMassiveOperation(MassiveType massiveType) {
        this.massiveOperation = massiveType != null ? massiveType : MassiveType.SIGN;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String str) {
        this.algorithm = str != null ? str : "SHA512withRSA";
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String str) {
        this.mode = str != null ? str : "explicit";
    }

    public String getDefaultFormat() {
        return this.defaultFormat;
    }

    public void setDefaultFormat(String str) {
        this.defaultFormat = str != null ? str : "CAdES";
        this.signatureFormat = this.defaultFormat;
    }

    public String getSignatureFormat() {
        return this.signatureFormat;
    }

    public void setSignatureFormat(String str) {
        this.signatureFormat = str != null ? str : this.defaultFormat;
    }

    public boolean isOriginalFormat() {
        return this.originalFormat;
    }

    public void setOriginalFormat(boolean z) {
        this.originalFormat = z;
    }

    public KeyStore.PrivateKeyEntry getKeyEntry() {
        return this.keyEntry;
    }

    public void setExtraParams(Properties properties) {
        if (properties != null) {
            this.extraParams = (Properties) properties.clone();
        } else {
            this.extraParams.clear();
        }
    }

    public Properties getExtraParams() {
        if (this.extraParams != null) {
            return (Properties) this.extraParams.clone();
        }
        return null;
    }
}
