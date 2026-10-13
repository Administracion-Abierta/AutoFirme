package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.ri.typed.ValueEncoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/TypedStreamWriter.class */
public abstract class TypedStreamWriter extends BaseStreamWriter {
    protected ValueEncoderFactory mValueEncoderFactory;

    protected abstract void writeTypedAttribute(String str, String str2, String str3, AsciiValueEncoder asciiValueEncoder) throws XMLStreamException;

    protected TypedStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg);
    }

    protected final ValueEncoderFactory valueEncoderFactory() {
        if (this.mValueEncoderFactory == null) {
            this.mValueEncoderFactory = new ValueEncoderFactory();
        }
        return this.mValueEncoderFactory;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBoolean(boolean value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInt(int value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLong(long value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloat(float value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDouble(double value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeInteger(BigInteger value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQName(QName name) throws XMLStreamException {
        writeCharacters(serializeQName(name));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public final void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        Base64Variant v = Base64Variants.getDefaultVariant();
        writeTypedElement(valueEncoderFactory().getEncoder(v, value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        writeTypedElement(valueEncoderFactory().getEncoder(v, value, from, length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void writeTypedElement(AsciiValueEncoder enc) throws XMLStreamException {
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && inPrologOrEpilog()) {
            reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            reportInvalidContent(4);
        }
        try {
            XMLValidator vld = this.mVldContent == 3 ? this.mValidator : null;
            if (vld == null) {
                this.mWriter.writeTypedElement(enc);
            } else {
                this.mWriter.writeTypedElement(enc, vld, getCopyBuffer());
            }
        } catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        writeAttribute(prefix, nsURI, localName, serializeQName(name));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        Base64Variant v = Base64Variants.getDefaultVariant();
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamWriter
    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        writeTypedAttribute(prefix, nsURI, localName, valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }

    private String serializeQName(QName name) throws XMLStreamException {
        String vp = validateQNamePrefix(name);
        String local = name.getLocalPart();
        if (vp == null || vp.length() == 0) {
            return local;
        }
        return vp + ":" + local;
    }
}
