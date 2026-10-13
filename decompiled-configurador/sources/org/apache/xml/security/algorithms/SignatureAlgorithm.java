package org.apache.xml.security.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.xml.security.algorithms.implementations.IntegrityHmac;
import org.apache.xml.security.algorithms.implementations.SignatureBaseRSA;
import org.apache.xml.security.algorithms.implementations.SignatureDSA;
import org.apache.xml.security.algorithms.implementations.SignatureECDSA;
import org.apache.xml.security.algorithms.implementations.SignatureEDDSA;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/algorithms/SignatureAlgorithm.class */
public class SignatureAlgorithm extends Algorithm {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) SignatureAlgorithm.class);
    private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash = new ConcurrentHashMap();
    private final SignatureAlgorithmSpi signatureAlgorithmSpi;
    private final String algorithmURI;

    public SignatureAlgorithm(Document doc, String algorithmURI) throws XMLSecurityException {
        this(doc, algorithmURI, (Provider) null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, Provider provider) throws XMLSecurityException {
        this(doc, algorithmURI, provider, (AlgorithmParameterSpec) null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, Provider provider, AlgorithmParameterSpec parameterSpec) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        this.signatureAlgorithmSpi = getSignatureAlgorithmSpi(algorithmURI, provider);
        if (parameterSpec != null) {
            this.signatureAlgorithmSpi.engineSetParameter(parameterSpec);
            this.signatureAlgorithmSpi.engineAddContextToElement(getElement());
        }
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, int hmacOutputLength) throws XMLSecurityException {
        this(doc, algorithmURI, hmacOutputLength, (Provider) null);
    }

    public SignatureAlgorithm(Document doc, String algorithmURI, int hmacOutputLength, Provider provider) throws XMLSecurityException {
        super(doc, algorithmURI);
        this.algorithmURI = algorithmURI;
        this.signatureAlgorithmSpi = getSignatureAlgorithmSpi(algorithmURI, provider);
        this.signatureAlgorithmSpi.engineSetHMACOutputLength(hmacOutputLength);
        this.signatureAlgorithmSpi.engineAddContextToElement(getElement());
    }

    public SignatureAlgorithm(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true, (Provider) null);
    }

    public SignatureAlgorithm(Element element, String baseURI, Provider provider) throws XMLSecurityException {
        this(element, baseURI, true, provider);
    }

    public SignatureAlgorithm(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        this(element, baseURI, secureValidation, (Provider) null);
    }

    public SignatureAlgorithm(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSecurityException {
        super(element, baseURI);
        this.algorithmURI = getURI();
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
        if (secureValidation && (XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5.equals(this.algorithmURI) || XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5.equals(this.algorithmURI))) {
            Object[] exArgs = {this.algorithmURI};
            throw new XMLSecurityException("signature.signatureAlgorithm", exArgs);
        }
        this.signatureAlgorithmSpi = getSignatureAlgorithmSpi(this.algorithmURI, provider);
        this.signatureAlgorithmSpi.engineGetContextFromElement(getElement());
    }

    private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String algorithmURI, Provider provider) throws XMLSignatureException {
        try {
            Class<? extends SignatureAlgorithmSpi> implementingClass = algorithmHash.get(algorithmURI);
            LOG.debug("Create URI \"{}\" class \"{}\"", algorithmURI, implementingClass);
            if (implementingClass == null) {
                Object[] exArgs = {algorithmURI};
                throw new XMLSignatureException("algorithms.NoSuchAlgorithmNoEx", exArgs);
            }
            if (provider != null) {
                try {
                    Constructor<? extends SignatureAlgorithmSpi> constructor = implementingClass.getConstructor(Provider.class);
                    return constructor.newInstance(provider);
                } catch (NoSuchMethodException e) {
                    LOG.warn("Class \"{}\" does not have a constructor with Provider", implementingClass);
                }
            }
            return (SignatureAlgorithmSpi) JavaUtils.newInstanceWithEmptyConstructor(implementingClass);
        } catch (IllegalAccessException | InstantiationException | NullPointerException | InvocationTargetException ex) {
            Object[] exArgs2 = {algorithmURI, ex.getMessage()};
            throw new XMLSignatureException(ex, "algorithms.NoSuchAlgorithm", exArgs2);
        }
    }

    public byte[] sign() throws XMLSignatureException {
        return this.signatureAlgorithmSpi.engineSign();
    }

    public String getJCEAlgorithmString() {
        return this.signatureAlgorithmSpi.engineGetJCEAlgorithmString();
    }

    public String getJCEProviderName() {
        return this.signatureAlgorithmSpi.engineGetJCEProviderName();
    }

    public void update(byte[] input) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(input);
    }

    public void update(byte input) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(input);
    }

    public void update(byte[] buf, int offset, int len) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineUpdate(buf, offset, len);
    }

    public void initSign(Key signingKey) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey);
    }

    public void initSign(Key signingKey, SecureRandom secureRandom) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey, secureRandom);
    }

    public void initSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitSign(signingKey, algorithmParameterSpec);
    }

    public void setParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineSetParameter(params);
    }

    public void initVerify(Key verificationKey) throws XMLSignatureException {
        this.signatureAlgorithmSpi.engineInitVerify(verificationKey);
    }

    public boolean verify(byte[] signature) throws XMLSignatureException {
        return this.signatureAlgorithmSpi.engineVerify(signature);
    }

    public final String getURI() {
        return getLocalAttribute("Algorithm");
    }

    public static void register(String str, String str2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        LOG.debug("Try to register {} {}", str, str2);
        Class<? extends SignatureAlgorithmSpi> cls = algorithmHash.get(str);
        if (cls != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[]{str, cls});
        }
        try {
            algorithmHash.put(str, (Class<? extends SignatureAlgorithmSpi>) ClassLoaderUtils.loadClass(str2, SignatureAlgorithm.class));
        } catch (NullPointerException e) {
            throw new XMLSignatureException(e, "algorithms.NoSuchAlgorithm", new Object[]{str, e.getMessage()});
        }
    }

    public static void register(String algorithmURI, Class<? extends SignatureAlgorithmSpi> implementingClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        LOG.debug("Try to register {} {}", algorithmURI, implementingClass);
        Class<? extends SignatureAlgorithmSpi> registeredClass = algorithmHash.get(algorithmURI);
        if (registeredClass != null) {
            Object[] exArgs = {algorithmURI, registeredClass};
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }
        algorithmHash.put(algorithmURI, implementingClass);
    }

    public static void registerDefaultAlgorithms() {
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", SignatureDSA.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256, SignatureDSA.SHA256.class);
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", SignatureBaseRSA.SignatureRSASHA1.class);
        algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, SignatureBaseRSA.SignatureRSAMD5.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160, SignatureBaseRSA.SignatureRSARIPEMD160.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224, SignatureBaseRSA.SignatureRSASHA224.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", SignatureBaseRSA.SignatureRSASHA256.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", SignatureBaseRSA.SignatureRSASHA384.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", SignatureBaseRSA.SignatureRSASHA512.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1_MGF1, SignatureBaseRSA.SignatureRSASHA1MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA224_MGF1, SignatureBaseRSA.SignatureRSASHA224MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1, SignatureBaseRSA.SignatureRSASHA256MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1, SignatureBaseRSA.SignatureRSASHA384MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512_MGF1, SignatureBaseRSA.SignatureRSASHA512MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_PSS, SignatureBaseRSA.SignatureRSASSAPSS.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1, SignatureBaseRSA.SignatureRSASHA3_224MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1, SignatureBaseRSA.SignatureRSASHA3_256MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1, SignatureBaseRSA.SignatureRSASHA3_384MGF1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1, SignatureBaseRSA.SignatureRSASHA3_512MGF1.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", SignatureECDSA.SignatureECDSASHA1.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA224, SignatureECDSA.SignatureECDSASHA224.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256, SignatureECDSA.SignatureECDSASHA256.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384, SignatureECDSA.SignatureECDSASHA384.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512, SignatureECDSA.SignatureECDSASHA512.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_224, SignatureECDSA.SignatureECDSASHA3_224.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_256, SignatureECDSA.SignatureECDSASHA3_256.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_384, SignatureECDSA.SignatureECDSASHA3_384.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA3_512, SignatureECDSA.SignatureECDSASHA3_512.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_RIPEMD160, SignatureECDSA.SignatureECDSARIPEMD160.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED25519, SignatureEDDSA.SignatureEd25519.class);
        algorithmHash.put(XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED448, SignatureEDDSA.SignatureEd448.class);
        algorithmHash.put(XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5, IntegrityHmac.IntegrityHmacMD5.class);
        algorithmHash.put(XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160, IntegrityHmac.IntegrityHmacRIPEMD160.class);
        algorithmHash.put(XMLSignature.ALGO_ID_MAC_HMAC_SHA224, IntegrityHmac.IntegrityHmacSHA224.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
        algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
    }

    @Override // org.apache.xml.security.utils.SignatureElementProxy, org.apache.xml.security.utils.ElementProxy
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "SignatureMethod";
    }
}
