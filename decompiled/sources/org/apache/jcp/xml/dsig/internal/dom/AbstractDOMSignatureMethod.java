package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/AbstractDOMSignatureMethod.class */
abstract class AbstractDOMSignatureMethod extends DOMStructure implements SignatureMethod {

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/AbstractDOMSignatureMethod$Type.class */
    enum Type {
        DSA,
        RSA,
        ECDSA,
        EDDSA,
        HMAC
    }

    abstract boolean verify(Key key, SignedInfo signedInfo, byte[] bArr, XMLValidateContext xMLValidateContext) throws SignatureException, XMLSignatureException, InvalidKeyException;

    abstract byte[] sign(Key key, SignedInfo signedInfo, XMLSignContext xMLSignContext) throws XMLSignatureException, InvalidKeyException;

    abstract String getJCAAlgorithm();

    abstract Type getAlgorithmType();

    AbstractDOMSignatureMethod() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element smElem = DOMUtils.createElement(ownerDoc, "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttribute(smElem, "Algorithm", getAlgorithm());
        if (getParameterSpec() != null) {
            marshalParams(smElem, dsPrefix);
        }
        parent.appendChild(smElem);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    void marshalParams(Element parent, String paramsPrefix) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    SignatureMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
    }

    void checkParams(SignatureMethodParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm");
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureMethod)) {
            return false;
        }
        SignatureMethod osm = (SignatureMethod) o;
        return getAlgorithm().equals(osm.getAlgorithm()) && paramsEqual(osm.getParameterSpec());
    }

    public int hashCode() {
        int result = (31 * 17) + getAlgorithm().hashCode();
        AlgorithmParameterSpec spec = getParameterSpec();
        if (spec != null) {
            result = (31 * result) + spec.hashCode();
        }
        return result;
    }

    boolean paramsEqual(AlgorithmParameterSpec spec) {
        return getParameterSpec() == spec;
    }
}
