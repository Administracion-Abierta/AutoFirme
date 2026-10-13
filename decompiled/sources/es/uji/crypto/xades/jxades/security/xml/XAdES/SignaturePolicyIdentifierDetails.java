package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/SignaturePolicyIdentifierDetails.class */
public class SignaturePolicyIdentifierDetails extends XAdESStructure {
    public SignaturePolicyIdentifierDetails(Document document, SignedSignatureProperties signedSignatureProperties, SignaturePolicyIdentifier signaturePolicyIdentifier, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, signedSignatureProperties, XAdESConstants.TAG_SIGNATURE_POLICY_IDENTIFIER, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        if (signaturePolicyIdentifier.isImplied()) {
            Element implied = createElement("SignaturePolicyImplied");
            getNode().appendChild(implied);
            return;
        }
        Element signaturePolicyId = createElement("SignaturePolicyId");
        Element identifier = createElement("Identifier");
        identifier.appendChild(getDocument().createTextNode(signaturePolicyIdentifier.getIdentifier()));
        Element description = createElement("Description");
        description.appendChild(getDocument().createTextNode(signaturePolicyIdentifier.getDescription()));
        Element sigPolicyId = createElement("SigPolicyId");
        sigPolicyId.appendChild(identifier);
        sigPolicyId.appendChild(description);
        signaturePolicyId.appendChild(sigPolicyId);
        Element digestMethod = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, "DigestMethod");
        digestMethod.setAttributeNS(xmlSignaturePrefix, "Algorithm", signaturePolicyIdentifier.getHashAlgorithm());
        Element digestValue = createElementNS("http://www.w3.org/2000/09/xmldsig#", xmlSignaturePrefix, "DigestValue");
        digestValue.setTextContent(signaturePolicyIdentifier.getHashBase64());
        Element sigPolicyHash = createElement("SigPolicyHash");
        sigPolicyHash.appendChild(digestMethod);
        sigPolicyHash.appendChild(digestValue);
        signaturePolicyId.appendChild(sigPolicyHash);
        if (signaturePolicyIdentifier.getQualifier() != null) {
            Element spURI = createElement(XAdESConstants.TAG_SPURI);
            spURI.setTextContent(signaturePolicyIdentifier.getQualifier());
            Element sigPolicyQualifier = createElement("SigPolicyQualifier");
            sigPolicyQualifier.appendChild(spURI);
            Element sigPolicyQualifiers = createElement("SigPolicyQualifiers");
            sigPolicyQualifiers.appendChild(sigPolicyQualifier);
            signaturePolicyId.appendChild(sigPolicyQualifiers);
        }
        getNode().appendChild(signaturePolicyId);
    }
}
