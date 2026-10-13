package org.apache.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMKeyInfo.class */
public final class DOMKeyInfo extends DOMStructure implements KeyInfo {
    private final String id;
    private final List<XMLStructure> keyInfoTypes;

    public static List<XMLStructure> getContent(KeyInfo ki) {
        return ki.getContent();
    }

    public DOMKeyInfo(List<? extends XMLStructure> content, String id) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        this.keyInfoTypes = Collections.unmodifiableList(new ArrayList(content));
        if (this.keyInfoTypes.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        int size = this.keyInfoTypes.size();
        for (int i = 0; i < size; i++) {
            if (!(this.keyInfoTypes.get(i) instanceof XMLStructure)) {
                throw new ClassCastException("content[" + i + "] is not a valid KeyInfo type");
            }
        }
        this.id = id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMKeyInfo(Element kiElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        Attr attr = kiElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            kiElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        List<XMLStructure> content = new ArrayList<>();
        Node firstChild = kiElem.getFirstChild();
        if (firstChild == null) {
            throw new MarshalException("KeyInfo must contain at least one type");
        }
        while (firstChild != null) {
            if (firstChild.getNodeType() == 1) {
                Element childElem = (Element) firstChild;
                String localName = childElem.getLocalName();
                String namespace = childElem.getNamespaceURI();
                if (Constants._TAG_X509DATA.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    content.add(new DOMX509Data(childElem));
                } else if (Constants._TAG_KEYNAME.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    content.add(new DOMKeyName(childElem));
                } else if (Constants._TAG_KEYVALUE.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    content.add(DOMKeyValue.unmarshal(childElem));
                } else if (Constants._TAG_RETRIEVALMETHOD.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    content.add(new DOMRetrievalMethod(childElem, context, provider));
                } else if (Constants._TAG_PGPDATA.equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    content.add(new DOMPGPData(childElem));
                } else {
                    content.add(new javax.xml.crypto.dom.DOMStructure(childElem));
                }
            }
            firstChild = firstChild.getNextSibling();
        }
        this.keyInfoTypes = Collections.unmodifiableList(content);
    }

    public String getId() {
        return this.id;
    }

    public List<XMLStructure> getContent() {
        return this.keyInfoTypes;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public void marshal(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        if (!(parent instanceof javax.xml.crypto.dom.DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }
        Node pNode = ((javax.xml.crypto.dom.DOMStructure) parent).getNode();
        String dsPrefix = DOMUtils.getSignaturePrefix(context);
        Element kiElem = DOMUtils.createElement(DOMUtils.getOwnerDocument(pNode), Constants._TAG_KEYINFO, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        if (dsPrefix == null || dsPrefix.length() == 0) {
            kiElem.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "http://www.w3.org/2000/09/xmldsig#");
        } else {
            kiElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + dsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        Node nextSibling = null;
        if (context instanceof DOMSignContext) {
            nextSibling = ((DOMSignContext) context).getNextSibling();
        }
        marshal(pNode, kiElem, nextSibling, dsPrefix, (DOMCryptoContext) context);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        marshal(parent, null, dsPrefix, context);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public void marshal(Node parent, Node nextSibling, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element kiElem = DOMUtils.createElement(ownerDoc, Constants._TAG_KEYINFO, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        marshal(parent, kiElem, nextSibling, dsPrefix, context);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private void marshal(Node parent, Element kiElem, Node nextSibling, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Iterator<XMLStructure> it = this.keyInfoTypes.iterator();
        while (it.hasNext()) {
            javax.xml.crypto.dom.DOMStructure dOMStructure = (XMLStructure) it.next();
            if (dOMStructure instanceof DOMStructure) {
                ((DOMStructure) dOMStructure).marshal(kiElem, dsPrefix, context);
            } else {
                DOMUtils.appendChild(kiElem, dOMStructure.getNode());
            }
        }
        DOMUtils.setAttributeID(kiElem, "Id", this.id);
        parent.insertBefore(kiElem, nextSibling);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyInfo)) {
            return false;
        }
        KeyInfo oki = (KeyInfo) o;
        if (this.id == null) {
            zEquals = oki.getId() == null;
        } else {
            zEquals = this.id.equals(oki.getId());
        }
        boolean idsEqual = zEquals;
        return this.keyInfoTypes.equals(oki.getContent()) && idsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        return (31 * result) + this.keyInfoTypes.hashCode();
    }
}
