package es.uji.crypto.xades.jxades.security.xml.XAdES;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/DataObjectFormatImpl.class */
public class DataObjectFormatImpl implements DataObjectFormat {
    private String description;
    private ObjectIdentifier objectIdentifier;
    private String mimeType;
    private String encoding;
    private String objectReference;

    public DataObjectFormatImpl() {
    }

    public DataObjectFormatImpl(String description, ObjectIdentifier objectIdentifier, String mimeType, String encoding, String objectReference) {
        this.description = description;
        this.objectIdentifier = objectIdentifier;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.objectReference = objectReference;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat
    public ObjectIdentifier getObjectIdentifier() {
        return this.objectIdentifier;
    }

    public void setObjectIdentifier(ObjectIdentifier objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat
    public String getObjectReference() {
        return this.objectReference;
    }

    public void setObjectReference(String objectReference) {
        this.objectReference = objectReference;
    }
}
