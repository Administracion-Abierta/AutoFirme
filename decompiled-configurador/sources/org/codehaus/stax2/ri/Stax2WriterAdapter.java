package org.codehaus.stax2.ri;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.util.StreamWriterDelegate;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/Stax2WriterAdapter.class */
public class Stax2WriterAdapter extends StreamWriterDelegate implements XMLStreamWriter2, XMLStreamConstants {
    protected String mEncoding;
    protected SimpleValueEncoder mValueEncoder;
    protected final boolean mNsRepairing;

    protected Stax2WriterAdapter(XMLStreamWriter sw) {
        super(sw);
        this.mDelegate = sw;
        Object value = sw.getProperty("javax.xml.stream.isRepairingNamespaces");
        this.mNsRepairing = (value instanceof Boolean) && ((Boolean) value).booleanValue();
    }

    public static XMLStreamWriter2 wrapIfNecessary(XMLStreamWriter sw) {
        if (sw instanceof XMLStreamWriter2) {
            return (XMLStreamWriter2) sw;
        }
        return new Stax2WriterAdapter(sw);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBoolean(boolean b) throws XMLStreamException {
        this.mDelegate.writeCharacters(b ? "true" : "false");
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInt(int value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLong(long value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloat(float value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDouble(double value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInteger(BigInteger value) throws XMLStreamException {
        this.mDelegate.writeCharacters(value.toString());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        this.mDelegate.writeCharacters(value.toString());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQName(QName name) throws XMLStreamException {
        this.mDelegate.writeCharacters(serializeQNameValue(name));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(getValueEncoder().encodeAsString(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(getValueEncoder().encodeAsString(v, value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        writeBinary(Base64Variants.getDefaultVariant(), value, from, length);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean b) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, b ? "true" : "false");
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, serializeQNameValue(name));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        writeBinaryAttribute(Base64Variants.getDefaultVariant(), prefix, nsURI, localName, value);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, getValueEncoder().encodeAsString(v, value, 0, value.length));
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public boolean setProperty(String name, Object value) {
        throw new IllegalArgumentException("No settable property '" + name + "'");
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public XMLStreamLocation2 getLocation() {
        return null;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public String getEncoding() {
        return this.mEncoding;
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        writeCData(new String(text, start, len));
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE");
        sb.append(rootName);
        if (systemId != null) {
            if (publicId != null) {
                sb.append(" PUBLIC \"");
                sb.append(publicId);
                sb.append("\" \"");
            } else {
                sb.append(" SYSTEM \"");
            }
            sb.append(systemId);
            sb.append('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            sb.append(" [");
            sb.append(internalSubset);
            sb.append(']');
        }
        sb.append('>');
        writeDTD(sb.toString());
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate.writeCharacters("");
        this.mDelegate.writeEndElement();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(String text) throws XMLStreamException {
        writeRaw(text);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        writeRaw(text, offset, length);
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        writeStartDocument(encoding, version);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text) throws XMLStreamException {
        writeRaw(text, 0, text.length());
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(String text, int offset, int len) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
        writeRaw(new String(text, offset, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void copyEventFromReader(XMLStreamReader2 sr, boolean preserveEventData) throws XMLStreamException {
        switch (sr.getEventType()) {
            case 1:
                copyStartElement(sr);
                return;
            case 2:
                writeEndElement();
                return;
            case 3:
                writeProcessingInstruction(sr.getPITarget(), sr.getPIData());
                return;
            case 4:
                writeCharacters(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            case 5:
                writeComment(sr.getText());
                return;
            case 6:
                writeSpace(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            case 7:
                String version = sr.getVersion();
                if (version != null && version.length() != 0) {
                    if (sr.standaloneSet()) {
                        writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                        return;
                    } else {
                        writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                        return;
                    }
                }
                return;
            case 8:
                writeEndDocument();
                return;
            case 9:
                writeEntityRef(sr.getLocalName());
                return;
            case 10:
            case 13:
            case 14:
            case 15:
            default:
                throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
            case 11:
                DTDInfo info = sr.getDTDInfo();
                if (info == null) {
                    throw new XMLStreamException("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                }
                writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
                return;
            case 12:
                writeCData(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
        }
    }

    @Override // org.codehaus.stax2.XMLStreamWriter2
    public void closeCompletely() throws XMLStreamException {
        close();
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        throw new UnsupportedOperationException("Not yet implemented");
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

    protected void copyStartElement(XMLStreamReader sr) throws XMLStreamException {
        int nsCount = sr.getNamespaceCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; i++) {
                String prefix = sr.getNamespacePrefix(i);
                String uri = sr.getNamespaceURI(i);
                if (prefix == null || prefix.length() == 0) {
                    setDefaultNamespace(uri);
                } else {
                    setPrefix(prefix, uri);
                }
            }
        }
        writeStartElement(sr.getPrefix(), sr.getLocalName(), sr.getNamespaceURI());
        if (nsCount > 0) {
            for (int i2 = 0; i2 < nsCount; i2++) {
                String prefix2 = sr.getNamespacePrefix(i2);
                String uri2 = sr.getNamespaceURI(i2);
                if (prefix2 == null || prefix2.length() == 0) {
                    writeDefaultNamespace(uri2);
                } else {
                    writeNamespace(prefix2, uri2);
                }
            }
        }
        int attrCount = sr.getAttributeCount();
        if (attrCount > 0) {
            for (int i3 = 0; i3 < attrCount; i3++) {
                writeAttribute(sr.getAttributePrefix(i3), sr.getAttributeNamespace(i3), sr.getAttributeLocalName(i3), sr.getAttributeValue(i3));
            }
        }
    }

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
}
