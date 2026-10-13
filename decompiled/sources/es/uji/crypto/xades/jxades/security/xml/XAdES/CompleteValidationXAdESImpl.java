package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.cert.X509Certificate;
import java.util.Collection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CompleteValidationXAdESImpl.class */
public class CompleteValidationXAdESImpl extends TimestampXAdESImpl implements XAdES_C {
    public CompleteValidationXAdESImpl(Document document, Element baseElement, boolean readOnlyMode, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix, String digestMethod) {
        super(document, baseElement, readOnlyMode, xadesPrefix, xadesNamespace, xmlSignaturePrefix, digestMethod);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES_C
    public CompleteCertificateRefs getCompleteCertificateRefs() {
        return (CompleteCertificateRefs) this.data.get(XAdES.Element.COMPLETE_CERTIFICATE_REFS);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES_C
    public void setCompleteCertificateRefs(Collection<X509Certificate> caCertificates) {
        if (this.readOnlyMode) {
            throw new UnsupportedOperationException("Set Method is not allowed. Read-only mode.");
        }
        this.data.put(XAdES.Element.COMPLETE_CERTIFICATE_REFS, caCertificates);
    }

    public CompleteRevocationRefs getCompleteRevocationRefs() {
        return (CompleteRevocationRefs) this.data.get(XAdES.Element.COMPLETE_REVOCATION_REFS);
    }
}
