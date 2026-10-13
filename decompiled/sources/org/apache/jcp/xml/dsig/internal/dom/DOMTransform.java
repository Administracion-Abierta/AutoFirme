package org.apache.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMTransform.class */
public class DOMTransform extends DOMStructure implements Transform {
    protected TransformService spi;

    public DOMTransform(TransformService spi) {
        this.spi = spi;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMTransform(Element transElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        String algorithm = DOMUtils.getAttributeValue(transElem, "Algorithm");
        if (provider == null) {
            try {
                this.spi = TransformService.getInstance(algorithm, "DOM");
            } catch (NoSuchAlgorithmException e1) {
                throw new MarshalException(e1);
            }
        } else {
            try {
                this.spi = TransformService.getInstance(algorithm, "DOM", provider);
            } catch (NoSuchAlgorithmException e) {
                try {
                    this.spi = TransformService.getInstance(algorithm, "DOM");
                } catch (NoSuchAlgorithmException e2) {
                    throw new MarshalException(e2);
                }
            }
        }
        try {
            this.spi.init(new javax.xml.crypto.dom.DOMStructure(transElem), context);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.spi.getParameterSpec();
    }

    public final String getAlgorithm() {
        return this.spi.getAlgorithm();
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Element transformElem;
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        if ("Transforms".equals(parent.getLocalName())) {
            transformElem = DOMUtils.createElement(ownerDoc, Constants._TAG_TRANSFORM, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        } else {
            transformElem = DOMUtils.createElement(ownerDoc, Constants._TAG_CANONICALIZATIONMETHOD, "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        }
        DOMUtils.setAttribute(transformElem, "Algorithm", getAlgorithm());
        this.spi.marshalParams(new javax.xml.crypto.dom.DOMStructure(transformElem), context);
        parent.appendChild(transformElem);
    }

    public Data transform(Data data, XMLCryptoContext xc) throws TransformException {
        return this.spi.transform(data, xc);
    }

    public Data transform(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        return this.spi.transform(data, xc, os);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transform)) {
            return false;
        }
        Transform otransform = (Transform) o;
        return getAlgorithm().equals(otransform.getAlgorithm()) && DOMUtils.paramsEqual(getParameterSpec(), otransform.getParameterSpec());
    }

    public int hashCode() {
        int result = (31 * 17) + getAlgorithm().hashCode();
        AlgorithmParameterSpec spec = getParameterSpec();
        if (spec != null) {
            result = (31 * result) + spec.hashCode();
        }
        return result;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    Data transform(Data data, XMLCryptoContext xc, DOMSignContext context) throws TransformException, MarshalException {
        marshal(context.getParent(), DOMUtils.getSignaturePrefix(context), context);
        return transform(data, xc);
    }
}
