package es.uji.crypto.xades.jxades.util;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/util/XMLUtils.class */
public final class XMLUtils {
    private static Charset charset = StandardCharsets.UTF_8;

    public static String getAttributeByName(Node node, String name) {
        Node attribute;
        if (node == null || (attribute = node.getAttributes().getNamedItem(name)) == null) {
            return null;
        }
        return attribute.getNodeValue().trim();
    }

    public static String getElementTextValueDeprecated(Element parentNode) {
        Text text = getElementTextNode(parentNode);
        if (text != null) {
            return text.getData();
        }
        return null;
    }

    public static void setElementTextValue(Element e, String data) {
        Text txt = getElementTextNode(e);
        if (txt != null) {
            txt.setData(data);
        } else {
            e.appendChild(e.getOwnerDocument().createTextNode(data));
        }
    }

    public static void setElementCDataValue(Element e, String data) {
        CDATASection txt = getElementCDataNode(e);
        if (txt != null) {
            txt.setData(data);
        } else {
            e.appendChild(e.getOwnerDocument().createCDATASection(data));
        }
    }

    public static String getElementCDataValue(Element e) {
        CDATASection text = getElementCDataNode(e);
        if (text != null) {
            return text.getData().trim();
        }
        return null;
    }

    public static CDATASection getElementCDataNode(Element element) {
        return (CDATASection) getChildNodeByType(element, (short) 4);
    }

    public static Text getElementTextNode(Element element) {
        return (Text) getChildNodeByType(element, (short) 3);
    }

    private static Node getChildNodeByType(Element element, short nodeType) {
        NodeList nodes;
        String data;
        if (element == null || (nodes = element.getChildNodes()) == null || nodes.getLength() < 1) {
            return null;
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            short type = node.getNodeType();
            if (type == nodeType && ((type != 3 && type != 4) || ((data = ((Text) node).getData()) != null && data.trim().length() >= 1))) {
                return node;
            }
        }
        return null;
    }

    public static void writeXML(File file, Node node) throws FileNotFoundException {
        writeXML(new FileOutputStream(file), node);
    }

    public static void writeXML(OutputStream outStream, Node node) {
        writeXML((Writer) new BufferedWriter(new OutputStreamWriter(outStream, charset)), node, true);
    }

    public static void writeXML(OutputStream outStream, Node node, boolean indent) {
        writeXML(new BufferedWriter(new OutputStreamWriter(outStream, charset)), node, indent);
    }

    public static void writeXML(Writer writer, Node node, boolean indent) {
        Document document = null;
        if (node instanceof Element) {
            document = node.getOwnerDocument();
        } else if (node instanceof Document) {
            document = (Document) node;
        }
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        serializer.getDomConfig().setParameter("namespaces", false);
        DOMOutputImpl output = new DOMOutputImpl();
        output.setCharacterStream(writer);
        serializer.write(node, output);
    }

    public static void writeXML(Writer writer, Document document, String doctypeSystem, String doctypePublic) {
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        DOMOutputImpl output = new DOMOutputImpl();
        output.setCharacterStream(writer);
        serializer.write(document.getDocumentElement(), output);
    }

    public static Element getChildElementByChain(Element element, String[] chain, boolean create) {
        if (chain == null) {
            return null;
        }
        Element e = element;
        for (String element2 : chain) {
            if (e == null) {
                return null;
            }
            e = getChildElementByTagName(e, element2);
        }
        return e;
    }

    public static Element createLastPathComponent(Document doc, String[] path) {
        Element parent = (Element) doc.getFirstChild();
        if (path == null || parent == null || doc == null) {
            throw new IllegalArgumentException("Document parent and path must not be null");
        }
        Element e = parent;
        for (String element : path) {
            Element newEl = getChildElementByTagName(e, element);
            if (newEl == null) {
                newEl = doc.createElement(element);
                e.appendChild(newEl);
            }
            e = newEl;
        }
        return e;
    }

    public static Element getChildElementByTagNameNS(Element parent, String tagName, String nsName) {
        String ns;
        NodeList nl = parent.getChildNodes();
        int size = nl.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == 1 && tagName.equals(node.getLocalName()) && (ns = node.getNamespaceURI()) != null && ns.equals(nsName)) {
                return (Element) node;
            }
        }
        return null;
    }

    public static Element getChildElementByTagName(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return null;
        }
        NodeList nodes = parent.getChildNodes();
        int len = nodes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == 1 && ((Element) node).getNodeName().equals(tagName)) {
                return (Element) node;
            }
        }
        return null;
    }

    public static List<Element> getChildElementsByTagNameNS(Element parent, String tagName, String nsName) {
        String ns;
        if (parent == null || tagName == null) {
            return Collections.emptyList();
        }
        NodeList nl = parent.getChildNodes();
        int size = nl.getLength();
        ArrayList<Element> childElements = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == 1 && tagName.equals(node.getLocalName()) && (ns = node.getNamespaceURI()) != null && ns.equals(nsName)) {
                childElements.add((Element) node);
            }
        }
        return childElements;
    }

    public static List<Element> getChildElementsByTagName(Element parent, String tagName) {
        if (parent == null || tagName == null) {
            return Collections.emptyList();
        }
        NodeList nodes = parent.getChildNodes();
        int len = nodes.getLength();
        ArrayList<Element> childElements = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == 1 && ((Element) node).getNodeName().equals(tagName)) {
                childElements.add((Element) node);
            }
        }
        return childElements;
    }

    public static void printChildElements(Element parent, PrintStream out, boolean deep, String prefix) {
        out.print(prefix + "<" + parent.getNodeName());
        NamedNodeMap attrs = parent.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node node = attrs.item(i);
            out.print(" " + node.getNodeName() + "=\"" + node.getNodeValue() + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        out.println(">");
        String data = parent.getNodeValue();
        if (data != null && data.trim().length() > 0) {
            out.println(prefix + TlbBase.TAB + data);
        }
        String data2 = getElementCDataValue(parent);
        if (data2 != null && data2.trim().length() > 0) {
            out.println(prefix + "\t<![CDATA[" + data2 + "]]>");
        }
        NodeList nodes = parent.getChildNodes();
        for (int i2 = 0; i2 < nodes.getLength(); i2++) {
            Node node2 = nodes.item(i2);
            if (node2.getNodeType() == 1) {
                if (deep) {
                    printChildElements((Element) node2, out, deep, prefix + TlbBase.TAB);
                } else {
                    out.println(prefix + node2.getNodeName());
                }
            }
        }
        out.println(prefix + "</" + parent.getNodeName() + ">");
    }
}
