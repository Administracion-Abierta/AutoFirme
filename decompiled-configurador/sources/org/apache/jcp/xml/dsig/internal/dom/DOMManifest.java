package org.apache.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMManifest.class */
public final class DOMManifest extends DOMStructure implements Manifest {
    private final List<Reference> references;
    private final String id;

    public DOMManifest(List<? extends Reference> references, String id) {
        if (references == null) {
            throw new NullPointerException("references cannot be null");
        }
        this.references = Collections.unmodifiableList(new ArrayList(references));
        if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
        }
        int size = this.references.size();
        for (int i = 0; i < size; i++) {
            if (!(this.references.get(i) instanceof Reference)) {
                throw new ClassCastException("references[" + i + "] is not a valid type");
            }
        }
        this.id = id;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMManifest(Element manElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.id = DOMUtils.getIdAttributeValue(manElem, "Id");
        boolean secVal = Utils.secureValidation(context);
        Element refElem = DOMUtils.getFirstChildElement(manElem, "Reference", "http://www.w3.org/2000/09/xmldsig#");
        List<Reference> refs = new ArrayList<>();
        refs.add(new DOMReference(refElem, context, provider));
        Element nextSiblingElement = DOMUtils.getNextSiblingElement(refElem);
        while (true) {
            Element refElem2 = nextSiblingElement;
            if (refElem2 != null) {
                String localName = refElem2.getLocalName();
                String namespace = refElem2.getNamespaceURI();
                if (!"Reference".equals(localName) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    throw new MarshalException("Invalid element name: " + namespace + ":" + localName + ", expected Reference");
                }
                refs.add(new DOMReference(refElem2, context, provider));
                if (secVal && refs.size() > 30) {
                    throw new MarshalException("A maximum of 30 references per Manifest are allowed with secure validation");
                }
                nextSiblingElement = DOMUtils.getNextSiblingElement(refElem2);
            } else {
                this.references = Collections.unmodifiableList(refs);
                return;
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public static List<Reference> getManifestReferences(Manifest mf) {
        return mf.getReferences();
    }

    public List<Reference> getReferences() {
        return this.references;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element manElem = DOMUtils.createElement(ownerDoc, Constants._TAG_MANIFEST, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(manElem, "Id", this.id);
        for (Reference ref : this.references) {
            ((DOMReference) ref).marshal(manElem, dsPrefix, context);
        }
        parent.appendChild(manElem);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manifest)) {
            return false;
        }
        Manifest oman = (Manifest) o;
        if (this.id == null) {
            zEquals = oman.getId() == null;
        } else {
            zEquals = this.id.equals(oman.getId());
        }
        boolean idsEqual = zEquals;
        return idsEqual && this.references.equals(oman.getReferences());
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        return (31 * result) + this.references.hashCode();
    }
}
