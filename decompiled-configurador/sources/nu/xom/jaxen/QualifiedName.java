package nu.xom.jaxen;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/QualifiedName.class */
class QualifiedName implements Serializable {
    private static final long serialVersionUID = 2734958615642751535L;
    private String namespaceURI;
    private String localName;

    QualifiedName(String str, String str2) {
        this.namespaceURI = str == null ? "" : str;
        this.localName = str2;
    }

    public int hashCode() {
        return this.localName.hashCode() ^ this.namespaceURI.hashCode();
    }

    public boolean equals(Object obj) {
        QualifiedName qualifiedName = (QualifiedName) obj;
        return this.namespaceURI.equals(qualifiedName.namespaceURI) && qualifiedName.localName.equals(this.localName);
    }

    String getClarkForm() {
        return "".equals(this.namespaceURI) ? this.localName : new StringBuffer().append("{").append(this.namespaceURI).append("}").append(":").append(this.localName).toString();
    }
}
