package es.gob.afirma.signers.ooxml.relprovider;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.core.misc.SecureXmlBuilder;
import es.gob.afirma.core.misc.SecureXmlTransformer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/relprovider/RelationshipTransformService.class */
public final class RelationshipTransformService extends TransformService {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
    private static final String SIGNATURE_SPEC_NS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String TRANSFORM_URI = "http://schemas.openxmlformats.org/package/2006/RelationshipTransform";
    private final List<String> sourceIds = new LinkedList();

    public void init(TransformParameterSpec transformParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(transformParameterSpec instanceof RelationshipTransformParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        Iterator<String> it = ((RelationshipTransformParameterSpec) transformParameterSpec).getSourceIds().iterator();
        while (it.hasNext()) {
            this.sourceIds.add(it.next());
        }
    }

    public void init(XMLStructure xMLStructure, XMLCryptoContext xMLCryptoContext) throws InvalidAlgorithmParameterException {
        Node node = ((DOMStructure) xMLStructure).getNode();
        try {
            toString(node);
            try {
                XPath xPathNewXPath = XPathFactory.newInstance().newXPath();
                xPathNewXPath.setNamespaceContext(new NamespaceContext() { // from class: es.gob.afirma.signers.ooxml.relprovider.RelationshipTransformService.1
                    @Override // javax.xml.namespace.NamespaceContext
                    public Iterator getPrefixes(String str) {
                        throw new UnsupportedOperationException();
                    }

                    @Override // javax.xml.namespace.NamespaceContext
                    public String getPrefix(String str) {
                        throw new UnsupportedOperationException();
                    }

                    @Override // javax.xml.namespace.NamespaceContext
                    public String getNamespaceURI(String str) {
                        if (str == null) {
                            throw new IllegalArgumentException("El prefijo no puede ser nulo");
                        }
                        if ("xml".equals(str)) {
                            return "http://www.w3.org/XML/1998/namespace";
                        }
                        if ("ds".equals(str)) {
                            return "http://www.w3.org/2000/09/xmldsig#";
                        }
                        if ("mdssi".equals(str)) {
                            return "http://schemas.openxmlformats.org/package/2006/digital-signature";
                        }
                        return "";
                    }
                });
                NodeList nodeList = (NodeList) xPathNewXPath.compile("mdssi:RelationshipReference/@SourceId").evaluate(node, XPathConstants.NODESET);
                if (0 == nodeList.getLength()) {
                    LOGGER.warning("no RelationshipReference/@SourceId parameters present");
                }
                for (int i = 0; i < nodeList.getLength(); i++) {
                    this.sourceIds.add(nodeList.item(i).getTextContent());
                }
            } catch (Exception e) {
                LOGGER.severe("Error en la transformacion XPath: " + e);
                throw new InvalidAlgorithmParameterException(e);
            }
        } catch (TransformerException e2) {
            throw new InvalidAlgorithmParameterException(e2);
        }
    }

    public void marshalParams(XMLStructure xMLStructure, XMLCryptoContext xMLCryptoContext) {
        Node node = ((DOMStructure) xMLStructure).getNode();
        Element element = (Element) node;
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", "http://schemas.openxmlformats.org/package/2006/digital-signature");
        Document ownerDocument = node.getOwnerDocument();
        for (String str : this.sourceIds) {
            Element elementCreateElementNS = ownerDocument.createElementNS("http://schemas.openxmlformats.org/package/2006/digital-signature", "mdssi:RelationshipReference");
            elementCreateElementNS.setAttribute("SourceId", str);
            element.appendChild(elementCreateElementNS);
        }
    }

    public AlgorithmParameterSpec getParameterSpec() {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.TransformException */
    public Data transform(Data data, XMLCryptoContext xMLCryptoContext) throws TransformException {
        try {
            InputStream octetStream = ((OctetStreamData) data).getOctetStream();
            Throwable th = null;
            try {
                try {
                    Document documentLoadDocument = loadDocument(octetStream);
                    octetStream.close();
                    if (octetStream != null) {
                        if (0 != 0) {
                            try {
                                octetStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            octetStream.close();
                        }
                    }
                    try {
                        toString(documentLoadDocument);
                        documentLoadDocument.createElement("ns").setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:tns", "http://schemas.openxmlformats.org/package/2006/relationships");
                        Element documentElement = documentLoadDocument.getDocumentElement();
                        NodeList childNodes = documentElement.getChildNodes();
                        int i = 0;
                        while (i < childNodes.getLength()) {
                            Node nodeItem = childNodes.item(i);
                            if (1 != nodeItem.getNodeType()) {
                                documentElement.removeChild(nodeItem);
                                i--;
                            } else {
                                Element element = (Element) nodeItem;
                                if (!this.sourceIds.contains(element.getAttribute("Id"))) {
                                    documentElement.removeChild(nodeItem);
                                    i--;
                                }
                                if (null == element.getAttributeNode("TargetMode")) {
                                    element.setAttribute("TargetMode", "Internal");
                                }
                            }
                            i++;
                        }
                        sortRelationshipElements(documentElement);
                        try {
                            return toOctetStreamData(documentLoadDocument);
                        } catch (TransformerException e) {
                            throw new TransformException(e.getMessage(), e);
                        }
                    } catch (TransformerException e2) {
                        throw new TransformException(e2.getMessage(), e2);
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (octetStream != null) {
                    if (th != null) {
                        try {
                            octetStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        octetStream.close();
                    }
                }
                throw th4;
            }
        } catch (Exception e3) {
            throw new TransformException(e3.getMessage(), e3);
        }
    }

    private static void sortRelationshipElements(Element element) {
        LinkedList linkedList = new LinkedList();
        NodeList elementsByTagName = element.getElementsByTagName("*");
        int length = elementsByTagName.getLength();
        for (int i = 0; i < length; i++) {
            Node nodeItem = elementsByTagName.item(0);
            linkedList.add((Element) nodeItem);
            element.removeChild(nodeItem);
        }
        Collections.sort(linkedList, new RelationshipComparator());
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            element.appendChild((Element) it.next());
        }
    }

    private static String toString(Node node) throws TransformerException {
        DOMSource dOMSource = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        Transformer secureTransformer = SecureXmlTransformer.getSecureTransformer();
        secureTransformer.setOutputProperty("omit-xml-declaration", XmlConsts.XML_SA_YES);
        secureTransformer.transform(dOMSource, streamResult);
        return stringWriter.getBuffer().toString();
    }

    private static OctetStreamData toOctetStreamData(Node node) throws TransformerException {
        DOMSource dOMSource = new DOMSource(node);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(byteArrayOutputStream);
        Transformer secureTransformer = SecureXmlTransformer.getSecureTransformer();
        secureTransformer.setOutputProperty("omit-xml-declaration", XmlConsts.XML_SA_YES);
        secureTransformer.transform(dOMSource, streamResult);
        return new OctetStreamData(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    private static Document loadDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        return SecureXmlBuilder.getSecureDocumentBuilder().parse(new InputSource(inputStream));
    }

    public Data transform(Data data, XMLCryptoContext xMLCryptoContext, OutputStream outputStream) {
        return null;
    }

    public boolean isFeatureSupported(String str) {
        return false;
    }
}
