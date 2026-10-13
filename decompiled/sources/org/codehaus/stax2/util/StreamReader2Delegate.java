package org.codehaus.stax2.util;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/util/StreamReader2Delegate.class */
public class StreamReader2Delegate extends StreamReaderDelegate implements XMLStreamReader2 {
    protected XMLStreamReader2 _delegate2;

    public StreamReader2Delegate(XMLStreamReader2 sr) {
        super(sr);
        this._delegate2 = sr;
    }

    public void setParent(XMLStreamReader pr) {
        super.setParent(pr);
        this._delegate2 = (XMLStreamReader2) pr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public void closeCompletely() throws XMLStreamException {
        this._delegate2.closeCompletely();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        return this._delegate2.getAttributeInfo();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public DTDInfo getDTDInfo() throws XMLStreamException {
        return this._delegate2.getDTDInfo();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getDepth() {
        return this._delegate2.getDepth();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public Object getFeature(String name) {
        return this._delegate2.getFeature(name);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public LocationInfo getLocationInfo() {
        return this._delegate2.getLocationInfo();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public NamespaceContext getNonTransientNamespaceContext() {
        return this._delegate2.getNonTransientNamespaceContext();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public String getPrefixedName() {
        return this._delegate2.getPrefixedName();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getText(Writer w, boolean preserveContents) throws XMLStreamException, IOException {
        return this._delegate2.getText(w, preserveContents);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isEmptyElement() throws XMLStreamException {
        return this._delegate2.isEmptyElement();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isPropertySupported(String name) {
        return this._delegate2.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public void setFeature(String name, Object value) {
        this._delegate2.setFeature(name, value);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean setProperty(String name, Object value) {
        return this._delegate2.setProperty(name, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public void skipElement() throws XMLStreamException {
        this._delegate2.skipElement();
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return this._delegate2.setValidationProblemHandler(h);
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this._delegate2.stopValidatingAgainst(schema);
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return this._delegate2.stopValidatingAgainst(validator);
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this._delegate2.validateAgainst(schema);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeIndex(String namespaceURI, String localName) {
        return this._delegate2.getAttributeIndex(namespaceURI, localName);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsBoolean(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDecimal(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDouble(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsFloat(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsInt(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsInt(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsInteger(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getAttributeAsLong(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsLong(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsQName(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsIntArray(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsLongArray(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsFloatArray(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDoubleArray(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        this._delegate2.getElementAs(tvd);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getElementAsBoolean() throws XMLStreamException {
        return this._delegate2.getElementAsBoolean();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        return this._delegate2.getElementAsDecimal();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getElementAsDouble() throws XMLStreamException {
        return this._delegate2.getElementAsDouble();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getElementAsFloat() throws XMLStreamException {
        return this._delegate2.getElementAsFloat();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getElementAsInt() throws XMLStreamException {
        return this._delegate2.getElementAsInt();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getElementAsInteger() throws XMLStreamException {
        return this._delegate2.getElementAsInteger();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getElementAsLong() throws XMLStreamException {
        return this._delegate2.getElementAsLong();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getElementAsQName() throws XMLStreamException {
        return this._delegate2.getElementAsQName();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this._delegate2.getElementAsBinary();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        return this._delegate2.getElementAsBinary(v);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        this._delegate2.getAttributeAs(index, tvd);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return this._delegate2.getAttributeAsArray(index, tad);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsBinary(index);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        return this._delegate2.getAttributeAsBinary(index, v);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsDoubleArray(value, from, length);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsFloatArray(value, from, length);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsIntArray(value, from, length);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsLongArray(value, from, length);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
        return this._delegate2.readElementAsArray(tad);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this._delegate2.readElementAsBinary(resultBuffer, offset, maxLength);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        return this._delegate2.readElementAsBinary(resultBuffer, offset, maxLength, v);
    }
}
