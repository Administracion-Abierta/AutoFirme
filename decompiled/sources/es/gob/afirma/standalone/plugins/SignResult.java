package es.gob.afirma.standalone.plugins;

import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/SignResult.class */
public class SignResult {
    private byte[] signature;
    private byte[] certificate;
    private Properties extraData;

    public byte[] getSignature() {
        return this.signature;
    }

    public void setSignature(byte[] bArr) {
        this.signature = bArr;
    }

    public byte[] getCertificate() {
        if (this.certificate != null) {
            return (byte[]) this.certificate.clone();
        }
        return null;
    }

    public void setCertificate(byte[] bArr) {
        this.certificate = bArr != null ? (byte[]) bArr.clone() : null;
    }

    public Properties getExtraData() {
        if (this.extraData != null) {
            return (Properties) this.extraData.clone();
        }
        return null;
    }

    public void setDataFilename(Properties properties) {
        this.extraData = properties != null ? (Properties) properties.clone() : null;
    }
}
