package org.apache.xml.security.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.algorithms.Algorithm;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.reference.ReferenceData;
import org.apache.xml.security.signature.reference.ReferenceNodeSetData;
import org.apache.xml.security.signature.reference.ReferenceOctetStreamData;
import org.apache.xml.security.signature.reference.ReferenceSubTreeData;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.DigesterOutputStream;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/Reference.class */
public class Reference extends SignatureElementProxy {
    public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
    public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
    public static final int MAXIMUM_TRANSFORM_COUNT = 5;
    private boolean secureValidation;
    private static boolean useC14N11 = ((Boolean) AccessController.doPrivileged(() -> {
        return Boolean.valueOf(Boolean.getBoolean("org.apache.xml.security.useC14N11"));
    })).booleanValue();
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) Reference.class);
    private Manifest manifest;
    private XMLSignatureInput transformsOutput;
    private Transforms transforms;
    private Element digestMethodElem;
    private Element digestValueElement;
    private ReferenceData referenceData;
    private static final Set<String> TRANSFORM_ALGORITHMS;

    static {
        Set<String> algorithms = new HashSet<>();
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#");
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#WithComments");
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11#WithComments");
        TRANSFORM_ALGORITHMS = Collections.unmodifiableSet(algorithms);
    }

    protected Reference(Document doc, String baseURI, String referenceURI, Manifest manifest, Transforms transforms, String messageDigestAlgorithm) throws XMLSignatureException {
        super(doc);
        this.secureValidation = true;
        addReturnToSelf();
        this.baseURI = baseURI;
        this.manifest = manifest;
        setURI(referenceURI);
        if (transforms != null) {
            this.transforms = transforms;
            appendSelf(transforms);
            addReturnToSelf();
        }
        Algorithm digestAlgorithm = new Algorithm(getDocument(), messageDigestAlgorithm) { // from class: org.apache.xml.security.signature.Reference.1
            @Override // org.apache.xml.security.utils.SignatureElementProxy, org.apache.xml.security.utils.ElementProxy
            public String getBaseNamespace() {
                return "http://www.w3.org/2000/09/xmldsig#";
            }

            @Override // org.apache.xml.security.utils.ElementProxy
            public String getBaseLocalName() {
                return "DigestMethod";
            }
        };
        this.digestMethodElem = digestAlgorithm.getElement();
        appendSelf(this.digestMethodElem);
        addReturnToSelf();
        this.digestValueElement = XMLUtils.createElementInSignatureSpace(getDocument(), "DigestValue");
        appendSelf(this.digestValueElement);
        addReturnToSelf();
    }

    protected Reference(Element element, String baseURI, Manifest manifest) throws XMLSecurityException {
        this(element, baseURI, manifest, true);
    }

    protected Reference(Element element, String baseURI, Manifest manifest, boolean secureValidation) throws XMLSecurityException {
        super(element, baseURI);
        this.secureValidation = true;
        this.secureValidation = secureValidation;
        this.baseURI = baseURI;
        Element el = XMLUtils.getNextElement(element.getFirstChild());
        if (el != null && "Transforms".equals(el.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(el.getNamespaceURI())) {
            this.transforms = new Transforms(el, this.baseURI);
            this.transforms.setSecureValidation(secureValidation);
            if (secureValidation && this.transforms.getLength() > 5) {
                Object[] exArgs = {Integer.valueOf(this.transforms.getLength()), 5};
                throw new XMLSecurityException("signature.tooManyTransforms", exArgs);
            }
            el = XMLUtils.getNextElement(el.getNextSibling());
        }
        this.digestMethodElem = el;
        if (this.digestMethodElem == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.digestMethodElem.getNamespaceURI()) || !"DigestMethod".equals(this.digestMethodElem.getLocalName())) {
            throw new XMLSecurityException("signature.Reference.NoDigestMethod");
        }
        this.digestValueElement = XMLUtils.getNextElement(this.digestMethodElem.getNextSibling());
        if (this.digestValueElement == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.digestValueElement.getNamespaceURI()) || !"DigestValue".equals(this.digestValueElement.getLocalName())) {
            throw new XMLSecurityException("signature.Reference.NoDigestValue");
        }
        this.manifest = manifest;
    }

    public MessageDigestAlgorithm getMessageDigestAlgorithm() throws XMLSignatureException {
        if (this.digestMethodElem == null) {
            return null;
        }
        String uri = this.digestMethodElem.getAttributeNS(null, "Algorithm");
        if (uri.isEmpty()) {
            return null;
        }
        if (this.secureValidation && MessageDigestAlgorithm.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5.equals(uri)) {
            Object[] exArgs = {uri};
            throw new XMLSignatureException("signature.signatureAlgorithm", exArgs);
        }
        return MessageDigestAlgorithm.getInstance(getDocument(), uri);
    }

    public void setURI(String uri) {
        if (uri != null) {
            setLocalAttribute("URI", uri);
        }
    }

    public String getURI() {
        return getLocalAttribute("URI");
    }

    public void setId(String id) {
        if (id != null) {
            setLocalIdAttribute("Id", id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public void setType(String type) {
        if (type != null) {
            setLocalAttribute("Type", type);
        }
    }

    public String getType() {
        return getLocalAttribute("Type");
    }

    public boolean typeIsReferenceToObject() {
        return "http://www.w3.org/2000/09/xmldsig#Object".equals(getType());
    }

    public boolean typeIsReferenceToManifest() {
        return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(getType());
    }

    private void setDigestValueElement(byte[] digestValue) {
        Node firstChild = this.digestValueElement.getFirstChild();
        while (true) {
            Node n = firstChild;
            if (n != null) {
                this.digestValueElement.removeChild(n);
                firstChild = n.getNextSibling();
            } else {
                String base64codedValue = XMLUtils.encodeToString(digestValue);
                Text t = createText(base64codedValue);
                this.digestValueElement.appendChild(t);
                return;
            }
        }
    }

    public void generateDigestValue() throws XMLSignatureException {
        setDigestValueElement(calculateDigest(false));
    }

    public XMLSignatureInput getContentsBeforeTransformation() throws ReferenceNotInitializedException {
        try {
            Attr uriAttr = getElement().getAttributeNodeNS(null, "URI");
            ResourceResolverContext resolverContext = new ResourceResolverContext(uriAttr, this.baseURI, this.secureValidation, this.manifest.getResolverProperties());
            return ResourceResolver.resolve(this.manifest.getPerManifestResolvers(), resolverContext);
        } catch (ResourceResolverException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
    }

    private XMLSignatureInput getContentsAfterTransformation(XMLSignatureInput input, OutputStream os) throws XMLSignatureException {
        XMLSignatureInput output;
        try {
            Transforms transforms = getTransforms();
            if (transforms != null) {
                output = transforms.performTransforms(input, os);
                this.transformsOutput = output;
            } else {
                output = input;
            }
            return output;
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public XMLSignatureInput getContentsAfterTransformation() throws XMLSignatureException {
        XMLSignatureInput input = getContentsBeforeTransformation();
        cacheDereferencedElement(input);
        return getContentsAfterTransformation(input, null);
    }

    public XMLSignatureInput getNodesetBeforeFirstCanonicalization() throws XMLSignatureException {
        try {
            XMLSignatureInput input = getContentsBeforeTransformation();
            cacheDereferencedElement(input);
            XMLSignatureInput output = input;
            Transforms transforms = getTransforms();
            if (transforms != null) {
                for (int i = 0; i < transforms.getLength(); i++) {
                    Transform t = transforms.item(i);
                    String uri = t.getURI();
                    if (TRANSFORM_ALGORITHMS.contains(uri)) {
                        break;
                    }
                    output = t.performTransform(output, null, this.secureValidation);
                }
                output.setSourceURI(input.getSourceURI());
            }
            return output;
        } catch (IOException | XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public String getHTMLRepresentation() throws XMLSignatureException {
        try {
            XMLSignatureInput nodes = getNodesetBeforeFirstCanonicalization();
            Transforms transforms = getTransforms();
            Transform c14nTransform = null;
            if (transforms != null) {
                for (int i = 0; i < transforms.getLength(); i++) {
                    Transform t = transforms.item(i);
                    String uri = t.getURI();
                    if (uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#") || uri.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) {
                        c14nTransform = t;
                        break;
                    }
                }
            }
            Set<String> inclusiveNamespaces = new HashSet();
            if (c14nTransform != null && c14nTransform.length("http://www.w3.org/2001/10/xml-exc-c14n#", InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES) == 1) {
                InclusiveNamespaces in = new InclusiveNamespaces(XMLUtils.selectNode(c14nTransform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES, 0), getBaseURI());
                inclusiveNamespaces = InclusiveNamespaces.prefixStr2Set(in.getInclusiveNamespaces());
            }
            return nodes.getHTMLRepresentation(inclusiveNamespaces);
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public XMLSignatureInput getTransformsOutput() {
        return this.transformsOutput;
    }

    public ReferenceData getReferenceData() {
        return this.referenceData;
    }

    protected XMLSignatureInput dereferenceURIandPerformTransforms(OutputStream os) throws XMLSignatureException {
        try {
            XMLSignatureInput input = getContentsBeforeTransformation();
            cacheDereferencedElement(input);
            XMLSignatureInput output = getContentsAfterTransformation(input, os);
            this.transformsOutput = output;
            return output;
        } catch (XMLSecurityException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
    }

    private void cacheDereferencedElement(XMLSignatureInput input) {
        if (input.isNodeSet()) {
            try {
                final Set<Node> s = input.getNodeSet();
                this.referenceData = new ReferenceNodeSetData() { // from class: org.apache.xml.security.signature.Reference.2
                    @Override // org.apache.xml.security.signature.reference.ReferenceNodeSetData
                    public Iterator<Node> iterator() {
                        return new Iterator<Node>() { // from class: org.apache.xml.security.signature.Reference.2.1
                            final Iterator<Node> sIterator;

                            {
                                this.sIterator = s.iterator();
                            }

                            @Override // java.util.Iterator
                            public boolean hasNext() {
                                return this.sIterator.hasNext();
                            }

                            /* JADX WARN: Can't rename method to resolve collision */
                            @Override // java.util.Iterator
                            public Node next() {
                                return this.sIterator.next();
                            }

                            @Override // java.util.Iterator
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
                return;
            } catch (Exception e) {
                LOG.warn("cannot cache dereferenced data: " + e);
                return;
            }
        }
        if (input.isElement()) {
            this.referenceData = new ReferenceSubTreeData(input.getSubNode(), input.isExcludeComments());
            return;
        }
        if (input.isOctetStream() || input.isByteArray()) {
            try {
                this.referenceData = new ReferenceOctetStreamData(input.getOctetStream(), input.getSourceURI(), input.getMIMEType());
            } catch (IOException ioe) {
                LOG.warn("cannot cache dereferenced data: " + ioe);
            }
        }
    }

    public Transforms getTransforms() throws XMLSecurityException {
        return this.transforms;
    }

    public byte[] getReferencedBytes() throws XMLSignatureException {
        try {
            XMLSignatureInput output = dereferenceURIandPerformTransforms(null);
            return output.getBytes();
        } catch (IOException | CanonicalizationException ex) {
            throw new ReferenceNotInitializedException(ex);
        }
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x011b: MOVE (r1 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('diOs' org.apache.xml.security.utils.DigesterOutputStream)])
    , block:B:48:0x011b */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x0119: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:47:0x0119 */
    private byte[] calculateDigest(boolean validating) throws XMLSignatureException {
        Throwable th;
        DigesterOutputStream diOs;
        XMLSignatureInput input = getContentsBeforeTransformation();
        if (input.isPreCalculatedDigest()) {
            return getPreCalculatedDigest(input);
        }
        cacheDereferencedElement(input);
        MessageDigestAlgorithm mda = getMessageDigestAlgorithm();
        mda.reset();
        XMLSignatureInput output = null;
        try {
            try {
                try {
                    DigesterOutputStream diOs2 = new DigesterOutputStream(mda);
                    OutputStream os = new UnsyncBufferedOutputStream(diOs2);
                    try {
                        XMLSignatureInput output2 = getContentsAfterTransformation(input, os);
                        this.transformsOutput = output2;
                        if (!useC14N11 || validating || output2.isOutputStreamSet() || output2.isOctetStream()) {
                            output2.updateOutputStream(os);
                        } else {
                            if (this.transforms == null) {
                                this.transforms = new Transforms(getDocument());
                                this.transforms.setSecureValidation(this.secureValidation);
                                getElement().insertBefore(this.transforms.getElement(), this.digestMethodElem);
                            }
                            this.transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
                            output2.updateOutputStream(os, true);
                        }
                        os.flush();
                        byte[] digestValue = diOs2.getDigestValue();
                        $closeResource(null, os);
                        $closeResource(null, diOs2);
                        if (output2 != null) {
                            try {
                                if (output2.getOctetStreamReal() != null) {
                                    output2.getOctetStreamReal().close();
                                }
                            } catch (IOException ex) {
                                throw new ReferenceNotInitializedException(ex);
                            }
                        }
                        return digestValue;
                    } catch (Throwable th2) {
                        $closeResource(null, os);
                        throw th2;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, diOs);
                    throw th3;
                }
            } catch (IOException | XMLSecurityException ex2) {
                throw new ReferenceNotInitializedException(ex2);
            }
        } catch (Throwable th4) {
            if (0 != 0) {
                try {
                    if (output.getOctetStreamReal() != null) {
                        output.getOctetStreamReal().close();
                    }
                } catch (IOException ex3) {
                    throw new ReferenceNotInitializedException(ex3);
                }
            }
            throw th4;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    private byte[] getPreCalculatedDigest(XMLSignatureInput input) throws ReferenceNotInitializedException {
        LOG.debug("Verifying element with pre-calculated digest");
        String preCalculatedDigest = input.getPreCalculatedDigest();
        return XMLUtils.decode(preCalculatedDigest);
    }

    public byte[] getDigestValue() throws XMLSecurityException {
        if (this.digestValueElement == null) {
            Object[] exArgs = {"DigestValue", "http://www.w3.org/2000/09/xmldsig#"};
            throw new XMLSecurityException("signature.Verification.NoSignatureElement", exArgs);
        }
        String content = XMLUtils.getFullTextChildrenFromNode(this.digestValueElement);
        return XMLUtils.decode(content);
    }

    public boolean verify() throws XMLSecurityException {
        byte[] elemDig = getDigestValue();
        byte[] calcDig = calculateDigest(true);
        boolean equal = MessageDigestAlgorithm.isEqual(elemDig, calcDig);
        if (!equal) {
            LOG.warn("Verification failed for URI \"" + getURI() + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            LOG.warn("Expected Digest: " + XMLUtils.encodeToString(elemDig));
            LOG.warn("Actual Digest: " + XMLUtils.encodeToString(calcDig));
        } else {
            LOG.debug("Verification successful for URI \"{}\"", getURI());
        }
        return equal;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "Reference";
    }
}
