package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/ObjectContainer.class */
public class ObjectContainer extends SignatureElementProxy {
    public ObjectContainer(Document doc) {
        super(doc);
    }

    public ObjectContainer(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public void setId(String Id) {
        if (Id != null) {
            setLocalIdAttribute("Id", Id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public void setMimeType(String MimeType) {
        if (MimeType != null) {
            setLocalAttribute("MimeType", MimeType);
        }
    }

    public String getMimeType() {
        return getLocalAttribute("MimeType");
    }

    public void setEncoding(String Encoding) {
        if (Encoding != null) {
            setLocalAttribute("Encoding", Encoding);
        }
    }

    public String getEncoding() {
        return getLocalAttribute("Encoding");
    }

    public Node appendChild(Node node) {
        appendSelf(node);
        return node;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_OBJECT;
    }
}
