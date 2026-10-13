package es.gob.afirma.core.signers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/AOSignInfo.class */
public final class AOSignInfo {
    private String format;
    private String variant = null;
    private String urlSignObject = null;
    private String urlSignedData = null;
    private String b64VerificationCode = null;

    public AOSignInfo(String str) {
        this.format = null;
        this.format = str != null ? str : "Desconocido";
    }

    public String getVariant() {
        return this.variant;
    }

    public void setVariant(String str) {
        this.variant = str;
    }

    public String getUrlSignObject() {
        return this.urlSignObject;
    }

    public void setUrlSignObject(String str) {
        this.urlSignObject = str;
    }

    public String getUrlSignedData() {
        return this.urlSignedData;
    }

    public void setUrlSignedData(String str) {
        this.urlSignedData = str;
    }

    public String getB64VerificationCode() {
        return this.b64VerificationCode;
    }

    public void setB64VerificationCode(String str) {
        this.b64VerificationCode = str;
    }

    public String getFormat() {
        return this.format;
    }
}
