package org.apache.xml.security.keys.content.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/x509/XMLX509Certificate.class */
public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent {
    public static final String JCA_CERT_ID = "X.509";

    public XMLX509Certificate(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public XMLX509Certificate(Document doc, byte[] certificateBytes) {
        super(doc);
        addBase64Text(certificateBytes);
    }

    public XMLX509Certificate(Document doc, X509Certificate x509certificate) throws XMLSecurityException {
        super(doc);
        try {
            addBase64Text(x509certificate.getEncoded());
        } catch (CertificateEncodingException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    public byte[] getCertificateBytes() throws XMLSecurityException {
        return getBytesFromTextChild();
    }

    public X509Certificate getX509Certificate() throws XMLSecurityException {
        byte[] certbytes = getCertificateBytes();
        try {
            InputStream is = new ByteArrayInputStream(certbytes);
            Throwable th = null;
            try {
                try {
                    CertificateFactory certFact = CertificateFactory.getInstance(JCA_CERT_ID);
                    X509Certificate x509Certificate = (X509Certificate) certFact.generateCertificate(is);
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        is.close();
                    }
                    return x509Certificate;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (th != null) {
                    try {
                        is.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    is.close();
                }
                throw th4;
            }
        } catch (IOException | CertificateException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    public PublicKey getPublicKey() throws XMLSecurityException, IOException {
        X509Certificate cert = getX509Certificate();
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509Certificate)) {
            return false;
        }
        XMLX509Certificate other = (XMLX509Certificate) obj;
        try {
            return Arrays.equals(other.getCertificateBytes(), getCertificateBytes());
        } catch (XMLSecurityException e) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = getCertificateBytes();
            for (byte element : bytes) {
                result = (31 * result) + element;
            }
        } catch (XMLSecurityException e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
        return result;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "X509Certificate";
    }
}
