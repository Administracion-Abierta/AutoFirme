package org.codehaus.stax2.ri.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/dom/DOMWrappingWriter.class */
public abstract class DOMWrappingWriter implements XMLStreamWriter2 {
    static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
    static final String DEFAULT_XML_VERSION = "1.0";
    protected final boolean mNsAware;
    protected final boolean mNsRepairing;
    protected String mEncoding = null;
    protected NamespaceContext mNsContext;
    protected final Document mDocument;
    protected SimpleValueEncoder mValueEncoder;

    public abstract NamespaceContext getNamespaceContext();

    public abstract String getPrefix(String str);

    public abstract Object getProperty(String str);

    public abstract void setDefaultNamespace(String str);

    public abstract void setPrefix(String str, String str2) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2, String str3) throws XMLStreamException;

    public abstract void writeAttribute(String str, String str2, String str3, String str4) throws XMLStreamException;

    public abstract void writeDefaultNamespace(String str) throws XMLStreamException;

    public abstract void writeEmptyElement(String str) throws XMLStreamException;

    public abstract void writeEmptyElement(String str, String str2) throws XMLStreamException;

    public abstract void writeEmptyElement(String str, String str2, String str3) throws XMLStreamException;

    public abstract void writeEndDocument() throws XMLStreamException;

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract boolean isPropertySupported(String str);

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract boolean setProperty(String str, Object obj);

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public abstract void writeDTD(String str, String str2, String str3, String str4) throws XMLStreamException;

    protected abstract void appendLeaf(Node node) throws IllegalStateException;

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected DOMWrappingWriter(Node treeRoot, boolean nsAware, boolean nsRepairing) throws XMLStreamException {
        if (treeRoot == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamWriter");
        }
        this.mNsAware = nsAware;
        this.mNsRepairing = nsRepairing;
        switch (treeRoot.getNodeType()) {
            case 1:
                this.mDocument = treeRoot.getOwnerDocument();
                break;
            case 9:
                this.mDocument = (Document) treeRoot;
                break;
            case 11:
                this.mDocument = treeRoot.getOwnerDocument();
                break;
            default:
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + treeRoot.getClass());
        }
        if (this.mDocument == null) {
            throw new XMLStreamException("Can not create an XMLStreamWriter for given node (of type " + treeRoot.getClass() + "): did not have owner document");
        }
    }

    public void close() {
    }

    public void flush() {
    }

    public void setNamespaceContext(NamespaceContext context) {
        this.mNsContext = context;
    }

    public void writeCData(String data) throws XMLStreamException {
        appendLeaf(this.mDocument.createCDATASection(data));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        writeCharacters(new String(text, start, len));
    }

    public void writeCharacters(String text) throws XMLStreamException {
        appendLeaf(this.mDocument.createTextNode(text));
    }

    public void writeComment(String data) throws XMLStreamException {
        appendLeaf(this.mDocument.createComment(data));
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        reportUnsupported("writeDTD()");
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        appendLeaf(this.mDocument.createEntityReference(name));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        writeProcessingInstruction(target, null);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        appendLeaf(this.mDocument.createProcessingInstruction(target, data));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument() throws XMLStreamException {
        writeStartDocument("UTF-8", "1.0");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument(null, version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.mEncoding = encoding;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public XMLStreamLocation2 getLocation() {
        return null;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public String getEncoding() {
        return this.mEncoding;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        writeCData(new String(text, start, len));
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeFullEndElement() throws XMLStreamException {
        writeEndElement();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(char[] text, int start, int len) throws XMLStreamException {
        writeSpace(new String(text, start, len));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(String text) throws XMLStreamException {
        writeCharacters(text);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        writeStartDocument(encoding, version);
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text) throws XMLStreamException {
        reportUnsupported("writeRaw()");
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text, int start, int offset) throws XMLStreamException {
        reportUnsupported("writeRaw()");
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
        reportUnsupported("writeRaw()");
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void copyEventFromReader(XMLStreamReader2 r, boolean preserveEventData) throws XMLStreamException {
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void closeCompletely() {
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBoolean(boolean value) throws XMLStreamException {
        writeCharacters(value ? "true" : "false");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInt(int value) throws XMLStreamException {
        writeCharacters(String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLong(long value) throws XMLStreamException {
        writeCharacters(String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloat(float value) throws XMLStreamException {
        writeCharacters(String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDouble(double value) throws XMLStreamException {
        writeCharacters(String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInteger(BigInteger value) throws XMLStreamException {
        writeCharacters(value.toString());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        writeCharacters(value.toString());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQName(QName name) throws XMLStreamException {
        writeCharacters(serializeQNameValue(name));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        writeBinary(Base64Variants.getDefaultVariant(), value, from, length);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        writeCharacters(getValueEncoder().encodeAsString(v, value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, value ? "true" : "false");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, value.toString());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, value.toString());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, serializeQNameValue(name));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        writeBinaryAttribute(Base64Variants.getDefaultVariant(), prefix, nsURI, localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(v, value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected String serializeQNameValue(QName name) throws XMLStreamException {
        String prefix;
        if (this.mNsRepairing) {
            String uri = name.getNamespaceURI();
            NamespaceContext ctxt = getNamespaceContext();
            prefix = ctxt == null ? null : ctxt.getPrefix(uri);
            if (prefix == null) {
                String origPrefix = name.getPrefix();
                if (origPrefix == null || origPrefix.length() == 0) {
                    prefix = "";
                    writeDefaultNamespace(uri);
                } else {
                    prefix = origPrefix;
                    writeNamespace(prefix, uri);
                }
            }
        } else {
            prefix = name.getPrefix();
        }
        String local = name.getLocalPart();
        if (prefix == null || prefix.length() == 0) {
            return local;
        }
        return prefix + ":" + local;
    }

    protected SimpleValueEncoder getValueEncoder() {
        if (this.mValueEncoder == null) {
            this.mValueEncoder = new SimpleValueEncoder();
        }
        return this.mValueEncoder;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void throwOutputError(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected static void throwOutputError(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        throwOutputError(msg);
    }

    protected void reportUnsupported(String operName) {
        throw new UnsupportedOperationException(operName + " can not be used with DOM-backed writer");
    }
}
