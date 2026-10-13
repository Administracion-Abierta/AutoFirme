package es.uji.crypto.xades.jxades.security.xml.XAdES;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/ExplicitPolicyXAdESImpl.class */
public class ExplicitPolicyXAdESImpl extends BasicXAdESImpl implements XAdES_EPES {
    public ExplicitPolicyXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        super(document, baseElement, readOnlyMode, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithExplicitPolicy
    public void setSignaturePolicyIdentifier(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        if (signaturePolicyIdentifier != null) {
            this.data.put(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER, signaturePolicyIdentifier);
        } else {
            this.data.remove(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER);
        }
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithExplicitPolicy
    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return (SignaturePolicyIdentifier) this.data.get(XAdES.Element.SIGNATURE_POLICY_IDENTIFIER);
    }
}
