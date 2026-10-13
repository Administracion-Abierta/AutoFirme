package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CertRefs.class */
public class CertRefs extends XAdESStructure {
    private List<Cert> certs;

    public CertRefs(Document document, XAdESStructure parent, Collection<X509Certificate> certificates, String signatureIdPrefix, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, "CertRefs", xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        if (certificates == null || certificates.isEmpty()) {
            throw new IllegalArgumentException("The certificates collection can not be NULL or empty.");
        }
        getElement();
        if (signatureIdPrefix != null) {
            setAttributeNS(null, "Id", signatureIdPrefix + "-CertRefs");
        }
        this.certs = new ArrayList(certificates.size());
        for (X509Certificate certificate : certificates) {
            Cert cert = new Cert(document, this, certificate, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
            this.certs.add(cert);
        }
    }

    public CertRefs(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public List<Cert> getCerts() {
        if (this.certs == null) {
            List<Element> elements = getChildElementsNS("Cert");
            if (elements != null && elements.size() > 0) {
                this.certs = new ArrayList(elements.size());
                for (Element element : elements) {
                    this.certs.add(new Cert(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix));
                }
            } else {
                this.certs = Collections.emptyList();
            }
        }
        return this.certs;
    }
}
