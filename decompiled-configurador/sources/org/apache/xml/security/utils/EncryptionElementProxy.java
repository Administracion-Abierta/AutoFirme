package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/EncryptionElementProxy.class */
public abstract class EncryptionElementProxy extends ElementProxy {
    protected EncryptionElementProxy() {
    }

    public EncryptionElementProxy(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document is null");
        }
        setDocument(doc);
        setElement(XMLUtils.createElementInEncryptionSpace(doc, getBaseLocalName()));
        String prefix = ElementProxy.getDefaultPrefix(getBaseNamespace());
        if (prefix != null && !prefix.isEmpty()) {
            getElement().setAttribute("xmlns:" + prefix, getBaseNamespace());
        }
    }

    public EncryptionElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/04/xmlenc#";
    }
}
