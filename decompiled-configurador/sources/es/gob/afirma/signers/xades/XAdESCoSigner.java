package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AGEPolicyIncompatibilityException;
import es.gob.afirma.core.AOException;
import es.gob.afirma.core.SigningLTSException;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicyPropertiesManager;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XMLConstants;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormatImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifierImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.XAdESBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/XAdESCoSigner.class */
public final class XAdESCoSigner {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private XAdESCoSigner() {
    }

    public static byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            return cosign(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)), str, privateKey, certificateArr, properties);
        } catch (Exception e) {
            throw new AOException("No se ha podido leer el documento XML de firmas", e);
        }
    }

    public static byte[] cosign(Document document, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return cosign(document, str, privateKey, certificateArr, properties, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public static byte[] cosign(Document document, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws MarshalException, XMLSignatureException, AOException {
        String str2;
        Node namedItem;
        String str3 = str != null ? str : "SHA512withRSA";
        Properties properties2 = properties != null ? properties : new Properties();
        checkParams(str3, properties2);
        String str4 = XMLConstants.SIGN_ALGOS_URI.get(str3);
        if (str4 == null) {
            throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str3 + "'");
        }
        String property = properties2.getProperty(XAdESExtraParams.REFERENCES_DIGEST_METHOD, "http://www.w3.org/2001/04/xmlenc#sha512");
        String property2 = properties2.getProperty(XAdESExtraParams.CANONICALIZATION_ALGORITHM, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        boolean z = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_VALUE, Boolean.TRUE.toString()));
        boolean z2 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_KEY_NAME, Boolean.FALSE.toString()));
        boolean z3 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.ADD_KEY_INFO_X509_ISSUER_SERIAL, Boolean.FALSE.toString()));
        boolean z4 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.KEEP_KEYINFO_UNSIGNED, Boolean.FALSE.toString()));
        String property3 = properties2.getProperty(XAdESExtraParams.OUTPUT_XML_ENCODING);
        String property4 = properties2.getProperty(XAdESExtraParams.CONTENT_TYPE_OID);
        String property5 = properties2.getProperty("mimeType");
        String property6 = properties2.getProperty("encoding");
        if ("base64".equalsIgnoreCase(property6)) {
            property6 = "http://www.w3.org/2000/09/xmldsig#base64";
        }
        if (property6 != null && !property6.isEmpty()) {
            try {
                new URI(property6);
            } catch (Exception e) {
                throw new AOException("La codificacion indicada en 'encoding' debe ser una URI: " + e, e);
            }
        }
        boolean z5 = Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INTERNAL_VALIDATE_PKCS1, Boolean.TRUE.toString()));
        String property7 = properties2.getProperty(XAdESExtraParams.PROFILE, "advanced");
        Document documentInsertarNodoAfirma = document;
        Element documentElement = document.getDocumentElement();
        if (documentElement.getLocalName().equals("Signature")) {
            try {
                documentInsertarNodoAfirma = AOXAdESSigner.insertarNodoAfirma(documentInsertarNodoAfirma);
                documentElement = documentInsertarNodoAfirma.getDocumentElement();
            } catch (Exception e2) {
                throw new AOException("No se ha estructurar el documento XML de firmas", e2);
            }
        }
        NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        String property8 = properties2.getProperty(XAdESExtraParams.ALLOW_SIGN_LTS_SIGNATURES);
        if (property8 == null || !Boolean.parseBoolean(property8)) {
            try {
                XAdESUtil.checkArchiveSignatures(elementsByTagNameNS);
            } catch (SigningLTSException e3) {
                if (property8 != null) {
                    throw new AOException(e3.getMessage());
                }
                throw new SigningLTSException("La cofirma de firmas de archivo invalidara el sello de archivo", e3, false);
            }
        }
        Map<String, String> originalXMLProperties = XAdESUtil.getOriginalXMLProperties(documentInsertarNodoAfirma, property3);
        XMLSignatureFactory dOMFactory = Utils.getDOMFactory();
        try {
            DigestMethod digestMethodNewDigestMethod = dOMFactory.newDigestMethod(property, (DigestMethodParameterSpec) null);
            Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(documentInsertarNodoAfirma.getDocumentElement());
            Element signedPropertiesReference = XAdESUtil.getSignedPropertiesReference(firstSignatureElement);
            String attribute = signedPropertiesReference.getAttribute("Type");
            if (attribute == null || attribute.isEmpty()) {
                attribute = "http://uri.etsi.org/01903#SignedProperties";
            }
            Element signedPropertiesElement = XAdESUtil.getSignedPropertiesElement(firstSignatureElement, signedPropertiesReference);
            String namespaceURI = signedPropertiesElement.getNamespaceURI();
            if (namespaceURI == null) {
                namespaceURI = "http://uri.etsi.org/01903/v1.3.2#";
            }
            if (AOSignConstants.SIGN_PROFILE_BASELINE.equals(property7) && !XAdESUtil.isBaselineCompatible(namespaceURI)) {
                LOGGER.warning("La firma original utiliza un espacio de nombres no compatible con baseline (" + namespaceURI + "). No se generara una firma baseline");
                property7 = "advanced";
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            XMLObject xMLObjectCreateSignatureObject = null;
            boolean z6 = false;
            for (Element element : XAdESUtil.getSignatureDataReferenceList(firstSignatureElement)) {
                try {
                    List<Transform> objectReferenceTransforms = Utils.getObjectReferenceTransforms(element, "ds");
                    if (element.getAttribute("Id") != null && element.getAttribute("Id").startsWith("StyleReference-")) {
                        str2 = "StyleReference-" + UUID.randomUUID().toString();
                    } else {
                        str2 = "Reference-" + UUID.randomUUID().toString();
                    }
                    String attribute2 = element.getAttribute("URI");
                    String attribute3 = element.getAttribute("Type");
                    if (attribute3 != null && attribute3.isEmpty()) {
                        attribute3 = null;
                    }
                    if ("http://www.w3.org/2000/09/xmldsig#Manifest".equals(attribute3)) {
                        Element elementCopyManifest = copyManifest(attribute2, firstSignatureElement);
                        ArrayList<DataObjectFormat> arrayListCopyDataObjectFormats = copyDataObjectFormats(signedPropertiesElement);
                        String strRenewManifestIds = renewManifestIds(elementCopyManifest, arrayListCopyDataObjectFormats);
                        xMLObjectCreateSignatureObject = createSignatureObject(elementCopyManifest, dOMFactory, property5, property6);
                        arrayList.add(dOMFactory.newReference("#" + strRenewManifestIds, digestMethodNewDigestMethod, objectReferenceTransforms, attribute3, str2));
                        arrayList2.addAll(arrayListCopyDataObjectFormats);
                    } else if ("".equals(attribute2)) {
                        if (property5 == null) {
                            property5 = "text/xml";
                        }
                        arrayList.add(dOMFactory.newReference(attribute2, digestMethodNewDigestMethod, objectReferenceTransforms, "http://www.w3.org/2000/09/xmldsig#Object", str2));
                        addReferenceDataObjectFormat(arrayList2, str2, property5, property4, property6);
                    } else {
                        String strSubstring = attribute2.substring(attribute2.startsWith("#") ? 1 : 0);
                        Element element2 = null;
                        Element documentElement2 = documentInsertarNodoAfirma.getDocumentElement();
                        Node namedItem2 = documentElement2.getAttributes() != null ? documentElement2.getAttributes().getNamedItem("Id") : null;
                        if (namedItem2 != null && strSubstring.equals(namedItem2.getNodeValue())) {
                            element2 = documentElement2;
                        } else {
                            NodeList childNodes = documentElement2.getChildNodes();
                            for (int length = childNodes.getLength() - 1; length >= 0; length--) {
                                Node namedItem3 = childNodes.item(length).getAttributes() != null ? childNodes.item(length).getAttributes().getNamedItem("Id") : null;
                                if (namedItem3 != null && strSubstring.equals(namedItem3.getNodeValue())) {
                                    element2 = (Element) childNodes.item(length);
                                    break;
                                }
                                if ("Signature".equals(childNodes.item(length).getLocalName())) {
                                    NodeList childNodes2 = childNodes.item(length).getChildNodes();
                                    for (int length2 = childNodes2.getLength() - 1; length2 >= 0; length2--) {
                                        Node namedItem4 = childNodes2.item(length2).getAttributes() != null ? childNodes2.item(length2).getAttributes().getNamedItem("Id") : null;
                                        if (namedItem4 != null && strSubstring.equals(namedItem4.getNodeValue())) {
                                            element2 = (Element) childNodes2.item(length2);
                                            break;
                                        }
                                    }
                                    if (element2 != null) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (element2 != null) {
                            if (property5 == null) {
                                property5 = element2.getAttribute("MimeType");
                            }
                            if (property6 == null) {
                                property6 = element2.getAttribute("Encoding");
                            }
                        }
                        NodeList childNodes3 = documentInsertarNodoAfirma.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0).getChildNodes();
                        for (int i = 0; i < childNodes3.getLength(); i++) {
                            NamedNodeMap attributes = childNodes3.item(i).getAttributes();
                            if (attributes != null && (namedItem = attributes.getNamedItem("Id")) != null && strSubstring.equals(namedItem.getNodeValue())) {
                                z6 = true;
                            }
                        }
                        if (z6 && element2 != null) {
                            if (AdESPolicyPropertiesManager.isAgePolicyConfigurated(properties2.getProperty(XAdESExtraParams.POLICY_IDENTIFIER))) {
                                String property9 = properties2.getProperty(XAdESExtraParams.AVOID_AGE_POLICY_INCOMPATIBILITIES);
                                if (property9 == null) {
                                    throw new AGEPolicyIncompatibilityException("La politica de la AGE no soporta la cofirma XAdES Enveloping", 2);
                                }
                                if (Boolean.parseBoolean(property9)) {
                                    new AGEPolicyIncompatibilityException("La politica de la AGE no soporta la cofirma XAdES Enveloping").prepareOperationWithConfirmation(properties2);
                                } else {
                                    throw new AOException("La politica de la AGE no soporta la cofirma XAdES Enveloping");
                                }
                            }
                            ArrayList arrayList3 = new ArrayList(1);
                            arrayList3.add(new DOMStructure(element2.getFirstChild().cloneNode(true)));
                            String str5 = "Object-" + UUID.randomUUID().toString();
                            xMLObjectCreateSignatureObject = dOMFactory.newXMLObject(arrayList3, str5, property5, property6);
                            arrayList.add(dOMFactory.newReference("#" + str5, digestMethodNewDigestMethod, objectReferenceTransforms, attribute3, str2));
                        } else {
                            arrayList.add(dOMFactory.newReference(attribute2, digestMethodNewDigestMethod, objectReferenceTransforms, attribute3, str2));
                        }
                        addReferenceDataObjectFormat(arrayList2, str2, property5, property4, property6);
                    }
                } catch (InvalidAlgorithmParameterException e4) {
                    throw new AOException("Se han especificado parametros erroneos para una transformacion personalizada", e4);
                } catch (NoSuchAlgorithmException e5) {
                    throw new AOException("Se ha declarado una transformacion personalizada de un tipo no soportado", e5);
                }
            }
            XAdESBase xAdESBaseNewInstance = XAdESUtil.newInstance(property7, namespaceURI, "xades", "ds", property, documentElement.getOwnerDocument(), documentElement, (X509Certificate) certificateArr[0]);
            XAdESCommonMetadataUtil.addCommonMetadata(xAdESBaseNewInstance, properties2);
            if (!arrayList2.isEmpty()) {
                xAdESBaseNewInstance.setDataObjectFormats(arrayList2);
            }
            AOXMLAdvancedSignature xmlAdvancedSignature = XAdESUtil.getXmlAdvancedSignature(xAdESBaseNewInstance, attribute, property, property2, uRIDereferencer);
            if (xMLObjectCreateSignatureObject != null) {
                xmlAdvancedSignature.addXMLObject(xMLObjectCreateSignatureObject);
            }
            try {
                if (Boolean.parseBoolean(properties2.getProperty(XAdESExtraParams.INCLUDE_ONLY_SIGNNING_CERTIFICATE, Boolean.FALSE.toString()))) {
                    xmlAdvancedSignature.sign((X509Certificate) certificateArr[0], privateKey, str4, arrayList, "Signature-" + UUID.randomUUID().toString());
                } else {
                    xmlAdvancedSignature.sign(Arrays.asList(certificateArr), privateKey, str4, arrayList, "Signature-" + UUID.randomUUID().toString(), z, z2, z3, z4, z5);
                }
                return Utils.writeXML(documentElement, originalXMLProperties, null, null);
            } catch (AOException e6) {
                throw e6;
            } catch (NoSuchAlgorithmException e7) {
                throw new IllegalArgumentException("Los formatos de firma XML no soportan el algoritmo de firma '" + str3 + "':" + e7, e7);
            } catch (Exception e8) {
                throw new AOException("Error al generar la cofirma XAdES", e8);
            }
        } catch (Exception e9) {
            throw new AOException("No se ha podido obtener un generador de huellas digitales para el algoritmo '" + property + "'", e9);
        }
    }

    private static Element copyManifest(String str, Element element) throws AOException {
        if (!str.startsWith("#")) {
            throw new AOException("La URI de la firma original que referencia al manifest debe ser local");
        }
        Element elementFindElementById = XAdESUtil.findElementById(str.substring(1), element, false);
        if (elementFindElementById == null) {
            throw new AOException("No se encontro el manifest dentro de la firma");
        }
        return (Element) elementFindElementById.cloneNode(true);
    }

    private static ArrayList<DataObjectFormat> copyDataObjectFormats(Element element) {
        Element element2 = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; element2 == null && i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == 1 && XAdESConstants.TAG_SIGNED_DATA_OBJECT_PROPERTIES.equals(childNodes.item(i).getLocalName())) {
                element2 = (Element) childNodes.item(i);
            }
        }
        ArrayList<DataObjectFormat> arrayList = new ArrayList<>();
        if (element2 != null) {
            NodeList elementsByTagNameNS = element2.getElementsByTagNameNS(element2.getNamespaceURI(), XAdESConstants.TAG_DATA_OBJECT_FORMAT);
            for (int i2 = 0; i2 < elementsByTagNameNS.getLength(); i2++) {
                arrayList.add(DataObjectFormatParser.parseDataObjectFormat((Element) elementsByTagNameNS.item(i2)));
            }
        }
        return arrayList;
    }

    private static String renewManifestIds(Element element, ArrayList<DataObjectFormat> arrayList) {
        String str = "Manifest-" + UUID.randomUUID().toString();
        element.setAttribute("Id", str);
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS(element.getNamespaceURI(), "Reference");
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            Element element2 = (Element) elementsByTagNameNS.item(i);
            String attribute = element2.getAttribute("Id");
            if (attribute != null) {
                String str2 = "Reference-" + UUID.randomUUID().toString();
                element2.setAttribute("Id", str2);
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    DataObjectFormat dataObjectFormat = arrayList.get(i2);
                    String objectReference = dataObjectFormat.getObjectReference();
                    if (objectReference != null && objectReference.equals("#" + attribute)) {
                        arrayList.set(i2, new DataObjectFormatImpl(dataObjectFormat.getDescription(), dataObjectFormat.getObjectIdentifier(), dataObjectFormat.getMimeType(), dataObjectFormat.getEncoding(), "#" + str2));
                    }
                }
            }
        }
        return str;
    }

    private static XMLObject createSignatureObject(Element element, XMLSignatureFactory xMLSignatureFactory, String str, String str2) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new DOMStructure(element));
        return xMLSignatureFactory.newXMLObject(arrayList, "ManifestObject-" + UUID.randomUUID().toString(), str, str2);
    }

    private static void addReferenceDataObjectFormat(List<DataObjectFormat> list, String str, String str2, String str3, String str4) {
        String strTransformMimeTypeToOid = str3;
        if (strTransformMimeTypeToOid == null && str2 != null) {
            try {
                strTransformMimeTypeToOid = MimeHelper.transformMimeTypeToOid(str2);
            } catch (IOException e) {
                LOGGER.warning("Error en la obtencion del OID del tipo de datos a partir del MimeType: " + e);
            }
        }
        ObjectIdentifierImpl objectIdentifierImpl = null;
        if (strTransformMimeTypeToOid != null) {
            objectIdentifierImpl = new ObjectIdentifierImpl("OIDAsURN", (strTransformMimeTypeToOid.startsWith("urn:oid:") ? "" : "urn:oid:") + strTransformMimeTypeToOid, null, new ArrayList(0));
        }
        list.add(new DataObjectFormatImpl(null, objectIdentifierImpl, str2, str4, "#" + str));
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
        if (properties.containsKey(XAdESExtraParams.XADES_NAMESPACE)) {
            LOGGER.warning("Se ignorara el espacio de nombres indicado. En las cofirmas siempre se usara el mismo espacio de nombres que la firma original");
            properties.remove(XAdESExtraParams.XADES_NAMESPACE);
        }
        if (properties.containsKey(XAdESExtraParams.SIGNED_PROPERTIES_TYPE_URL)) {
            LOGGER.warning("Se ignorara la URL indicada para el tipo SignedProperties. En las cofirmas siempre se usara la misma URL que la firma original");
            properties.remove(XAdESExtraParams.SIGNED_PROPERTIES_TYPE_URL);
        }
    }
}
