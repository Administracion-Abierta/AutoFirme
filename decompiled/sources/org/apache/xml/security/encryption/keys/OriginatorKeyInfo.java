package org.apache.xml.security.encryption.keys;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/OriginatorKeyInfo.class */
public class OriginatorKeyInfo extends KeyInfoEnc {
    public OriginatorKeyInfo(Document doc) {
        super(doc);
    }

    public OriginatorKeyInfo(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.keys.KeyInfo, org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/04/xmlenc#";
    }

    @Override // org.apache.xml.security.keys.KeyInfo, org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_ORIGINATORKEYINFO;
    }
}
