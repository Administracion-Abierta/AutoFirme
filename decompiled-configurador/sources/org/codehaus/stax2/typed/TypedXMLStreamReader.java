package org.codehaus.stax2.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/typed/TypedXMLStreamReader.class */
public interface TypedXMLStreamReader extends XMLStreamReader {
    boolean getElementAsBoolean() throws XMLStreamException;

    int getElementAsInt() throws XMLStreamException;

    long getElementAsLong() throws XMLStreamException;

    float getElementAsFloat() throws XMLStreamException;

    double getElementAsDouble() throws XMLStreamException;

    BigInteger getElementAsInteger() throws XMLStreamException;

    BigDecimal getElementAsDecimal() throws XMLStreamException;

    QName getElementAsQName() throws XMLStreamException;

    byte[] getElementAsBinary() throws XMLStreamException;

    byte[] getElementAsBinary(Base64Variant base64Variant) throws XMLStreamException;

    void getElementAs(TypedValueDecoder typedValueDecoder) throws XMLStreamException;

    int readElementAsBinary(byte[] bArr, int i, int i2, Base64Variant base64Variant) throws XMLStreamException;

    int readElementAsBinary(byte[] bArr, int i, int i2) throws XMLStreamException;

    int readElementAsIntArray(int[] iArr, int i, int i2) throws XMLStreamException;

    int readElementAsLongArray(long[] jArr, int i, int i2) throws XMLStreamException;

    int readElementAsFloatArray(float[] fArr, int i, int i2) throws XMLStreamException;

    int readElementAsDoubleArray(double[] dArr, int i, int i2) throws XMLStreamException;

    int readElementAsArray(TypedArrayDecoder typedArrayDecoder) throws XMLStreamException;

    int getAttributeIndex(String str, String str2);

    boolean getAttributeAsBoolean(int i) throws XMLStreamException;

    int getAttributeAsInt(int i) throws XMLStreamException;

    long getAttributeAsLong(int i) throws XMLStreamException;

    float getAttributeAsFloat(int i) throws XMLStreamException;

    double getAttributeAsDouble(int i) throws XMLStreamException;

    BigInteger getAttributeAsInteger(int i) throws XMLStreamException;

    BigDecimal getAttributeAsDecimal(int i) throws XMLStreamException;

    QName getAttributeAsQName(int i) throws XMLStreamException;

    void getAttributeAs(int i, TypedValueDecoder typedValueDecoder) throws XMLStreamException;

    byte[] getAttributeAsBinary(int i) throws XMLStreamException;

    byte[] getAttributeAsBinary(int i, Base64Variant base64Variant) throws XMLStreamException;

    int[] getAttributeAsIntArray(int i) throws XMLStreamException;

    long[] getAttributeAsLongArray(int i) throws XMLStreamException;

    float[] getAttributeAsFloatArray(int i) throws XMLStreamException;

    double[] getAttributeAsDoubleArray(int i) throws XMLStreamException;

    int getAttributeAsArray(int i, TypedArrayDecoder typedArrayDecoder) throws XMLStreamException;
}
