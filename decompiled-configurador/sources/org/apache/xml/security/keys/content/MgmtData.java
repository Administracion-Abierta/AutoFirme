package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/MgmtData.class */
public class MgmtData extends SignatureElementProxy implements KeyInfoContent {
    public MgmtData(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public MgmtData(Document doc, String mgmtData) {
        super(doc);
        addText(mgmtData);
    }

    public String getMgmtData() {
        return getTextFromTextChild();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_MGMTDATA;
    }
}
