package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/Cert.class */
public class Cert extends XAdESStructure {
    private CertDigest certDigest;
    private IssuerSerial issuerSerial;

    public Cert(Document document, XAdESStructure parent, X509Certificate cert, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, "Cert", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.certDigest = new CertDigest(document, this, cert, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.issuerSerial = new IssuerSerial(document, this, cert, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public Cert(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public CertDigest getCertDigest() {
        Element element;
        if (this.certDigest == null && (element = getChildElementNS("CertDigest")) != null) {
            this.certDigest = new CertDigest(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.certDigest;
    }

    public IssuerSerial getIssuerSerial() {
        Element element;
        if (this.issuerSerial == null && (element = getChildElementNS("IssuerSerial")) != null) {
            this.issuerSerial = new IssuerSerial(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix);
        }
        return this.issuerSerial;
    }
}
