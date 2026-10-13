package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/SPKIData.class */
public class SPKIData extends SignatureElementProxy implements KeyInfoContent {
    public SPKIData(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_SPKIDATA;
    }
}
