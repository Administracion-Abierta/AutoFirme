package org.apache.xml.security.utils;

import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/Signature11ElementProxy.class */
public abstract class Signature11ElementProxy extends ElementProxy {
    protected Signature11ElementProxy() {
    }

    public Signature11ElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        setDocument(doc);
        setElement(XMLUtils.createElementInSignature11Space(doc, getBaseLocalName()));
        String prefix = ElementProxy.getDefaultPrefix(getBaseNamespace());
        if (prefix == null || prefix.length() == 0) {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, getBaseNamespace());
        } else {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, getBaseNamespace());
        }
    }

    public Signature11ElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2009/xmldsig11#";
    }
}
