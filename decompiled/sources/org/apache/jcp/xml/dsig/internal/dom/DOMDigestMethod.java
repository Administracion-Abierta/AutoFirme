package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod.class */
public abstract class DOMDigestMethod extends DOMStructure implements DigestMethod {
    static final String SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    static final String WHIRLPOOL = "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
    static final String SHA3_224 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
    static final String SHA3_256 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
    static final String SHA3_384 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
    static final String SHA3_512 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
    private DigestMethodParameterSpec params;

    abstract String getMessageDigestAlgorithm();

    DOMDigestMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof DigestMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec");
        }
        checkParams((DigestMethodParameterSpec) params);
        this.params = (DigestMethodParameterSpec) params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    DOMDigestMethod(Element dmElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(dmElem);
        if (paramsElem != null) {
            this.params = unmarshalParams(paramsElem);
        }
        try {
            checkParams(this.params);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new MarshalException(iape);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    static DigestMethod unmarshal(Element dmElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(dmElem, "Algorithm");
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#sha1")) {
            return new SHA1(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#sha224")) {
            return new SHA224(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#sha256")) {
            return new SHA256(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#sha384")) {
            return new SHA384(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#sha512")) {
            return new SHA512(dmElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmlenc#ripemd160")) {
            return new RIPEMD160(dmElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#whirlpool")) {
            return new WHIRLPOOL(dmElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-224")) {
            return new SHA3_224(dmElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-256")) {
            return new SHA3_256(dmElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-384")) {
            return new SHA3_384(dmElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-512")) {
            return new SHA3_512(dmElem);
        }
        throw new MarshalException("unsupported DigestMethod algorithm: " + alg);
    }

    void checkParams(DigestMethodParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
        }
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    DigestMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String prefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element dmElem = DOMUtils.createElement(ownerDoc, "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", prefix);
        DOMUtils.setAttribute(dmElem, "Algorithm", getAlgorithm());
        if (this.params != null) {
            marshalParams(dmElem, prefix);
        }
        parent.appendChild(dmElem);
    }

    public boolean equals(Object o) {
        boolean zEquals;
        if (this == o) {
            return true;
        }
        if (!(o instanceof DigestMethod)) {
            return false;
        }
        DigestMethod odm = (DigestMethod) o;
        if (this.params == null) {
            zEquals = odm.getParameterSpec() == null;
        } else {
            zEquals = this.params.equals(odm.getParameterSpec());
        }
        boolean paramsEqual = zEquals;
        return getAlgorithm().equals(odm.getAlgorithm()) && paramsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.params != null) {
            result = (31 * 17) + this.params.hashCode();
        }
        return (31 * result) + getAlgorithm().hashCode();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    void marshalParams(Element parent, String prefix) throws MarshalException {
        throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm");
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA1.class */
    static final class SHA1 extends DOMDigestMethod {
        SHA1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#sha1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA-1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA224.class */
    static final class SHA224 extends DOMDigestMethod {
        SHA224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#sha224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA-224";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA256.class */
    static final class SHA256 extends DOMDigestMethod {
        SHA256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA-256";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA384.class */
    static final class SHA384 extends DOMDigestMethod {
        SHA384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#sha384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA-384";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA512.class */
    static final class SHA512 extends DOMDigestMethod {
        SHA512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#sha512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA-512";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$RIPEMD160.class */
    static final class RIPEMD160 extends DOMDigestMethod {
        RIPEMD160(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmlenc#ripemd160";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "RIPEMD160";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$WHIRLPOOL.class */
    static final class WHIRLPOOL extends DOMDigestMethod {
        WHIRLPOOL(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        WHIRLPOOL(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "WHIRLPOOL";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA3_224.class */
    static final class SHA3_224 extends DOMDigestMethod {
        SHA3_224(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_224(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return MessageDigestAlgorithms.SHA3_224;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA3_256.class */
    static final class SHA3_256 extends DOMDigestMethod {
        SHA3_256(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_256(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return "SHA3-256";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA3_384.class */
    static final class SHA3_384 extends DOMDigestMethod {
        SHA3_384(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_384(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return MessageDigestAlgorithms.SHA3_384;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMDigestMethod$SHA3_512.class */
    static final class SHA3_512 extends DOMDigestMethod {
        SHA3_512(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_512(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMDigestMethod
        String getMessageDigestAlgorithm() {
            return MessageDigestAlgorithms.SHA3_512;
        }
    }
}
