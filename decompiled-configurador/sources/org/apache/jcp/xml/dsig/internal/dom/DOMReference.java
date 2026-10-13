package org.apache.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.apache.jcp.xml.dsig.internal.DigesterOutputStream;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMReference.class */
public final class DOMReference extends DOMStructure implements Reference, DOMURIReference {
    public static final int MAXIMUM_TRANSFORM_COUNT = 5;
    private static boolean useC14N11 = ((Boolean) AccessController.doPrivileged(() -> {
        return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
    })).booleanValue();
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMReference.class);
    private final DigestMethod digestMethod;
    private final String id;
    private final List<Transform> transforms;
    private List<Transform> allTransforms;
    private final Data appliedTransformData;
    private Attr here;
    private final String uri;
    private final String type;
    private byte[] digestValue;
    private byte[] calcDigestValue;
    private Element refElem;
    private boolean digested;
    private boolean validated;
    private boolean validationStatus;
    private Data derefData;
    private InputStream dis;
    private MessageDigest md;
    private Provider provider;

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> transforms, String id, Provider provider) {
        this(uri, type, dm, null, null, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> appliedTransforms, Data result, List<? extends Transform> transforms, String id, Provider provider) {
        this(uri, type, dm, appliedTransforms, result, transforms, id, null, provider);
    }

    public DOMReference(String uri, String type, DigestMethod dm, List<? extends Transform> appliedTransforms, Data result, List<? extends Transform> transforms, String id, byte[] digestValue, Provider provider) {
        this.digested = false;
        this.validated = false;
        if (dm == null) {
            throw new NullPointerException("DigestMethod must be non-null");
        }
        if (appliedTransforms == null) {
            this.allTransforms = new ArrayList();
        } else {
            this.allTransforms = new ArrayList(appliedTransforms);
            int size = this.allTransforms.size();
            for (int i = 0; i < size; i++) {
                if (!(this.allTransforms.get(i) instanceof Transform)) {
                    throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
                }
            }
        }
        if (transforms == null) {
            this.transforms = Collections.emptyList();
        } else {
            this.transforms = new ArrayList(transforms);
            int size2 = this.transforms.size();
            for (int i2 = 0; i2 < size2; i2++) {
                if (!(this.transforms.get(i2) instanceof Transform)) {
                    throw new ClassCastException("transforms[" + i2 + "] is not a valid type");
                }
            }
            this.allTransforms.addAll(this.transforms);
        }
        this.digestMethod = dm;
        this.uri = uri;
        if (uri != null && !uri.isEmpty()) {
            try {
                new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        this.type = type;
        this.id = id;
        if (digestValue != null) {
            this.digestValue = (byte[]) digestValue.clone();
            this.digested = true;
        }
        this.appliedTransformData = result;
        this.provider = provider;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMReference(Element refElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.digested = false;
        this.validated = false;
        boolean secVal = Utils.secureValidation(context);
        Element nextSibling = DOMUtils.getFirstChildElement(refElem);
        List<Transform> newTransforms = new ArrayList<>(5);
        if ("Transforms".equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI())) {
            Element transformElem = DOMUtils.getFirstChildElement(nextSibling, Constants._TAG_TRANSFORM, "http://www.w3.org/2000/09/xmldsig#");
            newTransforms.add(new DOMTransform(transformElem, context, provider));
            Element nextSiblingElement = DOMUtils.getNextSiblingElement(transformElem);
            while (true) {
                Element transformElem2 = nextSiblingElement;
                if (transformElem2 != null) {
                    String localName = transformElem2.getLocalName();
                    String namespace = transformElem2.getNamespaceURI();
                    if (!Constants._TAG_TRANSFORM.equals(localName) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                        throw new MarshalException("Invalid element name: " + localName + ", expected Transform");
                    }
                    newTransforms.add(new DOMTransform(transformElem2, context, provider));
                    if (secVal && newTransforms.size() > 5) {
                        throw new MarshalException("A maximum of 5 transforms per Reference are allowed with secure validation");
                    }
                    nextSiblingElement = DOMUtils.getNextSiblingElement(transformElem2);
                } else {
                    nextSibling = DOMUtils.getNextSiblingElement(nextSibling);
                    break;
                }
            }
        }
        if (!"DigestMethod".equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI())) {
            throw new MarshalException("Invalid element name: " + nextSibling.getLocalName() + ", expected DigestMethod");
        }
        Element dmElem = nextSibling;
        this.digestMethod = DOMDigestMethod.unmarshal(dmElem);
        String digestMethodAlgorithm = this.digestMethod.getAlgorithm();
        if (secVal && MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5.equals(digestMethodAlgorithm)) {
            throw new MarshalException("It is forbidden to use algorithm " + this.digestMethod + " when secure validation is enabled");
        }
        Element dvElem = DOMUtils.getNextSiblingElement(dmElem, "DigestValue", "http://www.w3.org/2000/09/xmldsig#");
        String content = XMLUtils.getFullTextChildrenFromNode(dvElem);
        this.digestValue = XMLUtils.decode(content);
        if (DOMUtils.getNextSiblingElement(dvElem) != null) {
            throw new MarshalException("Unexpected element after DigestValue element");
        }
        this.uri = DOMUtils.getAttributeValue(refElem, "URI");
        Attr attr = refElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            refElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        this.type = DOMUtils.getAttributeValue(refElem, "Type");
        this.here = refElem.getAttributeNodeNS(null, "URI");
        this.refElem = refElem;
        this.transforms = newTransforms;
        this.allTransforms = this.transforms;
        this.appliedTransformData = null;
        this.provider = provider;
    }

    public DigestMethod getDigestMethod() {
        return this.digestMethod;
    }

    public String getId() {
        return this.id;
    }

    public String getURI() {
        return this.uri;
    }

    public String getType() {
        return this.type;
    }

    public List<Transform> getTransforms() {
        return Collections.unmodifiableList(this.allTransforms);
    }

    public byte[] getDigestValue() {
        if (this.digestValue == null) {
            return null;
        }
        return (byte[]) this.digestValue.clone();
    }

    public byte[] getCalculatedDigestValue() {
        if (this.calcDigestValue == null) {
            return null;
        }
        return (byte[]) this.calcDigestValue.clone();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        LOG.debug("Marshalling Reference");
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        this.refElem = DOMUtils.createElement(ownerDoc, "Reference", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(this.refElem, "Id", this.id);
        DOMUtils.setAttribute(this.refElem, "URI", this.uri);
        DOMUtils.setAttribute(this.refElem, "Type", this.type);
        if (!this.allTransforms.isEmpty()) {
            Element transformsElem = DOMUtils.createElement(ownerDoc, "Transforms", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            this.refElem.appendChild(transformsElem);
            Iterator<Transform> it = this.allTransforms.iterator();
            while (it.hasNext()) {
                ((Transform) it.next()).marshal(transformsElem, dsPrefix, context);
            }
        }
        ((DOMDigestMethod) this.digestMethod).marshal(this.refElem, dsPrefix, context);
        LOG.debug("Adding digestValueElem");
        Element digestValueElem = DOMUtils.createElement(ownerDoc, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        if (this.digestValue != null) {
            digestValueElem.appendChild(ownerDoc.createTextNode(XMLUtils.encodeToString(this.digestValue)));
        }
        this.refElem.appendChild(digestValueElem);
        parent.appendChild(this.refElem);
        this.here = this.refElem.getAttributeNodeNS(null, "URI");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void digest(XMLSignContext signContext) throws XMLSignatureException {
        Data data;
        if (this.appliedTransformData == null) {
            data = dereference(signContext);
        } else {
            data = this.appliedTransformData;
        }
        this.digestValue = transform(data, signContext);
        String encodedDV = XMLUtils.encodeToString(this.digestValue);
        LOG.debug("Reference object uri = {}", this.uri);
        Element digestElem = DOMUtils.getLastChildElement(this.refElem);
        if (digestElem == null) {
            throw new XMLSignatureException("DigestValue element expected");
        }
        DOMUtils.removeAllChildren(digestElem);
        digestElem.appendChild(this.refElem.getOwnerDocument().createTextNode(encodedDV));
        this.digested = true;
        LOG.debug("Reference digesting completed");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public boolean validate(XMLValidateContext validateContext) throws XMLSignatureException {
        if (validateContext == null) {
            throw new NullPointerException("validateContext cannot be null");
        }
        if (this.validated) {
            return this.validationStatus;
        }
        Data data = dereference(validateContext);
        this.calcDigestValue = transform(data, validateContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Expected digest: " + XMLUtils.encodeToString(this.digestValue));
            LOG.debug("Actual digest: " + XMLUtils.encodeToString(this.calcDigestValue));
        }
        this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
        this.validated = true;
        return this.validationStatus;
    }

    public Data getDereferencedData() {
        return this.derefData;
    }

    public InputStream getDigestInputStream() {
        return this.dis;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private Data dereference(XMLCryptoContext context) throws XMLSignatureException {
        URIDereferencer deref = context.getURIDereferencer();
        if (deref == null) {
            deref = DOMURIDereferencer.INSTANCE;
        }
        try {
            Data data = deref.dereference(this, context);
            LOG.debug("URIDereferencer class name: {}", deref.getClass().getName());
            LOG.debug("Data class name: {}", data.getClass().getName());
            return data;
        } catch (URIReferenceException ure) {
            throw new XMLSignatureException(ure);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x031a: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('os' java.io.OutputStream)]) A[TRY_ENTER], block:B:113:0x031a */
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x032e: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('os' java.io.OutputStream)]), block:B:116:0x032e */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x0315: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:111:0x0315 */
    private byte[] transform(Data dereferencedData, XMLCryptoContext context) throws XMLSignatureException {
        DigesterOutputStream dos;
        Throwable th;
        OutputStream os;
        OutputStream os2;
        TransformService spi;
        TransformService spi2;
        Element transformsElem;
        if (this.md == null) {
            try {
                this.md = MessageDigest.getInstance(((DOMDigestMethod) this.digestMethod).getMessageDigestAlgorithm());
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        this.md.reset();
        Boolean cache = (Boolean) context.getProperty("javax.xml.crypto.dsig.cacheReference");
        if (cache != null && cache.booleanValue()) {
            this.derefData = copyDerefData(dereferencedData);
            dos = new DigesterOutputStream(this.md, true);
        } else {
            dos = new DigesterOutputStream(this.md);
        }
        Data data = dereferencedData;
        XMLSignatureInput xi = null;
        try {
            try {
                try {
                    OutputStream os3 = new UnsyncBufferedOutputStream(dos);
                    Throwable th2 = null;
                    int size = this.transforms.size();
                    for (int i = 0; i < size; i++) {
                        DOMTransform transform = (DOMTransform) this.transforms.get(i);
                        if (i < size - 1) {
                            data = transform.transform(data, context);
                        } else {
                            data = transform.transform(data, context, os3);
                        }
                    }
                    if (data != null) {
                        boolean c14n11 = useC14N11;
                        String c14nalg = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
                        if (context instanceof XMLSignContext) {
                            if (!c14n11) {
                                Boolean prop = (Boolean) context.getProperty("org.apache.xml.security.useC14N11");
                                c14n11 = prop != null && prop.booleanValue();
                                if (c14n11) {
                                    c14nalg = "http://www.w3.org/2006/12/xml-c14n11";
                                }
                            } else {
                                c14nalg = "http://www.w3.org/2006/12/xml-c14n11";
                            }
                        }
                        if (data instanceof ApacheData) {
                            xi = ((ApacheData) data).getXMLSignatureInput();
                        } else if (data instanceof OctetStreamData) {
                            xi = new XMLSignatureInput(((OctetStreamData) data).getOctetStream());
                        } else if (data instanceof NodeSetData) {
                            if (this.provider == null) {
                                spi = TransformService.getInstance(c14nalg, "DOM");
                            } else {
                                try {
                                    spi = TransformService.getInstance(c14nalg, "DOM", this.provider);
                                } catch (NoSuchAlgorithmException e) {
                                    spi = TransformService.getInstance(c14nalg, "DOM");
                                }
                            }
                            xi = new XMLSignatureInput(spi.transform(data, context).getOctetStream());
                        } else {
                            throw new XMLSignatureException("unrecognized Data type");
                        }
                        boolean secVal = Utils.secureValidation(context);
                        xi.setSecureValidation(secVal);
                        if ((context instanceof XMLSignContext) && c14n11 && !xi.isOctetStream() && !xi.isOutputStreamSet()) {
                            if (this.provider == null) {
                                spi2 = TransformService.getInstance(c14nalg, "DOM");
                            } else {
                                try {
                                    spi2 = TransformService.getInstance(c14nalg, "DOM", this.provider);
                                } catch (NoSuchAlgorithmException e2) {
                                    spi2 = TransformService.getInstance(c14nalg, "DOM");
                                }
                            }
                            DOMTransform t = new DOMTransform(spi2);
                            String dsPrefix = DOMUtils.getSignaturePrefix(context);
                            if (this.allTransforms.isEmpty()) {
                                transformsElem = DOMUtils.createElement(this.refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
                                this.refElem.insertBefore(transformsElem, DOMUtils.getFirstChildElement(this.refElem));
                            } else {
                                transformsElem = DOMUtils.getFirstChildElement(this.refElem);
                            }
                            t.marshal(transformsElem, dsPrefix, (DOMCryptoContext) context);
                            this.allTransforms.add(t);
                            xi.updateOutputStream(os3, true);
                        } else {
                            xi.updateOutputStream(os3);
                        }
                    } else {
                        LOG.warn("The input bytes to the digest operation are null. This may be due to a problem with the Reference URI or its Transforms.");
                    }
                    os3.flush();
                    if (cache != null && cache.booleanValue()) {
                        this.dis = dos.getInputStream();
                    }
                    byte[] digestValue = dos.getDigestValue();
                    if (0 != 0) {
                        try {
                            os3.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    } else {
                        os3.close();
                    }
                    if (xi != null && xi.getOctetStreamReal() != null) {
                        try {
                            xi.getOctetStreamReal().close();
                        } catch (IOException e3) {
                            throw new XMLSignatureException(e3);
                        }
                    }
                    if (dos != null) {
                        try {
                            dos.close();
                        } catch (IOException e4) {
                            throw new XMLSignatureException(e4);
                        }
                    }
                    return digestValue;
                } catch (Throwable th4) {
                    if (0 != 0 && xi.getOctetStreamReal() != null) {
                        try {
                            xi.getOctetStreamReal().close();
                        } catch (IOException e5) {
                            throw new XMLSignatureException(e5);
                        }
                    }
                    if (dos != null) {
                        try {
                            dos.close();
                        } catch (IOException e6) {
                            throw new XMLSignatureException(e6);
                        }
                    }
                    throw th4;
                }
            } catch (Throwable th5) {
                if (th != null) {
                    try {
                        os.close();
                    } catch (Throwable th6) {
                        th.addSuppressed(th6);
                    }
                } else {
                    os2.close();
                }
                throw th5;
            }
        } catch (NoSuchAlgorithmException | TransformException | MarshalException | IOException | CanonicalizationException e7) {
            throw new XMLSignatureException(e7);
        }
    }

    public Node getHere() {
        return this.here;
    }

    public boolean equals(Object o) {
        boolean zEquals;
        boolean zEquals2;
        boolean zEquals3;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        Reference oref = (Reference) o;
        if (this.id == null) {
            zEquals = oref.getId() == null;
        } else {
            zEquals = this.id.equals(oref.getId());
        }
        boolean idsEqual = zEquals;
        if (this.uri == null) {
            zEquals2 = oref.getURI() == null;
        } else {
            zEquals2 = this.uri.equals(oref.getURI());
        }
        boolean urisEqual = zEquals2;
        if (this.type == null) {
            zEquals3 = oref.getType() == null;
        } else {
            zEquals3 = this.type.equals(oref.getType());
        }
        boolean typesEqual = zEquals3;
        boolean digestValuesEqual = Arrays.equals(this.digestValue, oref.getDigestValue());
        return this.digestMethod.equals(oref.getDigestMethod()) && idsEqual && urisEqual && typesEqual && this.allTransforms.equals(oref.getTransforms()) && digestValuesEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        if (this.uri != null) {
            result = (31 * result) + this.uri.hashCode();
        }
        if (this.type != null) {
            result = (31 * result) + this.type.hashCode();
        }
        if (this.digestValue != null) {
            result = (31 * result) + Arrays.hashCode(this.digestValue);
        }
        return (31 * ((31 * result) + this.digestMethod.hashCode())) + this.allTransforms.hashCode();
    }

    boolean isDigested() {
        return this.digested;
    }

    private static Data copyDerefData(Data dereferencedData) {
        if (dereferencedData instanceof ApacheData) {
            ApacheData ad = (ApacheData) dereferencedData;
            XMLSignatureInput xsi = ad.getXMLSignatureInput();
            if (xsi.isNodeSet()) {
                try {
                    final Set<Node> s = xsi.getNodeSet();
                    return new NodeSetData() { // from class: org.apache.jcp.xml.dsig.internal.dom.DOMReference.1
                        public Iterator<Node> iterator() {
                            return s.iterator();
                        }
                    };
                } catch (Exception e) {
                    LOG.warn("cannot cache dereferenced data: " + e);
                    return null;
                }
            }
            if (xsi.isElement()) {
                return new DOMSubTreeData(xsi.getSubNode(), xsi.isExcludeComments());
            }
            if (xsi.isOctetStream() || xsi.isByteArray()) {
                try {
                    return new OctetStreamData(xsi.getOctetStream(), xsi.getSourceURI(), xsi.getMIMEType());
                } catch (IOException ioe) {
                    LOG.warn("cannot cache dereferenced data: " + ioe);
                    return null;
                }
            }
        }
        return dereferencedData;
    }
}
