package org.apache.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperty;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureProperty.class */
public final class DOMSignatureProperty extends DOMStructure implements SignatureProperty {
    private final String id;
    private final String target;
    private final List<XMLStructure> content;

    public DOMSignatureProperty(List<? extends XMLStructure> content, String target, String id) {
        if (target == null) {
            throw new NullPointerException("target cannot be null");
        }
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        this.content = Collections.unmodifiableList(new ArrayList(content));
        int size = this.content.size();
        for (int i = 0; i < size; i++) {
            if (!(this.content.get(i) instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid type");
            }
        }
        this.target = target;
        this.id = id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMSignatureProperty(Element propElem) throws MarshalException {
        this.target = DOMUtils.getAttributeValue(propElem, "Target");
        if (this.target == null) {
            throw new MarshalException("target cannot be null");
        }
        Attr attr = propElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            propElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        List<XMLStructure> newContent = new ArrayList<>();
        Node firstChild = propElem.getFirstChild();
        while (true) {
            Node firstChild2 = firstChild;
            if (firstChild2 == null) {
                break;
            }
            newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild2));
            firstChild = firstChild2.getNextSibling();
        }
        if (newContent.isEmpty()) {
            throw new MarshalException("content cannot be empty");
        }
        this.content = Collections.unmodifiableList(newContent);
    }

    public List<XMLStructure> getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public String getTarget() {
        return this.target;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element propElem = DOMUtils.createElement(ownerDoc, Constants._TAG_SIGNATUREPROPERTY, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(propElem, "Id", this.id);
        DOMUtils.setAttribute(propElem, "Target", this.target);
        Iterator<XMLStructure> it = this.content.iterator();
        while (it.hasNext()) {
            DOMUtils.appendChild(propElem, ((XMLStructure) it.next()).getNode());
        }
        parent.appendChild(propElem);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureProperty)) {
            return false;
        }
        SignatureProperty osp = (SignatureProperty) o;
        if (this.id == null) {
            zEquals = osp.getId() == null;
        } else {
            zEquals = this.id.equals(osp.getId());
        }
        boolean idsEqual = zEquals;
        List<XMLStructure> ospContent = osp.getContent();
        return equalsContent(this.content, ospContent) && this.target.equals(osp.getTarget()) && idsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        return (31 * ((31 * result) + this.target.hashCode())) + this.content.hashCode();
    }
}
