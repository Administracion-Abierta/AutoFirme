package org.apache.xml.security.keys.content.x509;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/x509/XMLX509CRL.class */
public class XMLX509CRL extends SignatureElementProxy implements XMLX509DataContent {
    public XMLX509CRL(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public XMLX509CRL(Document doc, byte[] crlBytes) {
        super(doc);
        addBase64Text(crlBytes);
    }

    public byte[] getCRLBytes() throws XMLSecurityException {
        return getBytesFromTextChild();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_X509CRL;
    }
}
