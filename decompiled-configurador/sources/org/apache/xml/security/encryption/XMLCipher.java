package org.apache.xml.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.encryption.keys.KeyInfoEnc;
import org.apache.xml.security.encryption.keys.content.AgreementMethodImpl;
import org.apache.xml.security.encryption.params.KeyAgreementParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.KeyUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher.class */
public class XMLCipher {
    public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String AES_128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
    public static final String AES_192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
    public static final String AES_256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
    public static final String SEED_128 = "http://www.w3.org/2007/05/xmldsig-more#seed128-cbc";
    public static final String CAMELLIA_128 = "http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc";
    public static final String CAMELLIA_192 = "http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc";
    public static final String CAMELLIA_256 = "http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc";
    public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String RSA_OAEP_11 = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
    public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
    public static final String DIFFIE_HELLMAN_EC = "http://www.w3.org/2009/xmlenc11#ECDH-ES";
    public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
    public static final String CAMELLIA_128_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia128";
    public static final String CAMELLIA_192_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia192";
    public static final String CAMELLIA_256_KeyWrap = "http://www.w3.org/2001/04/xmldsig-more#kw-camellia256";
    public static final String SEED_128_KeyWrap = "http://www.w3.org/2007/05/xmldsig-more#kw-seed128";
    public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
    public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String PHYSICAL_XML_N14C = "http://santuario.apache.org/c14n/physical";
    public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    public static final int UNWRAP_MODE = 4;
    public static final int WRAP_MODE = 3;
    private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\nhttp://www.w3.org/2007/05/xmldsig-more#seed128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia192-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia256-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia128\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia192\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia256\nhttp://www.w3.org/2007/05/xmldsig-more#kw-seed128\n";
    private static final Set<String> SUPPORTED_ALGORITHMS;
    private Cipher contextCipher;
    private final String algorithm;
    private final String requestedJCEProvider;
    private Document contextDocument;
    private final Factory factory;
    private final Serializer serializer;
    private Key key;
    private Key kek;
    private EncryptedKey ek;
    private EncryptedData ed;
    private String digestAlg;
    private List<KeyResolverSpi> internalKeyResolvers;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLCipher.class);
    private static final boolean HAVE_FUNCTIONAL_IDENTITY_TRANSFORMER = haveFunctionalIdentityTransformer();
    private int cipherMode = PKIFailureInfo.systemUnavail;
    private boolean secureValidation = true;

    static {
        Set<String> encryptionAlgorithms = new HashSet<>();
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#aes192-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2009/xmlenc11#aes128-gcm");
        encryptionAlgorithms.add("http://www.w3.org/2009/xmlenc11#aes192-gcm");
        encryptionAlgorithms.add("http://www.w3.org/2009/xmlenc11#aes256-gcm");
        encryptionAlgorithms.add("http://www.w3.org/2007/05/xmldsig-more#seed128-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#rsa-1_5");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p");
        encryptionAlgorithms.add("http://www.w3.org/2009/xmlenc11#rsa-oaep");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#kw-tripledes");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#kw-aes128");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#kw-aes256");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmlenc#kw-aes192");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192");
        encryptionAlgorithms.add("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256");
        encryptionAlgorithms.add("http://www.w3.org/2007/05/xmldsig-more#kw-seed128");
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(encryptionAlgorithms);
    }

    private XMLCipher(String transformation, String provider, String digestMethod, Serializer serializer) throws XMLEncryptionException {
        LOG.debug("Constructing XMLCipher...");
        this.factory = new Factory();
        this.algorithm = transformation;
        this.requestedJCEProvider = provider;
        this.digestAlg = digestMethod;
        this.serializer = serializer;
        if (transformation != null) {
            this.contextCipher = constructCipher(transformation, digestMethod);
        }
    }

    private static Serializer createSerializer(boolean secureValidation) throws XMLEncryptionException {
        return createSerializer(null, secureValidation);
    }

    private static Serializer createSerializer(String canonAlg, boolean secureValidation) throws XMLEncryptionException {
        String c14nAlg = canonAlg != null ? canonAlg : "http://santuario.apache.org/c14n/physical";
        try {
            if (HAVE_FUNCTIONAL_IDENTITY_TRANSFORMER) {
                return new TransformSerializer(c14nAlg, secureValidation);
            }
            return new DocumentSerializer(c14nAlg, secureValidation);
        } catch (TransformerConfigurationException | InvalidCanonicalizerException e) {
            throw new XMLEncryptionException(e);
        }
    }

    private static boolean isValidEncryptionAlgorithm(String algorithm) {
        return SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    private static void validateTransformation(String transformation) {
        if (null == transformation) {
            throw new NullPointerException("Transformation unexpectedly null...");
        }
        if (!isValidEncryptionAlgorithm(transformation)) {
            LOG.warn("Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\nhttp://www.w3.org/2007/05/xmldsig-more#seed128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia128-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia192-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#camellia256-cbc\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia128\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia192\nhttp://www.w3.org/2001/04/xmldsig-more#kw-camellia256\nhttp://www.w3.org/2007/05/xmldsig-more#kw-seed128\n");
        }
    }

    public static XMLCipher getInstance(String transformation) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation");
        validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, createSerializer(true));
    }

    public static XMLCipher getInstance(Serializer serializer, String transformation) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation");
        validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, serializer);
    }

    public static XMLCipher getInstance(String transformation, String canon) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and c14n algorithm");
        validateTransformation(transformation);
        return new XMLCipher(transformation, null, null, createSerializer(canon, true));
    }

    public static XMLCipher getInstance(String transformation, String canon, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and c14n algorithm");
        validateTransformation(transformation);
        return new XMLCipher(transformation, null, digestMethod, createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation and provider");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        validateTransformation(transformation);
        return new XMLCipher(transformation, provider, null, createSerializer(true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider, String canon) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        validateTransformation(transformation);
        return new XMLCipher(transformation, provider, null, createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(String transformation, String provider, String canon, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        validateTransformation(transformation);
        return new XMLCipher(transformation, provider, digestMethod, createSerializer(canon, true));
    }

    public static XMLCipher getProviderInstance(Serializer serializer, String transformation, String provider, String digestMethod) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with transformation, provider and c14n algorithm");
        if (null == provider) {
            throw new NullPointerException("Provider unexpectedly null..");
        }
        validateTransformation(transformation);
        return new XMLCipher(transformation, provider, digestMethod, serializer);
    }

    public static XMLCipher getInstance() throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with no arguments");
        return new XMLCipher(null, null, null, createSerializer(true));
    }

    public static XMLCipher getProviderInstance(String provider) throws XMLEncryptionException {
        LOG.debug("Getting XMLCipher with provider");
        return new XMLCipher(null, provider, null, createSerializer(true));
    }

    public void init(int opmode, Key key) throws XMLEncryptionException {
        LOG.debug("Initializing XMLCipher...");
        this.ek = null;
        this.ed = null;
        switch (opmode) {
            case 1:
                LOG.debug("opmode = ENCRYPT_MODE");
                this.ed = createEncryptedData(1, "NO VALUE YET");
                break;
            case 2:
                LOG.debug("opmode = DECRYPT_MODE");
                break;
            case 3:
                LOG.debug("opmode = WRAP_MODE");
                this.ek = createEncryptedKey(1, "NO VALUE YET");
                break;
            case 4:
                LOG.debug("opmode = UNWRAP_MODE");
                break;
            default:
                LOG.error("Mode unexpectedly invalid");
                throw new XMLEncryptionException("Invalid mode in init");
        }
        this.cipherMode = opmode;
        this.key = key;
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public void registerInternalKeyResolver(KeyResolverSpi keyResolver) {
        if (this.internalKeyResolvers == null) {
            this.internalKeyResolvers = new ArrayList();
        }
        this.internalKeyResolvers.add(keyResolver);
    }

    public EncryptedData getEncryptedData() {
        LOG.debug("Returning EncryptedData");
        return this.ed;
    }

    public EncryptedKey getEncryptedKey() {
        LOG.debug("Returning EncryptedKey");
        return this.ek;
    }

    public void setKEK(Key kek) {
        this.kek = kek;
    }

    public Element martial(EncryptedData encryptedData) {
        return this.factory.toElement(encryptedData);
    }

    public Element martial(Document context, EncryptedData encryptedData) {
        this.contextDocument = context;
        return this.factory.toElement(encryptedData);
    }

    public Element martial(EncryptedKey encryptedKey) {
        return this.factory.toElement(encryptedKey);
    }

    public Element martial(Document context, EncryptedKey encryptedKey) {
        this.contextDocument = context;
        return this.factory.toElement(encryptedKey);
    }

    public Element martial(ReferenceList referenceList) {
        return this.factory.toElement(referenceList);
    }

    public Element martial(Document context, ReferenceList referenceList) {
        this.contextDocument = context;
        return this.factory.toElement(referenceList);
    }

    private Document encryptElement(Element element) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        encryptData(this.contextDocument, element, false);
        Element encryptedElement = this.factory.toElement(this.ed);
        Node sourceParent = element.getParentNode();
        sourceParent.replaceChild(encryptedElement, element);
        return this.contextDocument;
    }

    private Document encryptElementContent(Element element) throws Exception {
        LOG.debug("Encrypting element content...");
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        encryptData(this.contextDocument, element, true);
        Element encryptedElement = this.factory.toElement(this.ed);
        removeContent(element);
        element.appendChild(encryptedElement);
        return this.contextDocument;
    }

    public Document doFinal(Document context, Document source) throws Exception {
        LOG.debug("Processing source document...");
        if (null == source) {
            throw new XMLEncryptionException("empty", "Source document unexpectedly null...");
        }
        return doFinal(context, source.getDocumentElement());
    }

    public Document doFinal(Document context, Element element) throws Exception {
        LOG.debug("Processing source element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Source element unexpectedly null...");
        }
        this.contextDocument = context;
        Document result = null;
        switch (this.cipherMode) {
            case 1:
                result = encryptElement(element);
                break;
            case 2:
                result = decryptElement(element);
                break;
            case 3:
            case 4:
                break;
            default:
                throw new XMLEncryptionException(new IllegalStateException());
        }
        return result;
    }

    public Document doFinal(Document context, Element element, boolean content) throws Exception {
        LOG.debug("Processing source element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Source element unexpectedly null...");
        }
        this.contextDocument = context;
        Document result = null;
        switch (this.cipherMode) {
            case 1:
                if (content) {
                    result = encryptElementContent(element);
                } else {
                    result = encryptElement(element);
                }
                break;
            case 2:
                if (content) {
                    result = decryptElementContent(element);
                } else {
                    result = decryptElement(element);
                }
                break;
            case 3:
            case 4:
                break;
            default:
                throw new XMLEncryptionException(new IllegalStateException());
        }
        return result;
    }

    public EncryptedData encryptData(Document context, Element element) throws Exception {
        return encryptData(context, element, false);
    }

    public EncryptedData encryptData(Document context, String type, InputStream serializedData) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == serializedData) {
            throw new XMLEncryptionException("empty", "Serialized data unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        return encryptData(context, null, type, serializedData);
    }

    public EncryptedData encryptData(Document context, Element element, boolean contentMode) throws Exception {
        LOG.debug("Encrypting element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 1) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        String type = contentMode ? EncryptionConstants.TYPE_CONTENT : EncryptionConstants.TYPE_ELEMENT;
        return encryptData(context, element, type, null);
    }

    /* JADX WARN: Code duplicated, block: B:63:0x01ca A[Catch: UnsupportedEncodingException | IllegalStateException | BadPaddingException | IllegalBlockSizeException -> 0x023c, TryCatch #4 {UnsupportedEncodingException | IllegalStateException | BadPaddingException | IllegalBlockSizeException -> 0x023c, blocks: (B:41:0x0138, B:42:0x014b, B:44:0x0159, B:46:0x016a, B:48:0x0174, B:50:0x018a, B:70:0x020e, B:72:0x0219, B:53:0x019e, B:52:0x0194, B:56:0x01ae, B:60:0x01b6, B:65:0x01d1, B:63:0x01ca, B:62:0x01c0, B:67:0x01d5, B:69:0x01e9), top: B:98:0x0135, inners: #1, #2, #6 }] */
    /* JADX WARN: Code duplicated, block: B:92:0x01b6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private EncryptedData encryptData(Document context, Element element, String type, InputStream serializedData) throws Exception {
        Cipher c;
        byte[] encryptedBytes;
        this.contextDocument = context;
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        if (element != null && element.getParentNode() == null) {
            throw new XMLEncryptionException("empty", "The element can't be serialized as it has no parent");
        }
        byte[] serializedOctets = null;
        if (serializedData == null) {
            if (EncryptionConstants.TYPE_CONTENT.equals(type)) {
                if (element == null) {
                    throw new XMLEncryptionException("empty", "Cannot encrypt null element");
                }
                NodeList children = element.getChildNodes();
                if (null != children) {
                    serializedOctets = this.serializer.serializeToByteArray(children);
                } else {
                    throw new XMLEncryptionException("empty", "Element has no content.");
                }
            } else {
                serializedOctets = this.serializer.serializeToByteArray(element);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Serialized octets:\n" + new String(serializedOctets, StandardCharsets.UTF_8));
            }
        }
        if (this.contextCipher == null) {
            c = constructCipher(this.algorithm, null);
        } else {
            c = this.contextCipher;
        }
        int ivLen = JCEMapper.getIVLengthFromURI(this.algorithm) / 8;
        byte[] iv = XMLSecurityConstants.generateBytes(ivLen);
        try {
            AlgorithmParameterSpec paramSpec = constructBlockCipherParameters(this.algorithm, iv);
            c.init(this.cipherMode, this.key, paramSpec);
            try {
                if (serializedData != null) {
                    byte[] buf = new byte[8192];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Throwable th = null;
                    while (true) {
                        try {
                            try {
                                int numBytes = serializedData.read(buf);
                                if (numBytes == -1) {
                                    break;
                                }
                                byte[] data = c.update(buf, 0, numBytes);
                                if (data != null) {
                                    baos.write(data);
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                throw th2;
                            }
                        } catch (Throwable th3) {
                            if (th != null) {
                                try {
                                    baos.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                baos.close();
                            }
                            throw th3;
                        }
                        if (th != null) {
                            baos.close();
                        } else {
                            baos.close();
                        }
                        throw th3;
                    }
                    baos.write(c.doFinal());
                    encryptedBytes = baos.toByteArray();
                    if (0 != 0) {
                        try {
                            baos.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        baos.close();
                    }
                } else {
                    encryptedBytes = c.doFinal(serializedOctets);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Expected cipher.outputSize = " + Integer.toString(c.getOutputSize(serializedOctets.length)));
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Actual cipher.outputSize = " + Integer.toString(encryptedBytes.length));
                }
                if (c.getIV() != null) {
                    iv = c.getIV();
                }
                byte[] finalEncryptedBytes = new byte[iv.length + encryptedBytes.length];
                System.arraycopy(iv, 0, finalEncryptedBytes, 0, iv.length);
                System.arraycopy(encryptedBytes, 0, finalEncryptedBytes, iv.length, encryptedBytes.length);
                String base64EncodedEncryptedOctets = XMLUtils.encodeToString(finalEncryptedBytes);
                LOG.debug("Encrypted octets:\n{}", base64EncodedEncryptedOctets);
                LOG.debug("Encrypted octets length = {}", Integer.valueOf(base64EncodedEncryptedOctets.length()));
                try {
                    CipherData cd = this.ed.getCipherData();
                    CipherValue cv = cd.getCipherValue();
                    cv.setValue(base64EncodedEncryptedOctets);
                    if (type != null) {
                        this.ed.setType(new URI(type).toString());
                    }
                    EncryptionMethod method = this.factory.newEncryptionMethod(new URI(this.algorithm).toString());
                    method.setDigestAlgorithm(this.digestAlg);
                    this.ed.setEncryptionMethod(method);
                    return this.ed;
                } catch (URISyntaxException ex) {
                    throw new XMLEncryptionException(ex);
                }
            } catch (UnsupportedEncodingException | IllegalStateException | BadPaddingException | IllegalBlockSizeException e) {
                throw new XMLEncryptionException(e);
            }
        } catch (InvalidKeyException ike) {
            throw new XMLEncryptionException(ike);
        }
    }

    private AlgorithmParameterSpec constructBlockCipherParameters(String algorithm, byte[] iv) {
        return XMLCipherUtil.constructBlockCipherParameters(algorithm, iv);
    }

    public EncryptedData loadEncryptedData(Document context, Element element) throws XMLEncryptionException {
        LOG.debug("Loading encrypted element...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Element unexpectedly null...");
        }
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        this.contextDocument = context;
        this.ed = this.factory.newEncryptedData(element);
        return this.ed;
    }

    public EncryptedKey loadEncryptedKey(Document context, Element element) throws XMLEncryptionException {
        LOG.debug("Loading encrypted key...");
        if (null == context) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (null == element) {
            throw new XMLEncryptionException("empty", "Context document unexpectedly null...");
        }
        if (this.cipherMode != 4 && this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
        }
        this.contextDocument = context;
        this.ek = this.factory.newEncryptedKey(element);
        return this.ek;
    }

    public EncryptedKey loadEncryptedKey(Element element) throws XMLEncryptionException {
        return loadEncryptedKey(element.getOwnerDocument(), element);
    }

    public EncryptedKey encryptKey(Document doc, Key key) throws XMLEncryptionException {
        return encryptKey(doc, key, (String) null, (byte[]) null);
    }

    public EncryptedKey encryptKey(Document doc, Key key, String mgfAlgorithm, byte[] oaepParams) throws XMLEncryptionException {
        return encryptKey(doc, key, mgfAlgorithm, oaepParams, null);
    }

    public EncryptedKey encryptKey(Document doc, Key key, String mgfAlgorithm, byte[] oaepParams, SecureRandom random) throws XMLEncryptionException {
        LOG.debug("Encrypting key ...");
        OAEPParameterSpec oaepParameters = XMLCipherUtil.constructOAEPParameters(this.algorithm, this.digestAlg, mgfAlgorithm, oaepParams);
        return encryptKey(doc, key, oaepParameters, random);
    }

    public EncryptedKey encryptKey(Document doc, Key key, AlgorithmParameterSpec params, SecureRandom random) throws XMLEncryptionException {
        Cipher c;
        LOG.debug("Encrypting key using algorithm specs [{0}] ...", params);
        if (null == key) {
            throw new XMLEncryptionException("empty", "Key unexpectedly null...");
        }
        if (this.cipherMode != 3) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in WRAP_MODE...");
        }
        if (this.algorithm == null) {
            throw new XMLEncryptionException("empty", "XMLCipher instance without transformation specified");
        }
        this.contextDocument = doc;
        if (this.contextCipher == null) {
            c = constructCipher(this.algorithm, null);
        } else {
            c = this.contextCipher;
        }
        AlgorithmParameterSpec cipherSpec = null;
        Key wrapKey = this.key;
        if (params instanceof OAEPParameterSpec) {
            cipherSpec = params;
        } else if (params instanceof KeyAgreementParameters) {
            KeyAgreementParameters keyAgreementParameter = (KeyAgreementParameters) params;
            validateAndUpdateKeyAgreementParameterKeys(keyAgreementParameter);
            wrapKey = KeyUtils.aesWrapKeyWithDHGeneratedKey(keyAgreementParameter);
        } else if (params != null) {
            throw new XMLEncryptionException("encryption.UnsupportedAlgorithmParameterSpec", params.getClass().getName());
        }
        try {
            if (random != null) {
                if (cipherSpec == null) {
                    c.init(3, wrapKey, random);
                } else {
                    c.init(3, wrapKey, cipherSpec, random);
                }
            } else if (cipherSpec == null) {
                c.init(3, wrapKey);
            } else {
                c.init(3, wrapKey, cipherSpec);
            }
            byte[] encryptedBytes = c.wrap(key);
            String base64EncodedEncryptedOctets = XMLUtils.encodeToString(encryptedBytes);
            LOG.debug("Encrypted key octets:\n{}", base64EncodedEncryptedOctets);
            LOG.debug("Encrypted key octets length = {}", Integer.valueOf(base64EncodedEncryptedOctets.length()));
            CipherValue cv = this.ek.getCipherData().getCipherValue();
            cv.setValue(base64EncodedEncryptedOctets);
            try {
                EncryptionMethod method = this.factory.newEncryptionMethod(new URI(this.algorithm).toString());
                method.setDigestAlgorithm(this.digestAlg);
                this.ek.setEncryptionMethod(method);
                if (params instanceof OAEPParameterSpec) {
                    OAEPParameterSpec oaepSpec = (OAEPParameterSpec) params;
                    String mgf1Uri = XMLCipherUtil.getMgf1URIForParameter((MGF1ParameterSpec) oaepSpec.getMGFParameters());
                    method.setMGFAlgorithm(mgf1Uri);
                    if (PSource.PSpecified.DEFAULT != oaepSpec.getPSource() && (oaepSpec.getPSource() instanceof PSource.PSpecified)) {
                        byte[] pSourceParams = ((PSource.PSpecified) oaepSpec.getPSource()).getValue();
                        method.setOAEPparams(pSourceParams);
                    }
                } else if (params instanceof KeyAgreementParameters) {
                    AgreementMethodImpl agreementMethod = new AgreementMethodImpl(this.contextDocument, (KeyAgreementParameters) params);
                    KeyInfoEnc keyInfo = new KeyInfoEnc(this.contextDocument);
                    keyInfo.add(agreementMethod);
                    this.ek.setKeyInfo(keyInfo);
                }
                return this.ek;
            } catch (URISyntaxException ex) {
                throw new XMLEncryptionException(ex);
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException e) {
            throw new XMLEncryptionException(e);
        }
    }

    public Key decryptKey(EncryptedKey encryptedKey, String algorithm) throws XMLEncryptionException {
        Cipher c;
        LOG.debug("Decrypting key from previously loaded EncryptedKey...");
        if (this.cipherMode != 4) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in UNWRAP_MODE...");
        }
        if (algorithm == null) {
            throw new XMLEncryptionException("empty", "Cannot decrypt a key without knowing the algorithm");
        }
        if (this.key == null) {
            LOG.debug("Trying to find a KEK via key resolvers");
            KeyInfo ki = encryptedKey.getKeyInfo();
            if (ki != null) {
                ki.setSecureValidation(this.secureValidation);
                try {
                    String keyWrapAlg = encryptedKey.getEncryptionMethod().getAlgorithm();
                    String keyType = JCEMapper.getJCEKeyAlgorithmFromURI(keyWrapAlg);
                    if ("RSA".equals(keyType) || "EC".equals(keyType)) {
                        this.key = ki.getPrivateKey();
                    } else {
                        this.key = ki.getSecretKey();
                    }
                } catch (Exception e) {
                    LOG.debug(e.getMessage(), (Throwable) e);
                }
            }
            if (this.key == null) {
                LOG.error("XMLCipher::decryptKey unable to resolve a KEK");
                throw new XMLEncryptionException("empty", "Unable to decrypt without a KEK");
            }
        }
        XMLCipherInput cipherInput = new XMLCipherInput(encryptedKey);
        cipherInput.setSecureValidation(this.secureValidation);
        byte[] encryptedBytes = cipherInput.getBytes();
        String jceKeyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(algorithm);
        LOG.debug("JCE Key Algorithm: {}", jceKeyAlgorithm);
        if (this.contextCipher == null) {
            c = constructCipher(encryptedKey.getEncryptionMethod().getAlgorithm(), encryptedKey.getEncryptionMethod().getDigestAlgorithm());
        } else {
            c = this.contextCipher;
        }
        try {
            AlgorithmParameterSpec params = getAlgorithmParameters(encryptedKey);
            try {
                if (params == null) {
                    c.init(4, this.key);
                } else if (params instanceof OAEPParameterSpec) {
                    c.init(4, this.key, params);
                }
                if (params instanceof KeyAgreementParameters) {
                    Key wrapKey = KeyUtils.aesWrapKeyWithDHGeneratedKey((KeyAgreementParameters) params);
                    c.init(4, wrapKey);
                }
                Key ret = c.unwrap(encryptedBytes, jceKeyAlgorithm, 3);
                LOG.debug("Decryption of key type {} OK", algorithm);
                return ret;
            } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException e2) {
                throw new XMLEncryptionException(e2);
            }
        } catch (XMLSecurityException e3) {
            throw new XMLEncryptionException(e3);
        }
    }

    public void validateAndUpdateKeyAgreementParameterKeys(KeyAgreementParameters keyAgreementParameter) throws XMLEncryptionException {
        if (keyAgreementParameter == null) {
            return;
        }
        if (keyAgreementParameter.getRecipientPublicKey() == null && this.key != null) {
            if (this.key instanceof PublicKey) {
                LOG.debug("Recipient's public key is not set in keyAgreementParameter, use the recipient's public key specified in XMLCipher instance init method.");
                keyAgreementParameter.setRecipientPublicKey((PublicKey) this.key);
            } else {
                throw new XMLEncryptionException("algorithms.WrongKeyForThisOperation", this.key.getClass().getName(), "java.security.PublicKey");
            }
        }
        if (keyAgreementParameter.getRecipientPublicKey() == null) {
            throw new XMLEncryptionException("encryption.nokey");
        }
        if (keyAgreementParameter.getOriginatorPrivateKey() == null) {
            LOG.debug("Originator's private key is not set in keyAgreementParameter, generate an ephemeral key for the originator's private key.");
            KeyPair originatorKeyPair = KeyUtils.generateEphemeralDHKeyPair(keyAgreementParameter.getRecipientPublicKey(), null);
            keyAgreementParameter.setOriginatorKeyPair(originatorKeyPair);
        }
    }

    private AlgorithmParameterSpec getAlgorithmParameters(EncryptedKey encryptedKey) throws XMLSecurityException {
        if (encryptedKey == null || encryptedKey.getEncryptionMethod() == null || encryptedKey.getEncryptionMethod().getAlgorithm() == null) {
            LOG.debug("EncryptedKey key algorithm is null");
            return null;
        }
        EncryptionMethod encMethod = encryptedKey.getEncryptionMethod();
        String encryptionAlgorithm = encMethod.getAlgorithm();
        if ("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(encryptionAlgorithm) || "http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(encryptionAlgorithm)) {
            LOG.debug("EncryptedKey key algorithm is RSA OAEP");
            return XMLCipherUtil.constructOAEPParameters(encryptionAlgorithm, encMethod.getDigestAlgorithm(), encMethod.getMGFAlgorithm(), encMethod.getOAEPparams());
        }
        KeyInfoEnc keyInfo = encryptedKey.getKeyInfo() instanceof KeyInfoEnc ? (KeyInfoEnc) encryptedKey.getKeyInfo() : null;
        return constructKeyAgreementParameters(keyInfo, encryptionAlgorithm);
    }

    private KeyAgreementParameters constructKeyAgreementParameters(KeyInfoEnc keyInfo, String encryptionAlgorithm) throws XMLSecurityException {
        if (keyInfo == null || !keyInfo.containsAgreementMethod()) {
            LOG.debug("EncryptedKey key does not contain AgreementMethod data");
            return null;
        }
        if (!(this.key instanceof PrivateKey)) {
            LOG.info("The EncryptedKey key is using Key agreement data, but provided key is not a PrivateKey. Skipping Key Agreement data processing.");
            return null;
        }
        LOG.debug("EncryptedKey key is using Key agreement data");
        AgreementMethod agreementMethod = keyInfo.itemAgreementMethod(0);
        return XMLCipherUtil.constructRecipientKeyAgreementParameters(encryptionAlgorithm, agreementMethod, (PrivateKey) this.key);
    }

    private Cipher constructCipher(String algorithm, String digestAlgorithm) throws XMLEncryptionException {
        Cipher c;
        String jceAlgorithm = JCEMapper.translateURItoJCEID(algorithm);
        LOG.debug("JCE Algorithm = {}", jceAlgorithm);
        try {
            if (this.requestedJCEProvider == null) {
                c = Cipher.getInstance(jceAlgorithm);
            } else {
                c = Cipher.getInstance(jceAlgorithm, this.requestedJCEProvider);
            }
        } catch (NoSuchAlgorithmException nsae) {
            c = constructCipher(algorithm, digestAlgorithm, nsae);
        } catch (NoSuchProviderException | NoSuchPaddingException e) {
            throw new XMLEncryptionException(e);
        }
        return c;
    }

    private Cipher constructCipher(String algorithm, String digestAlgorithm, Exception nsae) throws XMLEncryptionException {
        if (!"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(algorithm)) {
            throw new XMLEncryptionException(nsae);
        }
        if (digestAlgorithm == null || "http://www.w3.org/2000/09/xmldsig#sha1".equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", this.requestedJCEProvider);
            } catch (Exception ex) {
                throw new XMLEncryptionException(ex);
            }
        }
        if (MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA224.equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-224andMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-224andMGF1Padding", this.requestedJCEProvider);
            } catch (Exception ex2) {
                throw new XMLEncryptionException(ex2);
            }
        }
        if ("http://www.w3.org/2001/04/xmlenc#sha256".equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", this.requestedJCEProvider);
            } catch (Exception ex3) {
                throw new XMLEncryptionException(ex3);
            }
        }
        if (MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA384.equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-384AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-384AndMGF1Padding", this.requestedJCEProvider);
            } catch (Exception ex4) {
                throw new XMLEncryptionException(ex4);
            }
        }
        if ("http://www.w3.org/2001/04/xmlenc#sha512".equals(digestAlgorithm)) {
            try {
                if (this.requestedJCEProvider == null) {
                    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
                }
                return Cipher.getInstance("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", this.requestedJCEProvider);
            } catch (Exception ex5) {
                throw new XMLEncryptionException(ex5);
            }
        }
        throw new XMLEncryptionException(nsae);
    }

    public Key decryptKey(EncryptedKey encryptedKey) throws XMLEncryptionException {
        return decryptKey(encryptedKey, this.ed.getEncryptionMethod().getAlgorithm());
    }

    private static void removeContent(Node node) {
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }

    private Document decryptElement(Element element) throws XMLEncryptionException {
        LOG.debug("Decrypting element...");
        if (element == null) {
            throw new XMLEncryptionException("empty", "Cannot decrypt null element");
        }
        if (element.getParentNode() == null) {
            throw new XMLEncryptionException("empty", "The element can't be serialized as it has no parent");
        }
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        byte[] octets = decryptToByteArray(element);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Decrypted octets:\n" + new String(octets));
        }
        Node sourceParent = element.getParentNode();
        try {
            Node decryptedNode = this.serializer.deserialize(octets, sourceParent);
            if (sourceParent != null && 9 == sourceParent.getNodeType()) {
                this.contextDocument.removeChild(this.contextDocument.getDocumentElement());
                this.contextDocument.appendChild(decryptedNode);
            } else if (sourceParent != null) {
                sourceParent.replaceChild(decryptedNode, element);
            }
            return this.contextDocument;
        } catch (IOException ex) {
            throw new XMLEncryptionException(ex);
        }
    }

    private Document decryptElementContent(Element element) throws XMLEncryptionException {
        Element e = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
        if (null == e) {
            throw new XMLEncryptionException("empty", "No EncryptedData child element.");
        }
        return decryptElement(e);
    }

    public byte[] decryptToByteArray(Element element) throws XMLEncryptionException {
        Cipher c;
        LOG.debug("Decrypting to ByteArray...");
        if (this.cipherMode != 2) {
            throw new XMLEncryptionException("empty", "XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        EncryptedData encryptedData = this.factory.newEncryptedData(element);
        String encMethodAlgorithm = encryptedData.getEncryptionMethod().getAlgorithm();
        if (this.key == null) {
            KeyInfo ki = encryptedData.getKeyInfo();
            if (ki != null) {
                try {
                    EncryptedKeyResolver resolver = new EncryptedKeyResolver(encMethodAlgorithm, this.kek, this.internalKeyResolvers);
                    ki.registerInternalKeyResolver(resolver);
                    ki.setSecureValidation(this.secureValidation);
                    this.key = ki.getSecretKey();
                } catch (KeyResolverException kre) {
                    LOG.debug(kre.getMessage(), (Throwable) kre);
                }
            }
            if (this.key == null) {
                LOG.error("XMLCipher::decryptElement unable to resolve a decryption key");
                throw new XMLEncryptionException("empty", "encryption.nokey");
            }
        }
        XMLCipherInput cipherInput = new XMLCipherInput(encryptedData);
        cipherInput.setSecureValidation(this.secureValidation);
        byte[] encryptedBytes = cipherInput.getBytes();
        String jceAlgorithm = JCEMapper.translateURItoJCEID(encMethodAlgorithm);
        LOG.debug("JCE Algorithm = {}", jceAlgorithm);
        try {
            if (this.requestedJCEProvider == null) {
                c = Cipher.getInstance(jceAlgorithm);
            } else {
                c = Cipher.getInstance(jceAlgorithm, this.requestedJCEProvider);
            }
            int ivLen = JCEMapper.getIVLengthFromURI(encMethodAlgorithm) / 8;
            byte[] ivBytes = new byte[ivLen];
            System.arraycopy(encryptedBytes, 0, ivBytes, 0, ivLen);
            String blockCipherAlg = this.algorithm;
            if (blockCipherAlg == null) {
                blockCipherAlg = encMethodAlgorithm;
            }
            AlgorithmParameterSpec paramSpec = constructBlockCipherParameters(blockCipherAlg, ivBytes);
            try {
                c.init(this.cipherMode, this.key, paramSpec);
                try {
                    return c.doFinal(encryptedBytes, ivLen, encryptedBytes.length - ivLen);
                } catch (BadPaddingException | IllegalBlockSizeException e) {
                    throw new XMLEncryptionException(e);
                }
            } catch (InvalidAlgorithmParameterException | InvalidKeyException e2) {
                throw new XMLEncryptionException(e2);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e3) {
            throw new XMLEncryptionException(e3);
        }
    }

    public EncryptedData createEncryptedData(int type, String value) throws XMLEncryptionException {
        EncryptedData result = null;
        if (2 == type) {
            CipherReference cipherReference = this.factory.newCipherReference(value);
            CipherData data = this.factory.newCipherData(type);
            data.setCipherReference(cipherReference);
            result = this.factory.newEncryptedData(data);
        } else if (1 == type) {
            CipherValue cipherValue = this.factory.newCipherValue(value);
            CipherData data2 = this.factory.newCipherData(type);
            data2.setCipherValue(cipherValue);
            result = this.factory.newEncryptedData(data2);
        }
        return result;
    }

    public EncryptedKey createEncryptedKey(int type, String value) throws XMLEncryptionException {
        EncryptedKey result = null;
        if (2 == type) {
            CipherReference cipherReference = this.factory.newCipherReference(value);
            CipherData data = this.factory.newCipherData(type);
            data.setCipherReference(cipherReference);
            result = this.factory.newEncryptedKey(data);
        } else if (1 == type) {
            CipherValue cipherValue = this.factory.newCipherValue(value);
            CipherData data2 = this.factory.newCipherData(type);
            data2.setCipherValue(cipherValue);
            result = this.factory.newEncryptedKey(data2);
        }
        return result;
    }

    public AgreementMethod createAgreementMethod(String algorithm) {
        return this.factory.newAgreementMethod(algorithm);
    }

    public CipherData createCipherData(int type) {
        return this.factory.newCipherData(type);
    }

    public CipherReference createCipherReference(String uri) {
        return this.factory.newCipherReference(uri);
    }

    public CipherValue createCipherValue(String value) {
        return this.factory.newCipherValue(value);
    }

    public EncryptionMethod createEncryptionMethod(String algorithm) {
        return this.factory.newEncryptionMethod(algorithm);
    }

    public EncryptionProperties createEncryptionProperties() {
        return this.factory.newEncryptionProperties();
    }

    public EncryptionProperty createEncryptionProperty() {
        return this.factory.newEncryptionProperty();
    }

    public ReferenceList createReferenceList(int type) {
        return this.factory.newReferenceList(type);
    }

    public Transforms createTransforms() {
        return this.factory.newTransforms();
    }

    public Transforms createTransforms(Document doc) {
        return this.factory.newTransforms(doc);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory.class */
    private class Factory {
        private Factory() {
        }

        AgreementMethod newAgreementMethod(String algorithm) {
            return new AgreementMethodImpl(XMLCipher.this.contextDocument, algorithm);
        }

        CipherData newCipherData(int type) {
            return new CipherDataImpl(type);
        }

        CipherReference newCipherReference(String uri) {
            return new CipherReferenceImpl(uri);
        }

        CipherValue newCipherValue(String value) {
            return new CipherValueImpl(value);
        }

        EncryptedData newEncryptedData(CipherData data) {
            return new EncryptedDataImpl(data);
        }

        EncryptedKey newEncryptedKey(CipherData data) {
            return new EncryptedKeyImpl(data);
        }

        EncryptionMethod newEncryptionMethod(String algorithm) {
            return new EncryptionMethodImpl(algorithm);
        }

        EncryptionProperties newEncryptionProperties() {
            return new EncryptionPropertiesImpl();
        }

        EncryptionProperty newEncryptionProperty() {
            return new EncryptionPropertyImpl();
        }

        ReferenceList newReferenceList(int type) {
            return new ReferenceListImpl(type);
        }

        Transforms newTransforms() {
            return new TransformsImpl();
        }

        Transforms newTransforms(Document doc) {
            return new TransformsImpl(doc);
        }

        CipherData newCipherData(Element element) throws XMLEncryptionException {
            if (null == element) {
                throw new NullPointerException("element is null");
            }
            int type = 0;
            Element e = null;
            if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERVALUE).getLength() > 0) {
                type = 1;
                e = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERVALUE).item(0);
            } else if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERREFERENCE).getLength() > 0) {
                type = 2;
                e = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERREFERENCE).item(0);
            }
            CipherData result = newCipherData(type);
            if (type == 1) {
                result.setCipherValue(newCipherValue(e));
            } else if (type == 2) {
                result.setCipherReference(newCipherReference(e));
            }
            return result;
        }

        CipherReference newCipherReference(Element element) throws XMLEncryptionException {
            Attr uriAttr = element.getAttributeNodeNS(null, "URI");
            CipherReference result = new CipherReferenceImpl(uriAttr);
            NodeList transformsElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
            Element transformsElement = (Element) transformsElements.item(0);
            if (transformsElement != null) {
                XMLCipher.LOG.debug("Creating a DSIG based Transforms element");
                try {
                    result.setTransforms(new TransformsImpl(transformsElement));
                } catch (XMLSecurityException e) {
                    throw new XMLEncryptionException(e);
                }
            }
            return result;
        }

        CipherValue newCipherValue(Element element) {
            String value = XMLUtils.getFullTextChildrenFromNode(element);
            return newCipherValue(value);
        }

        EncryptedData newEncryptedData(Element element) throws XMLEncryptionException {
            NodeList dataElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERDATA);
            Element dataElement = (Element) dataElements.item(dataElements.getLength() - 1);
            CipherData data = newCipherData(dataElement);
            EncryptedData result = newEncryptedData(data);
            result.setId(element.getAttributeNS(null, "Id"));
            result.setType(element.getAttributeNS(null, "Type"));
            result.setMimeType(element.getAttributeNS(null, "MimeType"));
            result.setEncoding(element.getAttributeNS(null, "Encoding"));
            Element encryptionMethodElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONMETHOD).item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(newEncryptionMethod(encryptionMethodElement));
            }
            Element keyInfoElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYINFO).item(0);
            if (null != keyInfoElement) {
                KeyInfo ki = newKeyInfo(keyInfoElement);
                result.setKeyInfo(ki);
            }
            Element encryptionPropertiesElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTIES).item(0);
            if (null != encryptionPropertiesElement) {
                result.setEncryptionProperties(newEncryptionProperties(encryptionPropertiesElement));
            }
            return result;
        }

        EncryptedKey newEncryptedKey(Element element) throws XMLEncryptionException {
            NodeList dataElements = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERDATA);
            Element dataElement = (Element) dataElements.item(dataElements.getLength() - 1);
            CipherData data = newCipherData(dataElement);
            EncryptedKey result = newEncryptedKey(data);
            result.setId(element.getAttributeNS(null, "Id"));
            result.setType(element.getAttributeNS(null, "Type"));
            result.setMimeType(element.getAttributeNS(null, "MimeType"));
            result.setEncoding(element.getAttributeNS(null, "Encoding"));
            result.setRecipient(element.getAttributeNS(null, EncryptionConstants._ATT_RECIPIENT));
            Element encryptionMethodElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONMETHOD).item(0);
            if (null != encryptionMethodElement) {
                result.setEncryptionMethod(newEncryptionMethod(encryptionMethodElement));
            }
            Element keyInfoElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYINFO).item(0);
            if (null != keyInfoElement) {
                KeyInfo ki = newKeyInfo(keyInfoElement);
                result.setKeyInfo(ki);
            }
            Element encryptionPropertiesElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTIES).item(0);
            if (null != encryptionPropertiesElement) {
                result.setEncryptionProperties(newEncryptionProperties(encryptionPropertiesElement));
            }
            Element referenceListElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_REFERENCELIST).item(0);
            if (null != referenceListElement) {
                result.setReferenceList(newReferenceList(referenceListElement));
            }
            Element carriedNameElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CARRIEDKEYNAME).item(0);
            if (null != carriedNameElement) {
                result.setCarriedName(carriedNameElement.getFirstChild().getNodeValue());
            }
            return result;
        }

        KeyInfo newKeyInfo(Element element) throws XMLEncryptionException {
            try {
                KeyInfoEnc ki = new KeyInfoEnc(element, null);
                ki.setSecureValidation(XMLCipher.this.secureValidation);
                if (XMLCipher.this.internalKeyResolvers != null) {
                    int size = XMLCipher.this.internalKeyResolvers.size();
                    for (int i = 0; i < size; i++) {
                        ki.registerInternalKeyResolver((KeyResolverSpi) XMLCipher.this.internalKeyResolvers.get(i));
                    }
                }
                return ki;
            } catch (XMLSecurityException xse) {
                throw new XMLEncryptionException(xse, "KeyInfo.error");
            }
        }

        EncryptionMethod newEncryptionMethod(Element element) {
            String encAlgorithm = element.getAttributeNS(null, "Algorithm");
            EncryptionMethod result = newEncryptionMethod(encAlgorithm);
            Element keySizeElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KEYSIZE).item(0);
            if (null != keySizeElement) {
                result.setKeySize(Integer.parseInt(keySizeElement.getFirstChild().getNodeValue()));
            }
            Element oaepParamsElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_OAEPPARAMS).item(0);
            if (null != oaepParamsElement) {
                String oaepParams = oaepParamsElement.getFirstChild().getNodeValue();
                result.setOAEPparams(XMLUtils.decode(oaepParams.getBytes(StandardCharsets.UTF_8)));
            }
            Element digestElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "DigestMethod").item(0);
            if (digestElement != null) {
                String digestAlgorithm = digestElement.getAttributeNS(null, "Algorithm");
                result.setDigestAlgorithm(digestAlgorithm);
            }
            Element mgfElement = (Element) element.getElementsByTagNameNS("http://www.w3.org/2009/xmlenc11#", EncryptionConstants._TAG_MGF).item(0);
            if (mgfElement != null && !"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(XMLCipher.this.algorithm)) {
                String mgfAlgorithm = mgfElement.getAttributeNS(null, "Algorithm");
                result.setMGFAlgorithm(mgfAlgorithm);
            }
            return result;
        }

        EncryptionProperties newEncryptionProperties(Element element) {
            EncryptionProperties result = newEncryptionProperties();
            result.setId(element.getAttributeNS(null, "Id"));
            NodeList encryptionPropertyList = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
            int length = encryptionPropertyList.getLength();
            for (int i = 0; i < length; i++) {
                Node n = encryptionPropertyList.item(i);
                if (null != n) {
                    result.addEncryptionProperty(newEncryptionProperty((Element) n));
                }
            }
            return result;
        }

        EncryptionProperty newEncryptionProperty(Element element) {
            EncryptionProperty result = newEncryptionProperty();
            result.setTarget(element.getAttributeNS(null, "Target"));
            result.setId(element.getAttributeNS(null, "Id"));
            return result;
        }

        ReferenceList newReferenceList(Element element) {
            int type = 0;
            if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_DATAREFERENCE).item(0)) {
                type = 1;
            } else if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KEYREFERENCE).item(0)) {
                type = 2;
            }
            ReferenceList result = new ReferenceListImpl(type);
            if (1 == type) {
                NodeList list = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_DATAREFERENCE);
                int drLength = list.getLength();
                for (int i = 0; i < drLength; i++) {
                    String uri = ((Element) list.item(i)).getAttributeNS(null, "URI");
                    result.add(result.newDataReference(uri));
                }
            } else if (2 == type) {
                NodeList list2 = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_KEYREFERENCE);
                int krLength = list2.getLength();
                for (int i2 = 0; i2 < krLength; i2++) {
                    String uri2 = ((Element) list2.item(i2)).getAttributeNS(null, "URI");
                    result.add(result.newKeyReference(uri2));
                }
            }
            return result;
        }

        Element toElement(EncryptedData encryptedData) {
            return ((EncryptedDataImpl) encryptedData).toElement();
        }

        Element toElement(EncryptedKey encryptedKey) {
            return ((EncryptedKeyImpl) encryptedKey).toElement();
        }

        Element toElement(ReferenceList referenceList) {
            return ((ReferenceListImpl) referenceList).toElement();
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$CipherDataImpl.class */
        private class CipherDataImpl implements CipherData {
            private static final String valueMessage = "Data type is reference type.";
            private static final String referenceMessage = "Data type is value type.";
            private CipherValue cipherValue;
            private CipherReference cipherReference;
            private int cipherType;

            public CipherDataImpl(int type) {
                this.cipherType = PKIFailureInfo.systemUnavail;
                this.cipherType = type;
            }

            @Override // org.apache.xml.security.encryption.CipherData
            public CipherValue getCipherValue() {
                return this.cipherValue;
            }

            @Override // org.apache.xml.security.encryption.CipherData
            public void setCipherValue(CipherValue value) throws XMLEncryptionException {
                if (this.cipherType == 2) {
                    throw new XMLEncryptionException(new UnsupportedOperationException(valueMessage));
                }
                this.cipherValue = value;
            }

            @Override // org.apache.xml.security.encryption.CipherData
            public CipherReference getCipherReference() {
                return this.cipherReference;
            }

            @Override // org.apache.xml.security.encryption.CipherData
            public void setCipherReference(CipherReference reference) throws XMLEncryptionException {
                if (this.cipherType == 1) {
                    throw new XMLEncryptionException(new UnsupportedOperationException(referenceMessage));
                }
                this.cipherReference = reference;
            }

            @Override // org.apache.xml.security.encryption.CipherData
            public int getDataType() {
                return this.cipherType;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_CIPHERDATA);
                if (this.cipherType == 1) {
                    result.appendChild(((CipherValueImpl) this.cipherValue).toElement());
                } else if (this.cipherType == 2) {
                    result.appendChild(((CipherReferenceImpl) this.cipherReference).toElement());
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$CipherReferenceImpl.class */
        private class CipherReferenceImpl implements CipherReference {
            private String referenceURI;
            private Transforms referenceTransforms;
            private Attr referenceNode;

            public CipherReferenceImpl(String uri) {
                this.referenceURI = uri;
                this.referenceNode = null;
            }

            public CipherReferenceImpl(Attr uri) {
                this.referenceURI = uri.getNodeValue();
                this.referenceNode = uri;
            }

            @Override // org.apache.xml.security.encryption.CipherReference
            public String getURI() {
                return this.referenceURI;
            }

            @Override // org.apache.xml.security.encryption.CipherReference
            public Attr getURIAsAttr() {
                return this.referenceNode;
            }

            @Override // org.apache.xml.security.encryption.CipherReference
            public Transforms getTransforms() {
                return this.referenceTransforms;
            }

            @Override // org.apache.xml.security.encryption.CipherReference
            public void setTransforms(Transforms transforms) {
                this.referenceTransforms = transforms;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_CIPHERREFERENCE);
                result.setAttributeNS(null, "URI", this.referenceURI);
                if (null != this.referenceTransforms) {
                    result.appendChild(((TransformsImpl) this.referenceTransforms).toElement());
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$CipherValueImpl.class */
        private class CipherValueImpl implements CipherValue {
            private String cipherValue;

            public CipherValueImpl(String value) {
                this.cipherValue = value;
            }

            @Override // org.apache.xml.security.encryption.CipherValue
            public String getValue() {
                return this.cipherValue;
            }

            @Override // org.apache.xml.security.encryption.CipherValue
            public void setValue(String value) {
                this.cipherValue = value;
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_CIPHERVALUE);
                result.appendChild(XMLCipher.this.contextDocument.createTextNode(this.cipherValue));
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptedDataImpl.class */
        private class EncryptedDataImpl extends EncryptedTypeImpl implements EncryptedData {
            public EncryptedDataImpl(CipherData data) {
                super(data);
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
                if (null != super.getId()) {
                    result.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl) super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    result.appendChild(super.getKeyInfo().getElement().cloneNode(true));
                }
                result.appendChild(((CipherDataImpl) super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl) super.getEncryptionProperties()).toElement());
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptedKeyImpl.class */
        private class EncryptedKeyImpl extends EncryptedTypeImpl implements EncryptedKey {
            private String keyRecipient;
            private ReferenceList referenceList;
            private String carriedName;

            public EncryptedKeyImpl(CipherData data) {
                super(data);
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public String getRecipient() {
                return this.keyRecipient;
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public void setRecipient(String recipient) {
                this.keyRecipient = recipient;
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public ReferenceList getReferenceList() {
                return this.referenceList;
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public void setReferenceList(ReferenceList list) {
                this.referenceList = list;
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public String getCarriedName() {
                return this.carriedName;
            }

            @Override // org.apache.xml.security.encryption.EncryptedKey
            public void setCarriedName(String name) {
                this.carriedName = name;
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTEDKEY);
                if (null != super.getId()) {
                    result.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    result.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    result.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    result.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != getRecipient()) {
                    result.setAttributeNS(null, EncryptionConstants._ATT_RECIPIENT, getRecipient());
                }
                if (null != super.getEncryptionMethod()) {
                    result.appendChild(((EncryptionMethodImpl) super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    result.appendChild(super.getKeyInfo().getElement().cloneNode(true));
                }
                result.appendChild(((CipherDataImpl) super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    result.appendChild(((EncryptionPropertiesImpl) super.getEncryptionProperties()).toElement());
                }
                if (this.referenceList != null && !this.referenceList.isEmpty()) {
                    result.appendChild(((ReferenceListImpl) getReferenceList()).toElement());
                }
                if (null != this.carriedName) {
                    Element element = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CARRIEDKEYNAME);
                    Node node = XMLCipher.this.contextDocument.createTextNode(this.carriedName);
                    element.appendChild(node);
                    result.appendChild(element);
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptedTypeImpl.class */
        private abstract class EncryptedTypeImpl {
            private String id;
            private String type;
            private String mimeType;
            private String encoding;
            private EncryptionMethod encryptionMethod;
            private KeyInfo keyInfo;
            private CipherData cipherData;
            private EncryptionProperties encryptionProperties;

            protected EncryptedTypeImpl(CipherData data) {
                this.cipherData = data;
            }

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getType() {
                return this.type;
            }

            public void setType(String type) {
                if (type == null || type.length() == 0) {
                    this.type = null;
                    return;
                }
                try {
                    URI tmpType = new URI(type);
                    this.type = tmpType.toString();
                } catch (URISyntaxException ex) {
                    throw ((IllegalArgumentException) new IllegalArgumentException().initCause(ex));
                }
            }

            public String getMimeType() {
                return this.mimeType;
            }

            public void setMimeType(String type) {
                this.mimeType = type;
            }

            public String getEncoding() {
                return this.encoding;
            }

            public void setEncoding(String encoding) {
                if (encoding == null || encoding.length() == 0) {
                    this.encoding = null;
                    return;
                }
                try {
                    URI tmpEncoding = new URI(encoding);
                    this.encoding = tmpEncoding.toString();
                } catch (URISyntaxException ex) {
                    throw ((IllegalArgumentException) new IllegalArgumentException().initCause(ex));
                }
            }

            public EncryptionMethod getEncryptionMethod() {
                return this.encryptionMethod;
            }

            public void setEncryptionMethod(EncryptionMethod method) {
                this.encryptionMethod = method;
            }

            public KeyInfo getKeyInfo() {
                return this.keyInfo;
            }

            public void setKeyInfo(KeyInfo info) {
                this.keyInfo = info;
            }

            public CipherData getCipherData() {
                return this.cipherData;
            }

            public EncryptionProperties getEncryptionProperties() {
                return this.encryptionProperties;
            }

            public void setEncryptionProperties(EncryptionProperties properties) {
                this.encryptionProperties = properties;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptionMethodImpl.class */
        private class EncryptionMethodImpl implements EncryptionMethod {
            private String algorithm;
            private int keySize = PKIFailureInfo.systemUnavail;
            private byte[] oaepParams;
            private List<Element> encryptionMethodInformation;
            private String digestAlgorithm;
            private String mgfAlgorithm;

            public EncryptionMethodImpl(String algorithm) {
                try {
                    URI tmpAlgorithm = new URI(algorithm);
                    this.algorithm = tmpAlgorithm.toString();
                    this.encryptionMethodInformation = new LinkedList();
                } catch (URISyntaxException ex) {
                    throw ((IllegalArgumentException) new IllegalArgumentException().initCause(ex));
                }
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public String getAlgorithm() {
                return this.algorithm;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public int getKeySize() {
                return this.keySize;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void setKeySize(int size) {
                this.keySize = size;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public byte[] getOAEPparams() {
                return this.oaepParams;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void setOAEPparams(byte[] params) {
                this.oaepParams = params;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void setDigestAlgorithm(String digestAlgorithm) {
                this.digestAlgorithm = digestAlgorithm;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public String getDigestAlgorithm() {
                return this.digestAlgorithm;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void setMGFAlgorithm(String mgfAlgorithm) {
                this.mgfAlgorithm = mgfAlgorithm;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public String getMGFAlgorithm() {
                return this.mgfAlgorithm;
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public Iterator<Element> getEncryptionMethodInformation() {
                return this.encryptionMethodInformation.iterator();
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void addEncryptionMethodInformation(Element info) {
                this.encryptionMethodInformation.add(info);
            }

            @Override // org.apache.xml.security.encryption.EncryptionMethod
            public void removeEncryptionMethodInformation(Element info) {
                this.encryptionMethodInformation.remove(info);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_ENCRYPTIONMETHOD);
                result.setAttributeNS(null, "Algorithm", this.algorithm);
                if (this.keySize > 0) {
                    result.appendChild(XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_KEYSIZE).appendChild(XMLCipher.this.contextDocument.createTextNode(String.valueOf(this.keySize))));
                }
                if (null != this.oaepParams) {
                    Element oaepElement = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_OAEPPARAMS);
                    oaepElement.appendChild(XMLCipher.this.contextDocument.createTextNode(XMLUtils.encodeToString(this.oaepParams)));
                    result.appendChild(oaepElement);
                }
                if (this.digestAlgorithm != null) {
                    Element digestElement = XMLUtils.createElementInSignatureSpace(XMLCipher.this.contextDocument, "DigestMethod");
                    digestElement.setAttributeNS(null, "Algorithm", this.digestAlgorithm);
                    digestElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#"), "http://www.w3.org/2000/09/xmldsig#");
                    result.appendChild(digestElement);
                }
                if (this.mgfAlgorithm != null && !"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(this.algorithm)) {
                    Element mgfElement = XMLUtils.createElementInEncryption11Space(XMLCipher.this.contextDocument, EncryptionConstants._TAG_MGF);
                    mgfElement.setAttributeNS(null, "Algorithm", this.mgfAlgorithm);
                    mgfElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2009/xmlenc11#"), "http://www.w3.org/2009/xmlenc11#");
                    result.appendChild(mgfElement);
                }
                for (Element element : this.encryptionMethodInformation) {
                    result.appendChild(element);
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptionPropertiesImpl.class */
        private class EncryptionPropertiesImpl implements EncryptionProperties {
            private String id;
            private List<EncryptionProperty> encryptionProperties = new LinkedList();

            public EncryptionPropertiesImpl() {
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperties
            public String getId() {
                return this.id;
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperties
            public void setId(String id) {
                this.id = id;
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperties
            public Iterator<EncryptionProperty> getEncryptionProperties() {
                return this.encryptionProperties.iterator();
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperties
            public void addEncryptionProperty(EncryptionProperty property) {
                this.encryptionProperties.add(property);
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperties
            public void removeEncryptionProperty(EncryptionProperty property) {
                this.encryptionProperties.remove(property);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_ENCRYPTIONPROPERTIES);
                if (null != this.id) {
                    result.setAttributeNS(null, "Id", this.id);
                }
                Iterator<EncryptionProperty> itr = getEncryptionProperties();
                while (itr.hasNext()) {
                    result.appendChild(((EncryptionPropertyImpl) itr.next()).toElement());
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$EncryptionPropertyImpl.class */
        private class EncryptionPropertyImpl implements EncryptionProperty {
            private String target;
            private String id;
            private Map<String, String> attributeMap = new HashMap();
            private List<Element> encryptionInformation = new LinkedList();

            public EncryptionPropertyImpl() {
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public String getTarget() {
                return this.target;
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public void setTarget(String target) {
                if (target == null || target.length() == 0) {
                    this.target = null;
                    return;
                }
                if (target.charAt(0) == '#') {
                    this.target = target;
                    return;
                }
                try {
                    URI tmpTarget = new URI(target);
                    this.target = tmpTarget.toString();
                } catch (URISyntaxException ex) {
                    throw ((IllegalArgumentException) new IllegalArgumentException().initCause(ex));
                }
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public String getId() {
                return this.id;
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public void setId(String id) {
                this.id = id;
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public String getAttribute(String attribute) {
                return this.attributeMap.get(attribute);
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public void setAttribute(String attribute, String value) {
                this.attributeMap.put(attribute, value);
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public Iterator<Element> getEncryptionInformation() {
                return this.encryptionInformation.iterator();
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public void addEncryptionInformation(Element info) {
                this.encryptionInformation.add(info);
            }

            @Override // org.apache.xml.security.encryption.EncryptionProperty
            public void removeEncryptionInformation(Element info) {
                this.encryptionInformation.remove(info);
            }

            Element toElement() {
                Element result = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, EncryptionConstants._TAG_ENCRYPTIONPROPERTY);
                if (null != this.target) {
                    result.setAttributeNS(null, "Target", this.target);
                }
                if (null != this.id) {
                    result.setAttributeNS(null, "Id", this.id);
                }
                if (!this.attributeMap.isEmpty()) {
                    for (Map.Entry<String, String> entry : this.attributeMap.entrySet()) {
                        result.setAttributeNS("http://www.w3.org/XML/1998/namespace", entry.getKey(), entry.getValue());
                    }
                }
                if (!this.encryptionInformation.isEmpty()) {
                    for (Element element : this.encryptionInformation) {
                        result.appendChild(element);
                    }
                }
                return result;
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$TransformsImpl.class */
        private class TransformsImpl extends org.apache.xml.security.transforms.Transforms implements Transforms {
            public TransformsImpl() {
                super(XMLCipher.this.contextDocument);
            }

            public TransformsImpl(Document doc) {
                if (doc == null) {
                    throw new RuntimeException("Document is null");
                }
                setDocument(doc);
                setElement(createElementForFamilyLocal(getBaseNamespace(), getBaseLocalName()));
            }

            public TransformsImpl(Element element) throws XMLSecurityException {
                super(element, "");
            }

            public Element toElement() {
                if (getDocument() == null) {
                    setDocument(XMLCipher.this.contextDocument);
                }
                return getElement();
            }

            @Override // org.apache.xml.security.encryption.Transforms
            public org.apache.xml.security.transforms.Transforms getDSTransforms() {
                return this;
            }

            @Override // org.apache.xml.security.utils.SignatureElementProxy, org.apache.xml.security.utils.ElementProxy
            public String getBaseNamespace() {
                return "http://www.w3.org/2001/04/xmlenc#";
            }
        }

        /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$ReferenceListImpl.class */
        private class ReferenceListImpl implements ReferenceList {
            private Class<?> sentry;
            private List<Reference> references;

            public ReferenceListImpl(int type) {
                if (type == 1) {
                    this.sentry = DataReference.class;
                } else if (type == 2) {
                    this.sentry = KeyReference.class;
                } else {
                    throw new IllegalArgumentException();
                }
                this.references = new LinkedList();
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public void add(Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.add(reference);
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public void remove(Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.remove(reference);
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public int size() {
                return this.references.size();
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public boolean isEmpty() {
                return this.references.isEmpty();
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public Iterator<Reference> getReferences() {
                return this.references.iterator();
            }

            Element toElement() {
                Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_REFERENCELIST);
                for (Reference reference : this.references) {
                    result.appendChild(((ReferenceImpl) reference).toElement());
                }
                return result;
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public Reference newDataReference(String uri) {
                return new DataReference(uri);
            }

            @Override // org.apache.xml.security.encryption.ReferenceList
            public Reference newKeyReference(String uri) {
                return new KeyReference(uri);
            }

            /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$ReferenceListImpl$ReferenceImpl.class */
            private abstract class ReferenceImpl implements Reference {
                private String uri;
                private List<Element> referenceInformation = new LinkedList();

                @Override // org.apache.xml.security.encryption.Reference
                public abstract String getType();

                ReferenceImpl(String uri) {
                    this.uri = uri;
                }

                @Override // org.apache.xml.security.encryption.Reference
                public String getURI() {
                    return this.uri;
                }

                @Override // org.apache.xml.security.encryption.Reference
                public Iterator<Element> getElementRetrievalInformation() {
                    return this.referenceInformation.iterator();
                }

                @Override // org.apache.xml.security.encryption.Reference
                public void setURI(String uri) {
                    this.uri = uri;
                }

                @Override // org.apache.xml.security.encryption.Reference
                public void removeElementRetrievalInformation(Element node) {
                    this.referenceInformation.remove(node);
                }

                @Override // org.apache.xml.security.encryption.Reference
                public void addElementRetrievalInformation(Element node) {
                    this.referenceInformation.add(node);
                }

                public Element toElement() {
                    String tagName = getType();
                    Element result = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", tagName);
                    result.setAttributeNS(null, "URI", this.uri);
                    return result;
                }
            }

            /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$ReferenceListImpl$DataReference.class */
            private class DataReference extends ReferenceImpl {
                DataReference(String uri) {
                    super(uri);
                }

                @Override // org.apache.xml.security.encryption.XMLCipher.Factory.ReferenceListImpl.ReferenceImpl, org.apache.xml.security.encryption.Reference
                public String getType() {
                    return EncryptionConstants._TAG_DATAREFERENCE;
                }
            }

            /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipher$Factory$ReferenceListImpl$KeyReference.class */
            private class KeyReference extends ReferenceImpl {
                KeyReference(String uri) {
                    super(uri);
                }

                @Override // org.apache.xml.security.encryption.XMLCipher.Factory.ReferenceListImpl.ReferenceImpl, org.apache.xml.security.encryption.Reference
                public String getType() {
                    return EncryptionConstants._TAG_KEYREFERENCE;
                }
            }
        }
    }

    private static boolean haveFunctionalIdentityTransformer() {
        try {
            DOMResult domResult = new DOMResult();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.newTransformer().transform(new StreamSource(new ByteArrayInputStream("<a:e1 xmlns:a=\"a\" xmlns:b=\"b\"><a xmlns=\"a\" xmlns:b=\"b\"/></a:e1>".getBytes(StandardCharsets.UTF_8))), domResult);
            boolean result = false;
            if (domResult.getNode().getFirstChild().getFirstChild().hasAttributes() && domResult.getNode().getFirstChild().getFirstChild().getAttributes().getLength() >= 1) {
                result = "http://www.w3.org/2000/xmlns/".equals(domResult.getNode().getFirstChild().getFirstChild().getAttributes().item(1).getNamespaceURI());
            }
            LOG.debug("Have functional IdentityTransformer: {}", Boolean.valueOf(result));
            return result;
        } catch (Exception e) {
            LOG.debug(e.getMessage(), (Throwable) e);
            return false;
        }
    }
}
