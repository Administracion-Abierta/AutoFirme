package es.gob.afirma.signers.xades;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.AOInvalidFormatException;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSignInfo;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.signers.OptionalDataInterface;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.core.util.tree.AOTreeNode;
import es.gob.afirma.signers.xml.Utils;
import es.gob.afirma.signers.xml.XmlDSigProviderHelper;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.URIDereferencer;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xades/AOXAdESSigner.class */
public final class AOXAdESSigner implements AOSigner, OptionalDataInterface {
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String TIMESTAMP_TAG = "Timestamp";
    static final String DETACHED_CONTENT_ELEMENT_NAME = "CONTENT";
    static final String DETACHED_STYLE_ELEMENT_NAME = "STYLE";
    static final String XMLDSIG_ATTR_MIMETYPE_STR = "MimeType";
    static final String XMLDSIG_ATTR_ENCODING_STR = "Encoding";
    private URIDereferencer uriDereferencer = null;

    static {
        XmlDSigProviderHelper.configureXmlDSigProvider();
    }

    @Override // es.gob.afirma.core.signers.AOSimpleSigner
    public byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        Properties extraParams = getExtraParams(properties);
        return XAdESTspUtil.timestampXAdES(XAdESSigner.sign(bArr, str, privateKey, certificateArr, extraParams, this.uriDereferencer), extraParams);
    }

    public static boolean isDetached(Element element) {
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(element);
        if (firstSignatureElement == null) {
            return false;
        }
        return XAdESUtil.isSignatureElementInternallyDetached(element, XAdESUtil.getSignatureDataReferenceList(firstSignatureElement));
    }

    public static boolean isExternallyDetached(Element element) {
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(element);
        if (firstSignatureElement == null) {
            return false;
        }
        return XAdESUtil.isSignatureElementExternallyDetached(XAdESUtil.getSignatureDataReferenceList(firstSignatureElement));
    }

    public static boolean isManifestSignature(Element element) {
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(element);
        if (firstSignatureElement == null) {
            return false;
        }
        return XAdESUtil.isSignatureWithManifest(XAdESUtil.getSignatureDataReferenceList(firstSignatureElement));
    }

    public static boolean isEnveloped(Element element) {
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(element);
        if (firstSignatureElement == null) {
            return false;
        }
        return XAdESUtil.isSignatureElementEnveloped(firstSignatureElement, XAdESUtil.getSignatureDataReferenceList(firstSignatureElement));
    }

    public static boolean isEnveloping(Element element) {
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(element);
        if (firstSignatureElement == null) {
            return false;
        }
        return XAdESUtil.isSignatureElementEnveloping(firstSignatureElement, XAdESUtil.getSignatureDataReferenceList(firstSignatureElement));
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr) throws AOInvalidFormatException {
        return getData(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public byte[] getData(byte[] bArr, Properties properties) throws AOInvalidFormatException {
        try {
            return getData(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
        } catch (Exception e) {
            throw new AOInvalidFormatException("Error al leer el fichero de firmas: " + e, e);
        }
    }

    public static byte[] getData(Document document) throws AOInvalidFormatException {
        Element element;
        if (!isSign(document)) {
            throw new AOInvalidFormatException("El documento no es un documento de firmas valido.");
        }
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(document.getDocumentElement());
        if (firstSignatureElement == null) {
            return null;
        }
        List<Element> signatureDataReferenceList = XAdESUtil.getSignatureDataReferenceList(firstSignatureElement);
        try {
            Element documentElement = document.getDocumentElement();
            if (XAdESUtil.isSignatureElementExternallyDetached(signatureDataReferenceList) || XAdESUtil.isSignatureWithManifest(signatureDataReferenceList)) {
                element = null;
            } else if (XAdESUtil.isSignatureElementEnveloped(firstSignatureElement, signatureDataReferenceList)) {
                Element elementReferenced = getElementReferenced(documentElement, signatureDataReferenceList.get(0));
                removeEnvelopedSignatures(elementReferenced);
                if (elementReferenced != documentElement && elementReferenced.getFirstChild().getNodeType() == 3) {
                    if (isBase64TransformationDeclared(documentElement, signatureDataReferenceList.get(0))) {
                        return Base64.decode(elementReferenced.getTextContent());
                    }
                    return elementReferenced.getTextContent().getBytes();
                }
                element = elementReferenced;
            } else if (XAdESUtil.isSignatureElementInternallyDetached(documentElement, signatureDataReferenceList)) {
                Element element2 = (Element) documentElement.getFirstChild();
                if (element2.getFirstChild().getNodeType() == 3) {
                    if (isBase64TransformationDeclared(documentElement, element2.getAttribute("Id"))) {
                        return Base64.decode(element2.getTextContent());
                    }
                    return element2.getTextContent().getBytes();
                }
                element = (Element) element2.getFirstChild();
            } else if (XAdESUtil.isSignatureElementEnveloping(firstSignatureElement, signatureDataReferenceList)) {
                Element element3 = (Element) documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_OBJECT).item(0);
                if (element3.getAttribute("MimeType").equals("text/xml")) {
                    element = (Element) element3.getFirstChild();
                } else {
                    if (isBase64TransformationDeclared(documentElement, element3.getAttribute("Id"))) {
                        return Base64.decode(element3.getTextContent());
                    }
                    return element3.getTextContent().getBytes();
                }
            } else {
                element = null;
            }
            if (element == null) {
                return null;
            }
            return Utils.writeXML(element, null, null, null);
        } catch (Exception e) {
            throw new AOInvalidFormatException("Error al leer el fichero de firmas: " + e, e);
        }
    }

    private static Element getElementReferenced(Element element, Element element2) {
        String attribute = element2.getAttribute("URI");
        if (attribute.isEmpty()) {
            return element;
        }
        if (!attribute.startsWith("#")) {
            return null;
        }
        return XAdESUtil.findElementById(attribute.substring(1), element, false);
    }

    private static void removeEnvelopedSignatures(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == 1 && childNodes.item(i).getLocalName().equals("Signature")) {
                element.removeChild(childNodes.item(i));
                removeEnvelopedSignatures(element);
                return;
            }
        }
    }

    private static boolean isBase64TransformationDeclared(Element element, String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }
        Element element2 = null;
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference");
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            element2 = (Element) elementsByTagNameNS.item(i);
            if (element2.hasAttribute("URI") && ("#" + str).equals(element2.getAttribute("URI"))) {
                break;
            }
            element2 = null;
        }
        if (element2 != null) {
            NodeList elementsByTagNameNS2 = element2.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM);
            for (int i2 = 0; i2 < elementsByTagNameNS2.getLength(); i2++) {
                if (((Element) elementsByTagNameNS2.item(i2)).hasAttribute("Algorithm") && "http://www.w3.org/2000/09/xmldsig#base64".equals(((Element) elementsByTagNameNS2.item(i2)).getAttribute("Algorithm"))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isBase64TransformationDeclared(Element element, Element element2) {
        if (element2 == null) {
            return false;
        }
        NodeList elementsByTagNameNS = element2.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", Constants._TAG_TRANSFORM);
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            if (((Element) elementsByTagNameNS.item(i)).hasAttribute("Algorithm") && "http://www.w3.org/2000/09/xmldsig#base64".equals(((Element) elementsByTagNameNS.item(i)).getAttribute("Algorithm"))) {
                return true;
            }
        }
        return false;
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, byte[] bArr2, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            return cosign(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr2)), str, privateKey, certificateArr, getExtraParams(properties), this.uriDereferencer);
        } catch (Exception e) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
        }
    }

    @Override // es.gob.afirma.core.signers.AOCoSigner
    public byte[] cosign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        return cosign((byte[]) null, bArr, str, privateKey, certificateArr, properties);
    }

    private static byte[] cosign(Document document, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws AOException {
        if (!isSign(document)) {
            throw new AOInvalidFormatException("No se ha indicado una firma XAdES para cofirmar");
        }
        XAdESUtil.checkSignProfile(properties, checkCompatibility(document));
        return XAdESCoSigner.cosign(document, str, privateKey, certificateArr, properties, uRIDereferencer);
    }

    @Override // es.gob.afirma.core.signers.AOCounterSigner
    public byte[] countersign(byte[] bArr, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws AOException {
        try {
            Document document = Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            Properties extraParams = getExtraParams(properties);
            XAdESUtil.checkSignProfile(extraParams, checkCompatibility(document));
            return countersign(document, str, counterSignTarget, objArr, privateKey, certificateArr, extraParams, this.uriDereferencer);
        } catch (Exception e) {
            throw new AOInvalidFormatException("No se ha podido cargar el documento XML de firmas", e);
        }
    }

    private static byte[] countersign(Document document, String str, CounterSignTarget counterSignTarget, Object[] objArr, PrivateKey privateKey, Certificate[] certificateArr, Properties properties, URIDereferencer uRIDereferencer) throws AOException {
        if (!isSign(document)) {
            throw new AOInvalidFormatException("No se ha indicado una firma XAdES para contrafirmar");
        }
        return XAdESCounterSigner.countersign(document, str, counterSignTarget, objArr, privateKey, certificateArr, properties, uRIDereferencer);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, boolean z) throws AOInvalidFormatException {
        return getSignersStructure(bArr, null, z);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOTreeModel getSignersStructure(byte[] bArr, Properties properties, boolean z) throws AOInvalidFormatException {
        try {
            return getSignersStructure(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)), z);
        } catch (Exception e) {
            LOGGER.warning("Se ha producido un error al obtener la estructura de firmas: " + e);
            return null;
        }
    }

    public static AOTreeModel getSignersStructure(Document document, boolean z) throws AOInvalidFormatException {
        if (!isSign(document)) {
            throw new AOInvalidFormatException("Los datos indicados no son una firma XAdES compatible");
        }
        NodeList elementsByTagNameNS = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            Element element = (Element) elementsByTagNameNS.item(i);
            Node parentNode = elementsByTagNameNS.item(i).getParentNode();
            if (parentNode == null || !TIMESTAMP_TAG.equals(parentNode.getLocalName())) {
                arrayList.add(element.getAttribute("Id"));
                if (z) {
                    String namespaceURI = null;
                    Element signedPropertiesElement = XAdESUtil.getSignedPropertiesElement(XAdESUtil.getFirstSignatureElement(document.getDocumentElement()));
                    if (signedPropertiesElement != null) {
                        namespaceURI = signedPropertiesElement.getNamespaceURI();
                    }
                    arrayList3.add(new AOTreeNode(Utils.getSimpleSignInfoNode(namespaceURI, element)));
                } else {
                    arrayList3.add(new AOTreeNode(Utils.getStringInfoNode(element)));
                }
                if (element.getParentNode() != null && "CounterSignature".equals(element.getParentNode().getLocalName())) {
                    arrayList2.add(Utils.getCounterSignerReferenceId(element, document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue")));
                } else {
                    arrayList2.add("");
                }
            }
        }
        AOTreeNode aOTreeNode = new AOTreeNode("Datos");
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            if (((String) arrayList2.get(i2)).equals("")) {
                aOTreeNode.add(generateSignsTree(i2, arrayList.size() - 1, arrayList3, arrayList, arrayList2)[i2]);
            }
        }
        return new AOTreeModel(aOTreeNode);
    }

    private static AOTreeNode[] generateSignsTree(int i, int i2, List<AOTreeNode> list, List<String> list2, List<String> list3) {
        int size = list2.size();
        if (i < size && i2 > 0) {
            if (list2.get(i).equals(list3.get(i2))) {
                generateSignsTree(i + 1, i2 - 1, list, list2, list3);
            }
            if (i < i2) {
                generateSignsTree(i, i2 - 1, list, list2, list3);
            }
            if (!list2.get(i).equals(list3.get(i2))) {
                return (AOTreeNode[]) list.toArray(new AOTreeNode[0]);
            }
            generateSignsTree(i2, size - 1, list, list2, list3);
            list.get(i).add(list.get(i2));
        }
        return (AOTreeNode[]) list.toArray(new AOTreeNode[0]);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr) {
        return isSign(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isSign(byte[] bArr, Properties properties) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        try {
            return isSign(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
        } catch (Exception e) {
            LOGGER.fine("El documento no es un XML");
            return false;
        }
    }

    public static boolean isSign(Document document) {
        if (document == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        try {
            Element documentElement = document.getDocumentElement();
            ArrayList arrayList = new ArrayList();
            if (documentElement.getLocalName().equals("Signature")) {
                arrayList.add(documentElement);
            }
            NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                Node parentNode = elementsByTagNameNS.item(i).getParentNode();
                if (parentNode == null || !TIMESTAMP_TAG.equals(parentNode.getLocalName())) {
                    arrayList.add(elementsByTagNameNS.item(i));
                }
            }
            if (arrayList.size() == 0 || !XAdESUtil.checkSignNodes(arrayList)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al analizar si el XML era una firma XAdES", (Throwable) e);
            return false;
        }
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public boolean isValidDataFile(byte[] bArr) {
        if (bArr == null) {
            LOGGER.warning("Se han introducido datos nulos para su comprobacion");
            return false;
        }
        return true;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public String getSignedName(String str, String str2) {
        return str + (str2 != null ? str2 : "") + ".xsig";
    }

    static Document insertarNodoAfirma(Document document) throws ParserConfigurationException {
        Document documentNewDocument = Utils.getNewDocumentBuilder().newDocument();
        Element elementCreateElement = documentNewDocument.createElement("AFIRMA");
        elementCreateElement.setAttributeNS(null, "Id", "AfirmaRoot-" + UUID.randomUUID().toString());
        elementCreateElement.appendChild(documentNewDocument.adoptNode(document.getDocumentElement()));
        documentNewDocument.appendChild(elementCreateElement);
        return documentNewDocument;
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr) throws AOException {
        return getSignInfo(bArr, null);
    }

    @Override // es.gob.afirma.core.signers.AOSigner
    public AOSignInfo getSignInfo(byte[] bArr, Properties properties) throws AOException {
        if (bArr == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        try {
            return getSignInfo(Utils.getNewDocumentBuilder().parse(new ByteArrayInputStream(bArr)));
        } catch (Exception e) {
            LOGGER.warning("Error al analizar la firma: " + e);
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un documento XML", e);
        }
    }

    public static AOSignInfo getSignInfo(Document document) throws AOException {
        if (document == null) {
            throw new IllegalArgumentException("No se han introducido datos para analizar");
        }
        if (!isSign(document)) {
            throw new AOInvalidFormatException("Los datos introducidos no se corresponden con un objeto de firma");
        }
        Element documentElement = document.getDocumentElement();
        AOSignInfo aOSignInfo = new AOSignInfo(AOSignConstants.SIGN_FORMAT_XADES);
        Element firstSignatureElement = XAdESUtil.getFirstSignatureElement(document.getDocumentElement());
        List<Element> signatureDataReferenceList = XAdESUtil.getSignatureDataReferenceList(firstSignatureElement);
        if (XAdESUtil.isSignatureElementEnveloped(firstSignatureElement, signatureDataReferenceList)) {
            aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
        } else if (XAdESUtil.isSignatureElementExternallyDetached(signatureDataReferenceList)) {
            aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED);
        } else if (XAdESUtil.isSignatureElementInternallyDetached(documentElement, signatureDataReferenceList)) {
            aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XADES_DETACHED);
        } else if (XAdESUtil.isSignatureElementEnveloping(firstSignatureElement, signatureDataReferenceList)) {
            aOSignInfo.setVariant(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING);
        }
        return aOSignInfo;
    }

    @Override // es.gob.afirma.core.signers.OptionalDataInterface
    public boolean needData(String str, Properties properties) {
        return XAdESUtil.isDataMandatory(str, properties);
    }

    private static Properties getExtraParams(Properties properties) {
        Properties properties2 = properties != null ? (Properties) properties.clone() : new Properties();
        properties2.remove(XAdESExtraParams.INTERNAL_VALIDATE_PKCS1);
        return properties2;
    }

    public static boolean checkCompatibility(Document document) throws Exception {
        boolean z = false;
        try {
            Element documentElement = document.getDocumentElement();
            ArrayList arrayList = new ArrayList();
            if (documentElement.getLocalName().equals("Signature")) {
                arrayList.add(documentElement);
            }
            NodeList elementsByTagNameNS = documentElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
            for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
                Node parentNode = elementsByTagNameNS.item(i).getParentNode();
                if (parentNode == null || !TIMESTAMP_TAG.equals(parentNode.getLocalName())) {
                    arrayList.add(elementsByTagNameNS.item(i));
                }
            }
            if (XAdESUtil.checkCompatibility(arrayList)) {
                z = true;
            }
            return z;
        } catch (AOInvalidFormatException e) {
            throw e;
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error al analizar la firma XAdES que se debia multifirmar", (Throwable) e2);
            throw e2;
        }
    }

    public void setUriDereferencer(URIDereferencer uRIDereferencer) {
        this.uriDereferencer = uRIDereferencer;
    }
}
