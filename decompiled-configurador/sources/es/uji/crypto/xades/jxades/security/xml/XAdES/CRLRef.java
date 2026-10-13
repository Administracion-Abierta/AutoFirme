package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CRLRef.class */
public class CRLRef extends XAdESStructure {
    private DigestAlgAndValue digestAlgAndValue;
    private CRLIdentifier crlIdentifier;
    private ValidationResult validationResult;

    public CRLRef(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public CRLIdentifier getCRLIdentifier() {
        Element element;
        if (this.crlIdentifier == null && (element = getChildElementNS("CRLIdentifier")) != null) {
            this.crlIdentifier = new CRLIdentifier(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.crlIdentifier;
    }

    public DigestAlgAndValue getDigestAlgAndValue() {
        Element element;
        if (this.digestAlgAndValue == null && (element = getChildElementNS("DigestAlgAndValue")) != null) {
            this.digestAlgAndValue = new DigestAlgAndValue(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.digestAlgAndValue;
    }

    public ValidationResult getValidationResult() {
        Element element;
        if (this.validationResult == null && (element = getChildElementNS("ValidationResult")) != null) {
            this.validationResult = new ValidationResult(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.validationResult;
    }
}
