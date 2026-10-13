package com.aowagie.text.xml.xmp;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.xml.XMLConstants;
import com.aowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpReader.class */
public class XmpReader {
    private Document domDocument;
    private static DocumentBuilderFactory SECURE_FACTORY = null;
    private static final Logger LOGGER = Logger.getLogger(XmpReader.class.getName());

    public XmpReader(byte[] bytes) throws SAXException, IOException {
        try {
            DocumentBuilderFactory fact = getSecureDocumentFactory();
            fact.setNamespaceAware(true);
            DocumentBuilder db = fact.newDocumentBuilder();
            db.setEntityResolver(new SafeEmptyEntityResolver());
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            this.domDocument = db.parse(bais);
        } catch (ParserConfigurationException e) {
            throw new ExceptionConverter(e);
        }
    }

    private static synchronized DocumentBuilderFactory getSecureDocumentFactory() {
        if (SECURE_FACTORY != null) {
            return SECURE_FACTORY;
        }
        SECURE_FACTORY = DocumentBuilderFactory.newInstance();
        try {
            SECURE_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo configurar el procesador seguro: " + e);
        }
        String[] securityProperties = {XMLConstants.ACCESS_EXTERNAL_DTD, XMLConstants.ACCESS_EXTERNAL_SCHEMA, XMLConstants.ACCESS_EXTERNAL_STYLESHEET};
        for (String securityProperty : securityProperties) {
            try {
                SECURE_FACTORY.setAttribute(securityProperty, "");
            } catch (Exception e2) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "No se ha podido establecer una propiedad de seguridad '" + securityProperty + "' en la factoria XML: " + e2);
                }
            }
        }
        SECURE_FACTORY.setValidating(false);
        return SECURE_FACTORY;
    }

    public boolean replace(String parent, String namespaceURI, String localName, String value) {
        Node targetAttr;
        NodeList nodes = this.domDocument.getElementsByTagNameNS(namespaceURI, localName);
        if (nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                setNodeText(this.domDocument, node, value);
            }
            return true;
        }
        NodeList parentNodes = parent == null ? null : this.domDocument.getElementsByTagName(parent);
        if (parentNodes == null || parentNodes.getLength() == 0) {
            return false;
        }
        boolean replaced = false;
        for (int i2 = 0; i2 < parentNodes.getLength(); i2++) {
            Node node2 = parentNodes.item(i2);
            NamedNodeMap attrMap = node2.getAttributes();
            if (attrMap != null && (targetAttr = attrMap.getNamedItemNS(namespaceURI, localName)) != null) {
                targetAttr.setNodeValue(value);
                replaced = true;
            }
        }
        return replaced;
    }

    public boolean add(String parent, String namespaceURI, String localName, String value) {
        NodeList nodes = this.domDocument.getElementsByTagName(parent);
        if (nodes.getLength() == 0) {
            return false;
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node pNode = nodes.item(i);
            NamedNodeMap attrs = pNode.getAttributes();
            for (int j = 0; j < attrs.getLength(); j++) {
                if (namespaceURI.equals(attrs.item(j).getNodeValue())) {
                    Node node = this.domDocument.createElement(localName);
                    node.appendChild(this.domDocument.createTextNode(value));
                    pNode.appendChild(node);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean setNodeText(Document domDocument, Node n, String value) {
        if (n == null) {
            return false;
        }
        while (true) {
            Node nc = n.getFirstChild();
            if (nc != null) {
                n.removeChild(nc);
            } else {
                n.appendChild(domDocument.createTextNode(value));
                return true;
            }
        }
    }

    public byte[] serializeDoc() throws IOException {
        XmlDomWriter xw = new XmlDomWriter();
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        fout.write("<?xpacket begin=\"\ufeff\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n".getBytes(StandardCharsets.UTF_8));
        fout.flush();
        NodeList xmpmeta = this.domDocument.getElementsByTagName("x:xmpmeta");
        xw.write(xmpmeta.item(0));
        fout.flush();
        for (int i = 0; i < 20; i++) {
            fout.write("                                                                                                   \n".getBytes());
        }
        fout.write("<?xpacket end=\"w\"?>".getBytes());
        fout.close();
        return fout.toByteArray();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpReader$SafeEmptyEntityResolver.class */
    private static class SafeEmptyEntityResolver implements EntityResolver {
        private SafeEmptyEntityResolver() {
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }
}
