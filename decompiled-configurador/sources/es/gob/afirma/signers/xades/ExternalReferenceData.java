package es.gob.afirma.signers.xades;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/ExternalReferenceData.class */
public class ExternalReferenceData {
    private final String uri;
    private final byte[] messageDigest;
    private String id;
    private String mimeType;
    private String encoding;
    private String oid;

    public ExternalReferenceData(String str, byte[] bArr) {
        this.uri = str;
        this.messageDigest = bArr != null ? (byte[]) bArr.clone() : null;
    }

    public String getUri() {
        return this.uri;
    }

    public byte[] getMessageDigest() {
        if (this.messageDigest != null) {
            return (byte[]) this.messageDigest.clone();
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String str) {
        this.mimeType = str;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    public String getOid() {
        return this.oid;
    }

    public void setOid(String str) {
        this.oid = str;
    }
}
