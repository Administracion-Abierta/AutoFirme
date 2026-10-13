package org.apache.xml.security.signature;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.helper.AttrCompare;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/XMLSignatureInputDebugger.class */
public class XMLSignatureInputDebugger {
    private Set<Node> xpathNodeSet;
    private Set<String> inclusiveNamespaces;
    private Writer writer;
    static final String HTMLPrefix = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Canonical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n";
    static final String HTMLSuffix = "</pre></body></html>";
    static final String HTMLExcludePrefix = "<span class=\"EXCLUDED\">";
    static final String HTMLIncludePrefix = "<span class=\"INCLUDED\">";
    static final String HTMLIncludeOrExcludeSuffix = "</span>";
    static final String HTMLIncludedInclusiveNamespacePrefix = "<span class=\"INCLUDEDINCLUSIVENAMESPACE\">";
    static final String HTMLExcludedInclusiveNamespacePrefix = "<span class=\"EXCLUDEDINCLUSIVENAMESPACE\">";
    private static final int NODE_BEFORE_DOCUMENT_ELEMENT = -1;
    private static final int NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT = 0;
    private static final int NODE_AFTER_DOCUMENT_ELEMENT = 1;
    static final AttrCompare ATTR_COMPARE = new AttrCompare();

    public XMLSignatureInputDebugger(XMLSignatureInput xmlSignatureInput) {
        if (!xmlSignatureInput.isNodeSet()) {
            this.xpathNodeSet = null;
        } else {
            this.xpathNodeSet = xmlSignatureInput.getInputNodeSet();
        }
    }

    public XMLSignatureInputDebugger(XMLSignatureInput xmlSignatureInput, Set<String> inclusiveNamespace) {
        this(xmlSignatureInput);
        this.inclusiveNamespaces = inclusiveNamespace;
    }

    public String getHTMLRepresentation() throws XMLSignatureException {
        if (this.xpathNodeSet == null || this.xpathNodeSet.isEmpty()) {
            return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n<html>\n<head>\n<title>Canonical XML node set</title>\n<style type=\"text/css\">\n<!-- \n.INCLUDED { \n   color: #000000; \n   background-color: \n   #FFFFFF; \n   font-weight: bold; } \n.EXCLUDED { \n   color: #666666; \n   background-color: \n   #999999; } \n.INCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #FFFFFF; \n   font-weight: bold; \n   font-style: italic; } \n.EXCLUDEDINCLUSIVENAMESPACE { \n   color: #0000FF; \n   background-color: #999999; \n   font-style: italic; } \n--> \n</style> \n</head>\n<body bgcolor=\"#999999\">\n<h1>Explanation of the output</h1>\n<p>The following text contains the nodeset of the given Reference before it is canonicalized. There exist four different styles to indicate how a given node is treated.</p>\n<ul>\n<li class=\"INCLUDED\">A node which is in the node set is labeled using the INCLUDED style.</li>\n<li class=\"EXCLUDED\">A node which is <em>NOT</em> in the node set is labeled EXCLUDED style.</li>\n<li class=\"INCLUDEDINCLUSIVENAMESPACE\">A namespace which is in the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n<li class=\"EXCLUDEDINCLUSIVENAMESPACE\">A namespace which is in NOT the node set AND in the InclusiveNamespaces PrefixList is labeled using the INCLUDEDINCLUSIVENAMESPACE style.</li>\n</ul>\n<h1>Output</h1>\n<pre>\n<blink>no node set, sorry</blink></pre></body></html>";
        }
        Node n = this.xpathNodeSet.iterator().next();
        Document doc = XMLUtils.getOwnerDocument(n);
        try {
            try {
                this.writer = new StringWriter();
                canonicalizeXPathNodeSet(doc);
                this.writer.close();
                String string = this.writer.toString();
                this.xpathNodeSet = null;
                this.writer = null;
                return string;
            } catch (IOException ex) {
                throw new XMLSignatureException(ex);
            }
        } catch (Throwable th) {
            this.xpathNodeSet = null;
            this.writer = null;
            throw th;
        }
    }

    private void canonicalizeXPathNodeSet(Node currentNode) throws IOException, XMLSignatureException {
        int currentNodeType = currentNode.getNodeType();
        switch (currentNodeType) {
            case 1:
                Element currentElement = (Element) currentNode;
                if (this.xpathNodeSet.contains(currentNode)) {
                    this.writer.write(HTMLIncludePrefix);
                } else {
                    this.writer.write(HTMLExcludePrefix);
                }
                this.writer.write("&lt;");
                this.writer.write(currentElement.getTagName());
                this.writer.write(HTMLIncludeOrExcludeSuffix);
                NamedNodeMap attrs = currentElement.getAttributes();
                int attrsLength = attrs.getLength();
                Attr[] attrs2 = new Attr[attrsLength];
                for (int i = 0; i < attrsLength; i++) {
                    attrs2[i] = (Attr) attrs.item(i);
                }
                Arrays.sort(attrs2, ATTR_COMPARE);
                for (int i2 = 0; i2 < attrsLength; i2++) {
                    Attr a = attrs2[i2];
                    boolean included = this.xpathNodeSet.contains(a);
                    boolean inclusive = this.inclusiveNamespaces.contains(a.getName());
                    if (included) {
                        if (inclusive) {
                            this.writer.write(HTMLIncludedInclusiveNamespacePrefix);
                        } else {
                            this.writer.write(HTMLIncludePrefix);
                        }
                    } else if (inclusive) {
                        this.writer.write(HTMLExcludedInclusiveNamespacePrefix);
                    } else {
                        this.writer.write(HTMLExcludePrefix);
                    }
                    outputAttrToWriter(a.getNodeName(), a.getNodeValue());
                    this.writer.write(HTMLIncludeOrExcludeSuffix);
                }
                if (this.xpathNodeSet.contains(currentNode)) {
                    this.writer.write(HTMLIncludePrefix);
                } else {
                    this.writer.write(HTMLExcludePrefix);
                }
                this.writer.write("&gt;");
                this.writer.write(HTMLIncludeOrExcludeSuffix);
                Node firstChild = currentNode.getFirstChild();
                while (true) {
                    Node currentChild = firstChild;
                    if (currentChild != null) {
                        canonicalizeXPathNodeSet(currentChild);
                        firstChild = currentChild.getNextSibling();
                    } else {
                        if (this.xpathNodeSet.contains(currentNode)) {
                            this.writer.write(HTMLIncludePrefix);
                        } else {
                            this.writer.write(HTMLExcludePrefix);
                        }
                        this.writer.write("&lt;/");
                        this.writer.write(currentElement.getTagName());
                        this.writer.write("&gt;");
                        this.writer.write(HTMLIncludeOrExcludeSuffix);
                        return;
                    }
                }
                break;
            case 2:
            case 6:
            case 11:
            case 12:
                throw new XMLSignatureException("empty", new Object[]{"An incorrect node was provided for c14n: " + currentNodeType});
            case 3:
            case 4:
                if (this.xpathNodeSet.contains(currentNode)) {
                    this.writer.write(HTMLIncludePrefix);
                } else {
                    this.writer.write(HTMLExcludePrefix);
                }
                outputTextToWriter(currentNode.getNodeValue());
                Node nextSibling = currentNode.getNextSibling();
                while (true) {
                    Node nextSibling2 = nextSibling;
                    if (nextSibling2 != null && (nextSibling2.getNodeType() == 3 || nextSibling2.getNodeType() == 4)) {
                        outputTextToWriter(nextSibling2.getNodeValue());
                        nextSibling = nextSibling2.getNextSibling();
                    }
                }
                this.writer.write(HTMLIncludeOrExcludeSuffix);
                return;
            case 5:
            case 10:
            default:
                return;
            case 7:
                if (this.xpathNodeSet.contains(currentNode)) {
                    this.writer.write(HTMLIncludePrefix);
                } else {
                    this.writer.write(HTMLExcludePrefix);
                }
                int position = getPositionRelativeToDocumentElement(currentNode);
                if (position == 1) {
                    this.writer.write("\n");
                }
                outputPItoWriter((ProcessingInstruction) currentNode);
                if (position == -1) {
                    this.writer.write("\n");
                }
                this.writer.write(HTMLIncludeOrExcludeSuffix);
                return;
            case 8:
                if (this.xpathNodeSet.contains(currentNode)) {
                    this.writer.write(HTMLIncludePrefix);
                } else {
                    this.writer.write(HTMLExcludePrefix);
                }
                int position2 = getPositionRelativeToDocumentElement(currentNode);
                if (position2 == 1) {
                    this.writer.write("\n");
                }
                outputCommentToWriter((Comment) currentNode);
                if (position2 == -1) {
                    this.writer.write("\n");
                }
                this.writer.write(HTMLIncludeOrExcludeSuffix);
                return;
            case 9:
                this.writer.write(HTMLPrefix);
                Node firstChild2 = currentNode.getFirstChild();
                while (true) {
                    Node currentChild2 = firstChild2;
                    if (currentChild2 != null) {
                        canonicalizeXPathNodeSet(currentChild2);
                        firstChild2 = currentChild2.getNextSibling();
                    } else {
                        this.writer.write(HTMLSuffix);
                        return;
                    }
                }
                break;
        }
    }

    private int getPositionRelativeToDocumentElement(Node currentNode) {
        Document doc;
        Element documentElement;
        if (currentNode == null || currentNode.getParentNode() != (doc = currentNode.getOwnerDocument()) || (documentElement = doc.getDocumentElement()) == null || documentElement == currentNode) {
            return 0;
        }
        Node nextSibling = currentNode;
        while (true) {
            Node x = nextSibling;
            if (x != null) {
                if (x != documentElement) {
                    nextSibling = x.getNextSibling();
                } else {
                    return -1;
                }
            } else {
                return 1;
            }
        }
    }

    private void outputAttrToWriter(String name, String value) throws IOException {
        this.writer.write(" ");
        this.writer.write(name);
        this.writer.write("=\"");
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\t':
                    this.writer.write("&amp;#x9;");
                    break;
                case '\n':
                    this.writer.write("&amp;#xA;");
                    break;
                case '\r':
                    this.writer.write("&amp;#xD;");
                    break;
                case '\"':
                    this.writer.write("&amp;quot;");
                    break;
                case '&':
                    this.writer.write("&amp;amp;");
                    break;
                case '<':
                    this.writer.write("&amp;lt;");
                    break;
                default:
                    this.writer.write(c);
                    break;
            }
        }
        this.writer.write(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
    }

    private void outputPItoWriter(ProcessingInstruction currentPI) throws IOException {
        if (currentPI == null) {
            return;
        }
        this.writer.write("&lt;?");
        String target = currentPI.getTarget();
        int length = target.length();
        for (int i = 0; i < length; i++) {
            char c = target.charAt(i);
            switch (c) {
                case '\n':
                    this.writer.write("&para;\n");
                    break;
                case '\r':
                    this.writer.write("&amp;#xD;");
                    break;
                case ' ':
                    this.writer.write("&middot;");
                    break;
                default:
                    this.writer.write(c);
                    break;
            }
        }
        String data = currentPI.getData();
        int length2 = data.length();
        if (length2 > 0) {
            this.writer.write(" ");
            for (int i2 = 0; i2 < length2; i2++) {
                char c2 = data.charAt(i2);
                if (c2 == '\r') {
                    this.writer.write("&amp;#xD;");
                } else {
                    this.writer.write(c2);
                }
            }
        }
        this.writer.write("?&gt;");
    }

    private void outputCommentToWriter(Comment currentComment) throws IOException {
        if (currentComment == null) {
            return;
        }
        this.writer.write("&lt;!--");
        String data = currentComment.getData();
        int length = data.length();
        for (int i = 0; i < length; i++) {
            char c = data.charAt(i);
            switch (c) {
                case '\n':
                    this.writer.write("&para;\n");
                    break;
                case '\r':
                    this.writer.write("&amp;#xD;");
                    break;
                case ' ':
                    this.writer.write("&middot;");
                    break;
                default:
                    this.writer.write(c);
                    break;
            }
        }
        this.writer.write("--&gt;");
    }

    private void outputTextToWriter(String text) throws IOException {
        if (text == null) {
            return;
        }
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\n':
                    this.writer.write("&para;\n");
                    break;
                case '\r':
                    this.writer.write("&amp;#xD;");
                    break;
                case ' ':
                    this.writer.write("&middot;");
                    break;
                case '&':
                    this.writer.write("&amp;amp;");
                    break;
                case '<':
                    this.writer.write("&amp;lt;");
                    break;
                case '>':
                    this.writer.write("&amp;gt;");
                    break;
                default:
                    this.writer.write(c);
                    break;
            }
        }
    }
}
