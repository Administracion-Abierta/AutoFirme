package org.apache.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureProperties.class */
public final class DOMSignatureProperties extends DOMStructure implements SignatureProperties {
    private final String id;
    private final List<SignatureProperty> properties;

    public DOMSignatureProperties(List<? extends SignatureProperty> properties, String id) {
        if (properties == null) {
            throw new NullPointerException("properties cannot be null");
        }
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("properties cannot be empty");
        }
        this.properties = Collections.unmodifiableList(new ArrayList(properties));
        int size = this.properties.size();
        for (int i = 0; i < size; i++) {
            if (!(this.properties.get(i) instanceof SignatureProperty)) {
                throw new ClassCastException("properties[" + i + "] is not a valid type");
            }
        }
        this.id = id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMSignatureProperties(Element propsElem) throws MarshalException {
        Attr attr = propsElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            propsElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        List<SignatureProperty> newProperties = new ArrayList<>();
        Node firstChild = propsElem.getFirstChild();
        while (true) {
            Node firstChild2 = firstChild;
            if (firstChild2 != null) {
                if (firstChild2.getNodeType() == 1) {
                    String name = firstChild2.getLocalName();
                    String namespace = firstChild2.getNamespaceURI();
                    if (!Constants._TAG_SIGNATUREPROPERTY.equals(name) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        throw new MarshalException("Invalid element name: " + namespace + ":" + name + ", expected SignatureProperty");
                    }
                    newProperties.add(new DOMSignatureProperty((Element) firstChild2));
                }
                firstChild = firstChild2.getNextSibling();
            } else {
                if (newProperties.isEmpty()) {
                    throw new MarshalException("properties cannot be empty");
                }
                this.properties = Collections.unmodifiableList(newProperties);
                return;
            }
        }
    }

    public List<SignatureProperty> getProperties() {
        return this.properties;
    }

    public String getId() {
        return this.id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element propsElem = DOMUtils.createElement(ownerDoc, Constants._TAG_SIGNATUREPROPERTIES, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(propsElem, "Id", this.id);
        for (SignatureProperty property : this.properties) {
            ((DOMSignatureProperty) property).marshal(propsElem, dsPrefix, context);
        }
        parent.appendChild(propsElem);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureProperties)) {
            return false;
        }
        SignatureProperties osp = (SignatureProperties) o;
        if (this.id == null) {
            zEquals = osp.getId() == null;
        } else {
            zEquals = this.id.equals(osp.getId());
        }
        boolean idsEqual = zEquals;
        return this.properties.equals(osp.getProperties()) && idsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        return (31 * result) + this.properties.hashCode();
    }
}
