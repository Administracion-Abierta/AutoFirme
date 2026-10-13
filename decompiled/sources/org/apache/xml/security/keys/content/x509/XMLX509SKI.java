package org.apache.xml.security.keys.content.x509;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/x509/XMLX509SKI.class */
public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLX509SKI.class);
    public static final String SKI_OID = "2.5.29.14";

    public XMLX509SKI(Document doc, byte[] skiBytes) {
        super(doc);
        addBase64Text(skiBytes);
    }

    public XMLX509SKI(Document doc, X509Certificate x509certificate) throws XMLSecurityException {
        super(doc);
        addBase64Text(getSKIBytesFromCert(x509certificate));
    }

    public XMLX509SKI(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public byte[] getSKIBytes() throws XMLSecurityException {
        return getBytesFromTextChild();
    }

    public static byte[] getSKIBytesFromCert(X509Certificate cert) throws XMLSecurityException {
        if (cert.getVersion() < 3) {
            Object[] exArgs = {Integer.valueOf(cert.getVersion())};
            throw new XMLSecurityException("certificate.noSki.lowVersion", exArgs);
        }
        byte[] extensionValue = cert.getExtensionValue(SKI_OID);
        if (extensionValue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
        }
        byte[] skidValue = new byte[extensionValue.length - 4];
        System.arraycopy(extensionValue, 4, skidValue, 0, skidValue.length);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Base64 of SKI is " + XMLUtils.encodeToString(skidValue));
        }
        return skidValue;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509SKI)) {
            return false;
        }
        XMLX509SKI other = (XMLX509SKI) obj;
        try {
            return Arrays.equals(other.getSKIBytes(), getSKIBytes());
        } catch (XMLSecurityException e) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = getSKIBytes();
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
        return Constants._TAG_X509SKI;
    }
}
