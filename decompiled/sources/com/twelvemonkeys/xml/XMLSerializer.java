package com.twelvemonkeys.xml;

import com.aowagie.text.html.HtmlTags;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import com.twelvemonkeys.lang.StringUtil;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/xml/XMLSerializer.class */
public class XMLSerializer {
    private final OutputStream output;
    private final Charset encoding;
    private final SerializationContext context = new SerializationContext();

    public XMLSerializer(OutputStream outputStream, String str) {
        this.output = outputStream;
        this.encoding = Charset.forName(str);
    }

    public final XMLSerializer indentation(String str) {
        this.context.indent = str != null ? str : TlbBase.TAB;
        return this;
    }

    public final XMLSerializer stripComments(boolean z) {
        this.context.stripComments = z;
        return this;
    }

    public void serialize(Document document) {
        serialize(document, true);
    }

    public void serialize(Node node, boolean z) {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(this.output, this.encoding));
        if (z) {
            try {
                writeXMLDeclaration(printWriter);
            } finally {
                printWriter.flush();
            }
        }
        writeXML(printWriter, node, this.context.copy());
    }

    private void writeXMLDeclaration(PrintWriter printWriter) {
        printWriter.print("<?xml version=\"1.0\" encoding=\"");
        printWriter.print(this.encoding.name());
        printWriter.println("\"?>");
    }

    private void writeXML(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        writeNodeRecursive(printWriter, node, serializationContext);
    }

    private void writeNodeRecursive(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (node.getNodeType() != 3) {
            indentToLevel(printWriter, serializationContext);
        }
        switch (node.getNodeType()) {
            case 1:
                boolean z = serializationContext.preserveSpace;
                updatePreserveSpace(node, serializationContext);
                writeElement(printWriter, (Element) node, serializationContext);
                serializationContext.preserveSpace = z;
                return;
            case 2:
                throw new IllegalArgumentException("Malformed input Document: Attribute nodes should only occur inside Element nodes");
            case 3:
                writeText(printWriter, node, serializationContext);
                return;
            case 4:
                writeCData(printWriter, node);
                return;
            case 5:
            case 6:
            case 12:
            default:
                throw new InternalError("Lazy programmer never implemented serialization of " + node.getClass());
            case 7:
                writeProcessingInstruction(printWriter, (ProcessingInstruction) node);
                return;
            case 8:
                writeComment(printWriter, node, serializationContext);
                return;
            case 9:
            case 11:
                writeDocument(printWriter, node, serializationContext);
                return;
            case 10:
                writeDoctype(printWriter, (DocumentType) node);
                return;
        }
    }

    private void writeProcessingInstruction(PrintWriter printWriter, ProcessingInstruction processingInstruction) {
        printWriter.print("\n<?");
        printWriter.print(processingInstruction.getTarget());
        String data = processingInstruction.getData();
        if (data != null) {
            printWriter.print(" ");
            printWriter.print(data);
        }
        printWriter.println("?>");
    }

    private void writeText(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        String nodeValue = node.getNodeValue();
        if (serializationContext.preserveSpace) {
            printWriter.print(maybeEscapeElementValue(nodeValue));
        } else if (!StringUtil.isEmpty(nodeValue)) {
            String strMaybeEscapeElementValue = maybeEscapeElementValue(nodeValue.trim());
            indentToLevel(printWriter, serializationContext);
            printWriter.println(strMaybeEscapeElementValue);
        }
    }

    private void writeCData(PrintWriter printWriter, Node node) {
        printWriter.print("<![CDATA[");
        printWriter.print(validateCDataValue(node.getNodeValue()));
        printWriter.println("]]>");
    }

    private static void updatePreserveSpace(Node node, SerializationContext serializationContext) {
        Node namedItem;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && (namedItem = attributes.getNamedItem("xml:space")) != null) {
            if ("preserve".equals(namedItem.getNodeValue())) {
                serializationContext.preserveSpace = true;
            } else if (PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(namedItem.getNodeValue())) {
                serializationContext.preserveSpace = false;
            }
        }
    }

    private static void indentToLevel(PrintWriter printWriter, SerializationContext serializationContext) {
        for (int i = 0; i < serializationContext.level; i++) {
            printWriter.print(serializationContext.indent);
        }
    }

    private void writeComment(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (serializationContext.stripComments) {
            return;
        }
        String nodeValue = node.getNodeValue();
        validateCommentValue(nodeValue);
        if (nodeValue.startsWith(" ")) {
            printWriter.print("<!--");
        } else {
            printWriter.print("<!-- ");
        }
        printWriter.print(nodeValue);
        if (nodeValue.endsWith(" ")) {
            printWriter.println("-->");
        } else {
            printWriter.println(" -->");
        }
    }

    static String maybeEscapeElementValue(String str) {
        int iNeedsEscapeElement = needsEscapeElement(str);
        if (iNeedsEscapeElement < 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.substring(0, iNeedsEscapeElement));
        sb.ensureCapacity(str.length() + 30);
        int iAppendAndEscape = iNeedsEscapeElement;
        for (int i = iAppendAndEscape; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '&':
                    iAppendAndEscape = appendAndEscape(str, iAppendAndEscape, i, sb, "&amp;");
                    break;
                case '<':
                    iAppendAndEscape = appendAndEscape(str, iAppendAndEscape, i, sb, "&lt;");
                    break;
                case '>':
                    iAppendAndEscape = appendAndEscape(str, iAppendAndEscape, i, sb, "&gt;");
                    break;
            }
        }
        sb.append(str.substring(iAppendAndEscape));
        return sb.toString();
    }

    private static int appendAndEscape(String str, int i, int i2, StringBuilder sb, String str2) {
        sb.append((CharSequence) str, i, i2);
        sb.append(str2);
        return i2 + 1;
    }

    private static int needsEscapeElement(String str) {
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '&':
                case '<':
                case '>':
                    return i;
                default:
                    break;
            }
        }
        return -1;
    }

    private static String maybeEscapeAttributeValue(String str) {
        int iNeedsEscapeAttribute = needsEscapeAttribute(str);
        if (iNeedsEscapeAttribute < 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.substring(0, iNeedsEscapeAttribute));
        sb.ensureCapacity(str.length() + 16);
        int iAppendAndEscape = iNeedsEscapeAttribute;
        for (int i = iAppendAndEscape; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '\"':
                    iAppendAndEscape = appendAndEscape(str, iAppendAndEscape, i, sb, "&quot;");
                    break;
                case '&':
                    iAppendAndEscape = appendAndEscape(str, iAppendAndEscape, i, sb, "&amp;");
                    break;
            }
        }
        sb.append(str.substring(iAppendAndEscape));
        return sb.toString();
    }

    private static int needsEscapeAttribute(String str) {
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '\"':
                case '&':
                    return i;
                default:
                    break;
            }
        }
        return -1;
    }

    private static String validateCDataValue(String str) {
        if (str.contains("]]>")) {
            throw new IllegalArgumentException("Malformed input document: CDATA block may not contain the string ']]>'");
        }
        return str;
    }

    private static String validateCommentValue(String str) {
        if (str.contains("--")) {
            throw new IllegalArgumentException("Malformed input document: Comment may not contain the string '--'");
        }
        return str;
    }

    private void writeDocument(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (node.hasChildNodes()) {
            Node firstChild = node.getFirstChild();
            while (true) {
                Node node2 = firstChild;
                if (node2 != null) {
                    writeNodeRecursive(printWriter, node2, serializationContext);
                    firstChild = node2.getNextSibling();
                } else {
                    return;
                }
            }
        }
    }

    private void writeElement(PrintWriter printWriter, Element element, SerializationContext serializationContext) {
        printWriter.print("<");
        printWriter.print(element.getTagName());
        String namespaceURI = element.getNamespaceURI();
        if (namespaceURI != null && !namespaceURI.equals(serializationContext.defaultNamespace)) {
            String prefix = element.getPrefix();
            if (prefix == null) {
                serializationContext.defaultNamespace = namespaceURI;
                printWriter.print(" xmlns");
            } else {
                printWriter.print(" xmlns:");
                printWriter.print(prefix);
            }
            printWriter.print("=\"");
            printWriter.print(namespaceURI);
            printWriter.print(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        if (element.hasAttributes()) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attr = (Attr) attributes.item(i);
                String name = attr.getName();
                if (!name.startsWith(CanonicalizerBase.XMLNS) || (name.length() != 5 && name.charAt(5) != ':')) {
                    printWriter.print(" ");
                    printWriter.print(name);
                    printWriter.print("=\"");
                    printWriter.print(maybeEscapeAttributeValue(attr.getValue()));
                    printWriter.print(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                }
            }
        }
        if (element.hasChildNodes()) {
            printWriter.print(">");
            if (!serializationContext.preserveSpace) {
                printWriter.println();
            }
            Node firstChild = element.getFirstChild();
            while (true) {
                Node node = firstChild;
                if (node == null) {
                    break;
                }
                writeNodeRecursive(printWriter, node, serializationContext.push());
                firstChild = node.getNextSibling();
            }
            if (!serializationContext.preserveSpace) {
                indentToLevel(printWriter, serializationContext);
            }
            printWriter.print("</");
            printWriter.print(element.getTagName());
            printWriter.println(">");
            return;
        }
        if (element.getNodeValue() != null) {
            printWriter.print(">");
            printWriter.print(element.getNodeValue());
            printWriter.print("</");
            printWriter.print(element.getTagName());
            printWriter.println(">");
            return;
        }
        printWriter.println("/>");
    }

    private void writeDoctype(PrintWriter printWriter, DocumentType documentType) {
        if (documentType != null) {
            printWriter.print("<!DOCTYPE ");
            printWriter.print(documentType.getName());
            String publicId = documentType.getPublicId();
            if (!StringUtil.isEmpty(publicId)) {
                printWriter.print(" PUBLIC ");
                printWriter.print(publicId);
            }
            String systemId = documentType.getSystemId();
            if (!StringUtil.isEmpty(systemId)) {
                if (StringUtil.isEmpty(publicId)) {
                    printWriter.print(" SYSTEM \"");
                } else {
                    printWriter.print(" \"");
                }
                printWriter.print(systemId);
                printWriter.print(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            }
            String internalSubset = documentType.getInternalSubset();
            if (!StringUtil.isEmpty(internalSubset)) {
                printWriter.print(" [ ");
                printWriter.print(internalSubset);
                printWriter.print(" ]");
            }
            printWriter.println(">");
        }
    }

    public static void main(String[] strArr) throws SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        documentBuilderFactoryNewInstance.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilderNewDocumentBuilder = documentBuilderFactoryNewInstance.newDocumentBuilder();
            DOMImplementation dOMImplementation = documentBuilderNewDocumentBuilder.getDOMImplementation();
            Document documentCreateDocument = dOMImplementation.createDocument("http://www.twelvemonkeys.com/xml/test", "test", dOMImplementation.createDocumentType("test", null, null));
            Element documentElement = documentCreateDocument.getDocumentElement();
            documentCreateDocument.insertBefore(documentCreateDocument.createComment(new Date().toString()), documentElement);
            Element elementCreateElement = documentCreateDocument.createElement("sub");
            documentElement.appendChild(elementCreateElement);
            Element elementCreateElementNS = documentCreateDocument.createElementNS("http://more.com/1999/namespace", "more:more");
            elementCreateElementNS.setAttribute("foo", "test");
            elementCreateElementNS.setAttribute("bar", "'really' \"legal\" & ok");
            elementCreateElement.appendChild(elementCreateElementNS);
            elementCreateElementNS.appendChild(documentCreateDocument.createTextNode("Simply some text."));
            elementCreateElementNS.appendChild(documentCreateDocument.createCDATASection("&something escaped;"));
            elementCreateElementNS.appendChild(documentCreateDocument.createTextNode("More & <more>!"));
            elementCreateElementNS.appendChild(documentCreateDocument.createTextNode("\"<<'&'>>\""));
            elementCreateElement.appendChild(documentCreateDocument.createElement("another"));
            Element elementCreateElement2 = documentCreateDocument.createElement("yet-another");
            elementCreateElement2.setAttribute("this-one", "with-params");
            elementCreateElement.appendChild(elementCreateElement2);
            Element elementCreateElementNS2 = documentCreateDocument.createElementNS("http://www.twelvemonkeys.com/xml/test", HtmlTags.PRE);
            elementCreateElementNS2.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
            elementCreateElementNS2.appendChild(documentCreateDocument.createTextNode(" \t \n\r some text & white ' '   \n   "));
            elementCreateElement.appendChild(elementCreateElementNS2);
            Element elementCreateElementNS3 = documentCreateDocument.createElementNS("http://www.twelvemonkeys.com/xml/test", "tight");
            elementCreateElementNS3.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
            elementCreateElementNS3.appendChild(documentCreateDocument.createTextNode("no-space-around-me"));
            elementCreateElement.appendChild(elementCreateElementNS3);
            System.out.println("XMLSerializer:");
            XMLSerializer xMLSerializer = new XMLSerializer(System.out, "UTF-8");
            xMLSerializer.serialize(documentCreateDocument);
            System.out.println();
            System.out.println("DOMSerializer:");
            DOMSerializer dOMSerializer = new DOMSerializer(System.out, "UTF-8");
            dOMSerializer.setPrettyPrint(true);
            dOMSerializer.serialize(documentCreateDocument);
            System.out.println();
            System.out.println("\n");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new XMLSerializer(byteArrayOutputStream, "UTF-8").serialize(documentCreateDocument);
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            new DOMSerializer(byteArrayOutputStream2, "UTF-8").serialize(documentCreateDocument);
            Document document = documentBuilderNewDocumentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            System.out.println("XMLSerializer reparsed XMLSerializer:");
            xMLSerializer.serialize(document);
            System.out.println();
            System.out.println("DOMSerializer reparsed XMLSerializer:");
            dOMSerializer.serialize(document);
            System.out.println();
            Document document2 = documentBuilderNewDocumentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream2.toByteArray()));
            System.out.println("XMLSerializer reparsed DOMSerializer:");
            xMLSerializer.serialize(document2);
            System.out.println();
            System.out.println("DOMSerializer reparsed DOMSerializer:");
            dOMSerializer.serialize(document2);
            System.out.println();
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/xml/XMLSerializer$SerializationContext.class */
    static class SerializationContext implements Cloneable {
        String indent = TlbBase.TAB;
        int level = 0;
        boolean preserveSpace = false;
        boolean stripComments = false;
        String defaultNamespace;

        SerializationContext() {
        }

        public SerializationContext copy() {
            try {
                return (SerializationContext) clone();
            } catch (CloneNotSupportedException e) {
                throw new Error(e);
            }
        }

        public SerializationContext push() {
            SerializationContext serializationContextCopy = copy();
            serializationContextCopy.level++;
            return serializationContextCopy;
        }
    }
}
