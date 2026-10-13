package es.gob.afirma.signers.ooxml;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/Relationship.class */
final class Relationship {
    private String id;
    private String type;
    private String target;

    Relationship(String str, String str2, String str3) {
        this.id = null;
        this.type = null;
        this.target = null;
        this.id = str;
        this.type = str2;
        this.target = str3;
    }

    String getId() {
        return this.id;
    }

    String getType() {
        return this.type;
    }

    String getTarget() {
        return this.target;
    }

    public String toString() {
        return "<Relationship Id=\"" + this.id + "\" Type=\"" + this.type + "\" Target=\"" + this.target + "\"/>";
    }
}
