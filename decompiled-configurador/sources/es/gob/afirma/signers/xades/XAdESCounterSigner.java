package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XMLConstants;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormatImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifierImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/XAdESCounterSigner.class */
public final class XAdESCounterSigner {
    private static final String CSURI = "http://uri.etsi.org/01903#CountersignedSignature";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return countersign(bArr, str, counterSignTarget, objArr, privateKey, certificateArr, properties, (URIDereferencer) null);
    }

    public static byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws AOException {
        try {
            return countersign(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)), str, counterSignTarget, objArr, privateKey, certificateArr, properties, uRIDereferencer);
        } catch (Exception e) {
            throw new AOException("No se ha podido cargar el documento de firmas", e);
        }
    }

    public static byte[] countersign(Document document, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return countersign(document, str, counterSignTarget, objArr, privateKey, certificateArr, properties, (URIDereferencer) null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public static byte[] countersign(Document document, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        if (document == null) {
            throw new IllegalArgumentException("El objeto de firma no puede ser nulo");
        }
        String str2 = str != null ? str : "SHA512withRSA";
        Properties properties2 = properties != null ? properties : new Properties();
        checkParams(str2, properties2);
        if (XMLConstants.SIGN_ALGOS_URI.get(str2) == null) {
            throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str2 + "'");
        }
        String property = properties2.getProperty(XAdESExtraParams.OUTPUT_XML_ENCODING);
        boolean z = false;
        Document documentInsertarNodoAfirma = document;
        Element documentElement = document.getDocumentElement();
        try {
            Map<String, String> originalXMLProperties = XAdESUtil.getOriginalXMLProperties(document, property);
            if (documentElement.getLocalName().equals("Signature")) {
                z = true;
                documentInsertarNodoAfirma = AOXAdESSigner.insertarNodoAfirma(documentInsertarNodoAfirma);
                documentElement = documentInsertarNodoAfirma.getDocumentElement();
            }
            NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            String property2 = properties2.getProperty(XAdESExtraParams.ALLOW_SIGN_LTS_SIGNATURES);
            if (property2 == null || !Boolean.parseBoolean(property2)) {
                try {
                    XAdESUtil.checkArchiveSignatures(elementsByTagNameNS);
                } catch (SigningLTSException e) {
                    if (property2 != null) {
                        throw new AOException(e.getMessage());
                    }
                    throw new SigningLTSException("La contrafirma de firmas de archivo podria invalidar el sello de archivo si se aplicase a contrafirmas internas", e, true);
                }
            }
            try {
                if (counterSignTarget == CounterSignTarget.TREE) {
                    countersignTree(elementsByTagNameNS, privateKey, certificateArr, properties2, str2, documentInsertarNodoAfirma, uRIDereferencer);
                } else if (counterSignTarget == CounterSignTarget.LEAFS) {
                    countersignLeafs(elementsByTagNameNS, privateKey, certificateArr, properties2, str2, documentInsertarNodoAfirma, uRIDereferencer);
                } else if (counterSignTarget == CounterSignTarget.NODES) {
                    countersignNodes(elementsByTagNameNS, objArr, privateKey, certificateArr, properties2, str2, documentInsertarNodoAfirma, uRIDereferencer);
                } else if (counterSignTarget == CounterSignTarget.SIGNERS) {
                    countersignSigners(elementsByTagNameNS, objArr, privateKey, certificateArr, properties2, str2, documentInsertarNodoAfirma, uRIDereferencer);
                }
                if (z) {
                    try {
                        Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
                        documentNewDocument.appendChild(documentNewDocument.adoptNode(documentInsertarNodoAfirma.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0)));
                        documentInsertarNodoAfirma = documentNewDocument;
                    } catch (Exception e2) {
                        LOGGER.info("No se ha eliminado el nodo padre '<AFIRMA>': " + e2);
                    }
                }
                return Utils.writeXML(documentInsertarNodoAfirma.getDocumentElement(), originalXMLProperties, null, null);
            } catch (AOException e3) {
                throw e3;
            } catch (Exception e4) {
                throw new AOException("Error al generar la contrafirma", e4);
            }
        } catch (Exception e5) {
            throw new AOException("No se ha podido realizar la contrafirma: " + e5, e5);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static void countersignLeafs(NodeList nodeList, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, String str, Document document, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        int length = nodeList.getLength();
        int i = 0;
        while (i < length) {
            try {
                Element element = (Element) nodeList.item(i);
                if (element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").getLength() == 0) {
                    cs(element, privateKey, certificateArr, properties, str, document, uRIDereferencer);
                    length++;
                    i++;
                }
                i++;
            } catch (AOException e) {
                throw e;
            } catch (Exception e2) {
                throw new AOException("No se ha podido realizar la contrafirma de hojas", e2);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static void countersignNodes(NodeList nodeList, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, String str, Document document, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < objArr.length; i++) {
            if (!arrayList.contains(objArr[i])) {
                arrayList.add((Integer) objArr[i]);
            }
        }
        Object[] array = arrayList.toArray();
        Element[] elementArr = new Element[array.length];
        for (int i2 = 0; i2 < array.length; i2++) {
            try {
                elementArr[i2] = (Element) nodeList.item(((Integer) array[i2]).intValue());
                if (elementArr[i2] == null) {
                    throw new AOException("Posicion de nodo no valida.");
                }
            } catch (ClassCastException e) {
                throw new AOException("Valor de nodo no valido", e);
            }
        }
        try {
            for (Element element : elementArr) {
                cs(element, privateKey, certificateArr, properties, str, document, uRIDereferencer);
            }
        } catch (AOException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new AOException("No se ha podido realizar la contrafirma de nodos", e3);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static void countersignSigners(NodeList nodeList, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, String str, Document document, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        List listAsList = Arrays.asList(objArr);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (listAsList.contains(AOUtil.getCN(Utils.getCertificate(element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0))))) {
                arrayList.add(element);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            cs((Element) it.next(), privateKey, certificateArr, properties, str, document, uRIDereferencer);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static void countersignTree(NodeList nodeList, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, String str, Document document, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        int length = nodeList.getLength();
        Element[] elementArr = new Element[length];
        for (int i = 0; i < length; i++) {
            elementArr[i] = (Element) nodeList.item(i);
        }
        for (int i2 = 0; i2 < length; i2++) {
            try {
                cs(elementArr[i2], privateKey, certificateArr, properties, str, document, uRIDereferencer);
            } catch (AOException e) {
                throw e;
            } catch (Exception e2) {
                throw new AOException("No se ha podido realizar la contrafirma del arbol", e2);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static void cs(Element element, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, String str, Document document, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        Element elementCreateElement;
        Element elementCreateElement2;
        if (document == null) {
            throw new IllegalArgumentException("El documento DOM no puede ser nulo");
        }
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2001/04/xmlenc#sha512");
        String property2 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        boolean z = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INTERNAL_VALIDATE_PKCS1, Boolean.TRUE.toString()));
        Element signedPropertiesReference = XAdESUtil.getSignedPropertiesReference(element);
        Element signedPropertiesElement = XAdESUtil.getSignedPropertiesElement(element, signedPropertiesReference);
        if (signedPropertiesElement == null) {
            throw new AOException("No se han encontrado los atributos firmados de la firma original");
        }
        String prefix = signedPropertiesElement.getPrefix();
        String namespaceURI = signedPropertiesElement.getNamespaceURI();
        String attribute = signedPropertiesReference.getAttribute("Type");
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("*", XAdESConstants.TAG_UNSIGNED_PROPERTIES);
        if (elementsByTagNameNS.getLength() == 0) {
            elementCreateElement = document.createElement(addNSPrefix(prefix, XAdESConstants.TAG_UNSIGNED_PROPERTIES));
        } else {
            elementCreateElement = (Element) elementsByTagNameNS.item(0);
            prefix = elementCreateElement.getPrefix();
        }
        NodeList elementsByTagNameNS2 = elementCreateElement.getElementsByTagNameNS("*", XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES);
        if (elementsByTagNameNS2.getLength() == 0) {
            elementCreateElement2 = document.createElement(addNSPrefix(prefix, XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES));
        } else {
            elementCreateElement2 = (Element) elementsByTagNameNS2.item(0);
            prefix = elementCreateElement2.getPrefix();
        }
        Element elementCreateElement3 = document.createElement(addNSPrefix(prefix, "CounterSignature"));
        elementCreateElement2.appendChild(elementCreateElement3);
        elementCreateElement.appendChild(elementCreateElement2);
        element.getElementsByTagNameNS("*", XAdESConstants.TAG_QUALIFYING_PROPERTIES).item(0).appendChild(elementCreateElement);
        Element element2 = (Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0);
        ArrayList arrayList = new ArrayList();
        XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
        try {
            DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(property, (DigestMethodParameterSpec) null);
            String str2 = "Reference-" + UUID.randomUUID().toString();
            try {
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(dOMFactory.newTransform(property2, (TransformParameterSpec) null));
                arrayList.add(dOMFactory.newReference("#" + element2.getAttribute("Id"), digestMethodNewDigestMethod, arrayList2, needCounterSignatureReferenceType(namespaceURI) ? CSURI : null, str2));
                String property3 = properties2.getProperty(XAdESExtraParams.PROFILE, "advanced");
                if (AOSignConstants.SIGN_PROFILE_BASELINE.equals(property3) && !XAdESUtil.isBaselineCompatible(namespaceURI)) {
                    LOGGER.warning("La firma original utiliza un espacio de nombres no compatible con baseline (" + namespaceURI + "). No se generara una firma baseline");
                    property3 = "advanced";
                }
                XAdESBase xAdESBaseNewInstance = XAdESUtil.newInstance(property3, namespaceURI, prefix, "ds", property, elementCreateElement3.getOwnerDocument(), elementCreateElement3, (X509Certificate) certificateArr[0]);
                XAdESCommonMetadataUtil.addCommonMetadata(xAdESBaseNewInstance, properties2);
                if (!property3.equalsIgnoreCase(AOSignConstants.SIGN_PROFILE_BASELINE)) {
                    DataObjectFormatImpl dataObjectFormatImpl = new DataObjectFormatImpl(null, new ObjectIdentifierImpl("OIDAsURN", "urn:oid:1.2.840.10003.5.109.10", null, new ArrayList(0)), "text/xml", document.getInputEncoding(), "#" + str2);
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(dataObjectFormatImpl);
                    xAdESBaseNewInstance.setDataObjectFormats(arrayList3);
                }
                AOXMLAdvancedSignature xmlAdvancedSignature = XAdESUtil.getXmlAdvancedSignature(xAdESBaseNewInstance, attribute, property, property2, uRIDereferencer);
                try {
                    if (Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INCLUDE_ONLY_SIGNNING_CERTIFICATE, Boolean.FALSE.toString()))) {
                        xmlAdvancedSignature.sign((X509Certificate) certificateArr[0], privateKey, XMLConstants.SIGN_ALGOS_URI.get(str), arrayList, "Signature-" + UUID.randomUUID().toString());
                    } else {
                        xmlAdvancedSignature.sign(Arrays.asList(certificateArr), privateKey, XMLConstants.SIGN_ALGOS_URI.get(str), arrayList, "Signature-" + UUID.randomUUID().toString(), Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_VALUE, Boolean.TRUE.toString())), Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_NAME, Boolean.FALSE.toString())), Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_X509_ISSUER_SERIAL, Boolean.FALSE.toString())), Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.FALSE.toString())), z);
                    }
                } catch (AOException e) {
                    throw e;
                } catch (NoSuchAlgorithmException e2) {
                    throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str + "'", e2);
                } catch (Exception e3) {
                    throw new AOException("Error al generar la contrafirma XAdES", e3);
                }
            } catch (Exception e4) {
                throw new AOException("No se ha podido realizar la contrafirma", e4);
            }
        } catch (Exception e5) {
            throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + property + "'", e5);
        }
    }

    private static String addNSPrefix(String str, String str2) {
        if (str == null || str.isEmpty()) {
            return str2;
        }
        return str + ":" + str2;
    }

    private XAdESCounterSigner() {
    }

    private static void checkParams(String str, Properties properties) {
        if (str.toUpperCase(Locale.US).startsWith("MD")) {
            throw new IllegalArgumentException("XAdES no permite huellas digitales MD2 o MD5 (Decision 130/2011 CE)");
        }
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(properties.getProperty(XAdESExtraParams.PROFILE))) {
            if (AOSignConstants.isSHA1SignatureAlgorithm(str)) {
                LOGGER.warning("El algoritmo '" + str + "' no esta recomendado para su uso en las firmas baseline");
            }
            if ("http://www.w3.org/2000/09/xmldsig#sha1".equals(properties.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD))) {
                LOGGER.warning("El algoritmo SHA1 no esta recomendado para generar referencias en las firmas baseline");
            }
        }
    }

    public static boolean needCounterSignatureReferenceType(String str) {
        return (XAdESConstants.NAMESPACE_XADES_1_1_1.equals(str) || XAdESConstants.NAMESPACE_XADES_1_2_2.equals(str)) ? false : true;
    }
}
