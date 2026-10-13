package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/KeyName.class */
public class KeyName extends SignatureElementProxy implements KeyInfoContent {
    public KeyName(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public KeyName(Document doc, String keyName) {
        super(doc);
        addText(keyName);
    }

    public String getKeyName() {
        return getTextFromTextChild();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_KEYNAME;
    }
}
