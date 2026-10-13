package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CompleteCertificateRefsImpl.class */
public class CompleteCertificateRefsImpl extends XAdESStructure implements CompleteCertificateRefs {
    private CertRefs certRefs;

    public CompleteCertificateRefsImpl(Document document, XAdESStructure parent, Collection<X509Certificate> caCertificates, String signatureIdPrefix, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        if (caCertificates == null || caCertificates.isEmpty()) {
            throw new IllegalArgumentException("The CA Certificates collection can not be NULL or empty.");
        }
        this.certRefs = new CertRefs(document, this, caCertificates, signatureIdPrefix, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public CompleteCertificateRefsImpl(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    @Override // es.uji.crypto.xades.jxades.security.xml.XAdES.CompleteCertificateRefs
    public CertRefs getCertRefs() {
        Element element;
        if (this.certRefs == null && (element = getChildElementNS("CertRefs")) != null) {
            this.certRefs = new CertRefs(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.certRefs;
    }
}
