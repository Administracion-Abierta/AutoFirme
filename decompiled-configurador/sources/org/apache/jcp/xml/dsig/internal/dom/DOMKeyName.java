package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMKeyName.class */
public final class DOMKeyName extends DOMStructure implements KeyName {
    private final String name;

    public DOMKeyName(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.name = name;
    }

    public DOMKeyName(Element knElem) {
        this.name = knElem.getFirstChild().getNodeValue();
    }

    public String getName() {
        return this.name;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element knElem = DOMUtils.createElement(ownerDoc, Constants._TAG_KEYNAME, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        knElem.appendChild(ownerDoc.createTextNode(this.name));
        parent.appendChild(knElem);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof KeyName)) {
            return false;
        }
        KeyName okn = (KeyName) obj;
        return this.name.equals(okn.getName());
    }

    public int hashCode() {
        int result = (31 * 17) + this.name.hashCode();
        return result;
    }
}
