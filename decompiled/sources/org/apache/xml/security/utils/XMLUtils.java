package org.apache.xml.security.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.parser.XMLParser;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.parser.XMLParserImpl;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/XMLUtils.class */
public final class XMLUtils {
    private static boolean ignoreLineBreaks = ((Boolean) AccessController.doPrivileged(() -> {
        return Boolean.valueOf(Boolean.getBoolean("org.apache.xml.security.ignoreLineBreaks"));
    })).booleanValue();
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLUtils.class);
    private static XMLParser xmlParserImpl = (XMLParser) AccessController.doPrivileged(() -> {
        String xmlParserClass = System.getProperty("org.apache.xml.security.XMLParser");
        if (xmlParserClass != null) {
            try {
                return (XMLParser) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(xmlParserClass, XMLUtils.class));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LOG.error("Error instantiating XMLParser. Falling back to XMLParserImpl", (Throwable) e);
            }
        }
        return new XMLParserImpl();
    });
    private static volatile String dsPrefix = "ds";
    private static volatile String ds11Prefix = XMLSecurityConstants.PREFIX_DSIG11;
    private static volatile String xencPrefix = XMLSecurityConstants.PREFIX_XENC;
    private static volatile String xenc11Prefix = XMLSecurityConstants.PREFIX_XENC11;

    private XMLUtils() {
    }

    public static void setDsPrefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        dsPrefix = prefix;
    }

    public static void setDs11Prefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        ds11Prefix = prefix;
    }

    public static void setXencPrefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        xencPrefix = prefix;
    }

    public static void setXenc11Prefix(String prefix) {
        JavaUtils.checkRegisterPermission();
        xenc11Prefix = prefix;
    }

    public static Element getNextElement(Node el) {
        Node node;
        Node nextSibling = el;
        while (true) {
            node = nextSibling;
            if (node == null || node.getNodeType() == 1) {
                break;
            }
            nextSibling = node.getNextSibling();
        }
        return (Element) node;
    }

    public static void getSet(Node rootNode, Set<Node> result, Node exclude, boolean comments) {
        if (exclude != null && isDescendantOrSelf(exclude, rootNode)) {
            return;
        }
        getSetRec(rootNode, result, exclude, comments);
    }

    private static void getSetRec(Node rootNode, Set<Node> result, Node exclude, boolean comments) {
        if (rootNode == exclude) {
            return;
        }
        switch (rootNode.getNodeType()) {
            case 1:
                result.add(rootNode);
                Element el = (Element) rootNode;
                if (el.hasAttributes()) {
                    NamedNodeMap nl = el.getAttributes();
                    int length = nl.getLength();
                    for (int i = 0; i < length; i++) {
                        result.add(nl.item(i));
                    }
                }
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                result.add(rootNode);
                return;
            case 8:
                if (comments) {
                    result.add(rootNode);
                    return;
                }
                return;
            case 9:
                break;
            case 10:
                return;
        }
        Node firstChild = rootNode.getFirstChild();
        while (true) {
            Node r = firstChild;
            if (r != null) {
                if (r.getNodeType() == 3) {
                    result.add(r);
                    while (r != null && r.getNodeType() == 3) {
                        r = r.getNextSibling();
                    }
                    if (r == null) {
                        return;
                    }
                }
                getSetRec(r, result, exclude, comments);
                firstChild = r.getNextSibling();
            } else {
                return;
            }
        }
    }

    public static void outputDOM(Node contextNode, File outputFile) throws Exception {
        OutputStream os = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(), new OpenOption[0]), 8192);
        Throwable th = null;
        try {
            try {
                outputDOM(contextNode, os, false);
                $closeResource(null, os);
            } catch (Throwable th2) {
                th = th2;
                throw th2;
            }
        } catch (Throwable th3) {
            $closeResource(th, os);
            throw th3;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    public static void outputDOM(Node contextNode, OutputStream os) {
        outputDOM(contextNode, os, false);
    }

    public static void outputDOM(Node contextNode, OutputStream os, boolean addPreamble) {
        if (addPreamble) {
            try {
                os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
            } catch (IOException | CanonicalizationException | InvalidCanonicalizerException ex) {
                LOG.error(ex.getMessage(), (Throwable) ex);
                return;
            }
        }
        Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical").canonicalizeSubtree(contextNode, os);
    }

    public static void outputDOMc14nWithComments(Node contextNode, OutputStream os) {
        try {
            Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments").canonicalizeSubtree(contextNode, os);
        } catch (CanonicalizationException | InvalidCanonicalizerException ex) {
            LOG.error(ex.getMessage(), (Throwable) ex);
        }
    }

    public static String getFullTextChildrenFromNode(Node node) {
        StringBuilder sb = new StringBuilder();
        Node firstChild = node.getFirstChild();
        while (true) {
            Node child = firstChild;
            if (child != null) {
                if (child.getNodeType() == 3) {
                    sb.append(((Text) child).getData());
                }
                firstChild = child.getNextSibling();
            } else {
                return sb.toString();
            }
        }
    }

    public static Element createElementInSignatureSpace(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (dsPrefix == null || dsPrefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", dsPrefix + ":" + elementName);
    }

    public static Element createElementInSignature11Space(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (ds11Prefix == null || ds11Prefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2009/xmldsig11#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2009/xmldsig11#", ds11Prefix + ":" + elementName);
    }

    public static Element createElementInEncryptionSpace(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (xencPrefix == null || xencPrefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2001/04/xmlenc#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2001/04/xmlenc#", xencPrefix + ":" + elementName);
    }

    public static Element createElementInEncryption11Space(Document doc, String elementName) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        if (xenc11Prefix == null || xenc11Prefix.length() == 0) {
            return doc.createElementNS("http://www.w3.org/2009/xmlenc11#", elementName);
        }
        return doc.createElementNS("http://www.w3.org/2009/xmlenc11#", xenc11Prefix + ":" + elementName);
    }

    public static boolean elementIsInSignatureSpace(Element element, String localName) {
        return element != null && "http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInSignature11Space(Element element, String localName) {
        return element != null && "http://www.w3.org/2009/xmldsig11#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInEncryptionSpace(Element element, String localName) {
        return element != null && "http://www.w3.org/2001/04/xmlenc#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static boolean elementIsInEncryption11Space(Element element, String localName) {
        return element != null && "http://www.w3.org/2009/xmlenc11#".equals(element.getNamespaceURI()) && element.getLocalName().equals(localName);
    }

    public static Document getOwnerDocument(Node node) {
        if (node.getNodeType() == 9) {
            return (Document) node;
        }
        try {
            return node.getOwnerDocument();
        } catch (NullPointerException npe) {
            throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + npe.getMessage() + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
    }

    public static Document getOwnerDocument(Set<Node> xpathNodeSet) {
        NullPointerException npe = null;
        for (Node node : xpathNodeSet) {
            int nodeType = node.getNodeType();
            if (nodeType == 9) {
                return (Document) node;
            }
            try {
                if (nodeType == 2) {
                    return ((Attr) node).getOwnerElement().getOwnerDocument();
                }
                return node.getOwnerDocument();
            } catch (NullPointerException e) {
                npe = e;
            }
        }
        throw new NullPointerException(I18n.translate("endorsed.jdk1.4.0") + " Original message was \"" + (npe == null ? "" : npe.getMessage()) + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
    }

    public static void addReturnToElement(Element e) {
        if (!ignoreLineBreaks) {
            Document doc = e.getOwnerDocument();
            e.appendChild(doc.createTextNode("\n"));
        }
    }

    public static void addReturnToElement(Document doc, HelperNodeList nl) {
        if (!ignoreLineBreaks) {
            nl.appendChild(doc.createTextNode("\n"));
        }
    }

    public static void addReturnBeforeChild(Element e, Node child) {
        if (!ignoreLineBreaks) {
            Document doc = e.getOwnerDocument();
            e.insertBefore(doc.createTextNode("\n"), child);
        }
    }

    public static String encodeToString(byte[] bytes) {
        if (ignoreLineBreaks) {
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return java.util.Base64.getMimeEncoder().encodeToString(bytes);
    }

    public static byte[] decode(String encodedString) {
        return java.util.Base64.getMimeDecoder().decode(encodedString);
    }

    public static byte[] decode(byte[] encodedBytes) {
        return java.util.Base64.getMimeDecoder().decode(encodedBytes);
    }

    public static boolean isIgnoreLineBreaks() {
        return ignoreLineBreaks;
    }

    public static Set<Node> convertNodelistToSet(NodeList xpathNodeSet) {
        if (xpathNodeSet == null) {
            return new HashSet();
        }
        int length = xpathNodeSet.getLength();
        Set<Node> set = new HashSet<>(length);
        for (int i = 0; i < length; i++) {
            set.add(xpathNodeSet.item(i));
        }
        return set;
    }

    public static void circumventBug2650(Document doc) {
        Element documentElement = doc.getDocumentElement();
        Attr xmlnsAttr = documentElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS);
        if (xmlnsAttr == null) {
            documentElement.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "");
        }
        circumventBug2650internal(doc);
    }

    private static void circumventBug2650internal(Node node) {
        Node parent = null;
        Node sibling = null;
        while (true) {
            switch (node.getNodeType()) {
                case 1:
                    Element element = (Element) node;
                    if (element.hasChildNodes()) {
                        if (element.hasAttributes()) {
                            NamedNodeMap attributes = element.getAttributes();
                            int attributesLength = attributes.getLength();
                            Node firstChild = element.getFirstChild();
                            while (true) {
                                Node child = firstChild;
                                if (child != null) {
                                    if (child.getNodeType() == 1) {
                                        Element childElement = (Element) child;
                                        for (int i = 0; i < attributesLength; i++) {
                                            Attr currentAttr = (Attr) attributes.item(i);
                                            if ("http://www.w3.org/2000/xmlns/".equals(currentAttr.getNamespaceURI()) && !childElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", currentAttr.getLocalName())) {
                                                childElement.setAttributeNS("http://www.w3.org/2000/xmlns/", currentAttr.getName(), currentAttr.getNodeValue());
                                            }
                                        }
                                    }
                                    firstChild = child.getNextSibling();
                                }
                            }
                        }
                        break;
                    }
                case 5:
                case 9:
                    parent = node;
                    sibling = node.getFirstChild();
                    break;
            }
            while (sibling == null && parent != null) {
                sibling = parent.getNextSibling();
                parent = parent.getParentNode();
            }
            if (sibling == null) {
                return;
            }
            node = sibling;
            sibling = node.getNextSibling();
        }
    }

    public static Element selectDsNode(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2000/09/xmldsig#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element) sibling;
                }
                number--;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectDs11Node(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2009/xmldsig11#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element) sibling;
                }
                number--;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectXencNode(Node sibling, String nodeName, int number) {
        while (sibling != null) {
            if ("http://www.w3.org/2001/04/xmlenc#".equals(sibling.getNamespaceURI()) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element) sibling;
                }
                number--;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element selectXenc11Node(Node sibling, String nodeName, int number) {
        return selectNode(sibling, "http://www.w3.org/2009/xmlenc11#", nodeName, number);
    }

    public static Element selectNode(Node sibling, String uri, String nodeName, int number) {
        while (sibling != null) {
            if (sibling.getNamespaceURI() != null && sibling.getNamespaceURI().equals(uri) && sibling.getLocalName().equals(nodeName)) {
                if (number == 0) {
                    return (Element) sibling;
                }
                number--;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    public static Element[] selectDsNodes(Node sibling, String nodeName) {
        return selectNodes(sibling, "http://www.w3.org/2000/09/xmldsig#", nodeName);
    }

    public static Element[] selectDs11Nodes(Node sibling, String nodeName) {
        return selectNodes(sibling, "http://www.w3.org/2009/xmldsig11#", nodeName);
    }

    public static Element[] selectNodes(Node sibling, String uri, String nodeName) {
        List<Element> list = new ArrayList<>();
        while (sibling != null) {
            if (sibling.getNamespaceURI() != null && sibling.getNamespaceURI().equals(uri) && sibling.getLocalName().equals(nodeName)) {
                list.add((Element) sibling);
            }
            sibling = sibling.getNextSibling();
        }
        return (Element[]) list.toArray(new Element[list.size()]);
    }

    public static Set<Node> excludeNodeFromSet(Node signatureElement, Set<Node> inputSet) {
        return (Set) inputSet.stream().filter(inputNode -> {
            return !isDescendantOrSelf(signatureElement, inputNode);
        }).collect(Collectors.toSet());
    }

    public static String getStrFromNode(Node xpathnode) {
        if (xpathnode.getNodeType() == 3) {
            StringBuilder sb = new StringBuilder();
            Node firstChild = xpathnode.getParentNode().getFirstChild();
            while (true) {
                Node currentSibling = firstChild;
                if (currentSibling != null) {
                    if (currentSibling.getNodeType() == 3) {
                        sb.append(((Text) currentSibling).getData());
                    }
                    firstChild = currentSibling.getNextSibling();
                } else {
                    return sb.toString();
                }
            }
        } else {
            if (xpathnode.getNodeType() == 2) {
                return xpathnode.getNodeValue();
            }
            if (xpathnode.getNodeType() == 7) {
                return xpathnode.getNodeValue();
            }
            return null;
        }
    }

    public static boolean isDescendantOrSelf(Node ctx, Node descendantOrSelf) {
        if (ctx == descendantOrSelf) {
            return true;
        }
        Node ownerElement = descendantOrSelf;
        while (true) {
            Node parent = ownerElement;
            if (parent == null) {
                return false;
            }
            if (parent == ctx) {
                return true;
            }
            if (parent.getNodeType() == 2) {
                ownerElement = ((Attr) parent).getOwnerElement();
            } else {
                ownerElement = parent.getParentNode();
            }
        }
    }

    public static boolean ignoreLineBreaks() {
        return ignoreLineBreaks;
    }

    public static boolean protectAgainstWrappingAttack(Node startNode, String value) {
        String id = value.trim();
        if (!id.isEmpty() && id.charAt(0) == '#') {
            id = id.substring(1);
        }
        Node startParent = null;
        Element foundElement = null;
        if (startNode != null) {
            startParent = startNode.getParentNode();
        }
        while (startNode != null) {
            if (startNode.getNodeType() == 1) {
                Element se = (Element) startNode;
                NamedNodeMap attributes = se.getAttributes();
                if (attributes != null) {
                    int length = attributes.getLength();
                    for (int i = 0; i < length; i++) {
                        Attr attr = (Attr) attributes.item(i);
                        if (attr.isId() && id.equals(attr.getValue())) {
                            if (foundElement == null) {
                                foundElement = attr.getOwnerElement();
                            } else {
                                LOG.warn("Multiple elements with the same 'Id' attribute value!");
                                return false;
                            }
                        }
                    }
                }
            }
            Node processedNode = startNode;
            startNode = startNode.getFirstChild();
            if (startNode == null) {
                startNode = processedNode.getNextSibling();
            }
            while (startNode == null) {
                processedNode = processedNode.getParentNode();
                if (processedNode == startParent) {
                    return true;
                }
                startNode = processedNode.getNextSibling();
            }
        }
        return true;
    }

    public static boolean protectAgainstWrappingAttack(Node startNode, Element knownElement, String value) {
        Element se;
        NamedNodeMap attributes;
        String id = value.trim();
        if (!id.isEmpty() && id.charAt(0) == '#') {
            id = id.substring(1);
        }
        Node startParent = null;
        if (startNode != null) {
            startParent = startNode.getParentNode();
        }
        while (startNode != null) {
            if (startNode.getNodeType() == 1 && (attributes = (se = (Element) startNode).getAttributes()) != null) {
                int length = attributes.getLength();
                for (int i = 0; i < length; i++) {
                    Attr attr = (Attr) attributes.item(i);
                    if (attr.isId() && id.equals(attr.getValue()) && !knownElement.isSameNode(se)) {
                        LOG.warn("Multiple elements with the same 'Id' attribute value!");
                        return false;
                    }
                }
            }
            Node processedNode = startNode;
            startNode = startNode.getFirstChild();
            if (startNode == null) {
                startNode = processedNode.getNextSibling();
            }
            while (startNode == null) {
                processedNode = processedNode.getParentNode();
                if (processedNode == startParent) {
                    return true;
                }
                startNode = processedNode.getNextSibling();
            }
        }
        return true;
    }

    public static Document read(File file, boolean disallowDocTypeDeclarations) throws Exception {
        InputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]), 8192);
        Throwable th = null;
        try {
            try {
                Document document = read(inputStream, disallowDocTypeDeclarations);
                $closeResource(null, inputStream);
                return document;
            } catch (Throwable th2) {
                th = th2;
                throw th2;
            }
        } catch (Throwable th3) {
            $closeResource(th, inputStream);
            throw th3;
        }
    }

    public static Document readResource(String name, ClassLoader loader, boolean disallowDocTypeDeclarations) throws Exception {
        InputStream inputStream = loader.getResourceAsStream(name);
        Throwable th = null;
        try {
            try {
                Document document = read(inputStream, disallowDocTypeDeclarations);
                if (inputStream != null) {
                    $closeResource(null, inputStream);
                }
                return document;
            } catch (Throwable th2) {
                th = th2;
                throw th2;
            }
        } catch (Throwable th3) {
            if (inputStream != null) {
                $closeResource(th, inputStream);
            }
            throw th3;
        }
    }

    public static Document read(InputStream inputStream, boolean disallowDocTypeDeclarations) throws XMLParserException {
        return xmlParserImpl.parse(inputStream, disallowDocTypeDeclarations);
    }

    public static byte[] getBytes(BigInteger big, int bitlen) {
        int bitlen2 = ((bitlen + 7) >> 3) << 3;
        if (bitlen2 < big.bitLength()) {
            throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength"));
        }
        byte[] bigBytes = big.toByteArray();
        if (big.bitLength() % 8 != 0 && (big.bitLength() / 8) + 1 == bitlen2 / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int bigLen = bigBytes.length;
        if (big.bitLength() % 8 == 0) {
            startSrc = 1;
            bigLen--;
        }
        int startDst = (bitlen2 / 8) - bigLen;
        byte[] resizedBytes = new byte[bitlen2 / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, bigLen);
        return resizedBytes;
    }
}
