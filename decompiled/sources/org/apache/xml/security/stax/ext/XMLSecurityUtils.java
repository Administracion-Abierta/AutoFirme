package org.apache.xml.security.stax.ext;

import com.aowagie.text.xml.XMLConstants;
import jakarta.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.implementations.ECDSAUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509SKI;
import org.apache.xml.security.stax.config.TransformerAlgorithmMapper;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.util.ConcreteLSInput;
import org.apache.xml.security.stax.securityEvent.DefaultTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.EncryptedKeyTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyNameTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.KeyValueTokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityEvent.X509TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/XMLSecurityUtils.class */
public class XMLSecurityUtils {
    private static final int MAX_SYMMETRIC_KEY_SIZE = 1024;

    protected XMLSecurityUtils() {
    }

    public static String dropReferenceMarker(String reference) {
        if (reference != null && reference.length() > 0 && reference.charAt(0) == '#') {
            return reference.substring(1);
        }
        return reference;
    }

    public static String getXMLEventAsString(XMLSecEvent xmlSecEvent) {
        int eventType = xmlSecEvent.getEventType();
        switch (eventType) {
            case 1:
                return "START_ELEMENT";
            case 2:
                return "END_ELEMENT";
            case 3:
                return "PROCESSING_INSTRUCTION";
            case 4:
                return "CHARACTERS";
            case 5:
                return "COMMENT";
            case 6:
            case 9:
            case 12:
            default:
                throw new IllegalArgumentException("Illegal XMLSecEvent received: " + eventType);
            case 7:
                return "START_DOCUMENT";
            case 8:
                return "END_DOCUMENT";
            case 10:
                return "ATTRIBUTE";
            case 11:
                return "DTD";
            case 13:
                return "NAMESPACE";
        }
    }

    public static Transformer getTransformer(Transformer transformer, OutputStream outputStream, Map<String, Object> properties, String algorithm, XMLSecurityConstants.DIRECTION direction) throws XMLSecurityException {
        try {
            Transformer childTransformer = (Transformer) JavaUtils.newInstanceWithEmptyConstructor(TransformerAlgorithmMapper.getTransformerClass(algorithm, direction));
            if (properties != null) {
                childTransformer.setProperties(properties);
            }
            if (outputStream != null) {
                childTransformer.setOutputStream(outputStream);
            } else {
                childTransformer.setTransformer(transformer);
            }
            return childTransformer;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new XMLSecurityException(e);
        }
    }

    public static <T> T getQNameType(List<Object> list, QName qName) {
        for (Object obj : list) {
            if (obj instanceof JAXBElement) {
                JAXBElement jAXBElement = (JAXBElement) obj;
                if (jAXBElement.getName().equals(qName)) {
                    return (T) jAXBElement.getValue();
                }
            }
        }
        return null;
    }

    public static String getQNameAttribute(Map<QName, String> attributes, QName qName) {
        return attributes.get(qName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createKeyValueTokenStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates) throws XMLSecurityException, XMLStreamException {
        if (x509Certificates == null || x509Certificates.length == 0) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        X509Certificate x509Certificate = x509Certificates[0];
        PublicKey publicKey = x509Certificate.getPublicKey();
        createKeyValueTokenStructure(abstractOutputProcessor, outputProcessorChain, publicKey);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createKeyValueTokenStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, PublicKey publicKey) throws XMLSecurityException, XMLStreamException {
        if (publicKey == null) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        String algorithm = publicKey.getAlgorithm();
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyValue, true, (List<XMLSecAttribute>) null);
        if ("RSA".equals(algorithm)) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_RSAKeyValue, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Modulus, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(rsaPublicKey.getModulus().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Modulus);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Exponent, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Exponent);
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_RSAKeyValue);
        } else if ("DSA".equals(algorithm)) {
            DSAPublicKey dsaPublicKey = (DSAPublicKey) publicKey;
            BigInteger j = dsaPublicKey.getParams().getP().subtract(BigInteger.ONE).divide(dsaPublicKey.getParams().getQ());
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DSAKeyValue, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_P, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(dsaPublicKey.getParams().getP().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_P);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Q, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(dsaPublicKey.getParams().getQ().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Q);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_G, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(dsaPublicKey.getParams().getG().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_G);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Y, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(dsaPublicKey.getY().toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_Y);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_J, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(j.toByteArray()));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_J);
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_DSAKeyValue);
        } else if ("EC".equals(algorithm)) {
            ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
            List<XMLSecAttribute> attributes = new ArrayList<>(1);
            attributes.add(abstractOutputProcessor.createAttribute(XMLSecurityConstants.ATT_NULL_URI, "urn:oid:" + ECDSAUtils.getOIDFromPublicKey(ecPublicKey)));
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_ECKeyValue, true, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_NamedCurve, false, attributes);
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_NamedCurve);
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_PublicKey, false, (List<XMLSecAttribute>) null);
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(ECDSAUtils.encodePoint(ecPublicKey.getW(), ecPublicKey.getParams().getCurve())));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_PublicKey);
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig11_ECKeyValue);
        }
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyValue);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509SubjectKeyIdentifierStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates) throws XMLSecurityException, XMLStreamException {
        createX509SubjectKeyIdentifierStructure(abstractOutputProcessor, outputProcessorChain, x509Certificates, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509SubjectKeyIdentifierStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates, boolean outputX509Data) throws XMLSecurityException, XMLStreamException {
        if (x509Certificates == null || x509Certificates.length == 0) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        int version = x509Certificates[0].getVersion();
        if (version != 3) {
            throw new XMLSecurityException("certificate.noSki.lowVersion", new Object[]{Integer.valueOf(version)});
        }
        if (outputX509Data) {
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, (List<XMLSecAttribute>) null);
        }
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SKI, false, (List<XMLSecAttribute>) null);
        byte[] data = XMLX509SKI.getSKIBytesFromCert(x509Certificates[0]);
        abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(data));
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SKI);
        if (outputX509Data) {
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509CertificateStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates) throws XMLSecurityException, XMLStreamException {
        createX509CertificateStructure(abstractOutputProcessor, outputProcessorChain, x509Certificates, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509CertificateStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates, boolean outputX509Data) throws XMLSecurityException, XMLStreamException {
        if (x509Certificates == null || x509Certificates.length == 0) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        if (outputX509Data) {
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, (List<XMLSecAttribute>) null);
        }
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Certificate, false, (List<XMLSecAttribute>) null);
        try {
            byte[] data = x509Certificates[0].getEncoded();
            abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, XMLUtils.encodeToString(data));
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Certificate);
            if (outputX509Data) {
                abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
            }
        } catch (CertificateEncodingException e) {
            throw new XMLSecurityException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509SubjectNameStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates) throws XMLSecurityException, XMLStreamException {
        createX509SubjectNameStructure(abstractOutputProcessor, outputProcessorChain, x509Certificates, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509SubjectNameStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates, boolean outputX509Data) throws XMLSecurityException, XMLStreamException {
        if (x509Certificates == null || x509Certificates.length == 0) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        if (outputX509Data) {
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, (List<XMLSecAttribute>) null);
        }
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SubjectName, false, (List<XMLSecAttribute>) null);
        String subjectName = x509Certificates[0].getSubjectX500Principal().getName();
        abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, subjectName);
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SubjectName);
        if (outputX509Data) {
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509IssuerSerialStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates) throws XMLSecurityException, XMLStreamException {
        createX509IssuerSerialStructure(abstractOutputProcessor, outputProcessorChain, x509Certificates, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createX509IssuerSerialStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, X509Certificate[] x509Certificates, boolean outputX509Data) throws XMLSecurityException, XMLStreamException {
        if (x509Certificates == null || x509Certificates.length == 0) {
            throw new XMLSecurityException("stax.signature.publicKeyOrCertificateMissing");
        }
        if (outputX509Data) {
            abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data, true, (List<XMLSecAttribute>) null);
        }
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerSerial, false, (List<XMLSecAttribute>) null);
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerName, false, (List<XMLSecAttribute>) null);
        abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, x509Certificates[0].getIssuerX500Principal().getName());
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerName);
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SerialNumber, false, (List<XMLSecAttribute>) null);
        abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, x509Certificates[0].getSerialNumber().toString());
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509SerialNumber);
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509IssuerSerial);
        if (outputX509Data) {
            abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_X509Data);
        }
    }

    public static TokenSecurityEvent<? extends InboundSecurityToken> createTokenSecurityEvent(InboundSecurityToken inboundSecurityToken, String correlationID) throws XMLSecurityException {
        TokenSecurityEvent<? extends InboundSecurityToken> x509TokenSecurityEvent;
        SecurityTokenConstants.TokenType tokenType = inboundSecurityToken.getTokenType();
        if (SecurityTokenConstants.X509V1Token.equals(tokenType) || SecurityTokenConstants.X509V3Token.equals(tokenType) || SecurityTokenConstants.X509Pkcs7Token.equals(tokenType) || SecurityTokenConstants.X509PkiPathV1Token.equals(tokenType)) {
            x509TokenSecurityEvent = new X509TokenSecurityEvent();
        } else if (SecurityTokenConstants.KeyValueToken.equals(tokenType)) {
            x509TokenSecurityEvent = new KeyValueTokenSecurityEvent();
        } else if (SecurityTokenConstants.KeyNameToken.equals(tokenType)) {
            x509TokenSecurityEvent = new KeyNameTokenSecurityEvent();
        } else if (SecurityTokenConstants.DefaultToken.equals(tokenType)) {
            x509TokenSecurityEvent = new DefaultTokenSecurityEvent();
        } else if (SecurityTokenConstants.EncryptedKeyToken.equals(tokenType)) {
            x509TokenSecurityEvent = new EncryptedKeyTokenSecurityEvent();
        } else {
            throw new XMLSecurityException("stax.unsupportedToken", new Object[]{tokenType});
        }
        x509TokenSecurityEvent.setSecurityToken(inboundSecurityToken);
        x509TokenSecurityEvent.setCorrelationID(correlationID);
        return x509TokenSecurityEvent;
    }

    public static Set<String> getExcC14NInclusiveNamespacePrefixes(XMLSecStartElement xmlSecStartElement, boolean excludeVisible) {
        if (xmlSecStartElement == null) {
            return Collections.emptySet();
        }
        Set<String> prefixes = new TreeSet<>();
        XMLSecStartElement parentXMXmlSecStartElement = xmlSecStartElement.getParentXMLSecStartElement();
        if (parentXMXmlSecStartElement != null) {
            List<XMLSecNamespace> onElementDeclaredNamespaces = parentXMXmlSecStartElement.getOnElementDeclaredNamespaces();
            List<XMLSecNamespace> xmlSecNamespaces = new ArrayList<>();
            parentXMXmlSecStartElement.getNamespacesFromCurrentScope(xmlSecNamespaces);
            List<XMLSecNamespace> xmlSecNamespaces2 = xmlSecNamespaces.subList(0, xmlSecNamespaces.size() - onElementDeclaredNamespaces.size());
            for (int i = xmlSecNamespaces2.size() - 1; i >= 0; i--) {
                XMLSecNamespace xmlSecNamespace = xmlSecNamespaces2.get(i);
                String prefix = xmlSecNamespace.getPrefix();
                if (prefix == null || prefix.isEmpty()) {
                    prefixes.add("#default");
                } else {
                    prefixes.add(xmlSecNamespace.getPrefix());
                }
            }
            if (excludeVisible) {
                Iterator<XMLSecNamespace> it = onElementDeclaredNamespaces.iterator();
                while (it.hasNext()) {
                    String prefix2 = it.next().getPrefix();
                    if (prefix2 == null || prefix2.isEmpty()) {
                        prefixes.remove("#default");
                    } else {
                        prefixes.remove(prefix2);
                    }
                }
                if (xmlSecStartElement.getName().getPrefix() == null || xmlSecStartElement.getName().getPrefix().isEmpty()) {
                    prefixes.remove("#default");
                } else {
                    prefixes.remove(xmlSecStartElement.getName().getPrefix());
                }
            }
        }
        return prefixes;
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[4096];
        while (true) {
            int read = inputStream.read(buf);
            if (read != -1) {
                outputStream.write(buf, 0, read);
            } else {
                return;
            }
        }
    }

    public static SecretKey prepareSecretKey(String symEncAlgo, byte[] rawKey) {
        int size;
        SecretKeySpec keySpec;
        try {
            size = JCEMapper.getKeyLengthFromURI(symEncAlgo) / 8;
        } catch (Exception e) {
            size = 0;
        }
        String keyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(symEncAlgo);
        if (size > 0 && !symEncAlgo.endsWith("gcm") && !symEncAlgo.contains("hmac-")) {
            keySpec = new SecretKeySpec(rawKey, 0, rawKey.length > size ? size : rawKey.length, keyAlgorithm);
        } else if (rawKey.length > 1024) {
            keySpec = new SecretKeySpec(rawKey, 0, 1024, keyAlgorithm);
        } else {
            keySpec = new SecretKeySpec(rawKey, keyAlgorithm);
        }
        return keySpec;
    }

    public static Schema loadXMLSecuritySchemas() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
        schemaFactory.setResourceResolver(new LSResourceResolver() { // from class: org.apache.xml.security.stax.ext.XMLSecurityUtils.1
            @Override // org.w3c.dom.ls.LSResourceResolver
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                if ("http://www.w3.org/2001/XMLSchema.dtd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput = new ConcreteLSInput();
                    concreteLSInput.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/XMLSchema.dtd", XMLSecurityConstants.class));
                    return concreteLSInput;
                }
                if ("XMLSchema.dtd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput2 = new ConcreteLSInput();
                    concreteLSInput2.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/XMLSchema.dtd", XMLSecurityConstants.class));
                    return concreteLSInput2;
                }
                if ("datatypes.dtd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput3 = new ConcreteLSInput();
                    concreteLSInput3.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/datatypes.dtd", XMLSecurityConstants.class));
                    return concreteLSInput3;
                }
                if ("http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput4 = new ConcreteLSInput();
                    concreteLSInput4.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xmldsig-core-schema.xsd", XMLSecurityConstants.class));
                    return concreteLSInput4;
                }
                if ("http://www.w3.org/2001/xml.xsd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput5 = new ConcreteLSInput();
                    concreteLSInput5.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xml.xsd", XMLSecurityConstants.class));
                    return concreteLSInput5;
                }
                if ("dsig-more_2001_04.xsd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput6 = new ConcreteLSInput();
                    concreteLSInput6.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2001_04.xsd", XMLSecurityConstants.class));
                    return concreteLSInput6;
                }
                if ("dsig-more_2007_05.xsd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput7 = new ConcreteLSInput();
                    concreteLSInput7.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2007_05.xsd", XMLSecurityConstants.class));
                    return concreteLSInput7;
                }
                if ("dsig-more_2021_04.xsd".equals(systemId)) {
                    ConcreteLSInput concreteLSInput8 = new ConcreteLSInput();
                    concreteLSInput8.setByteStream(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2021_04.xsd", XMLSecurityConstants.class));
                    return concreteLSInput8;
                }
                return null;
            }
        });
        Schema schema = schemaFactory.newSchema(new Source[]{new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/exc-c14n.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xmldsig-core-schema.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xop-include.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xenc-schema.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xenc-schema-11.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/xmldsig11-schema.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2001_04.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2007_05.xsd", XMLSecurityConstants.class)), new StreamSource(ClassLoaderUtils.getResourceAsStream("bindings/schemas/dsig-more_2021_04.xsd", XMLSecurityConstants.class))});
        return schema;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void createKeyNameTokenStructure(AbstractOutputProcessor abstractOutputProcessor, OutputProcessorChain outputProcessorChain, String keyName) throws XMLSecurityException, XMLStreamException {
        if (keyName == null || keyName.isEmpty()) {
            throw new XMLSecurityException("stax.signature.keyNameMissing");
        }
        abstractOutputProcessor.createStartElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyName, true, (List<XMLSecAttribute>) null);
        abstractOutputProcessor.createCharactersAndOutputAsEvent(outputProcessorChain, keyName);
        abstractOutputProcessor.createEndElementAndOutputAsEvent(outputProcessorChain, XMLSecurityConstants.TAG_dsig_KeyName);
    }
}
