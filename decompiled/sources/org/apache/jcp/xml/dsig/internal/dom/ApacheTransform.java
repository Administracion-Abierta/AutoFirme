package org.apache.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/ApacheTransform.class */
public abstract class ApacheTransform extends TransformService {
    private static final Logger LOG;
    private Transform transform;
    protected Document ownerDoc;
    protected Element transformElem;
    protected TransformParameterSpec params;

    static {
        Init.init();
        LOG = LoggerFactory.getLogger((Class<?>) ApacheTransform.class);
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    public void init(XMLStructure parent, XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        if (context != null && !(context instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        if (parent == null) {
            throw new NullPointerException();
        }
        if (!(parent instanceof javax.xml.crypto.dom.DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }
        this.transformElem = (Element) ((javax.xml.crypto.dom.DOMStructure) parent).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }

    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        if (context != null && !(context instanceof DOMCryptoContext)) {
            throw new ClassCastException("context must be of type DOMCryptoContext");
        }
        if (parent == null) {
            throw new NullPointerException();
        }
        if (!(parent instanceof javax.xml.crypto.dom.DOMStructure)) {
            throw new ClassCastException("parent must be of type DOMStructure");
        }
        this.transformElem = (Element) ((javax.xml.crypto.dom.DOMStructure) parent).getNode();
        this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
    }

    public Data transform(Data data, XMLCryptoContext xc) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        return transformIt(data, xc, null);
    }

    public Data transform(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        if (data == null) {
            throw new NullPointerException("data must not be null");
        }
        if (os == null) {
            throw new NullPointerException("output stream must not be null");
        }
        return transformIt(data, xc, os);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.TransformException */
    private Data transformIt(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        XMLSignatureInput in;
        XMLSignatureInput in2;
        if (this.ownerDoc == null) {
            throw new TransformException("transform must be marshalled");
        }
        if (this.transform == null) {
            try {
                this.transform = new Transform(this.ownerDoc, getAlgorithm(), this.transformElem.getChildNodes());
                this.transform.setElement(this.transformElem, xc.getBaseURI());
                LOG.debug("Created transform for algorithm: {}", getAlgorithm());
            } catch (Exception ex) {
                throw new TransformException("Couldn't find Transform for: " + getAlgorithm(), ex);
            }
        }
        if (Utils.secureValidation(xc)) {
            String algorithm = getAlgorithm();
            if (Transforms.TRANSFORM_XSLT.equals(algorithm)) {
                throw new TransformException("Transform " + algorithm + " is forbidden when secure validation is enabled");
            }
        }
        if (data instanceof ApacheData) {
            LOG.debug("ApacheData = true");
            in = ((ApacheData) data).getXMLSignatureInput();
        } else if (data instanceof NodeSetData) {
            LOG.debug("isNodeSet() = true");
            if (data instanceof DOMSubTreeData) {
                LOG.debug("DOMSubTreeData = true");
                DOMSubTreeData subTree = (DOMSubTreeData) data;
                in = new XMLSignatureInput(subTree.getRoot());
                in.setExcludeComments(subTree.excludeComments());
            } else {
                Set<Node> nodeSet = Utils.toNodeSet(((NodeSetData) data).iterator());
                in = new XMLSignatureInput(nodeSet);
            }
        } else {
            LOG.debug("isNodeSet() = false");
            try {
                in = new XMLSignatureInput(((OctetStreamData) data).getOctetStream());
            } catch (Exception ex2) {
                throw new TransformException(ex2);
            }
        }
        boolean secVal = Utils.secureValidation(xc);
        in.setSecureValidation(secVal);
        try {
            if (os != null) {
                in2 = this.transform.performTransform(in, os, secVal);
                if (!in2.isNodeSet() && !in2.isElement()) {
                    return null;
                }
            } else {
                in2 = this.transform.performTransform(in, secVal);
            }
            if (in2.isOctetStream()) {
                return new ApacheOctetStreamData(in2);
            }
            return new ApacheNodeSetData(in2);
        } catch (Exception ex3) {
            throw new TransformException(ex3);
        }
    }

    public final boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }
}
