package org.apache.pdfbox.util;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/XMLUtil.class */
public final class XMLUtil {
    private XMLUtil() {
    }

    public static Document parse(InputStream is) throws IOException {
        return parse(is, false);
    }

    public static Document parse(InputStream is, boolean nsAware) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            builderFactory.setXIncludeAware(false);
            builderFactory.setExpandEntityReferences(false);
            builderFactory.setNamespaceAware(nsAware);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(is);
        } catch (FactoryConfigurationError e) {
            throw new IOException(e.getMessage(), e);
        } catch (ParserConfigurationException e2) {
            throw new IOException(e2.getMessage(), e2);
        } catch (SAXException e3) {
            throw new IOException(e3.getMessage(), e3);
        }
    }

    public static String getNodeValue(Element node) {
        StringBuilder sb = new StringBuilder();
        NodeList children = node.getChildNodes();
        int numNodes = children.getLength();
        for (int i = 0; i < numNodes; i++) {
            Node next = children.item(i);
            if (next instanceof Text) {
                sb.append(next.getNodeValue());
            }
        }
        return sb.toString();
    }
}
