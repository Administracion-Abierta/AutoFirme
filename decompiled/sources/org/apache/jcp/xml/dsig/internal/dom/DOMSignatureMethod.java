package org.apache.jcp.xml.dsig.internal.dom;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.jcp.xml.dsig.internal.SignerOutputStream;
import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.JavaUtils;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod.class */
public abstract class DOMSignatureMethod extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.SignatureProvider";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMSignatureMethod.class);
    private SignatureMethodParameterSpec params;
    private Signature signature;
    static final String RSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    static final String RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    static final String RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    static final String RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    static final String ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    static final String ECDSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    static final String ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    static final String ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    static final String ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    static final String DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    static final String ED25519 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519";
    static final String ED448 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448";
    static final String ECDSA_SHA3_224 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-224";
    static final String ECDSA_SHA3_256 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-256";
    static final String ECDSA_SHA3_384 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-384";
    static final String ECDSA_SHA3_512 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-512";
    static final String ECDSA_RIPEMD160 = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    static final String RSA_SHA1_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
    static final String RSA_SHA224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
    static final String RSA_SHA256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
    static final String RSA_SHA384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
    static final String RSA_SHA512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
    static final String RSA_RIPEMD160_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#ripemd160-rsa-MGF1";
    static final String RSA_SHA3_224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
    static final String RSA_SHA3_256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
    static final String RSA_SHA3_384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
    static final String RSA_SHA3_512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";

    abstract byte[] postSignFormat(Key key, byte[] bArr) throws IOException;

    abstract byte[] preVerifyFormat(Key key, byte[] bArr) throws IOException;

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

    DOMSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
        }
        checkParams((SignatureMethodParameterSpec) params);
        this.params = (SignatureMethodParameterSpec) params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    DOMSignatureMethod(Element smElem) throws MarshalException {
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

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    static SignatureMethod unmarshal(Element smElem) throws MarshalException {
        String alg = DOMUtils.getAttributeValue(smElem, "Algorithm");
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1")) {
            return new SHA1withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224")) {
            return new SHA224withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256")) {
            return new SHA256withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384")) {
            return new SHA384withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {
            return new SHA512withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160")) {
            return new RIPEMD160withRSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1")) {
            return new SHA1withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1")) {
            return new SHA224withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1")) {
            return new SHA256withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1")) {
            return new SHA384withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1")) {
            return new SHA512withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1")) {
            return new SHA3_224withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1")) {
            return new SHA3_256withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1")) {
            return new SHA3_384withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1")) {
            return new SHA3_512withRSAandMGF1(smElem);
        }
        if (alg.equals(XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS)) {
            return new DOMRSAPSSSignatureMethod.RSAPSS(smElem);
        }
        if (alg.equals(RSA_RIPEMD160_MGF1)) {
            return new RIPEMD160withRSAandMGF1(smElem);
        }
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1")) {
            return new SHA1withDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256")) {
            return new SHA256withDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1")) {
            return new SHA1withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224")) {
            return new SHA224withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256")) {
            return new SHA256withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384")) {
            return new SHA384withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512")) {
            return new SHA512withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-224")) {
            return new SHA3_224withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-256")) {
            return new SHA3_256withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-384")) {
            return new SHA3_384withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-512")) {
            return new SHA3_512withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160")) {
            return new RIPEMD160withECDSA(smElem);
        }
        if (alg.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1")) {
            return new DOMHMACSignatureMethod.SHA1(smElem);
        }
        if (alg.equals(XMLSignature.ALGO_ID_MAC_HMAC_SHA224)) {
            return new DOMHMACSignatureMethod.SHA224(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256")) {
            return new DOMHMACSignatureMethod.SHA256(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384")) {
            return new DOMHMACSignatureMethod.SHA384(smElem);
        }
        if (alg.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512")) {
            return new DOMHMACSignatureMethod.SHA512(smElem);
        }
        if (alg.equals(XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160)) {
            return new DOMHMACSignatureMethod.RIPEMD160(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519")) {
            return new EDDSA_ED25519(smElem);
        }
        if (alg.equals("http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448")) {
            return new EDDSA_ED448(smElem);
        }
        throw new MarshalException("unsupported SignatureMethod algorithm: " + alg);
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    Signature getSignature(Provider p) throws NoSuchAlgorithmException {
        if (p == null) {
            return Signature.getInstance(getJCAAlgorithm());
        }
        return Signature.getInstance(getJCAAlgorithm(), p);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws Exception {
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (this.signature == null) {
            Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
            try {
                this.signature = getSignature(p);
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        this.signature.initVerify((PublicKey) key);
        LOG.debug("Signature provider: {}", this.signature.getProvider());
        LOG.debug("Verifying with key: {}", key);
        LOG.debug("JCA Algorithm: {}", getJCAAlgorithm());
        LOG.debug("Signature Bytes length: {}", Integer.valueOf(sig.length));
        try {
            SignerOutputStream outputStream = new SignerOutputStream(this.signature);
            Throwable th = null;
            try {
                try {
                    ((DOMSignedInfo) si).canonicalize(context, outputStream);
                    byte[] s = preVerifyFormat(key, sig);
                    $closeResource(null, outputStream);
                    return this.signature.verify(s);
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, outputStream);
                throw th3;
            }
        } catch (IOException ioe) {
            throw new XMLSignatureException(ioe);
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

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws XMLSignatureException, InvalidKeyException {
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (this.signature == null) {
            Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
            try {
                this.signature = getSignature(p);
            } catch (NoSuchAlgorithmException nsae) {
                throw new XMLSignatureException(nsae);
            }
        }
        this.signature.initSign((PrivateKey) key);
        LOG.debug("Signature provider: {}", this.signature.getProvider());
        LOG.debug("JCA Algorithm: {}", getJCAAlgorithm());
        try {
            SignerOutputStream outputStream = new SignerOutputStream(this.signature);
            Throwable th = null;
            try {
                try {
                    ((DOMSignedInfo) si).canonicalize(context, outputStream);
                    byte[] bArrPostSignFormat = postSignFormat(key, this.signature.sign());
                    $closeResource(null, outputStream);
                    return bArrPostSignFormat;
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, outputStream);
                throw th3;
            }
        } catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractRSASignatureMethod.class */
    static abstract class AbstractRSASignatureMethod extends DOMSignatureMethod {
        AbstractRSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractRSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] postSignFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] preVerifyFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractRSAPSSSignatureMethod.class */
    static abstract class AbstractRSAPSSSignatureMethod extends AbstractRSASignatureMethod {
        public abstract PSSParameterSpec getPSSParameterSpec();

        AbstractRSAPSSSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractRSAPSSSignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        Signature getSignature(Provider p) throws NoSuchAlgorithmException {
            Signature signature;
            try {
                if (p == null) {
                    signature = Signature.getInstance("RSASSA-PSS");
                } else {
                    signature = Signature.getInstance("RSASSA-PSS", p);
                }
                Signature s = signature;
                try {
                    s.setParameter(getPSSParameterSpec());
                    return s;
                } catch (InvalidAlgorithmParameterException e) {
                    throw new NoSuchAlgorithmException("Should not happen", e);
                }
            } catch (NoSuchAlgorithmException e2) {
                return super.getSignature(p);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractP1363FormatSignatureMethod.class */
    static abstract class AbstractP1363FormatSignatureMethod extends DOMSignatureMethod {
        boolean asn1;

        abstract String getJCAFallbackAlgorithm();

        AbstractP1363FormatSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractP1363FormatSignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        Signature getSignature(Provider p) throws NoSuchAlgorithmException {
            Signature signature;
            Signature signature2;
            try {
                if (p == null) {
                    signature2 = Signature.getInstance(getJCAAlgorithm());
                } else {
                    signature2 = Signature.getInstance(getJCAAlgorithm(), p);
                }
                return signature2;
            } catch (NoSuchAlgorithmException e) {
                if (p == null) {
                    signature = Signature.getInstance(getJCAFallbackAlgorithm());
                } else {
                    signature = Signature.getInstance(getJCAFallbackAlgorithm(), p);
                }
                Signature s = signature;
                this.asn1 = true;
                return s;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractDSASignatureMethod.class */
    static abstract class AbstractDSASignatureMethod extends AbstractP1363FormatSignatureMethod {
        AbstractDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] postSignFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int size = ((DSAKey) key).getParams().getQ().bitLength();
                return JavaUtils.convertDsaASN1toXMLDSIG(sig, size / 8);
            }
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] preVerifyFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int size = ((DSAKey) key).getParams().getQ().bitLength();
                return JavaUtils.convertDsaXMLDSIGtoASN1(sig, size / 8);
            }
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.DSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractECDSASignatureMethod.class */
    static abstract class AbstractECDSASignatureMethod extends AbstractP1363FormatSignatureMethod {
        AbstractECDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractECDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] postSignFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                int rawLen = -1;
                if (key instanceof ECPrivateKey) {
                    ECPrivateKey ecKey = (ECPrivateKey) key;
                    rawLen = (ecKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
                }
                return SignatureECDSA.convertASN1toXMLDSIG(sig, rawLen);
            }
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] preVerifyFormat(Key key, byte[] sig) throws IOException {
            if (this.asn1) {
                return SignatureECDSA.convertXMLDSIGtoASN1(sig);
            }
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.ECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$AbstractEDDSASignatureMethod.class */
    static abstract class AbstractEDDSASignatureMethod extends DOMSignatureMethod {
        AbstractEDDSASignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        AbstractEDDSASignatureMethod(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] postSignFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod
        byte[] preVerifyFormat(Key key, byte[] sig) {
            return sig;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.EDDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA1withRSA.class */
    static final class SHA1withRSA extends AbstractRSASignatureMethod {
        SHA1withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA1WITHRSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA224withRSA.class */
    static final class SHA224withRSA extends AbstractRSASignatureMethod {
        SHA224withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA224withRSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA256withRSA.class */
    static final class SHA256withRSA extends AbstractRSASignatureMethod {
        SHA256withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA384withRSA.class */
    static final class SHA384withRSA extends AbstractRSASignatureMethod {
        SHA384withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA384WITHRSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA512withRSA.class */
    static final class SHA512withRSA extends AbstractRSASignatureMethod {
        SHA512withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA512withRSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$RIPEMD160withRSA.class */
    static final class RIPEMD160withRSA extends AbstractRSASignatureMethod {
        RIPEMD160withRSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withRSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "RIPEMD160withRSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA1withRSAandMGF1.class */
    static final class SHA1withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA1_MGF1_PARAMS = new PSSParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, 20, 1);

        SHA1withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA1_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA1withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA224withRSAandMGF1.class */
    static final class SHA224withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA224_MGF1_PARAMS = new PSSParameterSpec("SHA-224", "MGF1", MGF1ParameterSpec.SHA224, 28, 1);

        SHA224withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA224_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA224withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA256withRSAandMGF1.class */
    static final class SHA256withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA256_MGF1_PARAMS = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);

        SHA256withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA256_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA256withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA384withRSAandMGF1.class */
    static final class SHA384withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA384_MGF1_PARAMS = new PSSParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1);

        SHA384withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA384_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA384withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA512withRSAandMGF1.class */
    static final class SHA512withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA512_MGF1_PARAMS = new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1);

        SHA512withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA512_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA512withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_224withRSAandMGF1.class */
    static final class SHA3_224withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA3_224_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_224, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_224), 28, 1);

        SHA3_224withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_224withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_224_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-224withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_256withRSAandMGF1.class */
    static final class SHA3_256withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA3_256_MGF1_PARAMS = new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1);

        SHA3_256withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_256withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_256_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-256withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_384withRSAandMGF1.class */
    static final class SHA3_384withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA3_384_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_384, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_384), 48, 1);

        SHA3_384withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_384withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_384_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-384withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_512withRSAandMGF1.class */
    static final class SHA3_512withRSAandMGF1 extends AbstractRSAPSSSignatureMethod {
        private static final PSSParameterSpec SHA3_512_MGF1_PARAMS = new PSSParameterSpec(MessageDigestAlgorithms.SHA3_512, "MGF1", new MGF1ParameterSpec(MessageDigestAlgorithms.SHA3_512), 64, 1);

        SHA3_512withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_512withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractRSAPSSSignatureMethod
        public PSSParameterSpec getPSSParameterSpec() {
            return SHA3_512_MGF1_PARAMS;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-512withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$RIPEMD160withRSAandMGF1.class */
    static final class RIPEMD160withRSAandMGF1 extends AbstractRSASignatureMethod {
        RIPEMD160withRSAandMGF1(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withRSAandMGF1(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return DOMSignatureMethod.RSA_RIPEMD160_MGF1;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "RIPEMD160withRSAandMGF1";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA1withDSA.class */
    static final class SHA1withDSA extends AbstractDSASignatureMethod {
        SHA1withDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA1withDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA1WITHDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA256withDSA.class */
    static final class SHA256withDSA extends AbstractDSASignatureMethod {
        SHA256withDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA256withDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "SHA256withDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA1withECDSA.class */
    static final class SHA1withECDSA extends AbstractECDSASignatureMethod {
        SHA1withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA1withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA1withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA1WITHECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA224withECDSA.class */
    static final class SHA224withECDSA extends AbstractECDSASignatureMethod {
        SHA224withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA224withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA224withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA224WITHECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA256withECDSA.class */
    static final class SHA256withECDSA extends AbstractECDSASignatureMethod {
        SHA256withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA256withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA256withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA384withECDSA.class */
    static final class SHA384withECDSA extends AbstractECDSASignatureMethod {
        SHA384withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA384withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA384withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA512withECDSA.class */
    static final class SHA512withECDSA extends AbstractECDSASignatureMethod {
        SHA512withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA512withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA512withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return AOSignConstants.SIGN_ALGORITHM_SHA512WITHECDSA;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_224withECDSA.class */
    static final class SHA3_224withECDSA extends AbstractECDSASignatureMethod {
        SHA3_224withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_224withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-224";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-224withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "SHA3-224withECDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_256withECDSA.class */
    static final class SHA3_256withECDSA extends AbstractECDSASignatureMethod {
        SHA3_256withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_256withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-256";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-256withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "SHA3-256withECDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_384withECDSA.class */
    static final class SHA3_384withECDSA extends AbstractECDSASignatureMethod {
        SHA3_384withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_384withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-384";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-384withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "SHA3-384withECDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$SHA3_512withECDSA.class */
    static final class SHA3_512withECDSA extends AbstractECDSASignatureMethod {
        SHA3_512withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        SHA3_512withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-512";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "SHA3-512withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "SHA3-512withECDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$RIPEMD160withECDSA.class */
    static final class RIPEMD160withECDSA extends AbstractECDSASignatureMethod {
        RIPEMD160withECDSA(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RIPEMD160withECDSA(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "RIPEMD160withECDSAinP1363Format";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMSignatureMethod.AbstractP1363FormatSignatureMethod
        String getJCAFallbackAlgorithm() {
            return "RIPEMD160withECDSA";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$EDDSA_ED25519.class */
    static final class EDDSA_ED25519 extends AbstractEDDSASignatureMethod {
        EDDSA_ED25519(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        EDDSA_ED25519(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return EdDSAParameterSpec.Ed25519;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMSignatureMethod$EDDSA_ED448.class */
    static final class EDDSA_ED448 extends AbstractEDDSASignatureMethod {
        EDDSA_ED448(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        EDDSA_ED448(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return EdDSAParameterSpec.Ed448;
        }
    }
}
