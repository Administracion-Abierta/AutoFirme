package es.gob.afirma.signers.xades;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.ui.AOUIFactory;
import es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIdImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndication;
import es.uji.crypto.xades.jxades.security.xml.XAdES.CommitmentTypeIndicationImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdES;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBaselineAttributes;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XadesWithBasicAttributes;
import es.uji.crypto.xades.jxades.util.XMLUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/XAdESUtil.class */
public final class XAdESUtil {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String[] SUPPORTED_XADES_NAMESPACE_URIS = {XAdESConstants.NAMESPACE_XADES_NO_VERSION, XAdESConstants.NAMESPACE_XADES_1_2_2, "http://uri.etsi.org/01903/v1.3.2#", "http://uri.etsi.org/01903/v1.4.1#"};
    private static final String[] SIGNED_PROPERTIES_TYPES = {"http://uri.etsi.org/01903#SignedProperties", "http://uri.etsi.org/01903/v1.2.2#SignedProperties", "http://uri.etsi.org/01903/v1.3.2#SignedProperties", "http://uri.etsi.org/01903/v1.4.1#SignedProperties"};
    private static final String CRYPTO_OPERATION_SIGN = "SIGN";
    private static final String XPATH_ENVELOPED_EQ = "not(ancestor-or-self::%1$s:Signature)";
    private static final String XPATH_ENVELOPED_EQ2 = "count(ancestor-or-self::%1$s:Signature|here()/ancestor::%1$s:Signature[1])>count(ancestor-or-self::%1$s:Signature)";

    private XAdESUtil() {
    }

    static boolean checkSignNodes(List<Node> list) {
        for (Node node : list) {
            int length = 0;
            for (String str : SUPPORTED_XADES_NAMESPACE_URIS) {
                length += ((Element) node).getElementsByTagNameNS(str, XAdESConstants.TAG_QUALIFYING_PROPERTIES).getLength();
            }
            if (length == 0) {
                return false;
            }
        }
        return true;
    }

    static boolean checkCompatibility(List<Node> list) throws AOInvalidFormatException {
        boolean z = false;
        HashSet hashSet = new HashSet();
        Iterator<Node> it = list.iterator();
        while (it.hasNext()) {
            NodeList elementsByTagNameNS = ((Element) it.next()).getElementsByTagNameNS("*", XAdESConstants.TAG_QUALIFYING_PROPERTIES);
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                String namespaceURI = elementsByTagNameNS.item(i).getNamespaceURI();
                boolean z2 = false;
                for (String str : SUPPORTED_XADES_NAMESPACE_URIS) {
                    if (str.equals(namespaceURI)) {
                        z2 = true;
                        hashSet.add(namespaceURI);
                    }
                }
                if (!z2) {
                    throw new AOInvalidFormatException("Una de las firmas encontradas en el documento contiene una version inexistente de XAdES");
                }
                try {
                    Node nodeItem = ((Element) elementsByTagNameNS.item(i)).getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(1);
                    String localName = nodeItem.getLocalName();
                    String namespaceURI2 = nodeItem.getNamespaceURI();
                    if (XAdESConstants.TAG_SIGNING_CERTIFICATE_V2.equals(localName) && namespaceURI.equals(namespaceURI2)) {
                        z = true;
                    }
                } catch (Exception e) {
                    throw new AOInvalidFormatException("Error al intentar analizar el nodo SigningCertificateV2");
                }
            }
            if (hashSet.size() > 1) {
                throw new AOInvalidFormatException("El documento contiene firmas con distintas versiones de XAdES");
            }
        }
        return z;
    }

    static boolean isSignedPropertiesType(String str) {
        for (String str2 : SIGNED_PROPERTIES_TYPES) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    static AOXMLAdvancedSignature getXmlAdvancedSignature(XAdESBase xAdESBase, String str, String str2, String str3, URIDereferencer uRIDereferencer) throws AOException {
        try {
            AOXMLAdvancedSignature aOXMLAdvancedSignatureNewInstance = AOXMLAdvancedSignature.newInstance(xAdESBase);
            aOXMLAdvancedSignatureNewInstance.setSignedPropertiesTypeUrl(str);
            try {
                aOXMLAdvancedSignatureNewInstance.setDigestMethod(str2);
                aOXMLAdvancedSignatureNewInstance.setCanonicalizationMethod(str3);
                aOXMLAdvancedSignatureNewInstance.setUriDereferencer(uRIDereferencer);
                return aOXMLAdvancedSignatureNewInstance;
            } catch (Exception e) {
                throw new AOException("No se ha podido establecer el algoritmo de huella digital: " + e, e);
            }
        } catch (Exception e2) {
            throw new AOException("No se ha podido instanciar la firma XML Avanzada de JXAdES: " + e2, e2);
        }
    }

    static Element getFirstElementFromXPath(String str, Element element) throws AOException {
        try {
            NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath().evaluate(str, element, XPathConstants.NODESET);
            if (nodeList.getLength() < 1) {
                throw new AOException("La expresion indicada para la insercion de la firma Enveloped ('" + str + "') no ha devuelto ningun nodo");
            }
            if (nodeList.getLength() > 1) {
                LOGGER.warning("La expresion indicada para la insercion de la firma Enveloped ('" + str + "') ha devuelto varios nodos, se usara el primero");
            }
            return (Element) nodeList.item(0);
        } catch (XPathExpressionException e) {
            throw new AOException("No se ha podido evaluar la expresion indicada para la insercion de la firma Enveloped ('" + str + "'): " + e, e);
        }
    }

    static Element findElementById(String str, Element element, boolean z) {
        Element elementFindElementById;
        if (str.equals(element.getAttribute("Id"))) {
            return element;
        }
        if (z && element.getLocalName().equals("Signature")) {
            return null;
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1 && (elementFindElementById = findElementById(str, (Element) nodeItem, z)) != null) {
                return elementFindElementById;
            }
        }
        return null;
    }

    public static List<CommitmentTypeIndication> parseCommitmentTypeIndications(Properties properties, String str) {
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3 = new ArrayList();
        if (properties == null) {
            return arrayList3;
        }
        String property = properties.getProperty(XAdESExtraParams.COMMITMENT_TYPE_INDICATIONS);
        if (property == null) {
            return arrayList3;
        }
        try {
            int i = Integer.parseInt(property);
            if (i < 1) {
                throw new NumberFormatException();
            }
            for (int i2 = 0; i2 <= i; i2++) {
                String property2 = properties.getProperty(XAdESExtraParams.COMMITMENT_TYPE_INDICATION_PREFIX + Integer.toString(i2) + "Identifier");
                if (property2 != null) {
                    String str2 = XAdESExtraParams.COMMITMENT_TYPE_IDENTIFIERS.get(property2);
                    if (str2 == null) {
                        LOGGER.severe("El identificador del CommitmentTypeIndication " + i2 + " no es un tipo soportado (" + property2 + "), se omitira y se continuara con el siguiente");
                    } else {
                        String property3 = properties.getProperty(XAdESExtraParams.COMMITMENT_TYPE_INDICATION_PREFIX + Integer.toString(i2) + "Description");
                        String property4 = properties.getProperty(XAdESExtraParams.COMMITMENT_TYPE_INDICATION_PREFIX + Integer.toString(i2) + XAdESExtraParams.COMMITMENT_TYPE_INDICATION_DOCUMENTATION_REFERENCE);
                        if (property4 == null) {
                            arrayList = new ArrayList(0);
                        } else {
                            arrayList = new ArrayList();
                            for (String str3 : property4.split(Pattern.quote("|"))) {
                                try {
                                    arrayList.add(new URL(str3).toString());
                                } catch (MalformedURLException e) {
                                    LOGGER.severe("La referencia documental '" + str3 + "' del CommitmentTypeIndication " + i2 + " no es una URL, se omitira y se continuara con la siguiente referencia documental: " + e);
                                }
                            }
                        }
                        String property5 = properties.getProperty(XAdESExtraParams.COMMITMENT_TYPE_INDICATION_PREFIX + Integer.toString(i2) + XAdESExtraParams.COMMITMENT_TYPE_INDICATION_QUALIFIERS);
                        if (property5 == null) {
                            arrayList2 = new ArrayList(0);
                        } else {
                            arrayList2 = new ArrayList();
                            for (String str4 : property5.split(Pattern.quote("|"))) {
                                arrayList2.add(str4);
                            }
                        }
                        arrayList3.add(new CommitmentTypeIndicationImpl(new CommitmentTypeIdImpl(str2.startsWith("urn:oid:") ? "OIDAsURN" : null, str2, property3, arrayList), str != null ? "#" + str : null, arrayList2));
                    }
                }
            }
            return arrayList3;
        } catch (Exception e2) {
            LOGGER.severe("El parametro adicional 'CommitmentTypeIndications' debe contener un valor numerico entero (el valor actual es " + property + "), no se anadira el CommitmentTypeIndication: " + e2);
            return arrayList3;
        }
    }

    static String getDigestMethodByCommonName(String str) throws NoSuchAlgorithmException {
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
        if (AOSignConstants.DIGEST_ALGORITHM_SHA1.equalsIgnoreCase(digestAlgorithmName)) {
            return "http://www.w3.org/2000/09/xmldsig#sha1";
        }
        if ("SHA-256".equalsIgnoreCase(digestAlgorithmName)) {
            return "http://www.w3.org/2001/04/xmlenc#sha256";
        }
        if ("SHA-512".equalsIgnoreCase(digestAlgorithmName)) {
            return "http://www.w3.org/2001/04/xmlenc#sha512";
        }
        throw new NoSuchAlgorithmException("No se soporta el algoritmo: " + digestAlgorithmName);
    }

    static Element getRootElement(Document document, Properties properties) {
        Element elementCreateElementNS;
        Properties properties2 = properties != null ? properties : new Properties();
        String property = properties2.getProperty(XAdESExtraParams.ROOT_XML_NODE_NAME, "AFIRMA");
        String property2 = properties2.getProperty(XAdESExtraParams.ROOT_XML_NODE_NAMESPACE);
        String property3 = properties2.getProperty(XAdESExtraParams.ROOT_XML_NODE_NAMESPACE_PREFIX);
        if (property2 == null) {
            elementCreateElementNS = document.createElement(property);
        } else {
            elementCreateElementNS = document.createElementNS(property2, property);
            if (property3 != null) {
                elementCreateElementNS.setAttribute(property3.startsWith("xmlns:") ? property3 : "xmlns:" + property3, property2);
            }
        }
        elementCreateElementNS.setAttributeNS(null, "Id", property + "-Root-" + UUID.randomUUID().toString());
        return elementCreateElementNS;
    }

    static List<Reference> createManifest(List<Reference> list, XMLSignatureFactory xMLSignatureFactory, AOXMLAdvancedSignature aOXMLAdvancedSignature, DigestMethod digestMethod, Transform transform, String str) {
        LinkedList linkedList = new LinkedList();
        String str2 = "Manifest-" + UUID.randomUUID().toString();
        linkedList.add(xMLSignatureFactory.newManifest(new ArrayList(list), str2));
        aOXMLAdvancedSignature.addXMLObject(xMLSignatureFactory.newXMLObject(linkedList, "ManifestObject-" + UUID.nameUUIDFromBytes(str.getBytes()).toString(), (String) null, (String) null));
        list.clear();
        list.add(xMLSignatureFactory.newReference("#" + str2, digestMethod, transform != null ? Collections.singletonList(transform) : new ArrayList(0), "http://www.w3.org/2000/09/xmldsig#Manifest", Constants._TAG_MANIFEST + str));
        return list;
    }

    static Map<String, String> getOriginalXMLProperties(Document document, String str) {
        String systemId;
        Hashtable hashtable = new Hashtable();
        if (document != null) {
            if (str != null) {
                hashtable.put("encoding", str);
            } else if (document.getXmlEncoding() != null) {
                hashtable.put("encoding", document.getXmlEncoding());
            }
            String xmlVersion = document.getXmlVersion();
            if (xmlVersion != null) {
                hashtable.put(XmlConsts.XML_DECL_KW_VERSION, xmlVersion);
            }
            DocumentType doctype = document.getDoctype();
            if (doctype != null && (systemId = doctype.getSystemId()) != null) {
                hashtable.put("doctype-system", systemId);
            }
        }
        return hashtable;
    }

    public static Element getFirstSignatureElement(Element element) {
        if (element == null) {
            return null;
        }
        Element element2 = null;
        if ("Signature".equals(element.getLocalName())) {
            element2 = element;
        } else {
            NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            if (elementsByTagNameNS.getLength() > 0) {
                element2 = (Element) elementsByTagNameNS.item(0);
            }
        }
        return element2;
    }

    static Element getSignedPropertiesReference(Element element) {
        Element signedInfo = getSignedInfo(element);
        if (signedInfo == null) {
            return null;
        }
        NodeList elementsByTagNameNS = signedInfo.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            Element element2 = (Element) elementsByTagNameNS.item(i);
            String attribute = element2.getAttribute("Type");
            if (attribute != null && !attribute.isEmpty() && isSignedPropertiesType(attribute)) {
                return element2;
            }
        }
        return null;
    }

    public static Element getSignedInfo(Element element) {
        Element element2 = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength() && element2 == null; i++) {
            if (childNodes.item(i).getNodeType() == 1 && "http://www.w3.org/2000/09/xmldsig#".equals(childNodes.item(i).getNamespaceURI()) && "SignedInfo".equals(childNodes.item(i).getLocalName())) {
                element2 = (Element) childNodes.item(i);
            }
        }
        return element2;
    }

    static Element getSignedPropertiesElement(Element element, Element element2) {
        String attribute = element2.getAttribute("URI");
        if (attribute == null || !attribute.startsWith("#")) {
            return null;
        }
        return findElementById(attribute.substring(1), element, false);
    }

    static Element getSignedPropertiesElement(Element element) {
        String attribute;
        Element signedPropertiesReference = getSignedPropertiesReference(element);
        if (signedPropertiesReference == null || (attribute = signedPropertiesReference.getAttribute("URI")) == null || !attribute.startsWith("#")) {
            return null;
        }
        return findElementById(attribute.substring(1), element, false);
    }

    static Element getUnSignedPropertiesElement(Element element) {
        Element signedPropertiesElement = getSignedPropertiesElement(element);
        if (signedPropertiesElement == null) {
            return null;
        }
        return XMLUtils.getChildElementByTagNameNS((Element) signedPropertiesElement.getParentNode(), XAdESConstants.TAG_UNSIGNED_PROPERTIES, "http://uri.etsi.org/01903/v1.3.2#");
    }

    public static Element getSignatureMethodElement(Element element) {
        Element signedInfo = getSignedInfo(element);
        if (signedInfo == null) {
            return null;
        }
        return (Element) signedInfo.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod").item(0);
    }

    public static XAdESBase newInstance(String str, String str2, String str3, String str4, String str5, Document document, Element element, X509Certificate x509Certificate) throws AOException {
        XAdES xAdES = XAdES.EPES;
        if (AOSignConstants.SIGN_PROFILE_BASELINE.equalsIgnoreCase(str)) {
            xAdES = XAdES.B_LEVEL;
        }
        XAdESBase xAdESBaseNewInstance = XAdES.newInstance(xAdES, str2, str3, str4, str5, document, element);
        if (x509Certificate != null) {
            if (xAdESBaseNewInstance instanceof XadesWithBaselineAttributes) {
                ((XadesWithBaselineAttributes) xAdESBaseNewInstance).setSigningCertificateV2(x509Certificate, null);
            } else if (xAdESBaseNewInstance instanceof XadesWithBasicAttributes) {
                ((XadesWithBasicAttributes) xAdESBaseNewInstance).setSigningCertificate(x509Certificate);
            }
        }
        return xAdESBaseNewInstance;
    }

    static boolean isBaselineCompatible(String str) {
        return "http://uri.etsi.org/01903/v1.3.2#".equals(str) || "http://uri.etsi.org/01903/v1.4.1#".equals(str);
    }

    public static List<Element> getSignatureDataReferenceList(Element element) {
        Element signedInfo = getSignedInfo(element);
        if (signedInfo == null) {
            return null;
        }
        NodeList elementsByTagNameNS = signedInfo.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            Element element2 = (Element) elementsByTagNameNS.item(i);
            String attribute = element2.getAttribute("Type");
            if (attribute != null && !attribute.isEmpty()) {
                if (!isSignedPropertiesType(attribute)) {
                    arrayList.add(element2);
                }
            } else {
                String attribute2 = element2.getAttribute("URI");
                if (attribute2 != null && attribute2.startsWith("#")) {
                    Element elementFindElementById = findElementById(attribute2.substring(1), element, false);
                    if (elementFindElementById == null) {
                        arrayList.add(element2);
                    } else {
                        String localName = elementFindElementById.getLocalName();
                        if (!localName.equals(Constants._TAG_KEYINFO) && !localName.equals(XAdESConstants.TAG_SIGNED_PROPERTIES)) {
                            arrayList.add(element2);
                        }
                    }
                } else {
                    arrayList.add(element2);
                }
            }
        }
        return arrayList;
    }

    public static boolean hasHashManifestReference(Element element) {
        Element signedInfo = getSignedInfo(element);
        if (signedInfo == null) {
            return false;
        }
        NodeList elementsByTagNameNS = signedInfo.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            if ("http://www.w3.org/2000/09/xmldsig#Manifest".equals(((Element) elementsByTagNameNS.item(i)).getAttribute("Type"))) {
                return true;
            }
        }
        return false;
    }

    public static void checkSignProfile(Properties properties, boolean z) {
        if (Boolean.TRUE.equals(properties.get(XAdESExtraParams.CONFIRM_DIFFERENT_PROFILE))) {
            if (z != AOSignConstants.SIGN_PROFILE_BASELINE.equals(properties.getProperty(XAdESExtraParams.PROFILE, "advanced")) && AOUIFactory.showConfirmDialog(null, XAdESMessages.getString("AOXAdESSigner.0"), XAdESMessages.getString("AOXAdESSigner.1"), AOUIFactory.YES_NO_OPTION, AOUIFactory.WARNING_MESSAGE) == 0) {
                if (z) {
                    properties.put(XAdESExtraParams.PROFILE, AOSignConstants.SIGN_PROFILE_BASELINE);
                    return;
                } else {
                    properties.put(XAdESExtraParams.PROFILE, "advanced");
                    return;
                }
            }
            return;
        }
        if (z) {
            properties.put(XAdESExtraParams.PROFILE, AOSignConstants.SIGN_PROFILE_BASELINE);
        } else {
            properties.put(XAdESExtraParams.PROFILE, "advanced");
        }
    }

    public static boolean isDataMandatory(String str, Properties properties) {
        return (CRYPTO_OPERATION_SIGN.equalsIgnoreCase(str) && properties != null && (AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED.equals(properties.getProperty(XAdESExtraParams.FORMAT)) || Boolean.parseBoolean(properties.getProperty(XAdESExtraParams.USE_MANIFEST)))) ? false : true;
    }

    public static void checkArchiveSignatures(NodeList nodeList) throws SigningLTSException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            checkArchiveSignatures((Element) nodeList.item(i));
        }
    }

    public static void checkArchiveSignatures(Element element) throws SigningLTSException {
        Element childElementByTagNameNS;
        Element unSignedPropertiesElement = getUnSignedPropertiesElement(element);
        if (unSignedPropertiesElement != null && (childElementByTagNameNS = XMLUtils.getChildElementByTagNameNS(unSignedPropertiesElement, XAdESConstants.TAG_UNSIGNED_SIGNATURE_PROPERTIES, "http://uri.etsi.org/01903/v1.3.2#")) != null) {
            if (XMLUtils.getChildElementByTagNameNS(childElementByTagNameNS, XAdESConstants.TAG_ARCHIVE_TIMESTAMP, "http://uri.etsi.org/01903/v1.4.1#") != null) {
                throw new SigningLTSException("Se han encontrado firmas de sello de archivo");
            }
            if (XMLUtils.getChildElementByTagNameNS(childElementByTagNameNS, XAdESConstants.TAG_ARCHIVE_TIMESTAMP, "http://uri.etsi.org/01903/v1.3.2#") != null) {
                throw new SigningLTSException("Se han encontrado firmas de sello de archivo");
            }
            if (XMLUtils.getChildElementByTagNameNS(childElementByTagNameNS, XAdESConstants.TAG_ARCHIVE_TIMESTAMP, XAdESConstants.NAMESPACE_XADES_1_2_2) != null) {
                throw new SigningLTSException("Se han encontrado firmas de sello de archivo");
            }
        }
    }

    public static boolean isSignatureElementEnveloped(Element element, List<Element> list) {
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            NodeList elementsByTagNameNS = list.get(i).getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM);
            for (int i2 = 0; i2 < elementsByTagNameNS.getLength(); i2++) {
                String attribute = ((Element) elementsByTagNameNS.item(i2)).getAttribute("Algorithm");
                if ("http://www.w3.org/2000/09/xmldsig#enveloped-signature".equals(attribute)) {
                    return true;
                }
                if (Transforms.TRANSFORM_XPATH.equals(attribute)) {
                    String prefix = element.getPrefix();
                    String strReplaceAll = elementsByTagNameNS.item(i2).getTextContent().replaceAll("\\s+", "");
                    if (String.format(XPATH_ENVELOPED_EQ, prefix).equals(strReplaceAll) || String.format(XPATH_ENVELOPED_EQ2, prefix).equals(strReplaceAll)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSignatureWithManifest(List<Element> list) {
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String attribute = list.get(i).getAttribute("Type");
            if (attribute != null && attribute.equals("http://www.w3.org/2000/09/xmldsig#Manifest")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSignatureElementExternallyDetached(List<Element> list) {
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String attribute = list.get(i).getAttribute("URI");
            if (attribute != null && (attribute.toLowerCase(Locale.US).startsWith("http://") || attribute.toLowerCase(Locale.US).startsWith("https://"))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSignatureElementInternallyDetached(Element element, List<Element> list) {
        if (element == null || list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String attribute = list.get(i).getAttribute("URI");
            if (attribute != null && attribute.startsWith("#") && findElementById(attribute.substring(1), element, true) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSignatureElementEnveloping(Element element, List<Element> list) {
        if (element == null || list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            String attribute = list.get(i).getAttribute("URI");
            if (attribute != null && attribute.startsWith("#") && findElementById(attribute.substring(1), element, false) != null) {
                return true;
            }
        }
        return false;
    }
}
