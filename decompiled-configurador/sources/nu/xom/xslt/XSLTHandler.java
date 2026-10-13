package nu.xom.xslt;

import java.util.ArrayList;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XMLException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XSLTHandler.class */
class XSLTHandler implements ContentHandler, LexicalHandler {
    private final NodeFactory factory;
    private Element current;
    private AttributesImpl namespaceDeclarations = new AttributesImpl();
    private final Nodes result = new Nodes();
    private final ArrayList parents = new ArrayList();
    private StringBuffer buffer = new StringBuffer();

    XSLTHandler(NodeFactory nodeFactory) {
        this.factory = nodeFactory;
    }

    Nodes getResult() {
        flushText();
        return this.result;
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() {
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) {
        flushText();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            this.namespaceDeclarations.addAttribute(attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
        }
        AttributesImpl attributesImpl = this.namespaceDeclarations;
        Element elementStartMakingElement = this.factory.startMakingElement(str3, str);
        if (this.parents.isEmpty()) {
            this.current = elementStartMakingElement;
        } else {
            ((ParentNode) this.parents.get(this.parents.size() - 1)).appendChild(elementStartMakingElement);
        }
        this.parents.add(elementStartMakingElement);
        int length2 = attributesImpl.getLength();
        for (int i2 = 0; i2 < length2; i2++) {
            String qName = attributesImpl.getQName(i2);
            if (!qName.equals(CanonicalizerBase.XMLNS) && !qName.startsWith("xmlns:")) {
                Nodes nodesMakeAttribute = this.factory.makeAttribute(qName, attributesImpl.getURI(i2), attributesImpl.getValue(i2), Attribute.Type.UNDECLARED);
                int size = nodesMakeAttribute.size();
                for (int i3 = 0; i3 < size; i3++) {
                    Node node = nodesMakeAttribute.get(i3);
                    if (node instanceof Attribute) {
                        Attribute attribute = (Attribute) node;
                        while (true) {
                            try {
                                elementStartMakingElement.addAttribute(attribute);
                                break;
                            } catch (NamespaceConflictException e) {
                                attribute.setNamespace(new StringBuffer().append("p").append(attribute.getNamespacePrefix()).toString(), attribute.getNamespaceURI());
                            }
                        }
                    } else {
                        elementStartMakingElement.appendChild(node);
                    }
                }
            }
        }
        for (int i4 = 0; i4 < length2; i4++) {
            String qName2 = attributesImpl.getQName(i4);
            if (qName2.startsWith("xmlns:")) {
                String value = attributesImpl.getValue(i4);
                String strSubstring = qName2.substring(6);
                if (!value.equals(elementStartMakingElement.getNamespaceURI(strSubstring))) {
                    try {
                        elementStartMakingElement.addNamespaceDeclaration(strSubstring, value);
                    } catch (NamespaceConflictException e2) {
                    }
                }
            } else if (qName2.equals(CanonicalizerBase.XMLNS)) {
                String value2 = attributesImpl.getValue(i4);
                if (value2 == null) {
                    value2 = "";
                }
                if (!value2.equals(elementStartMakingElement.getNamespaceURI(""))) {
                    try {
                        elementStartMakingElement.addNamespaceDeclaration("", value2);
                    } catch (NamespaceConflictException e3) {
                        if ("http://www.w3.org/XML/1998/namespace".equals(value2) && "".equals("")) {
                            elementStartMakingElement.addNamespaceDeclaration("", "");
                        }
                    }
                }
            }
        }
        this.namespaceDeclarations = new AttributesImpl();
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) {
        flushText();
        Element element = (Element) this.parents.remove(this.parents.size() - 1);
        if (this.parents.isEmpty()) {
            Nodes nodesFinishMakingElement = this.factory.finishMakingElement(this.current);
            for (int i = 0; i < nodesFinishMakingElement.size(); i++) {
                this.result.append(nodesFinishMakingElement.get(i));
            }
            this.current = null;
            return;
        }
        Nodes nodesFinishMakingElement2 = this.factory.finishMakingElement(element);
        ParentNode parent = element.getParent();
        element.detach();
        for (int i2 = 0; i2 < nodesFinishMakingElement2.size(); i2++) {
            Node node = nodesFinishMakingElement2.get(i2);
            if (node instanceof Attribute) {
                ((Element) parent).addAttribute((Attribute) node);
            } else {
                parent.appendChild(node);
            }
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) {
        this.buffer.append(cArr, i, i2);
    }

    private void flushText() {
        if (this.buffer.length() > 0) {
            addToResultTree(this.factory.makeText(this.buffer.toString()));
            this.buffer = new StringBuffer();
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] cArr, int i, int i2) {
        characters(cArr, i, i2);
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String str, String str2) throws SAXException {
        if ("saxon:warning".equals(str)) {
            throw new SAXException("continue");
        }
        if ("javax.xml.transform.disable-output-escaping".equals(str) || "javax.xml.transform.enable-output-escaping".equals(str)) {
            return;
        }
        flushText();
        int iIndexOf = str2.indexOf("?>");
        while (true) {
            int i = iIndexOf;
            if (i == -1) {
                addToResultTree(this.factory.makeProcessingInstruction(str, str2));
                return;
            } else {
                str2 = new StringBuffer().append(str2.substring(0, i)).append("? >").append(str2.substring(i + 2)).toString();
                iIndexOf = str2.indexOf("?>");
            }
        }
    }

    private void addToResultTree(Nodes nodes) {
        if (this.parents.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                this.result.append(nodes.get(i));
            }
            return;
        }
        ParentNode parentNode = (ParentNode) this.parents.get(this.parents.size() - 1);
        for (int i2 = 0; i2 < nodes.size(); i2++) {
            Node node = nodes.get(i2);
            if (node instanceof Attribute) {
                ((Element) parentNode).addAttribute((Attribute) node);
            } else {
                parentNode.appendChild(node);
            }
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String str) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String str, String str2) {
        if ("".equals(str)) {
            this.namespaceDeclarations.addAttribute("", CanonicalizerBase.XMLNS, CanonicalizerBase.XMLNS, "CDATA", str2);
        } else {
            this.namespaceDeclarations.addAttribute("", new StringBuffer().append("xmlns:").append(str).toString(), new StringBuffer().append("xmlns:").append(str).toString(), "CDATA", str2);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String str) {
        flushText();
        throw new XMLException(new StringBuffer().append("Could not resolve entity ").append(str).toString());
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startCDATA() {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endCDATA() {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startDTD(String str, String str2, String str3) {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endDTD() {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startEntity(String str) {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endEntity(String str) {
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void comment(char[] cArr, int i, int i2) {
        flushText();
        String str = new String(cArr, i, i2);
        int iIndexOf = str.indexOf("--");
        while (true) {
            int i3 = iIndexOf;
            if (i3 == -1) {
                break;
            }
            str = new StringBuffer().append(str.substring(0, i3)).append("- -").append(str.substring(i3 + 2)).toString();
            iIndexOf = str.indexOf("--");
        }
        if (str.endsWith("-")) {
            str = new StringBuffer().append(str).append(' ').toString();
        }
        addToResultTree(this.factory.makeComment(str));
    }
}
