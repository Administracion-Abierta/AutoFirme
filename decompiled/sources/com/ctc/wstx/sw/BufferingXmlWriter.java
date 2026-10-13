package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.io.CharsetNames;
import com.ctc.wstx.io.CompletelyCloseable;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/BufferingXmlWriter.class */
public final class BufferingXmlWriter extends XmlWriter implements XMLStreamConstants {
    static final int DEFAULT_BUFFER_SIZE = 1000;
    static final int DEFAULT_SMALL_SIZE = 256;
    protected static final int HIGHEST_ENCODABLE_ATTR_CHAR = 60;
    protected static final int HIGHEST_ENCODABLE_TEXT_CHAR = 62;
    protected static final int[] QUOTABLE_TEXT_CHARS;
    protected final Writer mOut;
    protected char[] mOutputBuffer;
    protected final int mSmallWriteSize;
    protected int mOutputPtr;
    protected int mOutputBufLen;
    protected final OutputStream mUnderlyingStream;
    private final int mEncHighChar;
    final char mEncQuoteChar;
    final String mEncQuoteEntity;

    static {
        int[] q = new int[4096];
        Arrays.fill(q, 0, 32, 1);
        Arrays.fill(q, 127, 160, 1);
        q[9] = 0;
        q[10] = 0;
        q[60] = 1;
        q[62] = 1;
        q[38] = 1;
        QUOTABLE_TEXT_CHARS = q;
    }

    public BufferingXmlWriter(Writer out, WriterConfig cfg, String enc, boolean autoclose, OutputStream outs, int bitsize) throws IOException {
        super(cfg, enc, autoclose);
        this.mOut = out;
        this.mOutputBuffer = cfg.allocFullCBuffer(1000);
        this.mOutputBufLen = this.mOutputBuffer.length;
        this.mSmallWriteSize = 256;
        this.mOutputPtr = 0;
        this.mUnderlyingStream = outs;
        this.mEncQuoteChar = '\"';
        this.mEncQuoteEntity = "&quot;";
        bitsize = bitsize < 1 ? guessEncodingBitSize(enc) : bitsize;
        this.mEncHighChar = bitsize < 16 ? 1 << bitsize : JPEG.COM;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected int getOutputPtr() {
        return this.mOutputPtr;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected final OutputStream getOutputStream() {
        return this.mUnderlyingStream;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    protected final Writer getWriter() {
        return this.mOut;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void close(boolean forceRealClose) throws IOException {
        flush();
        this.mTextWriter = null;
        this.mAttrValueWriter = null;
        char[] buf = this.mOutputBuffer;
        if (buf != null) {
            this.mOutputBuffer = null;
            this.mConfig.freeFullCBuffer(buf);
        }
        if (forceRealClose || this.mAutoCloseOutput) {
            if (this.mOut instanceof CompletelyCloseable) {
                ((CompletelyCloseable) this.mOut).closeCompletely();
            } else {
                this.mOut.close();
            }
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void flush() throws IOException {
        flushBuffer();
        this.mOut.flush();
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len > this.mOutputBufLen) {
                flushBuffer();
            }
            System.arraycopy(cbuf, offset, this.mOutputBuffer, this.mOutputPtr, len);
            this.mOutputPtr += len;
            return;
        }
        int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                int needed = this.mSmallWriteSize - ptr;
                System.arraycopy(cbuf, offset, this.mOutputBuffer, ptr, needed);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            flushBuffer();
        }
        this.mOut.write(cbuf, offset, len);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeRawAscii(char[] cbuf, int offset, int len) throws IOException {
        writeRaw(cbuf, offset, len);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeRaw(String str) throws IOException {
        if (this.mOut == null) {
            return;
        }
        int len = str.length();
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                flushBuffer();
            }
            str.getChars(0, len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        writeRaw(str, 0, len);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeRaw(String str, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                flushBuffer();
            }
            str.getChars(offset, offset + len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                int needed = this.mSmallWriteSize - ptr;
                str.getChars(offset, offset + needed, this.mOutputBuffer, ptr);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            flushBuffer();
        }
        this.mOut.write(str, offset, len);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCDataStart() throws IOException {
        fastWriteRaw("<![CDATA[");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCDataEnd() throws IOException {
        fastWriteRaw("]]>");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCommentStart() throws IOException {
        fastWriteRaw("<!--");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeCommentEnd() throws IOException {
        fastWriteRaw("-->");
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writePIStart(String target, boolean addSpace) throws IOException {
        fastWriteRaw('<', '?');
        fastWriteRaw(target);
        if (addSpace) {
            fastWriteRaw(' ');
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writePIEnd() throws IOException {
        fastWriteRaw('?', '>');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeCData(String data) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = verifyCDataContent(data)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            writeSegmentedCData(data, ix);
            return -1;
        }
        fastWriteRaw("<![CDATA[");
        writeRaw(data, 0, data.length());
        fastWriteRaw("]]>");
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeCData(char[] cbuf, int offset, int len) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = verifyCDataContent(cbuf, offset, len)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            writeSegmentedCData(cbuf, offset, len, ix);
            return -1;
        }
        fastWriteRaw("<![CDATA[");
        writeRaw(cbuf, offset, len);
        fastWriteRaw("]]>");
        return -1;
    }

    /* JADX WARN: Code duplicated, block: B:55:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:64:0x011d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:65:0x0114 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:78:0x00fa A[SYNTHETIC] */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeCharacters(String text) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(text);
            return;
        }
        int inPtr = 0;
        int len = text.length();
        int[] QC = QUOTABLE_TEXT_CHARS;
        int highChar = this.mEncHighChar;
        int MAXQC = Math.min(QC.length, highChar);
        while (true) {
            String ent = null;
            while (inPtr < len) {
                int i = inPtr;
                inPtr++;
                char c = text.charAt(i);
                if (c < MAXQC) {
                    if (QC[c] != 0) {
                        if (c < ' ') {
                            if (c != '\n' && c != '\t') {
                                if (c == '\r') {
                                    if (this.mEscapeCR) {
                                        if (ent != null) {
                                            writeRaw(ent);
                                        } else {
                                            writeAsEntity(text.charAt(inPtr - 1));
                                        }
                                    }
                                } else if (!this.mXml11 || c == 0) {
                                    c = handleInvalidChar(c);
                                    ent = String.valueOf(c);
                                } else if (ent != null) {
                                    writeRaw(ent);
                                } else {
                                    writeAsEntity(text.charAt(inPtr - 1));
                                }
                            }
                        } else {
                            if (c == '<') {
                                ent = "&lt;";
                            } else if (c == '&') {
                                ent = "&amp;";
                            } else if (c == '>') {
                                if (inPtr < 2 || text.charAt(inPtr - 2) == ']') {
                                    ent = "&gt;";
                                }
                            } else if (c >= 127) {
                            }
                            if (ent != null) {
                                writeRaw(ent);
                            } else {
                                writeAsEntity(text.charAt(inPtr - 1));
                            }
                        }
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        flushBuffer();
                    }
                    char[] cArr = this.mOutputBuffer;
                    int i2 = this.mOutputPtr;
                    this.mOutputPtr = i2 + 1;
                    cArr[i2] = c;
                } else {
                    if (c >= highChar) {
                        if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(text.charAt(inPtr - 1));
                        }
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        flushBuffer();
                    }
                    char[] cArr2 = this.mOutputBuffer;
                    int i3 = this.mOutputPtr;
                    this.mOutputPtr = i3 + 1;
                    cArr2[i3] = c;
                }
            }
            return;
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeCharacters(char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(cbuf, offset, len);
            return;
        }
        int[] QC = QUOTABLE_TEXT_CHARS;
        int highChar = this.mEncHighChar;
        int MAXQC = Math.min(QC.length, highChar);
        int len2 = len + offset;
        do {
            int c = 0;
            int start = offset;
            String ent = null;
            while (offset < len2) {
                c = cbuf[offset];
                if (c < MAXQC) {
                    if (QC[c] == 0) {
                        continue;
                    } else {
                        if (c == 60) {
                            ent = "&lt;";
                            break;
                        }
                        if (c == 38) {
                            ent = "&amp;";
                            break;
                        }
                        if (c == 62) {
                            if (offset == start || cbuf[offset - 1] == ']') {
                                ent = "&gt;";
                                break;
                            }
                        } else if (c < 32) {
                            if (c != 10 && c != 9) {
                                if (c == 13) {
                                    if (this.mEscapeCR) {
                                        break;
                                    }
                                } else {
                                    if (this.mXml11 && c != 0) {
                                        break;
                                    }
                                    c = handleInvalidChar(c);
                                    ent = String.valueOf((char) c);
                                    break;
                                }
                            }
                        } else if (c >= 127) {
                            break;
                        }
                    }
                    offset++;
                } else if (c >= highChar) {
                    break;
                } else {
                    offset++;
                }
            }
            int outLen = offset - start;
            if (outLen > 0) {
                writeRaw(cbuf, start, outLen);
            }
            if (ent != null) {
                writeRaw(ent);
            } else if (offset < len2) {
                writeAsEntity(c);
            }
            offset++;
        } while (offset < len2);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writeComment(String data) throws IOException {
        int ix;
        if (this.mCheckContent && (ix = verifyCommentContent(data)) >= 0) {
            if (!this.mFixContent) {
                return ix;
            }
            writeSegmentedComment(data, ix);
            return -1;
        }
        fastWriteRaw("<!--");
        writeRaw(data);
        fastWriteRaw("-->");
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeDTD(String data) throws IOException {
        writeRaw(data);
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException, IOException {
        fastWriteRaw("<!DOCTYPE ");
        if (this.mCheckNames) {
            verifyNameValidity(rootName, false);
        }
        fastWriteRaw(rootName);
        if (systemId != null) {
            if (publicId != null) {
                fastWriteRaw(" PUBLIC \"");
                fastWriteRaw(publicId);
                fastWriteRaw("\" \"");
            } else {
                fastWriteRaw(" SYSTEM \"");
            }
            fastWriteRaw(systemId);
            fastWriteRaw('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            fastWriteRaw(' ', '[');
            fastWriteRaw(internalSubset);
            fastWriteRaw(']');
        }
        fastWriteRaw('>');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEntityReference(String name) throws XMLStreamException, IOException {
        if (this.mCheckNames) {
            verifyNameValidity(name, this.mNsAware);
        }
        fastWriteRaw('&');
        fastWriteRaw(name);
        fastWriteRaw(';');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeXmlDeclaration(String version, String encoding, String standalone) throws IOException {
        char chQuote = this.mUseDoubleQuotesInXmlDecl ? '\"' : '\'';
        fastWriteRaw("<?xml version=");
        fastWriteRaw(chQuote);
        fastWriteRaw(version);
        fastWriteRaw(chQuote);
        if (encoding != null && encoding.length() > 0) {
            fastWriteRaw(" encoding=");
            fastWriteRaw(chQuote);
            fastWriteRaw(encoding);
            fastWriteRaw(chQuote);
        }
        if (standalone != null) {
            fastWriteRaw(" standalone=");
            fastWriteRaw(chQuote);
            fastWriteRaw(standalone);
            fastWriteRaw(chQuote);
        }
        fastWriteRaw('?', '>');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public int writePI(String target, String data) throws XMLStreamException, IOException {
        int ix;
        int ix2;
        if (this.mCheckNames) {
            verifyNameValidity(target, this.mNsAware);
        }
        fastWriteRaw('<', '?');
        fastWriteRaw(target);
        if (data != null && data.length() > 0) {
            if (this.mCheckContent && (ix = data.indexOf(63)) >= 0 && (ix2 = data.indexOf("?>", ix)) >= 0) {
                return ix2;
            }
            fastWriteRaw(' ');
            writeRaw(data);
        }
        fastWriteRaw('?', '>');
        return -1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagStart(String localName) throws XMLStreamException, IOException {
        if (this.mCheckNames) {
            verifyNameValidity(localName, this.mNsAware);
        }
        int ptr = this.mOutputPtr;
        int extra = (this.mOutputBufLen - ptr) - (1 + localName.length());
        if (extra < 0) {
            fastWriteRaw('<');
            fastWriteRaw(localName);
            return;
        }
        char[] buf = this.mOutputBuffer;
        int ptr2 = ptr + 1;
        buf[ptr] = '<';
        int len = localName.length();
        localName.getChars(0, len, buf, ptr2);
        this.mOutputPtr = ptr2 + len;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagStart(String prefix, String localName) throws XMLStreamException, IOException {
        if (prefix == null || prefix.length() == 0) {
            writeStartTagStart(localName);
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(prefix, this.mNsAware);
            verifyNameValidity(localName, this.mNsAware);
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        int extra = (this.mOutputBufLen - ptr) - ((2 + localName.length()) + len);
        if (extra < 0) {
            fastWriteRaw('<');
            fastWriteRaw(prefix);
            fastWriteRaw(':');
            fastWriteRaw(localName);
            return;
        }
        char[] buf = this.mOutputBuffer;
        int ptr2 = ptr + 1;
        buf[ptr] = '<';
        prefix.getChars(0, len, buf, ptr2);
        int ptr3 = ptr2 + len;
        int ptr4 = ptr3 + 1;
        buf[ptr3] = ':';
        int len2 = localName.length();
        localName.getChars(0, len2, buf, ptr4);
        this.mOutputPtr = ptr4 + len2;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagEnd() throws IOException {
        fastWriteRaw('>');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeStartTagEmptyEnd() throws IOException {
        int ptr = this.mOutputPtr;
        if (ptr + 3 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            flushBuffer();
            ptr = this.mOutputPtr;
        }
        char[] buf = this.mOutputBuffer;
        if (this.mAddSpaceAfterEmptyElem) {
            int i = ptr;
            ptr++;
            buf[i] = ' ';
        }
        int i2 = ptr;
        int ptr2 = ptr + 1;
        buf[i2] = '/';
        buf[ptr2] = '>';
        this.mOutputPtr = ptr2 + 1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEndTag(String localName) throws IOException {
        int ptr = this.mOutputPtr;
        int extra = (this.mOutputBufLen - ptr) - (3 + localName.length());
        if (extra < 0) {
            fastWriteRaw('<', '/');
            fastWriteRaw(localName);
            fastWriteRaw('>');
            return;
        }
        char[] buf = this.mOutputBuffer;
        int ptr2 = ptr + 1;
        buf[ptr] = '<';
        int ptr3 = ptr2 + 1;
        buf[ptr2] = '/';
        int len = localName.length();
        localName.getChars(0, len, buf, ptr3);
        int ptr4 = ptr3 + len;
        buf[ptr4] = '>';
        this.mOutputPtr = ptr4 + 1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeEndTag(String prefix, String localName) throws IOException {
        if (prefix == null || prefix.length() == 0) {
            writeEndTag(localName);
            return;
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        int extra = (this.mOutputBufLen - ptr) - ((4 + localName.length()) + len);
        if (extra < 0) {
            fastWriteRaw('<', '/');
            fastWriteRaw(prefix);
            fastWriteRaw(':');
            fastWriteRaw(localName);
            fastWriteRaw('>');
            return;
        }
        char[] buf = this.mOutputBuffer;
        int ptr2 = ptr + 1;
        buf[ptr] = '<';
        int ptr3 = ptr2 + 1;
        buf[ptr2] = '/';
        prefix.getChars(0, len, buf, ptr3);
        int ptr4 = ptr3 + len;
        int ptr5 = ptr4 + 1;
        buf[ptr4] = ':';
        int len2 = localName.length();
        localName.getChars(0, len2, buf, ptr5);
        int ptr6 = ptr5 + len2;
        buf[ptr6] = '>';
        this.mOutputPtr = ptr6 + 1;
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String localName, String value) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(localName, this.mNsAware);
        }
        int len = localName.length();
        if ((this.mOutputBufLen - this.mOutputPtr) - (3 + len) < 0) {
            fastWriteRaw(' ');
            fastWriteRaw(localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            localName.getChars(0, len, buf, ptr2);
            int ptr3 = ptr2 + len;
            int ptr4 = ptr3 + 1;
            buf[ptr3] = '=';
            buf[ptr4] = '\"';
            this.mOutputPtr = ptr4 + 1;
        }
        int len2 = value == null ? 0 : value.length();
        if (len2 > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len2);
            } else {
                writeAttrValue(value, len2);
            }
        }
        fastWriteRaw('\"');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String localName, char[] value, int offset, int vlen) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(localName, this.mNsAware);
        }
        int len = localName.length();
        if ((this.mOutputBufLen - this.mOutputPtr) - (3 + len) < 0) {
            fastWriteRaw(' ');
            fastWriteRaw(localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            localName.getChars(0, len, buf, ptr2);
            int ptr3 = ptr2 + len;
            int ptr4 = ptr3 + 1;
            buf[ptr3] = '=';
            buf[ptr4] = '\"';
            this.mOutputPtr = ptr4 + 1;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            } else {
                writeAttrValue(value, offset, vlen);
            }
        }
        fastWriteRaw('\"');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String prefix, String localName, String value) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(prefix, this.mNsAware);
            verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if ((this.mOutputBufLen - this.mOutputPtr) - ((4 + localName.length()) + len) < 0) {
            fastWriteRaw(' ');
            if (len > 0) {
                fastWriteRaw(prefix);
                fastWriteRaw(':');
            }
            fastWriteRaw(localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            prefix.getChars(0, len, buf, ptr2);
            int ptr3 = ptr2 + len;
            int ptr4 = ptr3 + 1;
            buf[ptr3] = ':';
            int len2 = localName.length();
            localName.getChars(0, len2, buf, ptr4);
            int ptr5 = ptr4 + len2;
            int ptr6 = ptr5 + 1;
            buf[ptr5] = '=';
            buf[ptr6] = '\"';
            this.mOutputPtr = ptr6 + 1;
        }
        int len3 = value == null ? 0 : value.length();
        if (len3 > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len3);
            } else {
                writeAttrValue(value, len3);
            }
        }
        fastWriteRaw('\"');
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeAttribute(String prefix, String localName, char[] value, int offset, int vlen) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(prefix, this.mNsAware);
            verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if ((this.mOutputBufLen - this.mOutputPtr) - ((4 + localName.length()) + len) < 0) {
            fastWriteRaw(' ');
            if (len > 0) {
                fastWriteRaw(prefix);
                fastWriteRaw(':');
            }
            fastWriteRaw(localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            prefix.getChars(0, len, buf, ptr2);
            int ptr3 = ptr2 + len;
            int ptr4 = ptr3 + 1;
            buf[ptr3] = ':';
            int len2 = localName.length();
            localName.getChars(0, len2, buf, ptr4);
            int ptr5 = ptr4 + len2;
            int ptr6 = ptr5 + 1;
            buf[ptr5] = '=';
            buf[ptr6] = '\"';
            this.mOutputPtr = ptr6 + 1;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            } else {
                writeAttrValue(value, offset, vlen);
            }
        }
        fastWriteRaw('\"');
    }

    /* JADX WARN: Code duplicated, block: B:39:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:48:0x00cf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x00c6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:62:0x00ac A[SYNTHETIC] */
    private final void writeAttrValue(String value, int len) throws IOException {
        int inPtr = 0;
        char qchar = this.mEncQuoteChar;
        int highChar = this.mEncHighChar;
        while (true) {
            String ent = null;
            while (inPtr < len) {
                int i = inPtr;
                inPtr++;
                char c = value.charAt(i);
                if (c <= '<') {
                    if (c < ' ') {
                        if (c == '\r') {
                            if (this.mEscapeCR) {
                                if (ent != null) {
                                    writeRaw(ent);
                                } else {
                                    writeAsEntity(value.charAt(inPtr - 1));
                                }
                            }
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr = this.mOutputBuffer;
                            int i2 = this.mOutputPtr;
                            this.mOutputPtr = i2 + 1;
                            cArr[i2] = c;
                        } else if (c != '\n' && c != '\t' && (!this.mXml11 || c == 0)) {
                            c = handleInvalidChar(c);
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr2 = this.mOutputBuffer;
                            int i3 = this.mOutputPtr;
                            this.mOutputPtr = i3 + 1;
                            cArr2[i3] = c;
                        } else if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value.charAt(inPtr - 1));
                        }
                    } else {
                        if (c == qchar) {
                            ent = this.mEncQuoteEntity;
                        } else if (c == '<') {
                            ent = "&lt;";
                        } else {
                            if (c == '&') {
                                ent = "&amp;";
                            }
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr3 = this.mOutputBuffer;
                            int i4 = this.mOutputPtr;
                            this.mOutputPtr = i4 + 1;
                            cArr3[i4] = c;
                        }
                        if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value.charAt(inPtr - 1));
                        }
                    }
                } else {
                    if (c >= highChar) {
                        if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value.charAt(inPtr - 1));
                        }
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        flushBuffer();
                    }
                    char[] cArr4 = this.mOutputBuffer;
                    int i5 = this.mOutputPtr;
                    this.mOutputPtr = i5 + 1;
                    cArr4[i5] = c;
                }
            }
            return;
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:48:0x00cf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:49:0x00c6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:62:0x00ac A[SYNTHETIC] */
    private final void writeAttrValue(char[] value, int offset, int len) throws IOException {
        int len2 = len + offset;
        char qchar = this.mEncQuoteChar;
        int highChar = this.mEncHighChar;
        while (true) {
            String ent = null;
            while (offset < len2) {
                int i = offset;
                offset++;
                char c = value[i];
                if (c <= '<') {
                    if (c < ' ') {
                        if (c == '\r') {
                            if (this.mEscapeCR) {
                                if (ent != null) {
                                    writeRaw(ent);
                                } else {
                                    writeAsEntity(value[offset - 1]);
                                }
                            }
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr = this.mOutputBuffer;
                            int i2 = this.mOutputPtr;
                            this.mOutputPtr = i2 + 1;
                            cArr[i2] = c;
                        } else if (c != '\n' && c != '\t' && (!this.mXml11 || c == 0)) {
                            c = handleInvalidChar(c);
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr2 = this.mOutputBuffer;
                            int i3 = this.mOutputPtr;
                            this.mOutputPtr = i3 + 1;
                            cArr2[i3] = c;
                        } else if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value[offset - 1]);
                        }
                    } else {
                        if (c == qchar) {
                            ent = this.mEncQuoteEntity;
                        } else if (c == '<') {
                            ent = "&lt;";
                        } else {
                            if (c == '&') {
                                ent = "&amp;";
                            }
                            if (this.mOutputPtr >= this.mOutputBufLen) {
                                flushBuffer();
                            }
                            char[] cArr3 = this.mOutputBuffer;
                            int i4 = this.mOutputPtr;
                            this.mOutputPtr = i4 + 1;
                            cArr3[i4] = c;
                        }
                        if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value[offset - 1]);
                        }
                    }
                } else {
                    if (c >= highChar) {
                        if (ent != null) {
                            writeRaw(ent);
                        } else {
                            writeAsEntity(value[offset - 1]);
                        }
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        flushBuffer();
                    }
                    char[] cArr4 = this.mOutputBuffer;
                    int i5 = this.mOutputPtr;
                    this.mOutputPtr = i5 + 1;
                    cArr4[i5] = c;
                }
            }
            return;
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public final void writeTypedElement(AsciiValueEncoder enc) throws IOException {
        if (this.mOut == null) {
            return;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
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
        if (this.mOut == null) {
            return;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            flush();
        }
        int i = this.mOutputPtr;
        while (true) {
            int start = i;
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            validator.validateText(this.mOutputBuffer, start, this.mOutputPtr, false);
            if (!enc.isCompleted()) {
                flush();
                i = this.mOutputPtr;
            } else {
                return;
            }
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String localName, AsciiValueEncoder enc) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(localName, this.mNsAware);
        }
        int len = localName.length();
        if (this.mOutputPtr + 3 + len > this.mOutputBufLen) {
            fastWriteRaw(' ');
            fastWriteRaw(localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            localName.getChars(0, len, buf, ptr2);
            int ptr3 = ptr2 + len;
            int ptr4 = ptr3 + 1;
            buf[ptr3] = '=';
            buf[ptr4] = '\"';
            this.mOutputPtr = ptr4 + 1;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (!enc.isCompleted()) {
                flush();
            } else {
                fastWriteRaw('\"');
                return;
            }
        }
    }

    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String prefix, String localName, AsciiValueEncoder enc) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            verifyNameValidity(prefix, this.mNsAware);
            verifyNameValidity(localName, this.mNsAware);
        }
        int plen = prefix.length();
        int llen = localName.length();
        if (this.mOutputPtr + 4 + plen + llen > this.mOutputBufLen) {
            writePrefixedName(prefix, localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr2);
                int ptr3 = ptr2 + plen;
                ptr2 = ptr3 + 1;
                buf[ptr3] = ':';
            }
            localName.getChars(0, llen, buf, ptr2);
            int ptr4 = ptr2 + llen;
            int ptr5 = ptr4 + 1;
            buf[ptr4] = '=';
            buf[ptr5] = '\"';
            this.mOutputPtr = ptr5 + 1;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (!enc.isCompleted()) {
                flush();
            } else {
                fastWriteRaw('\"');
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.XmlWriter
    public void writeTypedAttribute(String prefix, String localName, String nsURI, AsciiValueEncoder enc, XMLValidator validator, char[] copyBuffer) throws XMLStreamException, IOException {
        if (this.mOut == null) {
            return;
        }
        if (prefix == null) {
            prefix = "";
        }
        if (nsURI == null) {
            nsURI = "";
        }
        int plen = prefix.length();
        if (this.mCheckNames) {
            if (plen > 0) {
                verifyNameValidity(prefix, this.mNsAware);
            }
            verifyNameValidity(localName, this.mNsAware);
        }
        if ((this.mOutputBufLen - this.mOutputPtr) - ((4 + localName.length()) + plen) < 0) {
            writePrefixedName(prefix, localName);
            fastWriteRaw('=', '\"');
        } else {
            int ptr = this.mOutputPtr;
            char[] buf = this.mOutputBuffer;
            int ptr2 = ptr + 1;
            buf[ptr] = ' ';
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr2);
                int ptr3 = ptr2 + plen;
                ptr2 = ptr3 + 1;
                buf[ptr3] = ':';
            }
            int llen = localName.length();
            localName.getChars(0, llen, buf, ptr2);
            int ptr4 = ptr2 + llen;
            int ptr5 = ptr4 + 1;
            buf[ptr4] = '=';
            buf[ptr5] = '\"';
            this.mOutputPtr = ptr5 + 1;
        }
        int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            flush();
        }
        int start = this.mOutputPtr;
        this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
        if (enc.isCompleted()) {
            validator.validateAttribute(localName, nsURI, prefix, this.mOutputBuffer, start, this.mOutputPtr);
            fastWriteRaw('\"');
            return;
        }
        StringBuilder sb = new StringBuilder(this.mOutputBuffer.length << 1);
        sb.append(this.mOutputBuffer, start, this.mOutputPtr - start);
        do {
            flush();
            int start2 = this.mOutputPtr;
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            sb.append(this.mOutputBuffer, start2, this.mOutputPtr - start2);
        } while (!enc.isCompleted());
        fastWriteRaw('\"');
        String valueStr = sb.toString();
        validator.validateAttribute(localName, nsURI, prefix, valueStr);
    }

    protected final void writePrefixedName(String prefix, String localName) throws IOException {
        fastWriteRaw(' ');
        if (prefix.length() > 0) {
            fastWriteRaw(prefix);
            fastWriteRaw(':');
        }
        fastWriteRaw(localName);
    }

    private final void flushBuffer() throws IOException {
        if (this.mOutputPtr > 0 && this.mOutputBuffer != null) {
            int ptr = this.mOutputPtr;
            this.mLocPastChars += ptr;
            this.mLocRowStartOffset -= ptr;
            this.mOutputPtr = 0;
            this.mOut.write(this.mOutputBuffer, 0, ptr);
        }
    }

    private final void fastWriteRaw(char c) throws IOException {
        if (this.mOutputPtr >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            } else {
                flushBuffer();
            }
        }
        char[] cArr = this.mOutputBuffer;
        int i = this.mOutputPtr;
        this.mOutputPtr = i + 1;
        cArr[i] = c;
    }

    private final void fastWriteRaw(char c1, char c2) throws IOException {
        if (this.mOutputPtr + 1 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            } else {
                flushBuffer();
            }
        }
        char[] cArr = this.mOutputBuffer;
        int i = this.mOutputPtr;
        this.mOutputPtr = i + 1;
        cArr[i] = c1;
        char[] cArr2 = this.mOutputBuffer;
        int i2 = this.mOutputPtr;
        this.mOutputPtr = i2 + 1;
        cArr2[i2] = c2;
    }

    private final void fastWriteRaw(String str) throws IOException {
        int len = str.length();
        int ptr = this.mOutputPtr;
        if (ptr + len >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            if (len > this.mOutputBufLen) {
                writeRaw(str);
                return;
            } else {
                flushBuffer();
                ptr = this.mOutputPtr;
            }
        }
        str.getChars(0, len, this.mOutputBuffer, ptr);
        this.mOutputPtr = ptr + len;
    }

    protected int verifyCDataContent(String content) {
        int ix;
        if (content != null && content.length() >= 3 && (ix = content.indexOf(93)) >= 0) {
            return content.indexOf("]]>", ix);
        }
        return -1;
    }

    protected int verifyCDataContent(char[] c, int start, int end) {
        if (c != null) {
            int start2 = start + 2;
            while (start2 < end) {
                char ch = c[start2];
                if (ch == ']') {
                    start2++;
                } else {
                    if (ch == '>' && c[start2 - 1] == ']' && c[start2 - 2] == ']') {
                        return start2 - 2;
                    }
                    start2 += 2;
                }
            }
            return -1;
        }
        return -1;
    }

    protected int verifyCommentContent(String content) {
        int ix = content.indexOf(45);
        if (ix >= 0 && ix < content.length() - 1) {
            ix = content.indexOf("--", ix);
        }
        return ix;
    }

    protected void writeSegmentedCData(String content, int index) throws IOException {
        int start = 0;
        while (index >= 0) {
            fastWriteRaw("<![CDATA[");
            writeRaw(content, start, (index + 2) - start);
            fastWriteRaw("]]>");
            start = index + 2;
            index = content.indexOf("]]>", start);
        }
        fastWriteRaw("<![CDATA[");
        writeRaw(content, start, content.length() - start);
        fastWriteRaw("]]>");
    }

    protected void writeSegmentedCData(char[] c, int start, int len, int index) throws IOException {
        int end = start + len;
        while (index >= 0) {
            fastWriteRaw("<![CDATA[");
            writeRaw(c, start, (index + 2) - start);
            fastWriteRaw("]]>");
            start = index + 2;
            index = verifyCDataContent(c, start, end);
        }
        fastWriteRaw("<![CDATA[");
        writeRaw(c, start, end - start);
        fastWriteRaw("]]>");
    }

    protected void writeSegmentedComment(String content, int index) throws IOException {
        int len = content.length();
        if (index == len - 1) {
            fastWriteRaw("<!--");
            writeRaw(content);
            fastWriteRaw(" -->");
            return;
        }
        fastWriteRaw("<!--");
        int start = 0;
        while (index >= 0) {
            writeRaw(content, start, (index + 1) - start);
            fastWriteRaw(' ');
            start = index + 1;
            index = content.indexOf("--", start);
        }
        writeRaw(content, start, len - start);
        if (content.charAt(len - 1) == '-') {
            fastWriteRaw(' ');
        }
        fastWriteRaw("-->");
    }

    public static int guessEncodingBitSize(String enc) {
        String enc2;
        if (enc == null || enc.length() == 0 || (enc2 = CharsetNames.normalize(enc)) == "UTF-8") {
            return 16;
        }
        if (enc2 == "ISO-8859-1") {
            return 8;
        }
        if (enc2 == CharsetNames.CS_US_ASCII) {
            return 7;
        }
        if (enc2 == CharsetNames.CS_UTF16 || enc2 == CharsetNames.CS_UTF16BE || enc2 == CharsetNames.CS_UTF16LE || enc2 == CharsetNames.CS_UTF32BE || enc2 == CharsetNames.CS_UTF32LE) {
            return 16;
        }
        return 8;
    }

    protected final void writeAsEntity(int c) throws IOException {
        int ptr;
        char[] buf = this.mOutputBuffer;
        int ptr2 = this.mOutputPtr;
        if (ptr2 + 10 >= buf.length) {
            flushBuffer();
            ptr2 = this.mOutputPtr;
        }
        int i = ptr2;
        int ptr3 = ptr2 + 1;
        buf[i] = '&';
        if (c < 256) {
            if (c == 38) {
                int ptr4 = ptr3 + 1;
                buf[ptr3] = 'a';
                int ptr5 = ptr4 + 1;
                buf[ptr4] = 'm';
                ptr = ptr5 + 1;
                buf[ptr5] = 'p';
            } else if (c == 60) {
                int ptr6 = ptr3 + 1;
                buf[ptr3] = 'l';
                ptr = ptr6 + 1;
                buf[ptr6] = 't';
            } else if (c == 62) {
                int ptr7 = ptr3 + 1;
                buf[ptr3] = 'g';
                ptr = ptr7 + 1;
                buf[ptr7] = 't';
            } else if (c == 39) {
                int ptr8 = ptr3 + 1;
                buf[ptr3] = 'a';
                int ptr9 = ptr8 + 1;
                buf[ptr8] = 'p';
                int ptr10 = ptr9 + 1;
                buf[ptr9] = 'o';
                ptr = ptr10 + 1;
                buf[ptr10] = 's';
            } else if (c == 34) {
                int ptr11 = ptr3 + 1;
                buf[ptr3] = 'q';
                int ptr12 = ptr11 + 1;
                buf[ptr11] = 'u';
                int ptr13 = ptr12 + 1;
                buf[ptr12] = 'o';
                ptr = ptr13 + 1;
                buf[ptr13] = 't';
            } else {
                int ptr14 = ptr3 + 1;
                buf[ptr3] = '#';
                int ptr15 = ptr14 + 1;
                buf[ptr14] = 'x';
                if (c >= 16) {
                    int digit = c >> 4;
                    ptr15++;
                    buf[ptr15] = (char) (digit < 10 ? 48 + digit : 87 + digit);
                    c &= 15;
                }
                int i2 = ptr15;
                ptr = ptr15 + 1;
                buf[i2] = (char) (c < 10 ? 48 + c : 87 + c);
            }
        } else {
            int ptr16 = ptr3 + 1;
            buf[ptr3] = '#';
            int ptr17 = ptr16 + 1;
            buf[ptr16] = 'x';
            int shift = 20;
            do {
                int digit2 = (c >> shift) & 15;
                if (digit2 > 0 || ptr17 != ptr17) {
                    int i3 = ptr17;
                    ptr17++;
                    buf[i3] = (char) (digit2 < 10 ? 48 + digit2 : 87 + digit2);
                }
                shift -= 4;
            } while (shift > 0);
            int c2 = c & 15;
            int i4 = ptr17;
            ptr = ptr17 + 1;
            buf[i4] = (char) (c2 < 10 ? 48 + c2 : 87 + c2);
        }
        buf[ptr] = ';';
        this.mOutputPtr = ptr + 1;
    }
}
