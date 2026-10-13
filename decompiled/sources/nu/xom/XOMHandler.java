package nu.xom;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import java.util.ArrayList;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/XOMHandler.class */
class XOMHandler implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler {
    protected Document document;
    protected String documentBaseURI;
    protected ParentNode parent;
    protected ParentNode current;
    protected ArrayList parents;
    protected boolean inProlog;
    protected boolean inDTD;
    protected int position;
    protected Locator locator;
    protected DocType doctype;
    protected StringBuffer internalDTDSubset;
    protected NodeFactory factory;
    boolean usingCrimson = false;
    protected String textString = null;
    protected StringBuffer buffer = null;
    protected boolean inExternalSubset = false;
    protected boolean inCDATA = false;
    protected boolean finishedCDATA = false;

    XOMHandler(NodeFactory nodeFactory) {
        this.factory = nodeFactory;
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    Document getDocument() {
        return this.document;
    }

    void freeMemory() {
        this.document = null;
        this.parent = null;
        this.current = null;
        this.parents = null;
        this.locator = null;
        this.doctype = null;
        this.internalDTDSubset = null;
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() {
        this.inDTD = false;
        this.document = this.factory.startMakingDocument();
        this.parent = this.document;
        this.current = this.document;
        this.parents = new ArrayList();
        this.parents.add(this.document);
        this.inProlog = true;
        this.position = 0;
        this.textString = null;
        this.doctype = null;
        if (this.locator != null) {
            this.documentBaseURI = this.locator.getSystemId();
            this.document.setBaseURI(this.documentBaseURI);
        }
        this.buffer = null;
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() {
        this.factory.finishMakingDocument(this.document);
        this.parents.remove(this.parents.size() - 1);
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) {
        Element elementMakeRootElement;
        String systemId;
        flushText();
        if (this.parent != this.document) {
            elementMakeRootElement = this.factory.startMakingElement(str3, str);
        } else {
            elementMakeRootElement = this.factory.makeRootElement(str3, str);
            if (elementMakeRootElement == null) {
                throw new NullPointerException("Factory failed to create root element.");
            }
            this.document.setRootElement(elementMakeRootElement);
            this.inProlog = false;
        }
        this.current = elementMakeRootElement;
        this.parents.add(elementMakeRootElement);
        if (elementMakeRootElement != null) {
            if (this.parent != this.document) {
                this.parent.appendChild(elementMakeRootElement);
            }
            if (this.locator != null && (systemId = this.locator.getSystemId()) != null && !systemId.equals(this.documentBaseURI)) {
                elementMakeRootElement.setActualBaseURI(systemId);
            }
            for (int i = 0; i < attributes.getLength(); i++) {
                String qName = attributes.getQName(i);
                if (!qName.startsWith("xmlns:") && !qName.equals(CanonicalizerBase.XMLNS)) {
                    Nodes nodesMakeAttribute = this.factory.makeAttribute(qName, attributes.getURI(i), attributes.getValue(i), convertStringToType(attributes.getType(i)));
                    int i2 = 0;
                    for (int i3 = 0; i3 < nodesMakeAttribute.size(); i3++) {
                        Node node = nodesMakeAttribute.get(i3);
                        if (node.isAttribute()) {
                            this.factory.addAttribute(elementMakeRootElement, (Attribute) node);
                        } else {
                            int i4 = i2;
                            i2++;
                            this.factory.insertChild(elementMakeRootElement, node, i4);
                        }
                    }
                }
            }
            for (int i5 = 0; i5 < attributes.getLength(); i5++) {
                String qName2 = attributes.getQName(i5);
                if (qName2.startsWith("xmlns:")) {
                    String value = attributes.getValue(i5);
                    String strSubstring = qName2.substring(6);
                    if (!value.equals(elementMakeRootElement.getNamespaceURI(strSubstring)) && !strSubstring.equals(elementMakeRootElement.getNamespacePrefix())) {
                        elementMakeRootElement.addNamespaceDeclaration(strSubstring, value);
                    }
                } else if (qName2.equals(CanonicalizerBase.XMLNS)) {
                    String value2 = attributes.getValue(i5);
                    if (!value2.equals(elementMakeRootElement.getNamespaceURI("")) && !"".equals(elementMakeRootElement.getNamespacePrefix())) {
                        elementMakeRootElement.addNamespaceDeclaration("", value2);
                    }
                }
            }
            this.parent = elementMakeRootElement;
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) {
        this.current = (ParentNode) this.parents.remove(this.parents.size() - 1);
        flushText();
        if (this.current != null) {
            this.parent = this.current.getParent();
            Nodes nodesFinishMakingElement = this.factory.finishMakingElement((Element) this.current);
            if (nodesFinishMakingElement.size() == 1 && nodesFinishMakingElement.get(0) == this.current) {
                return;
            }
            if (!this.parent.isDocument()) {
                try {
                    this.parent.removeChild(this.parent.getChildCount() - 1);
                    for (int i = 0; i < nodesFinishMakingElement.size(); i++) {
                        Node node = nodesFinishMakingElement.get(i);
                        if (node.isAttribute()) {
                            ((Element) this.parent).addAttribute((Attribute) node);
                        } else {
                            this.parent.appendChild(node);
                        }
                    }
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new XMLException("Factory detached element in finishMakingElement()", e);
                }
            }
            Document document = (Document) this.parent;
            Element rootElement = document.getRootElement();
            boolean z = true;
            for (int i2 = 0; i2 < nodesFinishMakingElement.size(); i2++) {
                Node node2 = nodesFinishMakingElement.get(i2);
                if (node2.isElement()) {
                    if (node2 != rootElement) {
                        if (!z) {
                            throw new IllegalAddException("Factory returned multiple roots");
                        }
                        document.setRootElement((Element) node2);
                    }
                    z = false;
                } else if (z) {
                    document.insertChild(node2, document.indexOf(document.getRootElement()));
                } else {
                    document.appendChild(node2);
                }
            }
            if (z) {
                throw new WellformednessException("Factory attempted to remove the root element");
            }
        }
    }

    static Attribute.Type convertStringToType(String str) {
        if (str.equals("CDATA")) {
            return Attribute.Type.CDATA;
        }
        if (str.equals("ID")) {
            return Attribute.Type.ID;
        }
        if (str.equals(DTDParser.TYPE_IDREF)) {
            return Attribute.Type.IDREF;
        }
        if (str.equals(DTDParser.TYPE_IDREFS)) {
            return Attribute.Type.IDREFS;
        }
        if (str.equals(DTDParser.TYPE_NMTOKEN)) {
            return Attribute.Type.NMTOKEN;
        }
        if (str.equals(DTDParser.TYPE_NMTOKENS)) {
            return Attribute.Type.NMTOKENS;
        }
        if (str.equals(DTDParser.TYPE_ENTITY)) {
            return Attribute.Type.ENTITY;
        }
        if (str.equals(DTDParser.TYPE_ENTITIES)) {
            return Attribute.Type.ENTITIES;
        }
        if (str.equals(DTDParser.TYPE_NOTATION)) {
            return Attribute.Type.NOTATION;
        }
        if (!str.equals(DTDParser.TYPE_ENUMERATION) && !str.startsWith("(")) {
            return Attribute.Type.UNDECLARED;
        }
        return Attribute.Type.ENUMERATION;
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) {
        if (i2 <= 0) {
            return;
        }
        if (this.textString == null) {
            this.textString = new String(cArr, i, i2);
        } else {
            if (this.buffer == null) {
                this.buffer = new StringBuffer(this.textString);
            }
            this.buffer.append(cArr, i, i2);
        }
        if (this.finishedCDATA) {
            this.inCDATA = false;
        }
    }

    private void flushText() {
        if (this.buffer != null) {
            this.textString = this.buffer.toString();
            this.buffer = null;
        }
        if (this.textString != null) {
            Nodes nodesMakeText = !this.inCDATA ? this.factory.makeText(this.textString) : this.factory.makeCDATASection(this.textString);
            for (int i = 0; i < nodesMakeText.size(); i++) {
                Node node = nodesMakeText.get(i);
                if (node.isAttribute()) {
                    ((Element) this.parent).addAttribute((Attribute) node);
                } else {
                    this.parent.appendChild(node);
                }
            }
            this.textString = null;
        }
        this.inCDATA = false;
        this.finishedCDATA = false;
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] cArr, int i, int i2) {
        characters(cArr, i, i2);
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String str, String str2) {
        if (!this.inDTD) {
            flushText();
        }
        if (!this.inDTD || inInternalSubset()) {
            Nodes nodesMakeProcessingInstruction = this.factory.makeProcessingInstruction(str, str2);
            for (int i = 0; i < nodesMakeProcessingInstruction.size(); i++) {
                Node node = nodesMakeProcessingInstruction.get(i);
                if (this.inDTD) {
                    if (!node.isProcessingInstruction() && !node.isComment()) {
                        throw new XMLException(new StringBuffer().append("Factory tried to put a ").append(node.getClass().getName()).append(" in the internal DTD subset").toString());
                    }
                    this.internalDTDSubset.append("  ");
                    this.internalDTDSubset.append(node.toXML());
                    this.internalDTDSubset.append("\n");
                } else if (this.inProlog) {
                    this.parent.insertChild(node, this.position);
                    this.position++;
                } else if (node.isAttribute()) {
                    ((Element) this.parent).addAttribute((Attribute) node);
                } else {
                    this.parent.appendChild(node);
                }
            }
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String str, String str2) {
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String str) {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String str) {
        if (str.startsWith("%")) {
            return;
        }
        flushText();
        throw new XMLException(new StringBuffer().append("Could not resolve entity ").append(str).toString());
    }

    public void startDTD(String str, String str2, String str3) {
        this.inDTD = true;
        Nodes nodesMakeDocType = this.factory.makeDocType(str, str2, str3);
        for (int i = 0; i < nodesMakeDocType.size(); i++) {
            Node node = nodesMakeDocType.get(i);
            this.document.insertChild(node, this.position);
            this.position++;
            if (node.isDocType()) {
                this.internalDTDSubset = new StringBuffer();
                this.doctype = (DocType) node;
            }
        }
    }

    public void endDTD() {
        this.inDTD = false;
        if (this.doctype != null) {
            this.doctype.setInternalDTDSubset(this.internalDTDSubset.toString());
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startEntity(String str) {
        if (str.equals("[dtd]")) {
            this.inExternalSubset = true;
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endEntity(String str) {
        if (str.equals("[dtd]")) {
            this.inExternalSubset = false;
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void startCDATA() {
        if (this.textString == null) {
            this.inCDATA = true;
        }
        this.finishedCDATA = false;
    }

    @Override // org.xml.sax.ext.LexicalHandler
    public void endCDATA() {
        this.finishedCDATA = true;
    }

    public void comment(char[] cArr, int i, int i2) {
        if (!this.inDTD) {
            flushText();
        }
        if (!this.inDTD || inInternalSubset()) {
            Nodes nodesMakeComment = this.factory.makeComment(new String(cArr, i, i2));
            for (int i3 = 0; i3 < nodesMakeComment.size(); i3++) {
                Node node = nodesMakeComment.get(i3);
                if (this.inDTD) {
                    if (!node.isComment() && !node.isProcessingInstruction()) {
                        throw new XMLException(new StringBuffer().append("Factory tried to put a ").append(node.getClass().getName()).append(" in the internal DTD subset").toString());
                    }
                    this.internalDTDSubset.append("  ");
                    this.internalDTDSubset.append(node.toXML());
                    this.internalDTDSubset.append("\n");
                } else if (this.inProlog) {
                    this.parent.insertChild(node, this.position);
                    this.position++;
                } else if (node instanceof Attribute) {
                    ((Element) this.parent).addAttribute((Attribute) node);
                } else {
                    this.parent.appendChild(node);
                }
            }
        }
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void elementDecl(String str, String str2) {
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!ELEMENT ");
        this.internalDTDSubset.append(str);
        this.internalDTDSubset.append(' ');
        this.internalDTDSubset.append(str2);
        if (str2.indexOf("#PCDATA") > 0 && str2.indexOf(124) > 0 && str2.endsWith(")")) {
            this.internalDTDSubset.append('*');
        }
        this.internalDTDSubset.append(">\n");
    }

    protected boolean inInternalSubset() {
        if (!this.usingCrimson) {
            return !this.inExternalSubset;
        }
        String systemId = this.locator.getSystemId();
        if (systemId == this.documentBaseURI) {
            return true;
        }
        return systemId != null && systemId.equals(this.documentBaseURI);
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void attributeDecl(String str, String str2, String str3, String str4, String str5) {
        if (str3.startsWith("NOTATION ") && str3.indexOf(40) == -1 && !str3.endsWith(")")) {
            str3 = new StringBuffer().append("NOTATION (").append(str3.substring("NOTATION ".length())).append(")").toString();
        }
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!ATTLIST ");
        this.internalDTDSubset.append(str);
        this.internalDTDSubset.append(' ');
        this.internalDTDSubset.append(str2);
        this.internalDTDSubset.append(' ');
        this.internalDTDSubset.append(str3);
        if (str4 != null) {
            this.internalDTDSubset.append(' ');
            this.internalDTDSubset.append(str4);
        }
        if (str5 != null) {
            this.internalDTDSubset.append(' ');
            this.internalDTDSubset.append('\"');
            this.internalDTDSubset.append(escapeReservedCharactersInDefaultAttributeValues(str5));
            this.internalDTDSubset.append('\"');
        }
        this.internalDTDSubset.append(">\n");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void internalEntityDecl(String str, String str2) {
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!ENTITY ");
        if (str.startsWith("%")) {
            this.internalDTDSubset.append("% ");
            this.internalDTDSubset.append(str.substring(1));
        } else {
            this.internalDTDSubset.append(str);
        }
        this.internalDTDSubset.append(" \"");
        this.internalDTDSubset.append(escapeReservedCharactersInDeclarations(str2));
        this.internalDTDSubset.append("\">\n");
    }

    @Override // org.xml.sax.ext.DeclHandler
    public void externalEntityDecl(String str, String str2, String str3) {
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!ENTITY ");
        if (str.startsWith("%")) {
            this.internalDTDSubset.append("% ");
            this.internalDTDSubset.append(str.substring(1));
        } else {
            this.internalDTDSubset.append(str);
        }
        if (this.locator != null && URIUtil.isAbsolute(str3)) {
            String systemId = this.locator.getSystemId();
            if (systemId != null) {
                if (systemId.startsWith("file:/") && !systemId.startsWith("file:///")) {
                    systemId = new StringBuffer().append("file://").append(systemId.substring(5)).toString();
                }
                if (str3.startsWith("file:/") && !str3.startsWith("file:///")) {
                    str3 = new StringBuffer().append("file://").append(str3.substring(5)).toString();
                }
                str3 = URIUtil.relativize(systemId, str3);
            }
        }
        if (str2 != null) {
            this.internalDTDSubset.append(" PUBLIC \"");
            this.internalDTDSubset.append(str2);
            this.internalDTDSubset.append("\" \"");
            this.internalDTDSubset.append(str3);
        } else {
            this.internalDTDSubset.append(" SYSTEM \"");
            this.internalDTDSubset.append(str3);
        }
        this.internalDTDSubset.append("\">\n");
    }

    @Override // org.xml.sax.DTDHandler
    public void notationDecl(String str, String str2, String str3) {
        if (str3 != null) {
            str3 = escapeReservedCharactersInDeclarations(str3);
        }
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!NOTATION ");
        this.internalDTDSubset.append(str);
        if (str2 != null) {
            this.internalDTDSubset.append(" PUBLIC \"");
            this.internalDTDSubset.append(str2);
            this.internalDTDSubset.append('\"');
            if (str3 != null) {
                this.internalDTDSubset.append(" \"");
                this.internalDTDSubset.append(str3);
                this.internalDTDSubset.append('\"');
            }
        } else {
            this.internalDTDSubset.append(" SYSTEM \"");
            this.internalDTDSubset.append(str3);
            this.internalDTDSubset.append('\"');
        }
        this.internalDTDSubset.append(">\n");
    }

    @Override // org.xml.sax.DTDHandler
    public void unparsedEntityDecl(String str, String str2, String str3, String str4) {
        if (!inInternalSubset() || this.doctype == null) {
            return;
        }
        this.internalDTDSubset.append("  <!ENTITY ");
        if (str2 != null) {
            this.internalDTDSubset.append(str);
            this.internalDTDSubset.append(" PUBLIC \"");
            this.internalDTDSubset.append(str2);
            this.internalDTDSubset.append("\" \"");
            this.internalDTDSubset.append(str3);
            this.internalDTDSubset.append("\" NDATA ");
            this.internalDTDSubset.append(str4);
        } else {
            this.internalDTDSubset.append(str);
            this.internalDTDSubset.append(" SYSTEM \"");
            this.internalDTDSubset.append(str3);
            this.internalDTDSubset.append("\" NDATA ");
            this.internalDTDSubset.append(str4);
        }
        this.internalDTDSubset.append(">\n");
    }

    private static String escapeReservedCharactersInDeclarations(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '\r':
                    stringBuffer.append("&#x0D;");
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    break;
                case ' ':
                    stringBuffer.append(' ');
                    break;
                case '!':
                    stringBuffer.append('!');
                    break;
                case '\"':
                    stringBuffer.append("&#x22;");
                    break;
                case '#':
                    stringBuffer.append('#');
                    break;
                case '$':
                    stringBuffer.append('$');
                    break;
                case '%':
                    stringBuffer.append("&#x25;");
                    break;
                case '&':
                    stringBuffer.append("&#x26;");
                    break;
                default:
                    stringBuffer.append(cCharAt);
                    break;
            }
        }
        return stringBuffer.toString();
    }

    private static String escapeReservedCharactersInDefaultAttributeValues(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '\r':
                    stringBuffer.append("&#x0D;");
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    break;
                case ' ':
                    stringBuffer.append(' ');
                    break;
                case '!':
                    stringBuffer.append('!');
                    break;
                case '\"':
                    stringBuffer.append("&quot;");
                    break;
                case '#':
                    stringBuffer.append('#');
                    break;
                case '$':
                    stringBuffer.append('$');
                    break;
                case '%':
                    stringBuffer.append("&#x25;");
                    break;
                case '&':
                    stringBuffer.append("&amp;");
                    break;
                case '\'':
                    stringBuffer.append('\'');
                    break;
                case '(':
                    stringBuffer.append('(');
                    break;
                case ')':
                    stringBuffer.append(')');
                    break;
                case '*':
                    stringBuffer.append('*');
                    break;
                case '+':
                    stringBuffer.append('+');
                    break;
                case ',':
                    stringBuffer.append(',');
                    break;
                case '-':
                    stringBuffer.append('-');
                    break;
                case '.':
                    stringBuffer.append('.');
                    break;
                case '/':
                    stringBuffer.append('/');
                    break;
                case '0':
                    stringBuffer.append('0');
                    break;
                case '1':
                    stringBuffer.append('1');
                    break;
                case '2':
                    stringBuffer.append('2');
                    break;
                case '3':
                    stringBuffer.append('3');
                    break;
                case '4':
                    stringBuffer.append('4');
                    break;
                case '5':
                    stringBuffer.append('5');
                    break;
                case '6':
                    stringBuffer.append('6');
                    break;
                case '7':
                    stringBuffer.append('7');
                    break;
                case '8':
                    stringBuffer.append('8');
                    break;
                case '9':
                    stringBuffer.append('9');
                    break;
                case ':':
                    stringBuffer.append(':');
                    break;
                case ';':
                    stringBuffer.append(';');
                    break;
                case '<':
                    stringBuffer.append("&lt;");
                    break;
                default:
                    stringBuffer.append(cCharAt);
                    break;
            }
        }
        return stringBuffer.toString();
    }
}
