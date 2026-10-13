package org.apache.xml.security.stax.ext;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.validation.Schema;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants.class */
public class XMLSecurityConstants {
    public static final DatatypeFactory datatypeFactory;
    public static final XMLOutputFactory xmlOutputFactory;
    public static final XMLOutputFactory xmlOutputFactoryNonRepairingNs;
    private static volatile SecureRandom SECURE_RANDOM;
    private static final Object SECURE_RANDOM_LOCK = new Object();
    private static final String RANDOM_ALGORITHM_KEY = "org.apache.xml.security.securerandom.algorithm";
    private static JAXBContext jaxbContext;
    private static Schema schema;
    public static final String XMLINPUTFACTORY = "XMLInputFactory";
    public static final String NS_XML = "http://www.w3.org/2000/xmlns/";
    public static final String NS_XMLENC = "http://www.w3.org/2001/04/xmlenc#";
    public static final String NS_XMLENC11 = "http://www.w3.org/2009/xmlenc11#";
    public static final String NS_DSIG = "http://www.w3.org/2000/09/xmldsig#";
    public static final String NS_DSIG_MORE = "http://www.w3.org/2001/04/xmldsig-more#";
    public static final String NS_DSIG_MORE_2007_05 = "http://www.w3.org/2007/05/xmldsig-more#";
    public static final String NS_DSIG11 = "http://www.w3.org/2009/xmldsig11#";
    public static final String NS_WSSE11 = "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd";
    public static final String NS_XOP = "http://www.w3.org/2004/08/xop/include";
    public static final String PREFIX_XENC = "xenc";
    public static final String PREFIX_XENC11 = "xenc11";
    public static final QName TAG_xenc_EncryptedKey;
    public static final QName ATT_NULL_Id;
    public static final QName ATT_NULL_Type;
    public static final QName ATT_NULL_MimeType;
    public static final QName ATT_NULL_Encoding;
    public static final QName TAG_xenc_EncryptionMethod;
    public static final QName ATT_NULL_Algorithm;
    public static final QName TAG_xenc_OAEPparams;
    public static final QName TAG_xenc11_MGF;
    public static final String PREFIX_DSIG = "dsig";
    public static final String PREFIX_DSIG_MORE_PSS = "pss";
    public static final QName TAG_dsig_KeyInfo;
    public static final QName TAG_xenc_EncryptionProperties;
    public static final QName TAG_xenc_CipherData;
    public static final QName TAG_xenc_CipherValue;
    public static final QName TAG_xenc_CipherReference;
    public static final QName TAG_xenc_ReferenceList;
    public static final QName TAG_xenc_DataReference;
    public static final QName ATT_NULL_URI;
    public static final QName TAG_xenc_EncryptedData;
    public static final QName TAG_xenc_Transforms;
    public static final String PREFIX_WSSE11 = "wsse11";
    public static final QName TAG_wsse11_EncryptedHeader;
    public static final QName TAG_dsig_Signature;
    public static final QName TAG_dsig_SignedInfo;
    public static final QName TAG_dsig_CanonicalizationMethod;
    public static final QName TAG_dsig_SignatureMethod;
    public static final QName TAG_dsig_HMACOutputLength;
    public static final QName TAG_dsig_Reference;
    public static final QName TAG_dsig_Transforms;
    public static final QName TAG_dsig_Transform;
    public static final QName TAG_dsig_DigestMethod;
    public static final QName TAG_dsig_DigestValue;
    public static final QName TAG_dsig_SignatureValue;
    public static final QName TAG_dsig_Manifest;
    public static final QName TAG_dsig_X509Data;
    public static final QName TAG_dsig_X509IssuerSerial;
    public static final QName TAG_dsig_X509IssuerName;
    public static final QName TAG_dsig_X509SerialNumber;
    public static final QName TAG_dsig_X509SKI;
    public static final QName TAG_dsig_X509Certificate;
    public static final QName TAG_dsig_X509SubjectName;
    public static final QName TAG_dsig_KeyName;
    public static final QName TAG_dsig_KeyValue;
    public static final QName TAG_dsig_RSAKeyValue;
    public static final QName TAG_dsig_Modulus;
    public static final QName TAG_dsig_Exponent;
    public static final QName TAG_dsig_DSAKeyValue;
    public static final QName TAG_dsig_P;
    public static final QName TAG_dsig_Q;
    public static final QName TAG_dsig_G;
    public static final QName TAG_dsig_Y;
    public static final QName TAG_dsig_J;
    public static final QName TAG_dsig_Seed;
    public static final QName TAG_dsig_PgenCounter;
    public static final String PREFIX_DSIG11 = "dsig11";
    public static final QName TAG_dsig11_ECKeyValue;
    public static final QName TAG_dsig11_ECParameters;
    public static final QName TAG_dsig11_NamedCurve;
    public static final QName TAG_dsig11_PublicKey;
    public static final String NS_C14N_EXCL = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String NS_XMLDSIG_FILTER2 = "http://www.w3.org/2002/06/xmldsig-filter2";
    public static final String NS_XMLDSIG_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    public static final String NS_XMLDSIG_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String NS_XMLDSIG_HMACSHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String NS_XMLDSIG_RSASHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String NS_XMLDSIG_MANIFEST = "http://www.w3.org/2000/09/xmldsig#Manifest";
    public static final String NS_XMLDSIG_HMACSHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String NS_XMLDSIG_HMACSHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String NS_XMLDSIG_HMACSHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String NS_XMLDSIG_RSASHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String NS_XMLDSIG_RSASHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String NS_XMLDSIG_RSASHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String NS_XENC_TRIPLE_DES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String NS_XENC_AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String NS_XENC11_AES128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
    public static final String NS_XENC_AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String NS_XENC11_AES192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
    public static final String NS_XENC_AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String NS_XENC11_AES256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
    public static final String NS_XENC_RSA15 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String NS_XENC_RSAOAEPMGF1P = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String NS_XENC11_RSAOAEP = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
    public static final String NS_MGF1_SHA1 = "http://www.w3.org/2009/xmlenc11#mgf1sha1";
    public static final String NS_MGF1_SHA224 = "http://www.w3.org/2009/xmlenc11#mgf1sha224";
    public static final String NS_MGF1_SHA256 = "http://www.w3.org/2009/xmlenc11#mgf1sha256";
    public static final String NS_MGF1_SHA384 = "http://www.w3.org/2009/xmlenc11#mgf1sha384";
    public static final String NS_MGF1_SHA512 = "http://www.w3.org/2009/xmlenc11#mgf1sha512";
    public static final String NS_XENC_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String NS_XENC_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String PREFIX_C14N_EXCL = "c14nEx";
    public static final QName ATT_NULL_PrefixList;
    public static final QName TAG_c14nExcl_InclusiveNamespaces;
    public static final String NS_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String NS_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String NS_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String NS_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String NS_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
    public static final String NS_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    public static final QName TAG_XOP_INCLUDE;
    public static final String PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE = "PROP_USE_THIS_TOKEN_ID_FOR_SIGNATURE";
    public static final String PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION = "PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTION";
    public static final String PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTED_KEY = "PROP_USE_THIS_TOKEN_ID_FOR_ENCRYPTED_KEY";
    public static final String SIGNATURE_PARTS = "signatureParts";
    public static final String ENCRYPTION_PARTS = "encryptionParts";
    public static final Action SIGNATURE;
    public static final Action ENCRYPTION;

    @Deprecated
    public static final Action ENCRYPT;
    public static final QName TAG_dsigmore_RSAPSSPARAMS;
    public static final QName TAG_dsigmore_SALTLENGTH;
    public static final QName TAG_dsigmore_TRAILERFIELD;
    public static final AlgorithmUsage Sym_Key_Wrap;
    public static final AlgorithmUsage Asym_Key_Wrap;
    public static final AlgorithmUsage Sym_Sig;
    public static final AlgorithmUsage Asym_Sig;
    public static final AlgorithmUsage Enc;
    public static final AlgorithmUsage SigDig;
    public static final AlgorithmUsage EncDig;
    public static final AlgorithmUsage SigC14n;
    public static final AlgorithmUsage SigTransform;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$ContentType.class */
    public enum ContentType {
        PLAIN,
        SIGNATURE,
        ENCRYPTION
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$DIRECTION.class */
    public enum DIRECTION {
        IN,
        OUT
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$Phase.class */
    public enum Phase {
        PREPROCESSING,
        PROCESSING,
        POSTPROCESSING
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$TransformMethod.class */
    public enum TransformMethod {
        XMLSecEvent,
        InputStream
    }

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
            xmlOutputFactory = XMLOutputFactory.newInstance();
            xmlOutputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
            xmlOutputFactoryNonRepairingNs = XMLOutputFactory.newInstance();
            xmlOutputFactoryNonRepairingNs.setProperty("javax.xml.stream.isRepairingNamespaces", false);
            TAG_xenc_EncryptedKey = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTEDKEY, PREFIX_XENC);
            ATT_NULL_Id = new QName(null, "Id");
            ATT_NULL_Type = new QName(null, "Type");
            ATT_NULL_MimeType = new QName(null, "MimeType");
            ATT_NULL_Encoding = new QName(null, "Encoding");
            TAG_xenc_EncryptionMethod = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONMETHOD, PREFIX_XENC);
            ATT_NULL_Algorithm = new QName(null, "Algorithm");
            TAG_xenc_OAEPparams = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_OAEPPARAMS, PREFIX_XENC);
            TAG_xenc11_MGF = new QName("http://www.w3.org/2009/xmlenc11#", EncryptionConstants._TAG_MGF, PREFIX_XENC11);
            TAG_dsig_KeyInfo = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYINFO, PREFIX_DSIG);
            TAG_xenc_EncryptionProperties = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_ENCRYPTIONPROPERTIES, PREFIX_XENC);
            TAG_xenc_CipherData = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERDATA, PREFIX_XENC);
            TAG_xenc_CipherValue = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERVALUE, PREFIX_XENC);
            TAG_xenc_CipherReference = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_CIPHERREFERENCE, PREFIX_XENC);
            TAG_xenc_ReferenceList = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_REFERENCELIST, PREFIX_XENC);
            TAG_xenc_DataReference = new QName("http://www.w3.org/2001/04/xmlenc#", EncryptionConstants._TAG_DATAREFERENCE, PREFIX_XENC);
            ATT_NULL_URI = new QName(null, "URI");
            TAG_xenc_EncryptedData = new QName("http://www.w3.org/2001/04/xmlenc#", "EncryptedData", PREFIX_XENC);
            TAG_xenc_Transforms = new QName("http://www.w3.org/2001/04/xmlenc#", "Transforms", PREFIX_XENC);
            TAG_wsse11_EncryptedHeader = new QName(NS_WSSE11, "EncryptedHeader", PREFIX_WSSE11);
            TAG_dsig_Signature = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature", PREFIX_DSIG);
            TAG_dsig_SignedInfo = new QName("http://www.w3.org/2000/09/xmldsig#", "SignedInfo", PREFIX_DSIG);
            TAG_dsig_CanonicalizationMethod = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_CANONICALIZATIONMETHOD, PREFIX_DSIG);
            TAG_dsig_SignatureMethod = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod", PREFIX_DSIG);
            TAG_dsig_HMACOutputLength = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_HMACOUTPUTLENGTH, PREFIX_DSIG);
            TAG_dsig_Reference = new QName("http://www.w3.org/2000/09/xmldsig#", "Reference", PREFIX_DSIG);
            TAG_dsig_Transforms = new QName("http://www.w3.org/2000/09/xmldsig#", "Transforms", PREFIX_DSIG);
            TAG_dsig_Transform = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM, PREFIX_DSIG);
            TAG_dsig_DigestMethod = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod", PREFIX_DSIG);
            TAG_dsig_DigestValue = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue", PREFIX_DSIG);
            TAG_dsig_SignatureValue = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureValue", PREFIX_DSIG);
            TAG_dsig_Manifest = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_MANIFEST, PREFIX_DSIG);
            TAG_dsig_X509Data = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509DATA, PREFIX_DSIG);
            TAG_dsig_X509IssuerSerial = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509ISSUERSERIAL, PREFIX_DSIG);
            TAG_dsig_X509IssuerName = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509ISSUERNAME, PREFIX_DSIG);
            TAG_dsig_X509SerialNumber = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SERIALNUMBER, PREFIX_DSIG);
            TAG_dsig_X509SKI = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SKI, PREFIX_DSIG);
            TAG_dsig_X509Certificate = new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate", PREFIX_DSIG);
            TAG_dsig_X509SubjectName = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_X509SUBJECTNAME, PREFIX_DSIG);
            TAG_dsig_KeyName = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYNAME, PREFIX_DSIG);
            TAG_dsig_KeyValue = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_KEYVALUE, PREFIX_DSIG);
            TAG_dsig_RSAKeyValue = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_RSAKEYVALUE, PREFIX_DSIG);
            TAG_dsig_Modulus = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_MODULUS, PREFIX_DSIG);
            TAG_dsig_Exponent = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_EXPONENT, PREFIX_DSIG);
            TAG_dsig_DSAKeyValue = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_DSAKEYVALUE, PREFIX_DSIG);
            TAG_dsig_P = new QName("http://www.w3.org/2000/09/xmldsig#", "P", PREFIX_DSIG);
            TAG_dsig_Q = new QName("http://www.w3.org/2000/09/xmldsig#", "Q", PREFIX_DSIG);
            TAG_dsig_G = new QName("http://www.w3.org/2000/09/xmldsig#", "G", PREFIX_DSIG);
            TAG_dsig_Y = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_Y, PREFIX_DSIG);
            TAG_dsig_J = new QName("http://www.w3.org/2000/09/xmldsig#", "J", PREFIX_DSIG);
            TAG_dsig_Seed = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_SEED, PREFIX_DSIG);
            TAG_dsig_PgenCounter = new QName("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_PGENCOUNTER, PREFIX_DSIG);
            TAG_dsig11_ECKeyValue = new QName("http://www.w3.org/2009/xmldsig11#", Constants._TAG_ECKEYVALUE, PREFIX_DSIG11);
            TAG_dsig11_ECParameters = new QName("http://www.w3.org/2009/xmldsig11#", "ECParameters", PREFIX_DSIG11);
            TAG_dsig11_NamedCurve = new QName("http://www.w3.org/2009/xmldsig11#", "NamedCurve", PREFIX_DSIG11);
            TAG_dsig11_PublicKey = new QName("http://www.w3.org/2009/xmldsig11#", "PublicKey", PREFIX_DSIG11);
            ATT_NULL_PrefixList = new QName(null, InclusiveNamespaces._ATT_EC_PREFIXLIST);
            TAG_c14nExcl_InclusiveNamespaces = new QName("http://www.w3.org/2001/10/xml-exc-c14n#", InclusiveNamespaces._TAG_EC_INCLUSIVENAMESPACES, PREFIX_C14N_EXCL);
            TAG_XOP_INCLUDE = new QName(NS_XOP, "Include", "xop");
            SIGNATURE = new Action("Signature");
            ENCRYPTION = new Action("Encryption");
            ENCRYPT = ENCRYPTION;
            TAG_dsigmore_RSAPSSPARAMS = new QName("http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_RSAPSSPARAMS, PREFIX_DSIG_MORE_PSS);
            TAG_dsigmore_SALTLENGTH = new QName("http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_SALTLENGTH, PREFIX_DSIG_MORE_PSS);
            TAG_dsigmore_TRAILERFIELD = new QName("http://www.w3.org/2007/05/xmldsig-more#", Constants._TAG_TRAILERFIELD, PREFIX_DSIG_MORE_PSS);
            Sym_Key_Wrap = new AlgorithmUsage("Sym_Key_Wrap");
            Asym_Key_Wrap = new AlgorithmUsage("Asym_Key_Wrap");
            Sym_Sig = new AlgorithmUsage("Sym_Sig");
            Asym_Sig = new AlgorithmUsage("Asym_Sig");
            Enc = new AlgorithmUsage("Enc");
            SigDig = new AlgorithmUsage("SigDig");
            EncDig = new AlgorithmUsage("EncDig");
            SigC14n = new AlgorithmUsage("SigC14n");
            SigTransform = new AlgorithmUsage("SigTransform");
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    protected XMLSecurityConstants() {
    }

    public static byte[] generateBytes(int length) throws XMLSecurityException {
        SecureRandom secureRandom;
        SecureRandom rnd = SECURE_RANDOM;
        if (rnd == null) {
            synchronized (SECURE_RANDOM_LOCK) {
                rnd = SECURE_RANDOM;
                if (rnd == null) {
                    try {
                        String prngAlgorithm = (String) AccessController.doPrivileged(() -> {
                            return System.getProperty(RANDOM_ALGORITHM_KEY);
                        });
                        if (prngAlgorithm != null) {
                            secureRandom = SecureRandom.getInstance(prngAlgorithm);
                        } else {
                            secureRandom = new SecureRandom();
                        }
                        rnd = secureRandom;
                        SECURE_RANDOM = secureRandom;
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        try {
            byte[] temp = new byte[length];
            rnd.nextBytes(temp);
            return temp;
        } catch (Exception ex) {
            throw new XMLSecurityException(ex);
        }
    }

    public static synchronized void setJaxbContext(JAXBContext jaxbContext2) {
        jaxbContext = jaxbContext2;
    }

    public static synchronized void setJaxbSchemas(Schema schema2) {
        schema = schema2;
    }

    public static synchronized Schema getJaxbSchemas() {
        return schema;
    }

    public static Unmarshaller getJaxbUnmarshaller(boolean disableSchemaValidation) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        if (!disableSchemaValidation) {
            unmarshaller.setSchema(schema);
        }
        return unmarshaller;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$Action.class */
    public static class Action extends ComparableType<Action> {
        public Action(String name) {
            super(name);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityConstants$AlgorithmUsage.class */
    public static class AlgorithmUsage extends ComparableType<AlgorithmUsage> {
        public AlgorithmUsage(String name) {
            super(name);
        }
    }
}
