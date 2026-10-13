package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CertDigest.class */
public class CertDigest extends DigestAlgAndValue {
    public CertDigest(Document document, XAdESStructure parent, X509Certificate cert, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) throws GeneralSecurityException {
        super(document, parent, "CertDigest", cert.getEncoded(), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public CertDigest(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }
}
