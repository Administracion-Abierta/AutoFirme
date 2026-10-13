package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/UnsignedSignatureProperties.class */
public class UnsignedSignatureProperties extends XAdESStructure {
    private CompleteCertificateRefs completeCertificateRefs;
    private CompleteRevocationRefs completeRevocationRefs;
    private Document document;

    public UnsignedSignatureProperties(Document document, UnsignedProperties up, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(document, up, XAdES.Element.UNSIGNED_SIGNATURE_PROPERTIES.getElementName(), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.document = document;
    }

    public UnsignedSignatureProperties(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public CompleteCertificateRefs getCompleteCertificateRefs() {
        Element element;
        if (this.completeCertificateRefs == null && (element = getChildElementNS(XAdESConstants.TAG_COMPLETE_CERTIFICATE_REFS)) != null) {
            this.completeCertificateRefs = new CompleteCertificateRefsImpl(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.completeCertificateRefs;
    }

    public void setCompleteCertificateRefs(Collection<X509Certificate> caCertificates, String signatureIdPrefix) throws GeneralSecurityException {
        this.completeCertificateRefs = getCompleteCertificateRefs();
        if (this.completeCertificateRefs != null) {
            throw new UnsupportedOperationException("The collection of CA Certificates already exists.");
        }
        this.completeCertificateRefs = new CompleteCertificateRefsImpl(this.document, this, caCertificates, signatureIdPrefix, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
    }

    public CompleteRevocationRefs getCompleteRevocationRefs() {
        Element element;
        if (this.completeRevocationRefs == null && (element = getChildElementNS(XAdESConstants.TAG_COMPLETE_REVOCATION_REFS)) != null) {
            this.completeRevocationRefs = new CompleteRevocationRefsImpl(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.completeRevocationRefs;
    }

    public void setSignatureTimeStamp(ArrayList<SignatureTimeStamp> signatureTimeStamp, String tsaURL) {
        for (SignatureTimeStamp sts : signatureTimeStamp) {
            new SignatureTimeStampDetails(this.document, this, sts, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix, tsaURL);
        }
    }
}
