package org.apache.xml.security.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.SignerOutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/signature/XMLSignature.class */
public final class XMLSignature extends SignatureElementProxy {
    public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    public static final String ALGO_ID_MAC_HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
    public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_RIPEMD160 = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_EDDSA_ED25519 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519";
    public static final String ALGO_ID_SIGNATURE_EDDSA_ED448 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA3_224 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-224";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA3_256 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-256";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA3_384 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-384";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA3_512 = "http://www.w3.org/2021/04/xmldsig-more#ecdsa-sha3-512";
    public static final String ALGO_ID_SIGNATURE_RSA_PSS = "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLSignature.class);
    private final SignedInfo signedInfo;
    private KeyInfo keyInfo;
    private boolean followManifestsDuringValidation;
    private Element signatureValueElement;
    private static final int MODE_SIGN = 0;
    private static final int MODE_VERIFY = 1;
    private int state;

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, canonicalizationMethodURI, null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, String canonicalizationMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, canonicalizationMethodURI, provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, canonicalizationMethodURI, null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, String canonicalizationMethodURI, Provider provider, AlgorithmParameterSpec spec) throws XMLSecurityException {
        super(doc);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        String xmlnsDsPrefix = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "http://www.w3.org/2000/09/xmldsig#");
        } else {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xmlnsDsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        addReturnToSelf();
        this.baseURI = baseURI;
        this.signedInfo = new SignedInfo(getDocument(), signatureMethodURI, hmacOutputLength, canonicalizationMethodURI, provider, spec);
        appendSelf(this.signedInfo);
        addReturnToSelf();
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(getDocument(), "SignatureValue");
        appendSelf(this.signatureValueElement);
        addReturnToSelf();
    }

    public XMLSignature(Document doc, String baseURI, Element signatureMethodElem, Element canonicalizationMethodElem) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodElem, canonicalizationMethodElem, (Provider) null);
    }

    public XMLSignature(Document doc, String baseURI, Element signatureMethodElem, Element canonicalizationMethodElem, Provider provider) throws XMLSecurityException {
        super(doc);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        String xmlnsDsPrefix = getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "http://www.w3.org/2000/09/xmldsig#");
        } else {
            getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xmlnsDsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        addReturnToSelf();
        this.baseURI = baseURI;
        this.signedInfo = new SignedInfo(getDocument(), signatureMethodElem, canonicalizationMethodElem, provider);
        appendSelf(this.signedInfo);
        addReturnToSelf();
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(getDocument(), "SignatureValue");
        appendSelf(this.signatureValueElement);
        addReturnToSelf();
    }

    public XMLSignature(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true, (Provider) null);
    }

    public XMLSignature(Element element, String baseURI, Provider provider) throws XMLSecurityException {
        this(element, baseURI, true, provider);
    }

    public XMLSignature(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        this(element, baseURI, secureValidation, (Provider) null);
    }

    public XMLSignature(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSecurityException {
        Element nextElement;
        super(element, baseURI);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        if (!"http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI()) || !"Signature".equals(element.getLocalName())) {
            Object[] exArgs = {element.getLocalName()};
            throw new XMLSignatureException("signature.Verification.InvalidElement", exArgs);
        }
        Element signedInfoElem = XMLUtils.getNextElement(element.getFirstChild());
        if (signedInfoElem == null || !"http://www.w3.org/2000/09/xmldsig#".equals(signedInfoElem.getNamespaceURI()) || !"SignedInfo".equals(signedInfoElem.getLocalName())) {
            Object[] exArgs2 = {"SignedInfo", "Signature"};
            throw new XMLSignatureException("xml.WrongContent", exArgs2);
        }
        this.signedInfo = new SignedInfo(signedInfoElem, baseURI, secureValidation, provider);
        this.signatureValueElement = XMLUtils.getNextElement(XMLUtils.getNextElement(element.getFirstChild()).getNextSibling());
        if (this.signatureValueElement == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.signatureValueElement.getNamespaceURI()) || !"SignatureValue".equals(this.signatureValueElement.getLocalName())) {
            Object[] exArgs3 = {"SignatureValue", "Signature"};
            throw new XMLSignatureException("xml.WrongContent", exArgs3);
        }
        Attr signatureValueAttr = this.signatureValueElement.getAttributeNodeNS(null, "Id");
        if (signatureValueAttr != null) {
            this.signatureValueElement.setIdAttributeNode(signatureValueAttr, true);
        }
        Element keyInfoElem = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
        if (keyInfoElem != null && "http://www.w3.org/2000/09/xmldsig#".equals(keyInfoElem.getNamespaceURI()) && Constants._TAG_KEYINFO.equals(keyInfoElem.getLocalName())) {
            this.keyInfo = new KeyInfo(keyInfoElem, baseURI);
            this.keyInfo.setSecureValidation(secureValidation);
            nextElement = XMLUtils.getNextElement(keyInfoElem.getNextSibling());
        } else {
            nextElement = keyInfoElem;
        }
        while (true) {
            Element objectElem = nextElement;
            if (objectElem != null) {
                if (!"http://www.w3.org/2000/09/xmldsig#".equals(objectElem.getNamespaceURI()) || !Constants._TAG_OBJECT.equals(objectElem.getLocalName())) {
                    Object[] exArgs4 = {objectElem.getLocalName()};
                    throw new XMLSignatureException("signature.Verification.InvalidElement", exArgs4);
                }
                Attr objectAttr = objectElem.getAttributeNodeNS(null, "Id");
                if (objectAttr != null) {
                    objectElem.setIdAttributeNode(objectAttr, true);
                }
                Node firstChild = objectElem.getFirstChild();
                while (true) {
                    Node firstChild2 = firstChild;
                    if (firstChild2 != null) {
                        if (firstChild2.getNodeType() == 1) {
                            Element childElem = (Element) firstChild2;
                            String tag = childElem.getLocalName();
                            if (Constants._TAG_MANIFEST.equals(tag)) {
                                new Manifest(childElem, baseURI);
                            } else if (Constants._TAG_SIGNATUREPROPERTIES.equals(tag)) {
                                new SignatureProperties(childElem, baseURI);
                            }
                        }
                        firstChild = firstChild2.getNextSibling();
                    }
                }
                nextElement = XMLUtils.getNextElement(objectElem.getNextSibling());
            } else {
                this.state = 1;
                return;
            }
        }
    }

    public void setId(String id) {
        if (id != null) {
            setLocalIdAttribute("Id", id);
        }
    }

    public String getId() {
        return getLocalAttribute("Id");
    }

    public SignedInfo getSignedInfo() {
        return this.signedInfo;
    }

    public byte[] getSignatureValue() throws XMLSignatureException {
        String content = XMLUtils.getFullTextChildrenFromNode(this.signatureValueElement);
        return XMLUtils.decode(content);
    }

    private void setSignatureValueElement(byte[] bytes) {
        while (this.signatureValueElement.hasChildNodes()) {
            this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
        }
        String base64codedValue = XMLUtils.encodeToString(bytes);
        if (base64codedValue.length() > 76 && !XMLUtils.ignoreLineBreaks()) {
            base64codedValue = "\n" + base64codedValue + "\n";
        }
        Text t = createText(base64codedValue);
        this.signatureValueElement.appendChild(t);
    }

    public KeyInfo getKeyInfo() {
        if (this.state == 0 && this.keyInfo == null) {
            this.keyInfo = new KeyInfo(getDocument());
            Element keyInfoElement = this.keyInfo.getElement();
            Element firstObject = XMLUtils.selectDsNode(getElement().getFirstChild(), Constants._TAG_OBJECT, 0);
            if (firstObject != null) {
                getElement().insertBefore(keyInfoElement, firstObject);
                XMLUtils.addReturnBeforeChild(getElement(), firstObject);
            } else {
                appendSelf(keyInfoElement);
                addReturnToSelf();
            }
        }
        return this.keyInfo;
    }

    public void appendObject(ObjectContainer object) throws XMLSignatureException {
        appendSelf(object);
        addReturnToSelf();
    }

    public ObjectContainer getObjectItem(int i) {
        Element objElem = XMLUtils.selectDsNode(getFirstChild(), Constants._TAG_OBJECT, i);
        try {
            return new ObjectContainer(objElem, this.baseURI);
        } catch (XMLSecurityException e) {
            return null;
        }
    }

    public int getObjectLength() {
        return length("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_OBJECT);
    }

    public void sign(Key signingKey) throws XMLSignatureException {
        if (signingKey instanceof PublicKey) {
            throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
        }
        SignedInfo si = getSignedInfo();
        SignatureAlgorithm sa = si.getSignatureAlgorithm();
        try {
            SignerOutputStream output = new SignerOutputStream(sa);
            try {
                OutputStream so = new UnsyncBufferedOutputStream(output);
                Throwable th = null;
                try {
                    try {
                        si.generateDigestValues();
                        sa.initSign(signingKey);
                        si.signInOctetStream(so);
                        setSignatureValueElement(sa.sign());
                        $closeResource(null, so);
                        $closeResource(null, output);
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, so);
                    throw th3;
                }
            } catch (Throwable th4) {
                $closeResource(null, output);
                throw th4;
            }
        } catch (IOException | XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        } catch (XMLSignatureException ex2) {
            throw ex2;
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

    public void addResourceResolver(ResourceResolverSpi resolver) {
        getSignedInfo().addResourceResolver(resolver);
    }

    public boolean checkSignatureValue(X509Certificate cert) throws XMLSignatureException {
        if (cert != null) {
            return checkSignatureValue(cert.getPublicKey());
        }
        Object[] exArgs = {"Didn't get a certificate"};
        throw new XMLSignatureException("empty", exArgs);
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00bd: MOVE (r1 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('so' org.apache.xml.security.utils.SignerOutputStream)])
    , block:B:25:0x00bd */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x00bb: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:24:0x00bb */
    public boolean checkSignatureValue(Key pk) throws Exception {
        Throwable th;
        SignerOutputStream so;
        if (pk == null) {
            Object[] exArgs = {"Didn't get a key"};
            throw new XMLSignatureException("empty", exArgs);
        }
        try {
            SignedInfo si = getSignedInfo();
            SignatureAlgorithm sa = si.getSignatureAlgorithm();
            LOG.debug("signatureMethodURI = {}", sa.getAlgorithmURI());
            LOG.debug("jceSigAlgorithm = {}", sa.getJCEAlgorithmString());
            LOG.debug("PublicKey = {}", pk);
            byte[] sigBytes = null;
            try {
                try {
                    SignerOutputStream so2 = new SignerOutputStream(sa);
                    OutputStream bos = new UnsyncBufferedOutputStream(so2);
                    Throwable th2 = null;
                    try {
                        try {
                            sa.initVerify(pk);
                            LOG.debug("jceSigProvider = {}", sa.getJCEProviderName());
                            si.signInOctetStream(bos);
                            sigBytes = getSignatureValue();
                            $closeResource(null, bos);
                            $closeResource(null, so2);
                        } catch (Throwable th3) {
                            th2 = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        $closeResource(th2, bos);
                        throw th4;
                    }
                } catch (Throwable th5) {
                    $closeResource(th, so);
                    throw th5;
                }
            } catch (IOException ex) {
                LOG.debug(ex.getMessage(), (Throwable) ex);
            } catch (XMLSecurityException ex2) {
                throw ex2;
            }
            if (sa.verify(sigBytes)) {
                return si.verify(this.followManifestsDuringValidation);
            }
            LOG.warn("Signature verification failed.");
            return false;
        } catch (XMLSignatureException ex3) {
            throw ex3;
        } catch (XMLSecurityException ex4) {
            throw new XMLSignatureException(ex4);
        }
    }

    public void addDocument(String referenceURI, Transforms trans, String digestURI, String referenceId, String referenceType) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, digestURI, referenceId, referenceType);
    }

    public void addDocument(String referenceURI, Transforms trans, String digestURI) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, digestURI, null, null);
    }

    public void addDocument(String referenceURI, Transforms trans) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }

    public void addDocument(String referenceURI) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }

    public void addKeyInfo(X509Certificate cert) throws XMLSecurityException {
        X509Data x509data = new X509Data(getDocument());
        x509data.addCertificate(cert);
        getKeyInfo().add(x509data);
    }

    public void addKeyInfo(PublicKey pk) {
        getKeyInfo().add(pk);
    }

    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return getSignedInfo().createSecretKey(secretKeyBytes);
    }

    public void setFollowNestedManifests(boolean followManifests) {
        this.followManifestsDuringValidation = followManifests;
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "Signature";
    }
}
