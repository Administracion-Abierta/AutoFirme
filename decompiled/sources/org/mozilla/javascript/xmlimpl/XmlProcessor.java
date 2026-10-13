package org.mozilla.javascript.xmlimpl;

import com.aowagie.text.ElementTags;
import com.aowagie.text.xml.XMLConstants;
import com.ctc.wstx.cfg.XmlConsts;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xmlimpl/XmlProcessor.class */
class XmlProcessor implements Serializable {
    private static final long serialVersionUID = 6903514433204808713L;
    private boolean ignoreComments;
    private boolean ignoreProcessingInstructions;
    private boolean ignoreWhitespace;
    private boolean prettyPrint;
    private int prettyIndent;
    private transient DocumentBuilderFactory dom;
    private transient TransformerFactory xform;
    private transient LinkedBlockingDeque<DocumentBuilder> documentBuilderPool;
    private RhinoSAXErrorHandler errorHandler = new RhinoSAXErrorHandler();

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.dom = DocumentBuilderFactory.newInstance();
        this.dom.setNamespaceAware(true);
        this.dom.setIgnoringComments(false);
        this.xform = TransformerFactory.newInstance();
        Context ctx = Context.getCurrentContext();
        if (ctx == null || ctx.hasFeature(20)) {
            configureSecureDBF(this.dom);
            configureSecureTF(this.xform);
        }
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        this.documentBuilderPool = new LinkedBlockingDeque<>(poolSize);
    }

    private void configureSecureDBF(DocumentBuilderFactory dbf) {
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setXIncludeAware(false);
            try {
                dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (ParserConfigurationException e) {
            }
            try {
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (ParserConfigurationException e2) {
            }
        } catch (ParserConfigurationException e3) {
            throw new RuntimeException("XML parser (DocumentBuilderFactory) cannot be securely configured.", e3);
        }
    }

    private void configureSecureTF(TransformerFactory xform) {
        try {
            xform.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            try {
                xform.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            } catch (IllegalArgumentException e) {
            }
            try {
                xform.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (IllegalArgumentException e2) {
            }
        } catch (TransformerConfigurationException e3) {
            throw new RuntimeException("XML parser (TransformerFactory) cannot be securely configured.", e3);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xmlimpl/XmlProcessor$RhinoSAXErrorHandler.class */
    private static class RhinoSAXErrorHandler implements ErrorHandler, Serializable {
        private static final long serialVersionUID = 6918417235413084055L;

        private RhinoSAXErrorHandler() {
        }

        private void throwError(SAXParseException e) {
            throw ScriptRuntime.constructError("TypeError", e.getMessage(), e.getLineNumber() - 1);
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException e) {
            throwError(e);
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException e) {
            throwError(e);
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException e) {
            Context.reportWarning(e.getMessage());
        }
    }

    XmlProcessor() {
        setDefault();
        this.dom = DocumentBuilderFactory.newInstance();
        this.dom.setNamespaceAware(true);
        this.dom.setIgnoringComments(false);
        this.xform = TransformerFactory.newInstance();
        Context ctx = Context.getCurrentContext();
        if (ctx == null || ctx.hasFeature(20)) {
            configureSecureDBF(this.dom);
            configureSecureTF(this.xform);
        }
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        this.documentBuilderPool = new LinkedBlockingDeque<>(poolSize);
    }

    final void setDefault() {
        setIgnoreComments(true);
        setIgnoreProcessingInstructions(true);
        setIgnoreWhitespace(true);
        setPrettyPrinting(true);
        setPrettyIndent(2);
    }

    final void setIgnoreComments(boolean b) {
        this.ignoreComments = b;
    }

    final void setIgnoreWhitespace(boolean b) {
        this.ignoreWhitespace = b;
    }

    final void setIgnoreProcessingInstructions(boolean b) {
        this.ignoreProcessingInstructions = b;
    }

    final void setPrettyPrinting(boolean b) {
        this.prettyPrint = b;
    }

    final void setPrettyIndent(int i) {
        this.prettyIndent = i;
    }

    final boolean isIgnoreComments() {
        return this.ignoreComments;
    }

    final boolean isIgnoreProcessingInstructions() {
        return this.ignoreProcessingInstructions;
    }

    final boolean isIgnoreWhitespace() {
        return this.ignoreWhitespace;
    }

    final boolean isPrettyPrinting() {
        return this.prettyPrint;
    }

    final int getPrettyIndent() {
        return this.prettyIndent;
    }

    private String toXmlNewlines(String rv) {
        StringBuilder nl = new StringBuilder();
        for (int i = 0; i < rv.length(); i++) {
            if (rv.charAt(i) == '\r') {
                if (rv.charAt(i + 1) != '\n') {
                    nl.append('\n');
                }
            } else {
                nl.append(rv.charAt(i));
            }
        }
        return nl.toString();
    }

    private DocumentBuilderFactory getDomFactory() {
        return this.dom;
    }

    private DocumentBuilder getDocumentBuilderFromPool() throws ParserConfigurationException {
        DocumentBuilder builder = this.documentBuilderPool.pollFirst();
        if (builder == null) {
            builder = getDomFactory().newDocumentBuilder();
        }
        builder.setErrorHandler(this.errorHandler);
        return builder;
    }

    private void returnDocumentBuilderToPool(DocumentBuilder db) {
        try {
            db.reset();
            this.documentBuilderPool.offerFirst(db);
        } catch (UnsupportedOperationException e) {
        }
    }

    private void addProcessingInstructionsTo(List<Node> list, Node node) {
        if (node instanceof ProcessingInstruction) {
            list.add(node);
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                addProcessingInstructionsTo(list, node.getChildNodes().item(i));
            }
        }
    }

    private void addCommentsTo(List<Node> list, Node node) {
        if (node instanceof Comment) {
            list.add(node);
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                addProcessingInstructionsTo(list, node.getChildNodes().item(i));
            }
        }
    }

    private void addTextNodesToRemoveAndTrim(List<Node> toRemove, Node node) {
        if (node instanceof Text) {
            Text text = (Text) node;
            if (0 == 0) {
                text.setData(text.getData().trim());
            } else if (text.getData().trim().length() == 0) {
                text.setData("");
            }
            if (text.getData().length() == 0) {
                toRemove.add(node);
            }
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                addTextNodesToRemoveAndTrim(toRemove, node.getChildNodes().item(i));
            }
        }
    }

    final Node toXml(String defaultNamespaceUri, String xml) throws SAXException {
        try {
            try {
                String syntheticXml = "<parent xmlns=\"" + defaultNamespaceUri + "\">" + xml + "</parent>";
                DocumentBuilder builder = getDocumentBuilderFromPool();
                Document document = builder.parse(new InputSource(new StringReader(syntheticXml)));
                if (this.ignoreProcessingInstructions) {
                    List<Node> list = new ArrayList<>();
                    addProcessingInstructionsTo(list, document);
                    for (Node node : list) {
                        node.getParentNode().removeChild(node);
                    }
                }
                if (this.ignoreComments) {
                    List<Node> list2 = new ArrayList<>();
                    addCommentsTo(list2, document);
                    for (Node node2 : list2) {
                        node2.getParentNode().removeChild(node2);
                    }
                }
                if (this.ignoreWhitespace) {
                    List<Node> list3 = new ArrayList<>();
                    addTextNodesToRemoveAndTrim(list3, document);
                    for (Node node3 : list3) {
                        node3.getParentNode().removeChild(node3);
                    }
                }
                NodeList rv = document.getDocumentElement().getChildNodes();
                if (rv.getLength() > 1) {
                    throw ScriptRuntime.constructError("SyntaxError", "XML objects may contain at most one node.");
                }
                if (rv.getLength() == 0) {
                    Node node4 = document.createTextNode("");
                    if (builder != null) {
                        returnDocumentBuilderToPool(builder);
                    }
                    return node4;
                }
                Node node5 = rv.item(0);
                document.getDocumentElement().removeChild(node5);
                if (builder != null) {
                    returnDocumentBuilderToPool(builder);
                }
                return node5;
            } catch (IOException e) {
                throw new RuntimeException("Unreachable.");
            } catch (ParserConfigurationException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                returnDocumentBuilderToPool(null);
            }
            throw th;
        }
    }

    Document newDocument() {
        DocumentBuilder builder = null;
        try {
            try {
                builder = getDocumentBuilderFromPool();
                Document documentNewDocument = builder.newDocument();
                if (builder != null) {
                    returnDocumentBuilderToPool(builder);
                }
                return documentNewDocument;
            } catch (ParserConfigurationException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Throwable th) {
            if (builder != null) {
                returnDocumentBuilderToPool(builder);
            }
            throw th;
        }
    }

    private String toString(Node node) {
        DOMSource source = new DOMSource(node);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            Transformer transformer = this.xform.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", XmlConsts.XML_SA_YES);
            transformer.setOutputProperty(ElementTags.INDENT, XmlConsts.XML_SA_NO);
            transformer.setOutputProperty("method", "xml");
            transformer.transform(source, result);
            return toXmlNewlines(writer.toString());
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (TransformerException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    String escapeAttributeValue(Object value) {
        String text = ScriptRuntime.toString(value);
        if (text.length() == 0) {
            return "";
        }
        Document dom = newDocument();
        Element e = dom.createElement(PDPageLabelRange.STYLE_LETTERS_LOWER);
        e.setAttribute(OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, text);
        String elementText = toString(e);
        int begin = elementText.indexOf(34);
        int end = elementText.lastIndexOf(34);
        return elementText.substring(begin + 1, end);
    }

    String escapeTextValue(Object value) {
        if (value instanceof XMLObjectImpl) {
            return ((XMLObjectImpl) value).toXMLString();
        }
        String text = ScriptRuntime.toString(value);
        if (text.length() == 0) {
            return text;
        }
        Document dom = newDocument();
        Element e = dom.createElement(PDPageLabelRange.STYLE_LETTERS_LOWER);
        e.setTextContent(text);
        String elementText = toString(e);
        int begin = elementText.indexOf(62) + 1;
        int end = elementText.lastIndexOf(60);
        return begin < end ? elementText.substring(begin, end) : "";
    }

    private String escapeElementValue(String s) {
        return escapeTextValue(s);
    }

    private String elementToXmlString(Element element) {
        Element copy = (Element) element.cloneNode(true);
        if (this.prettyPrint) {
            beautifyElement(copy, 0);
        }
        return toString(copy);
    }

    final String ecmaToXmlString(Node node) {
        StringBuilder s = new StringBuilder();
        if (this.prettyPrint) {
            for (int i = 0; i < 0; i++) {
                s.append(' ');
            }
        }
        if (node instanceof Text) {
            String data = ((Text) node).getData();
            String v = this.prettyPrint ? data.trim() : data;
            s.append(escapeElementValue(v));
            return s.toString();
        }
        if (node instanceof Attr) {
            String value = ((Attr) node).getValue();
            s.append(escapeAttributeValue(value));
            return s.toString();
        }
        if (node instanceof Comment) {
            s.append("<!--" + ((Comment) node).getNodeValue() + "-->");
            return s.toString();
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            s.append("<?" + pi.getTarget() + " " + pi.getData() + "?>");
            return s.toString();
        }
        s.append(elementToXmlString((Element) node));
        return s.toString();
    }

    private void beautifyElement(Element e, int indent) {
        StringBuilder s = new StringBuilder();
        s.append('\n');
        for (int i = 0; i < indent; i++) {
            s.append(' ');
        }
        String afterContent = s.toString();
        for (int i2 = 0; i2 < this.prettyIndent; i2++) {
            s.append(' ');
        }
        String beforeContent = s.toString();
        ArrayList<Node> toIndent = new ArrayList<>();
        boolean indentChildren = false;
        for (int i3 = 0; i3 < e.getChildNodes().getLength(); i3++) {
            if (i3 == 1) {
                indentChildren = true;
            }
            if (e.getChildNodes().item(i3) instanceof Text) {
                toIndent.add(e.getChildNodes().item(i3));
            } else {
                indentChildren = true;
                toIndent.add(e.getChildNodes().item(i3));
            }
        }
        if (indentChildren) {
            for (int i4 = 0; i4 < toIndent.size(); i4++) {
                e.insertBefore(e.getOwnerDocument().createTextNode(beforeContent), toIndent.get(i4));
            }
        }
        NodeList nodes = e.getChildNodes();
        ArrayList<Element> list = new ArrayList<>();
        for (int i5 = 0; i5 < nodes.getLength(); i5++) {
            if (nodes.item(i5) instanceof Element) {
                list.add((Element) nodes.item(i5));
            }
        }
        for (Element elem : list) {
            beautifyElement(elem, indent + this.prettyIndent);
        }
        if (indentChildren) {
            e.appendChild(e.getOwnerDocument().createTextNode(afterContent));
        }
    }
}
