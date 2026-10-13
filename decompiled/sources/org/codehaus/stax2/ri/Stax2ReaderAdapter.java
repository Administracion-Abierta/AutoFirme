package org.codehaus.stax2.ri;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/Stax2ReaderAdapter.class */
public class Stax2ReaderAdapter extends StreamReaderDelegate implements XMLStreamReader2, AttributeInfo, DTDInfo, LocationInfo {
    static final int INT_SPACE = 32;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder;
    protected int _depth;
    protected String _typedContent;

    protected Stax2ReaderAdapter(XMLStreamReader sr) {
        super(sr);
        this._base64Decoder = null;
        this._depth = 0;
    }

    public static XMLStreamReader2 wrapIfNecessary(XMLStreamReader sr) {
        if (sr instanceof XMLStreamReader2) {
            return (XMLStreamReader2) sr;
        }
        return new Stax2ReaderAdapter(sr);
    }

    public int next() throws XMLStreamException {
        if (this._typedContent != null) {
            this._typedContent = null;
            return 2;
        }
        int type = super.next();
        if (type == 1) {
            this._depth++;
        } else if (type == 2) {
            this._depth--;
        }
        return type;
    }

    public String getElementText() throws XMLStreamException {
        boolean hadStart = getEventType() == 1;
        String text = super.getElementText();
        if (hadStart) {
            this._depth--;
        }
        return text;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = _decoderFactory().getBooleanDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = _decoderFactory().getIntDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = _decoderFactory().getLongDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = _decoderFactory().getFloatDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = _decoderFactory().getDoubleDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = _decoderFactory().getIntegerDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = _decoderFactory().getDecimalDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = _decoderFactory().getQNameDecoder(getNamespaceContext());
        getElementAs(dec);
        return dec.getValue();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary() throws XMLStreamException {
        return getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        Stax2Util.ByteAggregator aggr = _base64Decoder().getByteAggregator();
        byte[] bArrStartAggregation = aggr.startAggregation();
        while (true) {
            byte[] buffer = bArrStartAggregation;
            int offset = 0;
            int len = buffer.length;
            do {
                int readCount = readElementAsBinary(buffer, offset, len, v);
                if (readCount < 1) {
                    return aggr.aggregateAll(buffer, offset);
                }
                offset += readCount;
                len -= readCount;
            } while (len > 0);
            bArrStartAggregation = aggr.addFullBlock(buffer);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        String value = Stax2Util.trimSpaces(getElementText());
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, value);
        }
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getIntArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getLongArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getFloatArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getDoubleArrayDecoder(value, from, length));
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00cf */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int readElementAsArray(org.codehaus.stax2.typed.TypedArrayDecoder r8) throws javax.xml.stream.XMLStreamException {
        /*
            Method dump skipped, instruction units count: 251
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.codehaus.stax2.ri.Stax2ReaderAdapter.readElementAsArray(org.codehaus.stax2.typed.TypedArrayDecoder):int");
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        int type;
        if (resultBuffer == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Illegal offset (" + offset + "), must be [0, " + resultBuffer.length + "[");
        }
        if (maxLength < 1 || offset + maxLength > resultBuffer.length) {
            if (maxLength == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + maxLength + "), has to be positive number, and offset+maxLength can not exceed" + resultBuffer.length);
        }
        StringBase64Decoder dec = _base64Decoder();
        int type2 = getEventType();
        if (((1 << type2) & MASK_TYPED_ACCESS_BINARY) == 0) {
            if (type2 == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                throwNotStartElemOrTextual(type2);
            }
        }
        if (type2 == 1) {
            while (true) {
                int type3 = next();
                if (type3 == 2) {
                    return -1;
                }
                if (type3 != 5 && type3 != 3) {
                    if (((1 << type3) & MASK_GET_ELEMENT_TEXT) == 0) {
                        throwNotStartElemOrTextual(type3);
                    }
                    dec.init(v, true, getText());
                    break;
                }
            }
        }
        int totalCount = 0;
        while (true) {
            try {
                int count = dec.decode(resultBuffer, offset, maxLength);
                offset += count;
                totalCount += count;
                maxLength -= count;
                if (maxLength < 1 || getEventType() == 2) {
                    break;
                }
                while (true) {
                    type = next();
                    if (type != 5 && type != 3 && type != 6) {
                        break;
                    }
                }
                if (type == 2) {
                    int left = dec.endOfContent();
                    if (left < 0) {
                        throw _constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                    }
                    if (left <= 0) {
                        break;
                    }
                } else {
                    if (((1 << type) & MASK_GET_ELEMENT_TEXT) == 0) {
                        throwNotStartElemOrTextual(type);
                    }
                    dec.init(v, false, getText());
                }
            } catch (IllegalArgumentException iae) {
                throw _constructTypeException(iae, "");
            }
        }
        if (totalCount > 0) {
            return totalCount;
        }
        return -1;
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeIndex(String namespaceURI, String localName) {
        return findAttributeIndex(namespaceURI, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = _decoderFactory().getBooleanDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsInt(int index) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = _decoderFactory().getIntDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getAttributeAsLong(int index) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = _decoderFactory().getLongDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = _decoderFactory().getFloatDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = _decoderFactory().getDoubleDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = _decoderFactory().getIntegerDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = _decoderFactory().getDecimalDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = _decoderFactory().getQNameDecoder(getNamespaceContext());
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        String value = Stax2Util.trimSpaces(getAttributeValue(index));
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, value);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = _decoderFactory().getIntArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = _decoderFactory().getLongArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = _decoderFactory().getFloatArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = _decoderFactory().getDoubleArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return _getAttributeAsArray(tad, getAttributeValue(index));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int _getAttributeAsArray(TypedArrayDecoder tad, String attrValue) throws XMLStreamException {
        int ptr = 0;
        int end = attrValue.length();
        String lexical = null;
        int count = 0;
        loop0: while (ptr < end) {
            while (attrValue.charAt(ptr) <= ' ') {
                try {
                    ptr++;
                    if (ptr >= end) {
                        break loop0;
                    }
                } catch (IllegalArgumentException iae) {
                    Location loc = getLocation();
                    throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
                }
            }
            int start = ptr;
            int ptr2 = ptr + 1;
            while (ptr2 < end && attrValue.charAt(ptr2) > ' ') {
                ptr2++;
            }
            int tokenEnd = ptr2;
            ptr = ptr2 + 1;
            lexical = attrValue.substring(start, tokenEnd);
            count++;
            if (tad.decodeValue(lexical) && !checkExpand(tad)) {
                break;
            }
        }
        return count;
    }

    private final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder) tad).expand();
            return true;
        }
        return false;
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        String lexical = getAttributeValue(index);
        StringBase64Decoder dec = _base64Decoder();
        dec.init(v, true, lexical);
        try {
            return dec.decodeCompletely();
        } catch (IllegalArgumentException iae) {
            throw new TypedXMLStreamException(lexical, iae.getMessage(), getLocation(), iae);
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public Object getFeature(String name) {
        return null;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public void setFeature(String name, Object value) {
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean setProperty(String name, Object value) {
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public void skipElement() throws XMLStreamException {
        if (getEventType() != 1) {
            throwNotStartElem(getEventType());
        }
        int nesting = 1;
        while (true) {
            int type = next();
            if (type == 1) {
                nesting++;
            } else if (type == 2) {
                nesting--;
                if (nesting == 0) {
                    return;
                }
            } else {
                continue;
            }
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (getEventType() != 1) {
            throwNotStartElem(getEventType());
        }
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (getEventType() != 11) {
            return null;
        }
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public final LocationInfo getLocationInfo() {
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getText(Writer w, boolean preserveContents) throws XMLStreamException, IOException {
        char[] cbuf = getTextCharacters();
        int start = getTextStart();
        int len = getTextLength();
        if (len > 0) {
            w.write(cbuf, start, len);
        }
        return len;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getDepth() {
        if (getEventType() == 2) {
            return this._depth + 1;
        }
        return this._depth;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public NamespaceContext getNonTransientNamespaceContext() {
        return null;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public String getPrefixedName() {
        switch (getEventType()) {
            case 1:
            case 2:
                String prefix = getPrefix();
                String ln = getLocalName();
                if (prefix == null || prefix.length() == 0) {
                    return ln;
                }
                StringBuffer sb = new StringBuffer(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return sb.toString();
            case 3:
                return getPITarget();
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
            case 9:
                return getLocalName();
            case 11:
                return getDTDRootName();
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public void closeCompletely() throws XMLStreamException {
        close();
    }

    @Override // org.codehaus.stax2.AttributeInfo, org.codehaus.stax2.validation.ValidationContext
    public int findAttributeIndex(String nsURI, String localName) {
        if ("".equals(nsURI)) {
            nsURI = null;
        }
        int len = getAttributeCount();
        for (int i = 0; i < len; i++) {
            if (getAttributeLocalName(i).equals(localName)) {
                String otherUri = getAttributeNamespace(i);
                if (nsURI == null) {
                    if (otherUri == null || otherUri.length() == 0) {
                        return i;
                    }
                } else if (nsURI.equals(otherUri)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public int getIdAttributeIndex() {
        int len = getAttributeCount();
        for (int i = 0; i < len; i++) {
            if ("ID".equals(getAttributeType(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public int getNotationAttributeIndex() {
        int len = getAttributeCount();
        for (int i = 0; i < len; i++) {
            if (DTDParser.TYPE_NOTATION.equals(getAttributeType(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public Object getProcessedDTD() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDRootName() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDPublicId() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDSystemId() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDInternalSubset() {
        if (getEventType() == 11) {
            return getText();
        }
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingCharOffset() {
        return 0L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public XMLStreamLocation2 getStartLocation() {
        return getCurrentLocation();
    }

    @Override // org.codehaus.stax2.LocationInfo
    public XMLStreamLocation2 getCurrentLocation() {
        return new Stax2LocationAdapter(getLocation());
    }

    @Override // org.codehaus.stax2.LocationInfo
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return getCurrentLocation();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        throwUnsupported();
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        throwUnsupported();
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        throwUnsupported();
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected StringBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new StringBase64Decoder();
        }
        return this._base64Decoder;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }

    protected void throwNotStartElem(int type) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(type) + ") not START_ELEMENT");
    }

    protected void throwNotStartElemOrTextual(int type) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(type) + ") not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA");
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        String msg = iae.getMessage();
        if (msg == null) {
            msg = "";
        }
        Location loc = getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg, iae);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc, iae);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        Location loc = getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }
}
