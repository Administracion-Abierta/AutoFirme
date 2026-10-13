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
import javax.xml.crypto.dsig.XMLObject;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXMLObject.class */
public final class DOMXMLObject extends DOMStructure implements XMLObject {
    private final String id;
    private final String mimeType;
    private final String encoding;
    private final List<XMLStructure> content;
    private Element objectElem;

    public DOMXMLObject(List<? extends XMLStructure> content, String id, String mimeType, String encoding) {
        if (content == null || content.isEmpty()) {
            this.content = Collections.emptyList();
        } else {
            this.content = Collections.unmodifiableList(new ArrayList(content));
            int size = this.content.size();
            for (int i = 0; i < size; i++) {
                if (!(this.content.get(i) instanceof XMLStructure)) {
                    throw new ClassCastException("content[" + i + "] is not a valid type");
                }
            }
        }
        this.id = id;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    public DOMXMLObject(Element objElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.encoding = DOMUtils.getAttributeValue(objElem, "Encoding");
        Attr attr = objElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            objElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        this.mimeType = DOMUtils.getAttributeValue(objElem, "MimeType");
        List<XMLStructure> newContent = new ArrayList<>();
        Node firstChild = objElem.getFirstChild();
        while (true) {
            Node firstChild2 = firstChild;
            if (firstChild2 == null) {
                break;
            }
            if (firstChild2.getNodeType() == 1) {
                Element childElem = (Element) firstChild2;
                String tag = childElem.getLocalName();
                String namespace = childElem.getNamespaceURI();
                if (Constants._TAG_MANIFEST.equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMManifest(childElem, context, provider));
                } else if (Constants._TAG_SIGNATUREPROPERTIES.equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMSignatureProperties(childElem));
                } else if (Constants._TAG_X509DATA.equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMX509Data(childElem));
                } else {
                    newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild2));
                }
            } else {
                newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild2));
            }
            firstChild = firstChild2.getNextSibling();
        }
        NamedNodeMap nnm = objElem.getAttributes();
        for (int idx = 0; idx < nnm.getLength(); idx++) {
            Node nsDecl = nnm.item(idx);
            if (DOMUtils.isNamespace(nsDecl)) {
                newContent.add(new javax.xml.crypto.dom.DOMStructure(nsDecl));
            }
        }
        if (newContent.isEmpty()) {
            this.content = Collections.emptyList();
        } else {
            this.content = Collections.unmodifiableList(newContent);
        }
        this.objectElem = objElem;
    }

    public List<XMLStructure> getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element objElem = this.objectElem;
        if (objElem == null) {
            objElem = DOMUtils.createElement(ownerDoc, Constants._TAG_OBJECT, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            DOMUtils.setAttributeID(objElem, "Id", this.id);
            DOMUtils.setAttribute(objElem, "MimeType", this.mimeType);
            DOMUtils.setAttribute(objElem, "Encoding", this.encoding);
            Iterator<XMLStructure> it = this.content.iterator();
            while (it.hasNext()) {
                javax.xml.crypto.dom.DOMStructure dOMStructure = (XMLStructure) it.next();
                if (dOMStructure instanceof DOMStructure) {
                    ((DOMStructure) dOMStructure).marshal(objElem, dsPrefix, context);
                } else {
                    javax.xml.crypto.dom.DOMStructure domObject = dOMStructure;
                    DOMUtils.appendChild(objElem, domObject.getNode());
                }
            }
        }
        parent.appendChild(objElem);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        boolean zEquals2;
        boolean zEquals3;
        if (this == o) {
            return true;
        }
        if (!(o instanceof XMLObject)) {
            return false;
        }
        XMLObject oxo = (XMLObject) o;
        if (this.id == null) {
            zEquals = oxo.getId() == null;
        } else {
            zEquals = this.id.equals(oxo.getId());
        }
        boolean idsEqual = zEquals;
        if (this.encoding == null) {
            zEquals2 = oxo.getEncoding() == null;
        } else {
            zEquals2 = this.encoding.equals(oxo.getEncoding());
        }
        boolean encodingsEqual = zEquals2;
        if (this.mimeType == null) {
            zEquals3 = oxo.getMimeType() == null;
        } else {
            zEquals3 = this.mimeType.equals(oxo.getMimeType());
        }
        boolean mimeTypesEqual = zEquals3;
        return idsEqual && encodingsEqual && mimeTypesEqual && equalsContent(this.content, oxo.getContent());
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        if (this.encoding != null) {
            result = (31 * result) + this.encoding.hashCode();
        }
        if (this.mimeType != null) {
            result = (31 * result) + this.mimeType.hashCode();
        }
        return (31 * result) + this.content.hashCode();
    }
}
