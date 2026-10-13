package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.AOException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/ContentTypeManager.class */
final class ContentTypeManager {
    private final Map<String, String> defaultContentTypes = new HashMap();
    private final Map<String, String> overrideContentTypes = new HashMap();
    private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();
    private static final String SLASH = "/";

    static {
        DOC_FACTORY.setNamespaceAware(true);
    }

    ContentTypeManager(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        NodeList childNodes = loadDocument(inputStream).getChildNodes();
        if (childNodes.getLength() > 0) {
            NodeList childNodes2 = childNodes.item(0).getChildNodes();
            for (int i = 0; i < childNodes2.getLength(); i++) {
                try {
                    Node nodeItem = childNodes2.item(i);
                    if (nodeItem.getNodeType() == 1) {
                        if ("Default".equals(nodeItem.getNodeName())) {
                            NamedNodeMap attributes = nodeItem.getAttributes();
                            this.defaultContentTypes.put(getAttributeValue(attributes, "Extension"), getAttributeValue(attributes, "ContentType"));
                        } else if ("Override".equals(nodeItem.getNodeName())) {
                            NamedNodeMap attributes2 = nodeItem.getAttributes();
                            this.overrideContentTypes.put(getAttributeValue(attributes2, "PartName"), getAttributeValue(attributes2, "ContentType"));
                        }
                    }
                } catch (AOException e) {
                    Logger.getLogger("es.gob.afirma").warning("Se encontro un nodo en el [Content_Types].xml no valido: " + e);
                }
            }
        }
    }

    private static Document loadDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        return getNewDocumentBuilder().parse(inputStream);
    }

    private static DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException {
        return DOC_FACTORY.newDocumentBuilder();
    }

    private static String getAttributeValue(NamedNodeMap namedNodeMap, String str) throws AOException {
        if (namedNodeMap == null) {
            throw new AOException("El nodo no contenia atributos");
        }
        Node namedItem = namedNodeMap.getNamedItem(str);
        if (namedItem == null) {
            throw new AOException("No existe el atributo: " + str);
        }
        return namedItem.getNodeValue();
    }

    String getContentType(String str) {
        String strConcat;
        if (!str.startsWith(SLASH)) {
            strConcat = SLASH.concat(str);
        } else {
            strConcat = str;
        }
        if (this.overrideContentTypes.containsKey(strConcat)) {
            return this.overrideContentTypes.get(strConcat);
        }
        String extension = getExtension(strConcat);
        if (extension != null && this.defaultContentTypes.containsKey(extension)) {
            return this.defaultContentTypes.get(extension);
        }
        return null;
    }

    private static String getExtension(String str) {
        int iLastIndexOf = str.lastIndexOf(46);
        if (iLastIndexOf == -1 || iLastIndexOf == str.length() - 1) {
            return null;
        }
        return str.substring(iLastIndexOf + 1);
    }
}
