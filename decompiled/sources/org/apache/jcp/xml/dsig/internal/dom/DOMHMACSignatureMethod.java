package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.MacOutputStream;
import org.apache.xml.security.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod.class */
public abstract class DOMHMACSignatureMethod extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.MacProvider";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMHMACSignatureMethod.class);
    static final String HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
    static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    static final String HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    private Mac hmac;
    private int outputLength;
    private boolean outputLengthSet;
    private SignatureMethodParameterSpec params;

    abstract int getDigestLength();

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod, org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public /* bridge */ /* synthetic */ void marshal(Node node, String str, DOMCryptoContext dOMCryptoContext) throws MarshalException {
        super.marshal(node, str, dOMCryptoContext);
    }

    DOMHMACSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        checkParams((SignatureMethodParameterSpec) params);
        this.params = (SignatureMethodParameterSpec) params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    DOMHMACSignatureMethod(Element smElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(smElem);
        if (paramsElem != null) {
            this.params = unmarshalParams(paramsElem);
        }
        try {
            checkParams(this.params);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    void checkParams(SignatureMethodParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            if (!(params instanceof HMACParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec");
            }
            this.outputLength = ((HMACParameterSpec) params).getOutputLength();
            this.outputLengthSet = true;
            LOG.debug("Setting outputLength from HMACParameterSpec to: {}", Integer.valueOf(this.outputLength));
        }
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    SignatureMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        try {
            this.outputLength = Integer.parseInt(paramsElem.getFirstChild().getNodeValue());
            this.outputLengthSet = true;
            LOG.debug("unmarshalled outputLength: {}", Integer.valueOf(this.outputLength));
            return new HMACParameterSpec(this.outputLength);
        } catch (NumberFormatException e) {
            throw new MarshalException("Invalid output length supplied: " + paramsElem.getFirstChild().getNodeValue());
        }
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    void marshalParams(Element parent, String prefix) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element hmacElem = DOMUtils.createElement(ownerDoc, Constants._TAG_HMACOUTPUTLENGTH, "http://www.w3.org/2000/09/xmldsig#", prefix);
        hmacElem.appendChild(ownerDoc.createTextNode(String.valueOf(this.outputLength)));
        parent.appendChild(hmacElem);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws SignatureException, XMLSignatureException, InvalidKeyException {
        Mac mac;
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
        }
        if (this.hmac == null) {
            try {
                Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
                if (p == null) {
                    mac = Mac.getInstance(getJCAAlgorithm());
                } else {
                    mac = Mac.getInstance(getJCAAlgorithm(), p);
                }
                this.hmac = mac;
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        if (this.outputLengthSet && this.outputLength < getDigestLength()) {
            throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength());
        }
        this.hmac.init(key);
        ((DOMSignedInfo) si).canonicalize(context, new MacOutputStream(this.hmac));
        byte[] result = this.hmac.doFinal();
        return MessageDigest.isEqual(sig, result);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws XMLSignatureException, InvalidKeyException {
        Mac mac;
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("key must be SecretKey");
        }
        if (this.hmac == null) {
            try {
                Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
                if (p == null) {
                    mac = Mac.getInstance(getJCAAlgorithm());
                } else {
                    mac = Mac.getInstance(getJCAAlgorithm(), p);
                }
                this.hmac = mac;
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        if (this.outputLengthSet && this.outputLength < getDigestLength()) {
            throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength());
        }
        this.hmac.init(key);
        ((DOMSignedInfo) si).canonicalize(context, new MacOutputStream(this.hmac));
        return this.hmac.doFinal();
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    boolean paramsEqual(AlgorithmParameterSpec spec) {
        if (getParameterSpec() == spec) {
            return true;
        }
        if (!(spec instanceof HMACParameterSpec)) {
            return false;
        }
        HMACParameterSpec ospec = (HMACParameterSpec) spec;
        return this.outputLength == ospec.getOutputLength();
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    AbstractDOMSignatureMethod.Type getAlgorithmType() {
        return AbstractDOMSignatureMethod.Type.HMAC;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$SHA1.class */
    static final class SHA1 extends DOMHMACSignatureMethod {
        SHA1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HmacSHA1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 160;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$SHA224.class */
    static final class SHA224 extends DOMHMACSignatureMethod {
        SHA224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HmacSHA224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 224;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$SHA256.class */
    static final class SHA256 extends DOMHMACSignatureMethod {
        SHA256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HmacSHA256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 256;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$SHA384.class */
    static final class SHA384 extends DOMHMACSignatureMethod {
        SHA384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HmacSHA384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 384;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$SHA512.class */
    static final class SHA512 extends DOMHMACSignatureMethod {
        SHA512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HmacSHA512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 512;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMHMACSignatureMethod$RIPEMD160.class */
    static final class RIPEMD160 extends DOMHMACSignatureMethod {
        RIPEMD160(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "HMACRIPEMD160";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMHMACSignatureMethod
        int getDigestLength() {
            return 160;
        }
    }
}
