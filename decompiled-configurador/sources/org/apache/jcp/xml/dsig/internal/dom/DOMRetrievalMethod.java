package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMRetrievalMethod.class */
public final class DOMRetrievalMethod extends DOMStructure implements RetrievalMethod, DOMURIReference {
    private final List<Transform> transforms;
    private String uri;
    private String type;
    private Attr here;

    public DOMRetrievalMethod(String uri, String type, List<? extends Transform> transforms) {
        if (uri == null) {
            throw new NullPointerException("uri cannot be null");
        }
        if (transforms == null || transforms.isEmpty()) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = Collections.unmodifiableList(new ArrayList(transforms));
            int size = this.transforms.size();
            for (int i = 0; i < size; i++) {
                if (!(this.transforms.get(i) instanceof Transform)) {
                    throw new ClassCastException("transforms[" + i + "] is not a valid type");
                }
            }
        }
        this.uri = uri;
        if (!uri.isEmpty()) {
            try {
                new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        this.type = type;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMRetrievalMethod(Element rmElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.uri = DOMUtils.getAttributeValue(rmElem, "URI");
        this.type = DOMUtils.getAttributeValue(rmElem, "Type");
        this.here = rmElem.getAttributeNodeNS(null, "URI");
        boolean secVal = Utils.secureValidation(context);
        List<Transform> newTransforms = new ArrayList<>();
        Element transformsElem = DOMUtils.getFirstChildElement(rmElem);
        if (transformsElem != null) {
            String localName = transformsElem.getLocalName();
            String namespace = transformsElem.getNamespaceURI();
            if (!"Transforms".equals(localName) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                throw new MarshalException("Invalid element name: " + namespace + ":" + localName + ", expected Transforms");
            }
            Element firstChildElement = DOMUtils.getFirstChildElement(transformsElem, Constants._TAG_TRANSFORM, "http://www.w3.org/2000/09/xmldsig#");
            while (true) {
                Element transformElem = firstChildElement;
                if (transformElem == null) {
                    break;
                }
                String name = transformElem.getLocalName();
                String namespace2 = transformElem.getNamespaceURI();
                if (!Constants._TAG_TRANSFORM.equals(name) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace2)) {
                    throw new MarshalException("Invalid element name: " + name + ", expected Transform");
                }
                newTransforms.add(new DOMTransform(transformElem, context, provider));
                if (secVal && newTransforms.size() > 5) {
                    throw new MarshalException("A maximum of 5 transforms per Reference are allowed with secure validation");
                }
                firstChildElement = DOMUtils.getNextSiblingElement(transformElem);
            }
        }
        if (newTransforms.isEmpty()) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = Collections.unmodifiableList(newTransforms);
        }
    }

    public String getURI() {
        return this.uri;
    }

    public String getType() {
        return this.type;
    }

    public List<Transform> getTransforms() {
        return this.transforms;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element rmElem = DOMUtils.createElement(ownerDoc, Constants._TAG_RETRIEVALMETHOD, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttribute(rmElem, "URI", this.uri);
        DOMUtils.setAttribute(rmElem, "Type", this.type);
        if (!this.transforms.isEmpty()) {
            Element transformsElem = DOMUtils.createElement(ownerDoc, "Transforms", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            rmElem.appendChild(transformsElem);
            for (Transform transform : this.transforms) {
                ((DOMTransform) transform).marshal(transformsElem, dsPrefix, context);
            }
        }
        parent.appendChild(rmElem);
        this.here = rmElem.getAttributeNodeNS(null, "URI");
    }

    public Node getHere() {
        return this.here;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.TransformException */
    public Data dereference(XMLCryptoContext context) throws TransformException, URIReferenceException {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        URIDereferencer deref = context.getURIDereferencer();
        if (deref == null) {
            deref = DOMURIDereferencer.INSTANCE;
        }
        Data data = deref.dereference(this, context);
        try {
            for (Transform transform : this.transforms) {
                data = ((DOMTransform) transform).transform(data, context);
            }
            if ((data instanceof NodeSetData) && Utils.secureValidation(context)) {
                NodeSetData nsd = (NodeSetData) data;
                Iterator<?> i = nsd.iterator();
                if (i.hasNext()) {
                    Node root = (Node) i.next();
                    if (Constants._TAG_RETRIEVALMETHOD.equals(root.getLocalName())) {
                        throw new URIReferenceException("It is forbidden to have one RetrievalMethod point to another when secure validation is enabled");
                    }
                }
            }
            return data;
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext context) throws URIReferenceException {
        boolean secVal = Utils.secureValidation(context);
        ApacheData data = (ApacheData) dereference(context);
        try {
            InputStream is = new ByteArrayInputStream(data.getXMLSignatureInput().getBytes());
            Throwable th = null;
            try {
                try {
                    Document doc = XMLUtils.read(is, secVal);
                    Element kiElem = doc.getDocumentElement();
                    if (Constants._TAG_X509DATA.equals(kiElem.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(kiElem.getNamespaceURI())) {
                        DOMX509Data dOMX509Data = new DOMX509Data(kiElem);
                        if (0 != 0) {
                            try {
                                is.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            is.close();
                        }
                        return dOMX509Data;
                    }
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        is.close();
                    }
                    return null;
                } catch (Throwable th4) {
                    if (th != null) {
                        try {
                            is.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        is.close();
                    }
                    throw th4;
                }
            } catch (Throwable th6) {
                th = th6;
                throw th6;
            }
        } catch (Exception e) {
            throw new URIReferenceException(e);
        }
        throw new URIReferenceException(e);
    }

    public boolean equals(Object obj) {
        boolean zEquals;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RetrievalMethod)) {
            return false;
        }
        RetrievalMethod orm = (RetrievalMethod) obj;
        if (this.type == null) {
            zEquals = orm.getType() == null;
        } else {
            zEquals = this.type.equals(orm.getType());
        }
        boolean typesEqual = zEquals;
        return this.uri.equals(orm.getURI()) && this.transforms.equals(orm.getTransforms()) && typesEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.type != null) {
            result = (31 * 17) + this.type.hashCode();
        }
        return (31 * ((31 * result) + this.uri.hashCode())) + this.transforms.hashCode();
    }
}
