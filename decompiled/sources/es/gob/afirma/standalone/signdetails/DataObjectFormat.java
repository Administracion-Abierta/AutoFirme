package es.gob.afirma.standalone.signdetails;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/DataObjectFormat.class */
public class DataObjectFormat {
    private String identifier;
    private String description;
    private String mimeType;
    private String encoding;

    public DataObjectFormat() {
    }

    public DataObjectFormat(String str) {
        this.mimeType = str;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String str) {
        this.identifier = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
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
}
