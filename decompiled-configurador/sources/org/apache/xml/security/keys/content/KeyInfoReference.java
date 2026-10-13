package org.apache.xml.security.keys.content;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/keys/content/KeyInfoReference.class */
public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent {
    public KeyInfoReference(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public KeyInfoReference(Document doc, String uri) {
        super(doc);
        setLocalAttribute("URI", uri);
    }

    public Attr getURIAttr() {
        return getElement().getAttributeNodeNS(null, "URI");
    }

    public String getURI() {
        return getURIAttr().getNodeValue();
    }

    public void setId(String id) {
        setLocalIdAttribute("Id", id);
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_KEYINFOREFERENCE;
    }
}
