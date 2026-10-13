package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputData;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/TypedStreamReader.class */
public class TypedStreamReader extends BasicStreamReader {
    protected static final int MASK_TYPED_ACCESS_ARRAY = 4182;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    static final int MIN_BINARY_CHUNK = 2000;
    protected ValueDecoderFactory _decoderFactory;
    protected CharArrayBase64Decoder _base64Decoder;

    protected TypedStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
        this._base64Decoder = null;
    }

    public static TypedStreamReader createStreamReader(BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputBootstrapper bs, boolean forER) throws XMLStreamException {
        TypedStreamReader sr = new TypedStreamReader(bs, input, owner, cfg, createElementStack(cfg), forER);
        return sr;
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
        return _verifyQName(dec.getValue());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public final byte[] getElementAsBinary() throws XMLStreamException {
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
        if (this.mCurrToken != 1) {
            throwParseError(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        if (this.mStEmptyElem) {
            this.mStEmptyElem = false;
            this.mCurrToken = 2;
            _handleEmptyValue(tvd);
            return;
        }
        while (true) {
            int type = next();
            if (type == 2) {
                _handleEmptyValue(tvd);
                return;
            }
            if (type != 5 && type != 3) {
                if (((1 << type) & 4688) == 0) {
                    throwParseError("Expected a text token, got " + tokenTypeDesc(type) + ".");
                }
                if (this.mTokenState < 3) {
                    readCoalescedText(this.mCurrToken, false);
                }
                if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
                    this.mInputPtr += 2;
                    this.mCurrToken = 2;
                    try {
                        this.mTextBuffer.decode(tvd);
                        readEndElem();
                        return;
                    } catch (IllegalArgumentException iae) {
                        throw _constructTypeException(iae, this.mTextBuffer.contentsAsString());
                    }
                }
                int extra = 1 + (this.mTextBuffer.size() >> 1);
                StringBuilder sb = this.mTextBuffer.contentsAsStringBuilder(extra);
                while (true) {
                    int type2 = next();
                    if (type2 == 2) {
                        break;
                    }
                    if (((1 << type2) & 4688) != 0) {
                        if (this.mTokenState < 3) {
                            readCoalescedText(type2, false);
                        }
                        this.mTextBuffer.contentsToStringBuilder(sb);
                    } else if (type2 != 5 && type2 != 3) {
                        throwParseError("Expected a text token, got " + tokenTypeDesc(type2) + ".");
                    }
                }
                String str = sb.toString();
                String tstr = Stax2Util.trimSpaces(str);
                if (tstr == null) {
                    _handleEmptyValue(tvd);
                    return;
                }
                try {
                    tvd.decode(tstr);
                    return;
                } catch (IllegalArgumentException iae2) {
                    throw _constructTypeException(iae2, str);
                }
            }
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

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public final int readElementAsArray(TypedArrayDecoder dec) throws XMLStreamException {
        int type = this.mCurrToken;
        if (((1 << type) & MASK_TYPED_ACCESS_ARRAY) == 0) {
            throwNotTextualOrElem(type);
        }
        if (type == 1) {
            if (!this.mStEmptyElem) {
                while (true) {
                    type = next();
                    if (type == 2) {
                        return -1;
                    }
                    if (type != 5 && type != 3) {
                        if (type != 4 && type != 12) {
                            throw _constructUnexpectedInTyped(type);
                        }
                        break;
                        break;
                    }
                }
            } else {
                this.mStEmptyElem = false;
                this.mCurrToken = 2;
                return -1;
            }
        }
        int count = 0;
        while (type != 2) {
            if (type == 4 || type == 12 || type == 6) {
                if (this.mTokenState < 3) {
                    readCoalescedText(type, false);
                }
                count += this.mTextBuffer.decodeElements(dec, this);
                if (!dec.hasRoom()) {
                    break;
                }
                type = next();
            } else if (type == 5 || type == 3) {
                type = next();
            } else {
                throw _constructUnexpectedInTyped(type);
            }
        }
        if (count > 0) {
            return count;
        }
        return -1;
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public final int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
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
        CharArrayBase64Decoder dec = _base64Decoder();
        int type2 = this.mCurrToken;
        if (((1 << type2) & MASK_TYPED_ACCESS_BINARY) == 0) {
            if (type2 == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                throwNotTextualOrElem(type2);
            }
        } else if (type2 == 1) {
            if (!this.mStEmptyElem) {
                while (true) {
                    int type3 = next();
                    if (type3 == 2) {
                        return -1;
                    }
                    if (type3 != 5 && type3 != 3) {
                        if (this.mTokenState < this.mStTextThreshold) {
                            finishToken(false);
                        }
                        _initBinaryChunks(v, dec, type3, true);
                        break;
                    }
                }
            } else {
                this.mStEmptyElem = false;
                this.mCurrToken = 2;
                return -1;
            }
        }
        int totalCount = 0;
        while (true) {
            try {
                int count = dec.decode(resultBuffer, offset, maxLength);
                offset += count;
                totalCount += count;
                maxLength -= count;
                if (maxLength < 1 || this.mCurrToken == 2) {
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
                    if (this.mTokenState < this.mStTextThreshold) {
                        finishToken(false);
                    }
                    _initBinaryChunks(v, dec, type, false);
                }
            } catch (IllegalArgumentException iae) {
                throw _constructTypeException(iae.getMessage(), "");
            }
        }
        if (totalCount > 0) {
            return totalCount;
        }
        return -1;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private final void _initBinaryChunks(Base64Variant v, CharArrayBase64Decoder dec, int type, boolean isFirst) throws XMLStreamException {
        if (type == 4) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = readTextSecondary(2000, false) ? 3 : 2;
            }
        } else if (type == 12) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = readCDataSecondary(2000) ? 3 : 2;
            }
        } else {
            throw _constructUnexpectedInTyped(type);
        }
        this.mTextBuffer.initBinaryChunks(v, dec, isFirst);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeIndex(String namespaceURI, String localName) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack.findAttributeIndex(namespaceURI, localName);
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
        return _verifyQName(dec.getValue());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        try {
            this.mAttrCollector.decodeValue(index, tvd);
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, this.mAttrCollector.getValue(index));
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = _decoderFactory().getIntArrayDecoder();
        getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = _decoderFactory().getLongArrayDecoder();
        getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = _decoderFactory().getFloatArrayDecoder();
        getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = _decoderFactory().getDoubleArrayDecoder();
        getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.decodeValues(index, tad, this);
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        return this.mAttrCollector.decodeBinary(index, v, _base64Decoder(), this);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Byte code manipulation detected: skipped illegal throws declarations: [org.codehaus.stax2.typed.TypedXMLStreamException] */
    protected QName _verifyQName(QName n) throws XMLStreamException {
        String ln = n.getLocalPart();
        int ix = WstxInputData.findIllegalNameChar(ln, this.mCfgNsEnabled, this.mXml11);
        if (ix >= 0) {
            String prefix = n.getPrefix();
            String pname = (prefix == null || prefix.length() <= 0) ? ln : prefix + ":" + ln;
            throw _constructTypeException("Invalid local name \"" + ln + "\" (character at #" + ix + " is invalid)", pname);
        }
        return n;
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected CharArrayBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new CharArrayBase64Decoder();
        }
        return this._base64Decoder;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void _handleEmptyValue(TypedValueDecoder dec) throws XMLStreamException {
        try {
            dec.handleEmptyValue();
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, "");
        }
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, iae.getMessage(), getStartLocation(), iae);
    }
}
