package nu.xom.converters;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/converters/SAXConverter.class */
public class SAXConverter {
    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;
    private LocatorImpl locator;
    private boolean stripBaseAttributes = true;

    public SAXConverter(ContentHandler contentHandler) {
        setContentHandler(contentHandler);
    }

    public void setContentHandler(ContentHandler contentHandler) {
        if (contentHandler == null) {
            throw new NullPointerException("ContentHandler must be non-null.");
        }
        if ("nu.xom.xslt.XSLTHandler".equals(contentHandler.getClass().getName())) {
            this.stripBaseAttributes = false;
        } else {
            this.contentHandler = contentHandler;
        }
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public void convert(Document document) throws SAXException {
        this.locator = new LocatorImpl();
        this.locator.setSystemId(document.getBaseURI());
        this.contentHandler.setDocumentLocator(this.locator);
        this.contentHandler.startDocument();
        for (int i = 0; i < document.getChildCount(); i++) {
            process(document.getChild(i));
        }
        this.contentHandler.endDocument();
    }

    private void process(Node node) throws SAXException {
        if (node instanceof Element) {
            convertElement((Element) node);
            return;
        }
        if (node instanceof Text) {
            String value = node.getValue();
            this.contentHandler.characters(value.toCharArray(), 0, value.length());
            return;
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction processingInstruction = (ProcessingInstruction) node;
            this.contentHandler.processingInstruction(processingInstruction.getTarget(), processingInstruction.getValue());
            return;
        }
        if ((node instanceof Comment) && this.lexicalHandler != null) {
            String value2 = node.getValue();
            this.lexicalHandler.comment(value2.toCharArray(), 0, value2.length());
        } else {
            if (!(node instanceof DocType) || this.lexicalHandler == null) {
                return;
            }
            DocType docType = (DocType) node;
            this.lexicalHandler.startDTD(docType.getRootElementName(), docType.getPublicID(), docType.getSystemID());
            this.lexicalHandler.endDTD();
        }
    }

    private boolean convertNamespace(Element element, String str) throws SAXException {
        String namespaceURI = element.getNamespaceURI(str);
        ParentNode parent = element.getParent();
        Element element2 = null;
        if (parent instanceof Element) {
            element2 = (Element) parent;
        }
        if (element2 != null && namespaceURI.equals(element2.getNamespaceURI(str))) {
            return false;
        }
        if (element2 == null && "".equals(namespaceURI)) {
            return false;
        }
        this.contentHandler.startPrefixMapping(str, namespaceURI);
        return true;
    }

    private void convertElement(Element element) throws SAXException {
        this.locator.setSystemId(element.getBaseURI());
        int namespaceDeclarationCount = element.getNamespaceDeclarationCount();
        String[] strArr = new String[namespaceDeclarationCount];
        int i = 0;
        for (int i2 = 0; i2 < namespaceDeclarationCount; i2++) {
            String namespacePrefix = element.getNamespacePrefix(i2);
            if (convertNamespace(element, namespacePrefix)) {
                strArr[i] = namespacePrefix;
                i++;
            }
        }
        AttributesImpl attributesImpl = new AttributesImpl();
        int attributeCount = element.getAttributeCount();
        for (int i3 = 0; i3 < attributeCount; i3++) {
            Attribute attribute = element.getAttribute(i3);
            if (!"base".equals(attribute.getLocalName()) || !"http://www.w3.org/XML/1998/namespace".equals(attribute.getNamespaceURI()) || !this.stripBaseAttributes) {
                attributesImpl.addAttribute(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getQualifiedName(), getSAXType(attribute), attribute.getValue());
            }
        }
        this.contentHandler.startElement(element.getNamespaceURI(), element.getLocalName(), element.getQualifiedName(), attributesImpl);
        int childCount = element.getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            process(element.getChild(i4));
        }
        this.contentHandler.endElement(element.getNamespaceURI(), element.getLocalName(), element.getQualifiedName());
        for (int i5 = 0; i5 < i; i5++) {
            this.contentHandler.endPrefixMapping(strArr[i5]);
        }
    }

    private static String getSAXType(Attribute attribute) {
        Attribute.Type type = attribute.getType();
        if (type.equals(Attribute.Type.UNDECLARED) || type.equals(Attribute.Type.CDATA)) {
            return "CDATA";
        }
        if (type.equals(Attribute.Type.ID)) {
            return DTDParser.TYPE_ID;
        }
        if (type.equals(Attribute.Type.IDREF)) {
            return DTDParser.TYPE_IDREF;
        }
        if (type.equals(Attribute.Type.IDREFS)) {
            return DTDParser.TYPE_IDREFS;
        }
        if (type.equals(Attribute.Type.NMTOKEN)) {
            return DTDParser.TYPE_NMTOKEN;
        }
        if (type.equals(Attribute.Type.NMTOKENS)) {
            return DTDParser.TYPE_NMTOKENS;
        }
        if (type.equals(Attribute.Type.ENTITY)) {
            return DTDParser.TYPE_ENTITY;
        }
        if (type.equals(Attribute.Type.ENTITIES)) {
            return DTDParser.TYPE_ENTITIES;
        }
        return type.equals(Attribute.Type.NOTATION) ? DTDParser.TYPE_NOTATION : DTDParser.TYPE_NMTOKEN;
    }

    public void convert(Nodes nodes) throws SAXException {
        if (nodes.size() == 1 && (nodes.get(0) instanceof Document)) {
            convert((Document) nodes.get(0));
            return;
        }
        this.locator = new LocatorImpl();
        this.contentHandler.setDocumentLocator(this.locator);
        this.contentHandler.startDocument();
        for (int i = 0; i < nodes.size(); i++) {
            process(nodes.get(i));
        }
        this.contentHandler.endDocument();
    }
}
