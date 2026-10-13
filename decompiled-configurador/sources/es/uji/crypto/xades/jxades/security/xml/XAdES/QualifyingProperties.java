package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/QualifyingProperties.class */
public class QualifyingProperties extends XAdESStructure {
    private String signatureIdPrefix;
    private SignedProperties signedProperties;
    private UnsignedProperties unsignedProperties;
    private Document document;

    public QualifyingProperties(Document document, Node node, String signatureIdPrefix, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document, node, XAdESConstants.TAG_QUALIFYING_PROPERTIES, signatureIdPrefix, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    private QualifyingProperties(Document document, Node node, String elementName, String signatureIdPrefix, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document.createElementNS(xadesNamespace, elementName), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.document = document;
        this.signatureIdPrefix = signatureIdPrefix;
        Element element = getElement();
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xmlSignaturePrefix, "http://www.w3.org/2000/09/xmldsig#");
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xadesPrefix, xadesNamespace);
        element.setPrefix(xadesPrefix);
        String target = "#" + signatureIdPrefix + "-Signature";
        setAttributeNS(null, "Target", target);
        String id = signatureIdPrefix + "-" + elementName;
        setAttributeNS(null, "Id", id);
    }

    public QualifyingProperties(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public SignedProperties getSignedProperties() {
        if (this.signedProperties == null) {
            this.signedProperties = new SignedProperties(this.document, this, this.signatureIdPrefix, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.signedProperties;
    }

    public UnsignedProperties getUnsignedProperties() {
        return this.unsignedProperties;
    }
}
