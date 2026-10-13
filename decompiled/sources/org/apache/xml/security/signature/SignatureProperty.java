package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/SignatureProperty.class */
public class SignatureProperty extends SignatureElementProxy {
    public SignatureProperty(Document doc, String target) {
        this(doc, target, null);
    }

    public SignatureProperty(Document doc, String target, String id) {
        super(doc);
        setTarget(target);
        setId(id);
    }

    public SignatureProperty(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public void setId(String id) {
        if (id != null) {
            setLocalIdAttribute("Id", id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public void setTarget(String target) {
        if (target != null) {
            setLocalAttribute("Target", target);
        }
    }

    public String getTarget() {
        return getLocalAttribute("Target");
    }

    public Node appendChild(Node node) {
        appendSelf(node);
        return node;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREPROPERTY;
    }
}
