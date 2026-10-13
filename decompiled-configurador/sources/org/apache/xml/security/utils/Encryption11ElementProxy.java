package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/Encryption11ElementProxy.class */
public abstract class Encryption11ElementProxy extends ElementProxy {
    protected Encryption11ElementProxy() {
    }

    public Encryption11ElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        setDocument(doc);
        setElement(XMLUtils.createElementInEncryption11Space(doc, getBaseLocalName()));
        String prefix = ElementProxy.getDefaultPrefix(getBaseNamespace());
        if (prefix != null && prefix.length() > 0) {
            getElement().setAttribute("xmlns:" + prefix, getBaseNamespace());
        }
    }

    public Encryption11ElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2009/xmlenc11#";
    }
}
