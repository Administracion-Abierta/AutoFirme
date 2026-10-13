package es.gob.afirma.signers.xml;

import es.gob.afirma.core.misc.AOFileUtils;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSimpleSignInfo;
import es.gob.afirma.signers.xades.XAdESConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/xml/Utils.class */
public final class Utils {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static DocumentBuilderFactory SECURE_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    static {
        try {
            SECURE_BUILDER_FACTORY.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se ha podido establecer el procesado seguro en la factoria XML: " + e);
        }
        for (String str : new String[]{"http://javax.xml.XMLConstants/property/accessExternalDTD", "http://javax.xml.XMLConstants/property/accessExternalSchema", "http://javax.xml.XMLConstants/property/accessExternalStylesheet"}) {
            try {
                SECURE_BUILDER_FACTORY.setAttribute(str, "");
            } catch (Exception e2) {
                LOGGER.log(Level.FINE, "No se ha podido establecer una propiedad de seguridad '" + str + "' en la factoria XML");
            }
        }
        SECURE_BUILDER_FACTORY.setValidating(false);
        SECURE_BUILDER_FACTORY.setNamespaceAware(true);
    }

    private Utils() {
    }

    public static DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException {
        return SECURE_BUILDER_FACTORY.newDocumentBuilder();
    }

    private static String addStyleSheetHeader(String str, String str2, String str3) {
        if (str3 == null) {
            return str;
        }
        String str4 = str2 != null ? str2 : "text/xsl";
        if (str == null || str.isEmpty()) {
            return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"" + str4 + "\" href=\"" + str3 + "\"?>";
        }
        return str.replaceFirst(">", ">\r\n<?xml-stylesheet type=\"" + str4 + "\" href=\"" + str3 + "\"?>");
    }

    public static void addCustomTransforms(List<Transform> list, Properties properties, String str) {
        XPathFilterParameterSpec xPathFilter2ParameterSpec;
        XPathType.Filter filter;
        List<Transform> arrayList = list != null ? list : new ArrayList<>();
        Properties properties2 = properties != null ? properties : new Properties();
        int i = Integer.parseInt(properties2.getProperty("xmlTransforms", "0"));
        for (int i2 = 0; i2 < i; i2++) {
            String property = properties2.getProperty("xmlTransform" + Integer.toString(i2) + "Type");
            String property2 = properties2.getProperty("xmlTransform" + Integer.toString(i2) + "Body");
            if (Transforms.TRANSFORM_XPATH.equals(property) && property2 != null) {
                try {
                    xPathFilter2ParameterSpec = new XPathFilterParameterSpec(property2, Collections.singletonMap(str, "http://www.w3.org/2000/09/xmldsig#"));
                    arrayList.add(getDOMFactory().newTransform(property, xPathFilter2ParameterSpec));
                } catch (Exception e) {
                    LOGGER.warning("No se han podido crear los parametros para una transformacion XPATH, se omitira: " + e);
                }
            } else if ("http://www.w3.org/2002/06/xmldsig-filter2".equals(property) && property2 != null) {
                String property3 = properties2.getProperty("xmlTransform" + Integer.toString(i2) + "Subtype");
                if (XPath2FilterContainer.SUBTRACT.equals(property3)) {
                    filter = XPathType.Filter.SUBTRACT;
                } else if (XPath2FilterContainer.INTERSECT.equals(property3)) {
                    filter = XPathType.Filter.INTERSECT;
                } else if (XPath2FilterContainer.UNION.equals(property3)) {
                    filter = XPathType.Filter.UNION;
                } else {
                    LOGGER.warning("Se ha solicitado aplicar una transformacion XPATH2 de un tipo no soportado: " + property3);
                }
                try {
                    xPathFilter2ParameterSpec = new XPathFilter2ParameterSpec(Collections.singletonList(new XPathType(property2, filter)));
                    arrayList.add(getDOMFactory().newTransform(property, xPathFilter2ParameterSpec));
                } catch (Exception e2) {
                    LOGGER.warning("No se han podido crear los parametros para una transformacion XPATH2, se omitira: " + e2);
                }
            } else if ("http://www.w3.org/2000/09/xmldsig#base64".equals(property) || "http://www.w3.org/2000/09/xmldsig#enveloped-signature".equals(property)) {
                xPathFilter2ParameterSpec = null;
                try {
                    arrayList.add(getDOMFactory().newTransform(property, xPathFilter2ParameterSpec));
                } catch (Exception e3) {
                    LOGGER.warning("No se ha podido aplicar la transformacion '" + property + "': " + e3);
                }
            } else {
                LOGGER.warning("Tipo de transformacion no soportada: " + property);
            }
        }
    }

    public static List<Transform> getObjectReferenceTransforms(Node node, String str) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ArrayList arrayList = new ArrayList();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if ("Transforms".equals(childNodes.item(i).getNodeName()) || (str + ":Transforms").equals(childNodes.item(i).getNodeName())) {
                NodeList childNodes2 = childNodes.item(i).getChildNodes();
                for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                    if (childNodes2.item(i2).getNodeType() == 1) {
                        arrayList.add(getDOMFactory().newTransform(getTransformAlgorithm(childNodes2.item(i2)), getTransformParameterSpec(childNodes2.item(i2), str)));
                    }
                }
                break;
            }
        }
        return arrayList;
    }

    private static String getTransformAlgorithm(Node node) {
        NamedNodeMap attributes;
        Node namedItem;
        String nodeValue = null;
        if (node != null && (attributes = node.getAttributes()) != null && (namedItem = attributes.getNamedItem("Algorithm")) != null) {
            nodeValue = namedItem.getNodeValue();
        }
        return nodeValue;
    }

    private static TransformParameterSpec getTransformParameterSpec(Node node, String str) throws InvalidAlgorithmParameterException {
        XPathType.Filter filter;
        XPathFilterParameterSpec xPathFilter2ParameterSpec = null;
        String transformAlgorithm = getTransformAlgorithm(node);
        if (transformAlgorithm != null && (Transforms.TRANSFORM_XPATH.equals(transformAlgorithm) || "http://www.w3.org/2002/06/xmldsig-filter2".equals(transformAlgorithm))) {
            if (Transforms.TRANSFORM_XPATH.equals(transformAlgorithm)) {
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node nodeItem = childNodes.item(i);
                    if ("XPath".equals(nodeItem.getNodeName()) || (str + ":XPath").equals(nodeItem.getNodeName())) {
                        if (str == null || str.isEmpty()) {
                            xPathFilter2ParameterSpec = new XPathFilterParameterSpec(nodeItem.getTextContent());
                            break;
                        }
                        xPathFilter2ParameterSpec = new XPathFilterParameterSpec(nodeItem.getTextContent(), Collections.singletonMap(str, "http://www.w3.org/2000/09/xmldsig#"));
                        break;
                    }
                }
                if (xPathFilter2ParameterSpec == null) {
                    throw new InvalidAlgorithmParameterException("No se ha indicado un cuerpo para una transformacion XPATH declarada");
                }
            } else if ("http://www.w3.org/2002/06/xmldsig-filter2".equals(transformAlgorithm)) {
                NodeList childNodes2 = node.getChildNodes();
                for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                    Node nodeItem2 = childNodes2.item(i2);
                    if ("XPath".equals(nodeItem2.getNodeName()) || (str + ":XPath").equals(nodeItem2.getNodeName())) {
                        Node namedItem = nodeItem2.getAttributes().getNamedItem("Filter");
                        if (namedItem != null) {
                            String nodeValue = namedItem.getNodeValue();
                            if (nodeValue.equals(XPath2FilterContainer.SUBTRACT)) {
                                filter = XPathType.Filter.SUBTRACT;
                            } else if (nodeValue.equals(XPath2FilterContainer.INTERSECT)) {
                                filter = XPathType.Filter.INTERSECT;
                            } else if (nodeValue.equals(XPath2FilterContainer.UNION)) {
                                filter = XPathType.Filter.UNION;
                            } else {
                                throw new InvalidAlgorithmParameterException("El subtipo '" + nodeValue + "' de la transformacion XPATH2 no es valido");
                            }
                            xPathFilter2ParameterSpec = new XPathFilter2ParameterSpec(Collections.singletonList(new XPathType(nodeItem2.getTextContent(), filter)));
                            break;
                        }
                        throw new InvalidAlgorithmParameterException("No se ha declarado un subtipo para la transformacion XPATH2");
                    }
                }
                if (xPathFilter2ParameterSpec == null) {
                    throw new InvalidAlgorithmParameterException("No se ha indicado un cuerpo para una transformacion XPATH2 declarada");
                }
            }
        }
        return xPathFilter2ParameterSpec;
    }

    public static void checkIllegalParams(String str, String str2, boolean z, URI uri, String str3, boolean z2) {
        if (z2) {
            if (str2 != null) {
                LOGGER.warning("El parametro 'mode' se ignora en las firmas XAdES");
            }
            if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED)) {
                if (uri == null && !z) {
                    throw new UnsupportedOperationException("Las firmas XML Externally Detached necesitan la URI para referenciar a los datos firmados");
                }
            } else if (uri != null) {
                LOGGER.warning("Se ignorara el parametro 'uri' ya que este solo se utiliza con el formato XAdES Externally Detached");
            }
            if (!str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_DETACHED) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_ENVELOPING) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XADES_EXTERNALLY_DETACHED)) {
                throw new UnsupportedOperationException("El formato de firma '" + str + "' no esta soportado");
            }
            return;
        }
        if (!str2.equalsIgnoreCase(AOSignConstants.SIGN_MODE_IMPLICIT) && !str2.equalsIgnoreCase("explicit")) {
            throw new UnsupportedOperationException("El modo de firma '" + str2 + "' no esta soportado");
        }
        if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED) && str2.equalsIgnoreCase("explicit")) {
            throw new UnsupportedOperationException("No se puede realizar una firma enveloped sobre un contenido explicito");
        }
        if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED) && str2.equalsIgnoreCase(AOSignConstants.SIGN_MODE_IMPLICIT)) {
            throw new UnsupportedOperationException("No se puede realizar una firma XML Externally Detached con contenido Implicito");
        }
        if (str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED) && uri == null && str3 == null) {
            throw new UnsupportedOperationException("La firma XML Externally Detached necesita un Message Digest precalculado o una URI accesible");
        }
        if (!str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_DETACHED) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPED) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_ENVELOPING) && !str.equalsIgnoreCase(AOSignConstants.SIGN_FORMAT_XMLDSIG_EXTERNALLY_DETACHED)) {
            throw new UnsupportedOperationException("El formato de firma '" + str + "' no esta soportado");
        }
    }

    public static int countSubstring(String str, String str2) {
        int i = 0;
        int length = 0;
        while (length <= str.length() - str2.length()) {
            if (str2.charAt(0) == str.charAt(length) && str2.equals(str.substring(length, length + str2.length()))) {
                i++;
                length += str2.length() - 1;
            }
            length++;
        }
        return i;
    }

    public static byte[] writeXML(Node node, Map<String, String> map, String str, String str2) {
        String strName;
        OutputStreamWriter outputStreamWriter;
        Map<String, String> hashtable = map != null ? map : new Hashtable<>(0);
        if (hashtable.containsKey("encoding")) {
            strName = hashtable.get("encoding");
        } else {
            strName = StandardCharsets.UTF_8.name();
        }
        String str3 = strName;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, str3);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warning("La codificacion '" + str3 + "' no es valida, se usara la por defecto: " + e);
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
        }
        writeXMLwithXALAN(outputStreamWriter, node, str3);
        if (!AOFileUtils.isXML(byteArrayOutputStream.toByteArray())) {
            return byteArrayOutputStream.toByteArray();
        }
        try {
            return addStyleSheetHeader(new String(byteArrayOutputStream.toByteArray(), str3), str2, str).getBytes(str3);
        } catch (Exception e2) {
            LOGGER.warning("La codificacion '" + str3 + "' no es valida, se usara la por defecto del sistema: " + e2);
            return addStyleSheetHeader(new String(byteArrayOutputStream.toByteArray()), str2, str).getBytes();
        }
    }

    private static void writeXMLwithXALAN(Writer writer, Node node, String str) {
        Document ownerDocument = node.getOwnerDocument();
        if (ownerDocument.getFeature("Core", "3.0") != null && ownerDocument.getFeature("LS", "3.0") != null) {
            DOMImplementationLS dOMImplementationLS = (DOMImplementationLS) ownerDocument.getImplementation().getFeature("LS", "3.0");
            LSOutput lSOutputCreateLSOutput = dOMImplementationLS.createLSOutput();
            lSOutputCreateLSOutput.setCharacterStream(writer);
            if (str != null) {
                lSOutputCreateLSOutput.setEncoding(str);
            }
            LSSerializer lSSerializerCreateLSSerializer = dOMImplementationLS.createLSSerializer();
            lSSerializerCreateLSSerializer.getDomConfig().setParameter("namespaces", Boolean.FALSE);
            lSSerializerCreateLSSerializer.write(node, lSOutputCreateLSOutput);
            return;
        }
        throw new RuntimeException("La implementacion DOM cargada no permite la serializacion");
    }

    public static AOSimpleSignInfo getSimpleSignInfoNode(String str, Element element) {
        byte[] bArrDecode;
        Date date = null;
        if (str != null) {
            Element element2 = (Element) element.getElementsByTagNameNS(str, XAdESConstants.TAG_SIGNING_TIME).item(0);
            if (element2 != null) {
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(element2.getTextContent());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se ha podido recuperar la fecha de firma", (Throwable) e);
                }
            } else {
                LOGGER.info("No se ha encontrado la hora de firma de una firma");
            }
        }
        ArrayList arrayList = new ArrayList();
        NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
        for (int i = 0; i < elementsByTagNameNS.getLength(); i++) {
            arrayList.add(getCertificate(elementsByTagNameNS.item(i)));
        }
        AOSimpleSignInfo aOSimpleSignInfo = new AOSimpleSignInfo((X509Certificate[]) arrayList.toArray(new X509Certificate[arrayList.size()]), date);
        aOSimpleSignInfo.setSignAlgorithm(((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod").item(0)).getAttribute("Algorithm"));
        try {
            bArrDecode = Base64.decode(((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0)).getTextContent());
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "No se pudo extraer el PKCS#1 de una firma", (Throwable) e2);
            bArrDecode = null;
        }
        aOSimpleSignInfo.setPkcs1(bArrDecode);
        return aOSimpleSignInfo;
    }

    public static String getStringInfoNode(Element element) {
        return AOUtil.getCN(getCertificate(element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0)));
    }

    public static X509Certificate getCertificate(Node node) {
        return createCert(node.getTextContent().trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", ""));
    }

    public static String getCounterSignerReferenceId(Element element, NodeList nodeList) {
        String strSubstring = ((Element) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference").item(0)).getAttribute("URI").substring(1);
        String attribute = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element2 = (Element) nodeList.item(i);
            if (element2.getAttribute("Id").equals(strSubstring)) {
                attribute = ((Element) element2.getParentNode()).getAttribute("Id");
                break;
            }
        }
        return attribute;
    }

    public static X509Certificate createCert(String str) {
        if (str == null || str.isEmpty()) {
            LOGGER.severe("Se ha proporcionado una cadena nula o vacia, se devolvera null");
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(str));
            Throwable th = null;
            try {
                try {
                    X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID).generateCertificate(byteArrayInputStream);
                    try {
                        byteArrayInputStream.close();
                    } catch (Exception e) {
                        LOGGER.warning("Error cerrando el flujo de lectura del certificado: " + e);
                    }
                    if (byteArrayInputStream != null) {
                        if (0 != 0) {
                            try {
                                byteArrayInputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            byteArrayInputStream.close();
                        }
                    }
                    return x509Certificate;
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (byteArrayInputStream != null) {
                    if (th != null) {
                        try {
                            byteArrayInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        byteArrayInputStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e2) {
            LOGGER.severe("No se pudo decodificar el certificado en Base64, se devolvera null: " + e2);
            return null;
        }
    }

    public static XMLSignatureFactory getDOMFactory() {
        return XMLSignatureFactory.getInstance("DOM");
    }
}
