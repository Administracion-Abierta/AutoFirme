package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/SignatureElementProxy.class */
public abstract class SignatureElementProxy extends ElementProxy {
    protected SignatureElementProxy() {
    }

    public SignatureElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        setDocument(doc);
        setElement(XMLUtils.createElementInSignatureSpace(doc, getBaseLocalName()));
    }

    public SignatureElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
}
