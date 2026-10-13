package com.ctc.wstx.sw;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.WriterConfig;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/EncodingXmlWriter.class */
public abstract class EncodingXmlWriter extends XmlWriter {
    static final int DEFAULT_BUFFER_SIZE = 4000;
    static final byte BYTE_SPACE = 32;
    static final byte BYTE_COLON = 58;
    static final byte BYTE_SEMICOLON = 59;
    static final byte BYTE_LBRACKET = 91;
    static final byte BYTE_RBRACKET = 93;
    static final byte BYTE_QMARK = 63;
    static final byte BYTE_EQ = 61;
    static final byte BYTE_SLASH = 47;
    static final byte BYTE_HASH = 35;
    static final byte BYTE_HYPHEN = 45;
    static final byte BYTE_LT = 60;
    static final byte BYTE_GT = 62;
    static final byte BYTE_AMP = 38;
    static final byte BYTE_QUOT = 34;
    static final byte BYTE_APOS = 39;
    static final byte BYTE_A = 97;
    static final byte BYTE_G = 103;
    static final byte BYTE_L = 108;
    static final byte BYTE_M = 109;
    static final byte BYTE_O = 111;
    static final byte BYTE_P = 112;
    static final byte BYTE_Q = 113;
    static final byte BYTE_S = 115;
    static final byte BYTE_T = 116;
    static final byte BYTE_U = 117;
    static final byte BYTE_X = 120;
    private final OutputStream mOut;
    protected byte[] mOutputBuffer;
    protected int mOutputPtr;
    protected int mSurrogate;

    @Override // com.ctc.wstx.sw.XmlWriter
    public abstract void writeRaw(char[] cArr, int i, int i2) throws IOException;

    @Override // com.ctc.wstx.sw.XmlWriter
    public abstract void writeRaw(String str, int i, int i2) throws IOException;

    protected abstract void writeAttrValue(String str) throws IOException;

    protected abstract void writeAttrValue(char[] cArr, int i, int i2) throws IOException;

    protected abstract int writeCDataContent(String str) throws IOException;

    protected abstract int writeCDataContent(char[] cArr, int i, int i2) throws IOException;

    protected abstract int writeCommentContent(String str) throws IOException;

    protected abstract int writePIData(String str) throws XMLStreamException, IOException;

    protected abstract void writeTextContent(String str) throws IOException;

    protected abstract void writeTextContent(char[] cArr, int i, int i2) throws IOException;

    public EncodingXmlWriter(OutputStream out, WriterConfig cfg, String encoding, boolean autoclose) throws IOException {
        super(cfg, encoding, autoclose);
        this.mSurrogate = 0;
        this.mOut = out;
        this.mOutputBuffer = cfg.allocFullBBuffer(4000);
        this.mOutputPtr = 0;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected int getOutputPtr() {
        return this.mOutputPtr;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected final OutputStream getOutputStream() {
        return this.mOut;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected final Writer getWriter() {
        return null;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void close(boolean forceRealClose) throws IOException {
        flush();
        byte[] buf = this.mOutputBuffer;
        if (buf != null) {
            this.mOutputBuffer = null;
            this.mConfig.freeFullBBuffer(buf);
        }
        if (forceRealClose || this.mAutoCloseOutput) {
            this.mOut.close();
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void flush() throws IOException {
        flushBuffer();
        this.mOut.flush();
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCDataStart() throws IOException {
        writeAscii("<![CDATA[");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCDataEnd() throws IOException {
        writeAscii("]]>");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCommentStart() throws IOException {
        writeAscii("<!--");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCommentEnd() throws IOException {
        writeAscii("-->");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writePIStart(String target, boolean addSpace) throws IOException {
        writeAscii((byte) 60, (byte) 63);
        writeRaw(target);
        if (addSpace) {
            writeAscii((byte) 32);
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writePIEnd() throws IOException {
        writeAscii((byte) 63, (byte) 62);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeCData(String data) throws IOException {
        writeAscii("<![CDATA[");
        int ix = writeCDataContent(data);
        if (ix >= 0) {
            return ix;
        }
        writeAscii("]]>");
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeCData(char[] cbuf, int offset, int len) throws IOException {
        writeAscii("<![CDATA[");
        int ix = writeCDataContent(cbuf, offset, len);
        if (ix >= 0) {
            return ix;
        }
        writeAscii("]]>");
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCharacters(String data) throws IOException {
        if (this.mTextWriter != null) {
            this.mTextWriter.write(data);
        } else {
            writeTextContent(data);
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCharacters(char[] cbuf, int offset, int len) throws IOException {
        if (this.mTextWriter != null) {
            this.mTextWriter.write(cbuf, offset, len);
        } else {
            writeTextContent(cbuf, offset, len);
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeComment(String data) throws IOException {
        writeAscii("<!--");
        int ix = writeCommentContent(data);
        if (ix >= 0) {
            return ix;
        }
        writeAscii("-->");
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeDTD(String data) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        writeRaw(data, 0, data.length());
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException, IOException {
        writeAscii("<!DOCTYPE ");
        writeAscii(rootName);
        if (systemId != null) {
            if (publicId != null) {
                writeAscii(" PUBLIC \"");
                writeRaw(publicId, 0, publicId.length());
                writeAscii("\" \"");
            } else {
                writeAscii(" SYSTEM \"");
            }
            writeRaw(systemId, 0, systemId.length());
            writeAscii((byte) 34);
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            writeAscii((byte) 32, (byte) 91);
            writeRaw(internalSubset, 0, internalSubset.length());
            writeAscii((byte) 93);
        }
        writeAscii((byte) 62);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEntityReference(String name) throws XMLStreamException, IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        writeAscii((byte) 38);
        writeName(name);
        writeAscii((byte) 59);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeXmlDeclaration(String version, String encoding, String standalone) throws IOException {
        byte byQuote = this.mUseDoubleQuotesInXmlDecl ? (byte) 34 : (byte) 39;
        writeAscii("<?xml version=");
        writeAscii(byQuote);
        writeAscii(version);
        writeAscii(byQuote);
        if (encoding != null && encoding.length() > 0) {
            writeAscii(" encoding=");
            writeAscii(byQuote);
            writeRaw(encoding, 0, encoding.length());
            writeAscii(byQuote);
        }
        if (standalone != null) {
            writeAscii(" standalone=");
            writeAscii(byQuote);
            writeAscii(standalone);
            writeAscii(byQuote);
        }
        writeAscii((byte) 63, (byte) 62);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public int writePI(String target, String data) throws XMLStreamException, IOException {
        writeAscii((byte) 60, (byte) 63);
        writeName(target);
        if (data != null && data.length() > 0) {
            writeAscii((byte) 32);
            int ix = writePIData(data);
            if (ix >= 0) {
                return ix;
            }
        }
        writeAscii((byte) 63, (byte) 62);
        return -1;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagStart(String localName) throws XMLStreamException, IOException {
        writeAscii((byte) 60);
        writeName(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagStart(String prefix, String localName) throws XMLStreamException, IOException {
        if (prefix == null || prefix.length() == 0) {
            writeStartTagStart(localName);
            return;
        }
        writeAscii((byte) 60);
        writeName(prefix);
        writeAscii((byte) 58);
        writeName(localName);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagEnd() throws IOException {
        writeAscii((byte) 62);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagEmptyEnd() throws IOException {
        if (this.mAddSpaceAfterEmptyElem) {
            writeAscii(" />");
        } else {
            writeAscii((byte) 47, (byte) 62);
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEndTag(String localName) throws IOException {
        writeAscii((byte) 60, (byte) 47);
        writeNameUnchecked(localName);
        writeAscii((byte) 62);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEndTag(String prefix, String localName) throws IOException {
        writeAscii((byte) 60, (byte) 47);
        if (prefix != null && prefix.length() > 0) {
            writeNameUnchecked(prefix);
            writeAscii((byte) 58);
        }
        writeNameUnchecked(localName);
        writeAscii((byte) 62);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String localName, String value) throws XMLStreamException, IOException {
        writeAscii((byte) 32);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        int len = value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                writeAttrValue(value);
            }
        }
        writeAscii((byte) 34);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String localName, char[] value, int offset, int len) throws XMLStreamException, IOException {
        writeAscii((byte) 32);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, len);
            } else {
                writeAttrValue(value, offset, len);
            }
        }
        writeAscii((byte) 34);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String prefix, String localName, String value) throws XMLStreamException, IOException {
        writeAscii((byte) 32);
        writeName(prefix);
        writeAscii((byte) 58);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        int len = value.length();
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            } else {
                writeAttrValue(value);
            }
        }
        writeAscii((byte) 34);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String prefix, String localName, char[] value, int offset, int len) throws XMLStreamException, IOException {
        writeAscii((byte) 32);
        writeName(prefix);
        writeAscii((byte) 58);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, len);
            } else {
                writeAttrValue(value, offset, len);
            }
        }
        writeAscii((byte) 34);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeTypedElement(AsciiValueEncoder enc) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (!enc.isCompleted()) {
                flush();
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeTypedElement(AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws XMLStreamException, IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        int copyBufferLen = copyBuffer.length;
        do {
            int ptr = enc.encodeMore(copyBuffer, 0, copyBufferLen);
            validator.validateText(copyBuffer, 0, ptr, false);
            writeRawAscii(copyBuffer, 0, ptr);
        } while (!enc.isCompleted());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String localName, AsciiValueEncoder enc) throws XMLStreamException, IOException {
        writeAscii((byte) 32);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (!enc.isCompleted()) {
                flush();
            } else {
                writeAscii((byte) 34);
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String prefix, String localName, AsciiValueEncoder enc) throws XMLStreamException, IOException {
        System.err.println("DEBUG: write typed attr/0 '" + localName + OperatorName.SHOW_TEXT_LINE);
        writeAscii((byte) 32);
        writeName(prefix);
        writeAscii((byte) 58);
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        if (enc.bufferNeedsFlush(this.mOutputBuffer.length - this.mOutputPtr)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBuffer.length);
            if (!enc.isCompleted()) {
                flush();
            } else {
                writeAscii((byte) 34);
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String prefix, String localName, String nsURI, AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws XMLStreamException, IOException {
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        if (nsURI == null) {
            nsURI = "";
        }
        System.err.println("DEBUG: write typed attr/1 '" + localName + "', vld == " + validator);
        writeAscii((byte) 32);
        if (hasPrefix) {
            writeName(prefix);
            writeAscii((byte) 58);
        }
        writeName(localName);
        writeAscii((byte) 61, (byte) 34);
        int copyBufferLen = copyBuffer.length;
        int last = enc.encodeMore(copyBuffer, 0, copyBufferLen);
        writeRawAscii(copyBuffer, 0, last);
        if (enc.isCompleted()) {
            validator.validateAttribute(localName, nsURI, prefix, copyBuffer, 0, last);
            return;
        }
        StringBuilder sb = new StringBuilder(copyBufferLen << 1);
        sb.append(copyBuffer, 0, last);
        do {
            int last2 = enc.encodeMore(copyBuffer, 0, copyBufferLen);
            writeRawAscii(copyBuffer, 0, last2);
            sb.append(copyBuffer, 0, last2);
        } while (!enc.isCompleted());
        writeAscii((byte) 34);
        String valueStr = sb.toString();
        validator.validateAttribute(localName, nsURI, prefix, valueStr);
    }

    protected final void flushBuffer() throws IOException {
        if (this.mOutputPtr > 0 && this.mOutputBuffer != null) {
            int ptr = this.mOutputPtr;
            this.mOutputPtr = 0;
            this.mOut.write(this.mOutputBuffer, 0, ptr);
        }
    }

    protected final void writeAscii(byte b) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        if (this.mOutputPtr >= this.mOutputBuffer.length) {
            flushBuffer();
        }
        byte[] bArr = this.mOutputBuffer;
        int i = this.mOutputPtr;
        this.mOutputPtr = i + 1;
        bArr[i] = b;
    }

    protected final void writeAscii(byte b1, byte b2) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        if (this.mOutputPtr + 1 >= this.mOutputBuffer.length) {
            flushBuffer();
        }
        byte[] bArr = this.mOutputBuffer;
        int i = this.mOutputPtr;
        this.mOutputPtr = i + 1;
        bArr[i] = b1;
        byte[] bArr2 = this.mOutputBuffer;
        int i2 = this.mOutputPtr;
        this.mOutputPtr = i2 + 1;
        bArr2[i2] = b2;
    }

    protected final void writeAscii(String str) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        int len = str.length();
        int ptr = this.mOutputPtr;
        byte[] buf = this.mOutputBuffer;
        if (ptr + len >= buf.length) {
            if (len > buf.length) {
                writeRaw(str, 0, len);
                return;
            } else {
                flushBuffer();
                ptr = this.mOutputPtr;
            }
        }
        this.mOutputPtr += len;
        for (int i = 0; i < len; i++) {
            int i2 = ptr;
            ptr++;
            buf[i2] = (byte) str.charAt(i);
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeRawAscii(char[] buf, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        byte[] dst = this.mOutputBuffer;
        if (ptr + len >= dst.length) {
            if (len > dst.length) {
                writeRaw(buf, offset, len);
                return;
            } else {
                flushBuffer();
                ptr = this.mOutputPtr;
            }
        }
        this.mOutputPtr += len;
        for (int i = 0; i < len; i++) {
            dst[ptr + i] = (byte) buf[offset + i];
        }
    }

    protected final int writeAsEntity(int c) throws IOException {
        int ptr;
        byte[] buf = this.mOutputBuffer;
        int ptr2 = this.mOutputPtr;
        if (ptr2 + 10 >= buf.length) {
            flushBuffer();
            ptr2 = this.mOutputPtr;
        }
        int i = ptr2;
        int ptr3 = ptr2 + 1;
        buf[i] = 38;
        if (c < 256) {
            if (c == 38) {
                int ptr4 = ptr3 + 1;
                buf[ptr3] = 97;
                int ptr5 = ptr4 + 1;
                buf[ptr4] = 109;
                ptr = ptr5 + 1;
                buf[ptr5] = 112;
            } else if (c == 60) {
                int ptr6 = ptr3 + 1;
                buf[ptr3] = 108;
                ptr = ptr6 + 1;
                buf[ptr6] = 116;
            } else if (c == 62) {
                int ptr7 = ptr3 + 1;
                buf[ptr3] = 103;
                ptr = ptr7 + 1;
                buf[ptr7] = 116;
            } else if (c == 39) {
                int ptr8 = ptr3 + 1;
                buf[ptr3] = 97;
                int ptr9 = ptr8 + 1;
                buf[ptr8] = 112;
                int ptr10 = ptr9 + 1;
                buf[ptr9] = 111;
                ptr = ptr10 + 1;
                buf[ptr10] = 115;
            } else if (c == 34) {
                int ptr11 = ptr3 + 1;
                buf[ptr3] = 113;
                int ptr12 = ptr11 + 1;
                buf[ptr11] = 117;
                int ptr13 = ptr12 + 1;
                buf[ptr12] = 111;
                ptr = ptr13 + 1;
                buf[ptr13] = 116;
            } else {
                int ptr14 = ptr3 + 1;
                buf[ptr3] = 35;
                int ptr15 = ptr14 + 1;
                buf[ptr14] = 120;
                if (c >= 16) {
                    int digit = c >> 4;
                    ptr15++;
                    buf[ptr15] = (byte) (digit < 10 ? 48 + digit : 87 + digit);
                    c &= 15;
                }
                int i2 = ptr15;
                ptr = ptr15 + 1;
                buf[i2] = (byte) (c < 10 ? 48 + c : 87 + c);
            }
        } else {
            int ptr16 = ptr3 + 1;
            buf[ptr3] = 35;
            int ptr17 = ptr16 + 1;
            buf[ptr16] = 120;
            int shift = 20;
            do {
                int digit2 = (c >> shift) & 15;
                if (digit2 > 0 || ptr17 != ptr17) {
                    int i3 = ptr17;
                    ptr17++;
                    buf[i3] = (byte) (digit2 < 10 ? 48 + digit2 : 87 + digit2);
                }
                shift -= 4;
            } while (shift > 0);
            int c2 = c & 15;
            int i4 = ptr17;
            ptr = ptr17 + 1;
            buf[i4] = (byte) (c2 < 10 ? 48 + c2 : 87 + c2);
        }
        int i5 = ptr;
        int ptr18 = ptr + 1;
        buf[i5] = 59;
        this.mOutputPtr = ptr18;
        return ptr18;
    }

    protected final void writeName(String name) throws XMLStreamException, IOException {
        if (this.mCheckNames) {
            verifyNameValidity(name, this.mNsAware);
        }
        writeRaw(name, 0, name.length());
    }

    protected final void writeNameUnchecked(String name) throws IOException {
        writeRaw(name, 0, name.length());
    }

    protected final int calcSurrogate(int secondSurr) throws IOException {
        int firstSurr = this.mSurrogate;
        this.mSurrogate = 0;
        if (firstSurr < 55296 || firstSurr > 56319) {
            throwUnpairedSurrogate(firstSurr);
        }
        if (secondSurr < 56320 || secondSurr > 57343) {
            throwUnpairedSurrogate(secondSurr);
        }
        int ch = 65536 + ((firstSurr - InvalidCharHandler.FailingHandler.SURR1_FIRST) << 10) + (secondSurr - InvalidCharHandler.FailingHandler.SURR2_FIRST);
        if (ch > 1114111) {
            throw new IOException("Illegal surrogate character pair, resulting code 0x" + Integer.toHexString(ch) + " above legal XML character range");
        }
        return ch;
    }

    protected final void throwUnpairedSurrogate() throws IOException {
        int surr = this.mSurrogate;
        this.mSurrogate = 0;
        throwUnpairedSurrogate(surr);
    }

    protected final void throwUnpairedSurrogate(int code) throws IOException {
        flush();
        throw new IOException("Unpaired surrogate character (0x" + Integer.toHexString(code) + ")");
    }
}
