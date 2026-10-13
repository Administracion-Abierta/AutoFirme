package org.apache.jcp.xml.dsig.internal.dom;

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
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.SignerOutputStream;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMRSAPSSSignatureMethod.class */
public abstract class DOMRSAPSSSignatureMethod extends AbstractDOMSignatureMethod {
    private static final String DOM_SIGNATURE_PROVIDER = "org.jcp.xml.dsig.internal.dom.SignatureProvider";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMRSAPSSSignatureMethod.class);
    private final SignatureMethodParameterSpec params;
    private Signature signature;
    static final String RSA_PSS = "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
    private int trailerField = 1;
    private int saltLength = 32;
    private String digestName = "SHA-256";

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

    DOMRSAPSSSignatureMethod(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof SignatureMethodParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec");
        }
        params = params == null ? getDefaultParameterSpec() : params;
        checkParams((SignatureMethodParameterSpec) params);
        this.params = (SignatureMethodParameterSpec) params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    DOMRSAPSSSignatureMethod(Element smElem) throws MarshalException {
        Element paramsElem = DOMUtils.getFirstChildElement(smElem);
        if (paramsElem != null) {
            this.params = unmarshalParams(paramsElem);
        } else {
            this.params = getDefaultParameterSpec();
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
            if (!(params instanceof RSAPSSParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params must be of type RSAPSSParameterSpec");
            }
            if (((RSAPSSParameterSpec) params).getTrailerField() > 0) {
                this.trailerField = ((RSAPSSParameterSpec) params).getTrailerField();
                LOG.debug("Setting trailerField from RSAPSSParameterSpec to: {}", Integer.valueOf(this.trailerField));
            }
            if (((RSAPSSParameterSpec) params).getSaltLength() > 0) {
                this.saltLength = ((RSAPSSParameterSpec) params).getSaltLength();
                LOG.debug("Setting saltLength from RSAPSSParameterSpec to: {}", Integer.valueOf(this.saltLength));
            }
            if (((RSAPSSParameterSpec) params).getDigestName() != null) {
                this.digestName = ((RSAPSSParameterSpec) params).getDigestName();
                LOG.debug("Setting digestName from RSAPSSParameterSpec to: {}", this.digestName);
            }
        }
    }

    public final AlgorithmParameterSpec getParameterSpec() {
        return this.params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    void marshalParams(Element element, String prefix) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(element);
        Element rsaPssParamsElement = ownerDoc.createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:RSAPSSParams");
        rsaPssParamsElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pss", "http://www.w3.org/2007/05/xmldsig-more#");
        Element digestMethodElement = DOMUtils.createElement(rsaPssParamsElement.getOwnerDocument(), "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", prefix);
        try {
            digestMethodElement.setAttributeNS(null, "Algorithm", SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromDigestAlgorithm(this.digestName).getXmlDigestAlgorithm());
            rsaPssParamsElement.appendChild(digestMethodElement);
            Element saltLengthElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:SaltLength");
            Text saltLengthText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.saltLength));
            saltLengthElement.appendChild(saltLengthText);
            rsaPssParamsElement.appendChild(saltLengthElement);
            Element trailerFieldElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:TrailerField");
            Text trailerFieldText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.trailerField));
            trailerFieldElement.appendChild(trailerFieldText);
            rsaPssParamsElement.appendChild(trailerFieldElement);
            element.appendChild(rsaPssParamsElement);
        } catch (XMLSignatureException | DOMException e) {
            throw new MarshalException("Invalid digest name supplied: " + this.digestName);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    SignatureMethodParameterSpec unmarshalParams(Element paramsElem) throws MarshalException {
        if (paramsElem != null) {
            Element saltLengthNode = XMLUtils.selectNode(paramsElem.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_SALTLENGTH, 0);
            Element trailerFieldNode = XMLUtils.selectNode(paramsElem.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_TRAILERFIELD, 0);
            int trailerField = 1;
            if (trailerFieldNode != null) {
                try {
                    trailerField = Integer.parseInt(trailerFieldNode.getTextContent());
                } catch (NumberFormatException e) {
                    throw new MarshalException("Invalid trailer field supplied: " + trailerFieldNode.getTextContent());
                }
            }
            String xmlAlgorithm = XMLUtils.selectDsNode(paramsElem.getFirstChild(), "DigestMethod", 0).getAttribute("Algorithm");
            try {
                SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm digestAlgorithm = SignatureBaseRSA.SignatureRSASSAPSS.DigestAlgorithm.fromXmlDigestAlgorithm(xmlAlgorithm);
                String digestName = digestAlgorithm.getDigestAlgorithm();
                RSAPSSParameterSpec params = new RSAPSSParameterSpec();
                params.setTrailerField(trailerField);
                try {
                    int saltLength = saltLengthNode == null ? digestAlgorithm.getSaltLength() : Integer.parseInt(saltLengthNode.getTextContent());
                    params.setSaltLength(saltLength);
                    params.setDigestName(digestName);
                    return params;
                } catch (NumberFormatException e2) {
                    throw new MarshalException("Invalid salt length supplied: " + saltLengthNode.getTextContent());
                }
            } catch (XMLSignatureException e3) {
                throw new MarshalException("Invalid digest algorithm supplied: " + xmlAlgorithm);
            }
        }
        return getDefaultParameterSpec();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    boolean verify(Key key, SignedInfo si, byte[] sig, XMLValidateContext context) throws Exception {
        Signature signature;
        if (key == null || si == null || sig == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("key must be PublicKey");
        }
        if (this.signature == null) {
            try {
                Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
                if (p == null) {
                    signature = Signature.getInstance(getJCAAlgorithm());
                } else {
                    signature = Signature.getInstance(getJCAAlgorithm(), p);
                }
                this.signature = signature;
            } catch (NoSuchAlgorithmException nsae) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(nsae);
            }
        }
        this.signature.initVerify((PublicKey) key);
        try {
            this.signature.setParameter(new PSSParameterSpec(this.digestName, "MGF1", new MGF1ParameterSpec(this.digestName), this.saltLength, this.trailerField));
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
                        boolean zVerify = this.signature.verify(sig);
                        $closeResource(null, outputStream);
                        return zVerify;
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, outputStream);
                    throw th3;
                }
            } catch (IOException ioe) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(ioe);
            }
        } catch (InvalidAlgorithmParameterException e) {
            throw new javax.xml.crypto.dsig.XMLSignatureException(e);
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
    byte[] sign(Key key, SignedInfo si, XMLSignContext context) throws javax.xml.crypto.dsig.XMLSignatureException, InvalidKeyException {
        Signature signature;
        if (key == null || si == null) {
            throw new NullPointerException();
        }
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("key must be PrivateKey");
        }
        if (this.signature == null) {
            try {
                Provider p = (Provider) context.getProperty(DOM_SIGNATURE_PROVIDER);
                if (p == null) {
                    signature = Signature.getInstance(getJCAAlgorithm());
                } else {
                    signature = Signature.getInstance(getJCAAlgorithm(), p);
                }
                this.signature = signature;
            } catch (NoSuchAlgorithmException nsae) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(nsae);
            }
        }
        this.signature.initSign((PrivateKey) key);
        try {
            this.signature.setParameter(new PSSParameterSpec(this.digestName, "MGF1", new MGF1ParameterSpec(this.digestName), this.saltLength, this.trailerField));
            LOG.debug("Signature provider: {}", this.signature.getProvider());
            LOG.debug("JCA Algorithm: {}", getJCAAlgorithm());
            try {
                SignerOutputStream outputStream = new SignerOutputStream(this.signature);
                Throwable th = null;
                try {
                    try {
                        ((DOMSignedInfo) si).canonicalize(context, outputStream);
                        byte[] bArrSign = this.signature.sign();
                        $closeResource(null, outputStream);
                        return bArrSign;
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, outputStream);
                    throw th3;
                }
            } catch (IOException | SignatureException e) {
                throw new javax.xml.crypto.dsig.XMLSignatureException(e);
            }
        } catch (InvalidAlgorithmParameterException e2) {
            throw new javax.xml.crypto.dsig.XMLSignatureException(e2);
        }
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
    boolean paramsEqual(AlgorithmParameterSpec spec) {
        return getParameterSpec().equals(spec);
    }

    private SignatureMethodParameterSpec getDefaultParameterSpec() {
        RSAPSSParameterSpec params = new RSAPSSParameterSpec();
        params.setTrailerField(this.trailerField);
        params.setSaltLength(this.saltLength);
        params.setDigestName(this.digestName);
        return params;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/jcp/xml/dsig/internal/dom/DOMRSAPSSSignatureMethod$RSAPSS.class */
    static final class RSAPSS extends DOMRSAPSSSignatureMethod {
        RSAPSS(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
            super(params);
        }

        RSAPSS(Element dmElem) throws MarshalException {
            super(dmElem);
        }

        public String getAlgorithm() {
            return "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        String getJCAAlgorithm() {
            return "RSASSA-PSS";
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.AbstractDOMSignatureMethod
        AbstractDOMSignatureMethod.Type getAlgorithmType() {
            return AbstractDOMSignatureMethod.Type.RSA;
        }
    }
}
