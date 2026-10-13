package nu.xom;

import com.aowagie.text.pdf.PdfObject;
import com.ctc.wstx.io.CharsetNames;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.helpers.NamespaceSupport;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Serializer.class */
public class Serializer {
    private TextWriter escaper;
    private boolean preserveBaseURI = false;
    private NamespaceSupport namespaces = new NamespaceSupport();

    public Serializer(OutputStream outputStream) {
        try {
            setOutputStream(outputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The VM is broken. It does not understand UTF-8.");
        }
    }

    public Serializer(OutputStream outputStream, String str) throws UnsupportedEncodingException {
        if (str == null) {
            throw new NullPointerException("Null encoding");
        }
        setOutputStream(outputStream, str);
    }

    public void setOutputStream(OutputStream outputStream) throws IOException {
        flush();
        int maxLength = getMaxLength();
        int indent = getIndent();
        String lineSeparator = getLineSeparator();
        boolean unicodeNormalizationFormC = getUnicodeNormalizationFormC();
        String encoding = this.escaper.getEncoding();
        boolean z = this.escaper.lineSeparatorSet;
        setOutputStream(outputStream, encoding);
        setIndent(indent);
        setMaxLength(maxLength);
        setUnicodeNormalizationFormC(unicodeNormalizationFormC);
        if (z) {
            setLineSeparator(lineSeparator);
        }
    }

    private void setOutputStream(OutputStream outputStream, String str) throws UnsupportedEncodingException {
        OutputStreamWriter outputStreamWriter;
        if (outputStream == null) {
            throw new NullPointerException("Null OutputStream");
        }
        String upperCase = str.toUpperCase(Locale.ENGLISH);
        if (upperCase.equals("UTF-8")) {
            outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        } else if (upperCase.equals(CharsetNames.CS_UTF16) || upperCase.equals("ISO-10646-UCS-2")) {
            outputStreamWriter = new OutputStreamWriter(outputStream, PdfObject.TEXT_UNICODE);
        } else if (upperCase.equals(CharsetNames.CS_EBCDIC_SUBSET) || upperCase.equals("CP037") || upperCase.equals("EBCDIC-CP-US") || upperCase.equals("EBCDIC-CP-CA") || upperCase.equals("EBCDIC-CP-WA") || upperCase.equals("EBCDIC-CP-NL") || upperCase.equals("CSIBM037")) {
            outputStreamWriter = new EBCDICWriter(outputStream);
        } else {
            outputStreamWriter = (upperCase.equals("ISO-8859-11") || upperCase.equals("TIS-620")) ? new OutputStreamWriter(outputStream, "TIS620") : new OutputStreamWriter(outputStream, str);
        }
        this.escaper = TextWriterFactory.getTextWriter(new UnsynchronizedBufferedWriter(outputStreamWriter), str);
    }

    public void write(Document document) throws IOException {
        this.escaper.reset();
        this.namespaces.reset();
        this.namespaces.declarePrefix("", "");
        writeXMLDeclaration();
        int childCount = document.getChildCount();
        for (int i = 0; i < childCount; i++) {
            writeChild(document.getChild(i));
            this.escaper.breakLine();
        }
        this.escaper.flush();
    }

    protected void writeXMLDeclaration() throws IOException {
        this.escaper.writeUncheckedMarkup("<?xml version=\"1.0\" encoding=\"");
        this.escaper.writeUncheckedMarkup(this.escaper.getEncoding());
        this.escaper.writeUncheckedMarkup("\"?>");
        this.escaper.breakLine();
    }

    protected void write(Element element) throws IOException {
        boolean z = false;
        int childCount = element.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = element.getChild(i);
            if (!child.isText() || !((Text) child).isEmpty()) {
                z = true;
                break;
            }
        }
        if (!z) {
            writeEmptyElementTag(element);
            return;
        }
        boolean zIsPreserveSpace = this.escaper.isPreserveSpace();
        writeStartTag(element);
        int i2 = 0;
        while (i2 < childCount) {
            Node child2 = element.getChild(i2);
            if (this.escaper.getNFC() && child2.isText()) {
                Text text = (Text) child2;
                while (i2 < childCount - 1) {
                    Node child3 = element.getChild(i2 + 1);
                    if (!child3.isText()) {
                        break;
                    }
                    text = new Text(new StringBuffer().append(text.getValue()).append(child3.getValue()).toString());
                    i2++;
                }
                writeChild(text);
            } else {
                writeChild(child2);
            }
            i2++;
        }
        writeEndTag(element);
        this.escaper.setPreserveSpace(zIsPreserveSpace);
    }

    private boolean hasNonTextChildren(Element element) {
        int childCount = element.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!element.getChild(i).isText()) {
                return true;
            }
        }
        return false;
    }

    protected void writeEndTag(Element element) throws IOException {
        this.escaper.decrementIndent();
        if (this.escaper.getIndent() > 0 && !this.escaper.isPreserveSpace() && hasNonTextChildren(element)) {
            this.escaper.breakLine();
        }
        this.escaper.write('<');
        this.escaper.write('/');
        this.escaper.writeName(element.getQualifiedName());
        this.escaper.write('>');
        this.namespaces.popContext();
    }

    protected void writeStartTag(Element element) throws IOException {
        writeTagBeginning(element);
        this.escaper.write('>');
        this.escaper.incrementIndent();
        String attributeValue = element.getAttributeValue("space", "http://www.w3.org/XML/1998/namespace");
        if (attributeValue != null) {
            if ("preserve".equals(attributeValue)) {
                this.escaper.setPreserveSpace(true);
            } else if (PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(attributeValue)) {
                this.escaper.setPreserveSpace(false);
            }
        }
    }

    protected void writeEmptyElementTag(Element element) throws IOException {
        writeTagBeginning(element);
        this.escaper.write('/');
        this.escaper.write('>');
        this.namespaces.popContext();
    }

    private void writeTagBeginning(Element element) throws IOException {
        this.namespaces.pushContext();
        if (this.escaper.isIndenting() && !this.escaper.isPreserveSpace() && !this.escaper.justBroke()) {
            this.escaper.breakLine();
        }
        this.escaper.write('<');
        this.escaper.writeName(element.getQualifiedName());
        writeAttributes(element);
        writeNamespaceDeclarations(element);
    }

    protected void writeAttributes(Element element) throws IOException {
        if (this.preserveBaseURI) {
            ParentNode parent = element.getParent();
            if (element.getAttribute("base", "http://www.w3.org/XML/1998/namespace") == null) {
                String baseURI = element.getBaseURI();
                if (parent == null || parent.isDocument() || !element.getBaseURI().equals(parent.getBaseURI())) {
                    this.escaper.write(' ');
                    write(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", baseURI));
                }
            }
        }
        int attributeCount = element.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = element.getAttribute(i);
            this.escaper.write(' ');
            write(attribute);
        }
    }

    protected void writeNamespaceDeclarations(Element element) throws IOException {
        String namespacePrefix = element.getNamespacePrefix();
        if (!"xml".equals(namespacePrefix)) {
            writeNamespaceDeclarationIfNecessary(namespacePrefix, element.getNamespaceURI());
        }
        int attributeCount = element.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = element.getAttribute(i);
            String namespacePrefix2 = attribute.getNamespacePrefix();
            if (namespacePrefix2.length() != 0 && !"xml".equals(namespacePrefix2)) {
                writeNamespaceDeclarationIfNecessary(namespacePrefix2, attribute.getNamespaceURI());
            }
        }
        Namespaces namespaces = element.namespaces;
        if (namespaces == null) {
            return;
        }
        int size = namespaces.size();
        for (int i2 = 0; i2 < size; i2++) {
            String prefix = namespaces.getPrefix(i2);
            writeNamespaceDeclarationIfNecessary(prefix, namespaces.getURI(prefix));
        }
    }

    private void writeNamespaceDeclarationIfNecessary(String str, String str2) throws IOException {
        String uri = this.namespaces.getURI(str);
        if ((uri == null && "".equals(str2)) || str2.equals(uri)) {
            return;
        }
        this.escaper.write(' ');
        writeNamespaceDeclaration(str, str2);
    }

    protected void writeNamespaceDeclaration(String str, String str2) throws IOException {
        this.namespaces.declarePrefix(str, str2);
        if ("".equals(str)) {
            this.escaper.writeUncheckedMarkup(CanonicalizerBase.XMLNS);
        } else {
            this.escaper.writeUncheckedMarkup("xmlns:");
            this.escaper.writeName(str);
        }
        this.escaper.write('=');
        this.escaper.write('\"');
        this.escaper.writePCDATA(str2);
        this.escaper.write('\"');
    }

    protected void write(Attribute attribute) throws IOException {
        this.escaper.writeName(attribute.getQualifiedName());
        this.escaper.write('=');
        this.escaper.write('\"');
        this.escaper.writeAttributeValue(attribute.getValue());
        this.escaper.write('\"');
    }

    protected void write(Comment comment) throws IOException {
        if (this.escaper.isIndenting()) {
            this.escaper.breakLine();
        }
        this.escaper.writeUncheckedMarkup("<!--");
        this.escaper.writeMarkup(comment.getValue());
        this.escaper.writeUncheckedMarkup("-->");
    }

    protected void write(ProcessingInstruction processingInstruction) throws IOException {
        if (this.escaper.isIndenting()) {
            this.escaper.breakLine();
        }
        this.escaper.writeUncheckedMarkup("<?");
        this.escaper.writeName(processingInstruction.getTarget());
        String value = processingInstruction.getValue();
        if (!"".equals(value)) {
            this.escaper.write(' ');
            this.escaper.writeMarkup(value);
        }
        this.escaper.writeUncheckedMarkup("?>");
    }

    protected void write(Text text) throws IOException {
        String value = text.getValue();
        if (!text.isCDATASection() || value.indexOf("]]>") != -1) {
            if (isBoundaryWhitespace(text, value)) {
                return;
            }
            this.escaper.writePCDATA(value);
            return;
        }
        if (!(this.escaper instanceof UnicodeWriter)) {
            int length = value.length();
            for (int i = 0; i < length; i++) {
                if (this.escaper.needsEscaping(value.charAt(i))) {
                    this.escaper.writePCDATA(value);
                    return;
                }
            }
        }
        this.escaper.writeUncheckedMarkup("<![CDATA[");
        this.escaper.writeMarkup(value);
        this.escaper.writeUncheckedMarkup("]]>");
    }

    private boolean isBoundaryWhitespace(Text text, String str) {
        if (getIndent() <= 0) {
            return false;
        }
        ParentNode parent = text.getParent();
        if (parent == null) {
            return "".equals(str.trim());
        }
        int childCount = parent.getChildCount();
        if (childCount == 1 || !"".equals(str.trim())) {
            return false;
        }
        int iIndexOf = parent.indexOf(text);
        Node child = null;
        Node child2 = null;
        if (iIndexOf != 0) {
            child = parent.getChild(iIndexOf - 1);
        }
        if (iIndexOf != childCount - 1) {
            child2 = parent.getChild(iIndexOf + 1);
        }
        if (child == null || !child.isText()) {
            return child2 == null || !child2.isText();
        }
        return false;
    }

    protected void write(DocType docType) throws IOException {
        this.escaper.writeUncheckedMarkup("<!DOCTYPE ");
        this.escaper.writeName(docType.getRootElementName());
        if (docType.getPublicID() != null) {
            this.escaper.writeMarkup(new StringBuffer().append(" PUBLIC \"").append(docType.getPublicID()).append("\" \"").append(docType.getSystemID()).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString());
        } else if (docType.getSystemID() != null) {
            this.escaper.writeMarkup(new StringBuffer().append(" SYSTEM \"").append(docType.getSystemID()).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString());
        }
        String internalDTDSubset = docType.getInternalDTDSubset();
        if (!internalDTDSubset.equals("")) {
            this.escaper.writeUncheckedMarkup(" [");
            this.escaper.breakLine();
            this.escaper.setInDocType(true);
            this.escaper.writeMarkup(internalDTDSubset);
            this.escaper.setInDocType(false);
            this.escaper.write(']');
        }
        this.escaper.write('>');
    }

    protected void writeChild(Node node) throws IOException {
        if (node.isElement()) {
            write((Element) node);
            return;
        }
        if (node.isText()) {
            write((Text) node);
            return;
        }
        if (node.isComment()) {
            write((Comment) node);
        } else if (node.isProcessingInstruction()) {
            write((ProcessingInstruction) node);
        } else {
            if (!node.isDocType()) {
                throw new XMLException(new StringBuffer().append("Cannot write a ").append(node.getClass().getName()).append(" from the writeChild() method").toString());
            }
            write((DocType) node);
        }
    }

    protected final void writeEscaped(String str) throws IOException {
        this.escaper.writePCDATA(str);
    }

    protected final void writeAttributeValue(String str) throws IOException {
        this.escaper.writeAttributeValue(str);
    }

    protected final void writeRaw(String str) throws IOException {
        this.escaper.writeMarkup(str);
    }

    protected final void breakLine() throws IOException {
        this.escaper.breakLine();
    }

    public void flush() throws IOException {
        this.escaper.flush();
    }

    public int getIndent() {
        return this.escaper.getIndent();
    }

    public void setIndent(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Indent cannot be negative");
        }
        this.escaper.setIndent(i);
    }

    public String getLineSeparator() {
        return this.escaper.getLineSeparator();
    }

    public void setLineSeparator(String str) {
        this.escaper.setLineSeparator(str);
    }

    public int getMaxLength() {
        return this.escaper.getMaxLength();
    }

    public void setMaxLength(int i) {
        this.escaper.setMaxLength(i);
    }

    public boolean getPreserveBaseURI() {
        return this.preserveBaseURI;
    }

    public void setPreserveBaseURI(boolean z) {
        this.preserveBaseURI = z;
    }

    public String getEncoding() {
        return this.escaper.getEncoding();
    }

    public void setUnicodeNormalizationFormC(boolean z) {
        this.escaper.setNFC(z);
    }

    public boolean getUnicodeNormalizationFormC() {
        return this.escaper.getNFC();
    }

    protected final int getColumnNumber() {
        return this.escaper.getColumnNumber();
    }
}
