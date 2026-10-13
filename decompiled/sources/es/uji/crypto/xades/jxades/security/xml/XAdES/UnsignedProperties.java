package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/UnsignedProperties.class */
public class UnsignedProperties extends XAdESStructure {
    private UnsignedSignatureProperties unsignedSignatureProperties;
    private Document document;

    public UnsignedProperties(Document document, QualifyingProperties qp, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, qp, XAdESConstants.TAG_UNSIGNED_PROPERTIES, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.document = document;
    }

    public UnsignedProperties(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public UnsignedSignatureProperties getUnsignedSignatureProperties() {
        if (this.unsignedSignatureProperties == null) {
            this.unsignedSignatureProperties = new UnsignedSignatureProperties(this.document, this, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.unsignedSignatureProperties;
    }
}
