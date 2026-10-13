package org.apache.xml.security.keys.content.x509;

import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/x509/XMLX509Digest.class */
public class XMLX509Digest extends Signature11ElementProxy implements XMLX509DataContent {
    public XMLX509Digest(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public XMLX509Digest(Document doc, byte[] digestBytes, String algorithmURI) {
        super(doc);
        addBase64Text(digestBytes);
        setLocalAttribute("Algorithm", algorithmURI);
    }

    public XMLX509Digest(Document doc, X509Certificate x509certificate, String algorithmURI) throws XMLSecurityException {
        super(doc);
        addBase64Text(getDigestBytesFromCert(x509certificate, algorithmURI));
        setLocalAttribute("Algorithm", algorithmURI);
    }

    public Attr getAlgorithmAttr() {
        return getElement().getAttributeNodeNS(null, "Algorithm");
    }

    public String getAlgorithm() {
        return getAlgorithmAttr().getNodeValue();
    }

    public byte[] getDigestBytes() throws XMLSecurityException {
        return getBytesFromTextChild();
    }

    public static byte[] getDigestBytesFromCert(X509Certificate cert, String algorithmURI) throws XMLSecurityException {
        String jcaDigestAlgorithm = JCEMapper.translateURItoJCEID(algorithmURI);
        if (jcaDigestAlgorithm == null) {
            Object[] exArgs = {algorithmURI};
            throw new XMLSecurityException("XMLX509Digest.UnknownDigestAlgorithm", exArgs);
        }
        try {
            MessageDigest md = MessageDigest.getInstance(jcaDigestAlgorithm);
            return md.digest(cert.getEncoded());
        } catch (Exception e) {
            Object[] exArgs2 = {jcaDigestAlgorithm};
            throw new XMLSecurityException("XMLX509Digest.FailedDigest", exArgs2);
        }
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_X509DIGEST;
    }
}
