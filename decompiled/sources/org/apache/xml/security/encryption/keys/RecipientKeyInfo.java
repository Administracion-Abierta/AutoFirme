package org.apache.xml.security.encryption.keys;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/encryption/keys/RecipientKeyInfo.class */
public class RecipientKeyInfo extends KeyInfoEnc {
    public RecipientKeyInfo(Document doc) {
        super(doc);
    }

    public RecipientKeyInfo(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.keys.KeyInfo, org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/04/xmlenc#";
    }

    @Override // org.apache.xml.security.keys.KeyInfo, org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return EncryptionConstants._TAG_RECIPIENTKEYINFO;
    }
}
