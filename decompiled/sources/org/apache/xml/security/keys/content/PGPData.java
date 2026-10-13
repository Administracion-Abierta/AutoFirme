package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/content/PGPData.class */
public class PGPData extends SignatureElementProxy implements KeyInfoContent {
    public PGPData(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_PGPDATA;
    }
}
