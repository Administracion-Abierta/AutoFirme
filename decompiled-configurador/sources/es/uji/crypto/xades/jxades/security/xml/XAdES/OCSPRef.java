package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/OCSPRef.class */
public class OCSPRef extends XAdESStructure {
    private OCSPIdentifier ocspIdentifier;
    private DigestAlgAndValue digestAlgAndValue;
    private ValidationResult validationResult;

    public OCSPRef(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public OCSPIdentifier getOCSPIdentifier() {
        Element element;
        if (this.ocspIdentifier == null && (element = getChildElementNS("OCSPIdentifier")) != null) {
            this.ocspIdentifier = new OCSPIdentifier(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.ocspIdentifier;
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
