package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/ISOLatin1XmlWriter.class */
public final class ISOLatin1XmlWriter extends EncodingXmlWriter {
    public ISOLatin1XmlWriter(OutputStream out, WriterConfig cfg, boolean autoclose) throws IOException {
        super(out, cfg, "ISO-8859-1", autoclose);
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter, com.ctc.wstx.sw.XmlWriter
    public void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = cbuf[offset];
                    if (c < 32) {
                        if (c != 10 && c != 13 && c != 9) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    } else if (c > 126) {
                        if (c > 255) {
                            this.mOutputPtr = ptr;
                            handleInvalidLatinChar(c);
                        } else if (this.mXml11 && c < 159 && c != 133) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    }
                    int i = ptr;
                    ptr++;
                    this.mOutputBuffer[i] = (byte) c;
                    offset++;
                }
            } else {
                int inEnd2 = offset + max;
                while (offset < inEnd2) {
                    int i2 = ptr;
                    ptr++;
                    this.mOutputBuffer[i2] = (byte) cbuf[offset];
                    offset++;
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter, com.ctc.wstx.sw.XmlWriter
    public void writeRaw(String str, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = str.charAt(offset);
                    if (c < 32) {
                        if (c != 10 && c != 13 && c != 9) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    } else if (c > 126) {
                        if (c > 255) {
                            this.mOutputPtr = ptr;
                            handleInvalidLatinChar(c);
                        } else if (this.mXml11 && c < 159 && c != 133) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    }
                    int i = ptr;
                    ptr++;
                    this.mOutputBuffer[i] = (byte) c;
                    offset++;
                }
            } else {
                int inEnd2 = offset + max;
                while (offset < inEnd2) {
                    int i2 = ptr;
                    ptr++;
                    this.mOutputBuffer[i2] = (byte) str.charAt(offset);
                    offset++;
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected void writeAttrValue(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int i = offset;
                offset++;
                int sec = data.charAt(i);
                int sec2 = calcSurrogate(sec);
                this.mOutputPtr = ptr;
                ptr = writeAsEntity(sec2);
                len--;
            } else {
                if (max > len) {
                    max = len;
                }
                int inEnd = offset + max;
                while (true) {
                    if (offset < inEnd) {
                        int i2 = offset;
                        offset++;
                        int c = data.charAt(i2);
                        if (c < 32) {
                            if (c == 13) {
                                if (!this.mEscapeCR) {
                                    int i3 = ptr;
                                    ptr++;
                                    this.mOutputBuffer[i3] = (byte) c;
                                } else {
                                    this.mOutputPtr = ptr;
                                    ptr = writeAsEntity(c);
                                    len = data.length() - offset;
                                }
                            } else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                                int i4 = ptr;
                                ptr++;
                                this.mOutputBuffer[i4] = (byte) handleInvalidChar(c);
                            } else {
                                this.mOutputPtr = ptr;
                                ptr = writeAsEntity(c);
                                len = data.length() - offset;
                            }
                        } else if (c < 127) {
                            if (c != 60 && c != 38 && c != 34) {
                                int i5 = ptr;
                                ptr++;
                                this.mOutputBuffer[i5] = (byte) c;
                            } else {
                                this.mOutputPtr = ptr;
                                ptr = writeAsEntity(c);
                                len = data.length() - offset;
                            }
                        } else if (c > 159 && c <= 255) {
                            int i6 = ptr;
                            ptr++;
                            this.mOutputBuffer[i6] = (byte) c;
                        } else {
                            if (c >= 55296 && c <= 57343) {
                                this.mSurrogate = c;
                                if (offset != inEnd) {
                                    offset++;
                                    c = calcSurrogate(data.charAt(offset));
                                }
                            }
                            this.mOutputPtr = ptr;
                            ptr = writeAsEntity(c);
                            len = data.length() - offset;
                        }
                    }
                    len -= max;
                }
            }
        }
        this.mOutputPtr = ptr;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected void writeAttrValue(char[] data, int offset, int len) throws IOException {
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int i = offset;
                offset++;
                int sec = calcSurrogate(data[i]);
                this.mOutputPtr = ptr;
                ptr = writeAsEntity(sec);
                len--;
            } else {
                if (max > len) {
                    max = len;
                }
                int inEnd = offset + max;
                while (offset < inEnd) {
                    int i2 = offset;
                    offset++;
                    int c = data[i2];
                    if (c < 32) {
                        if (c == 13) {
                            if (!this.mEscapeCR) {
                                int i3 = ptr;
                                ptr++;
                                this.mOutputBuffer[i3] = (byte) c;
                            } else {
                                this.mOutputPtr = ptr;
                                ptr = writeAsEntity(c);
                                max -= inEnd - offset;
                                break;
                            }
                        } else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                            int i4 = ptr;
                            ptr++;
                            this.mOutputBuffer[i4] = (byte) handleInvalidChar(c);
                        } else {
                            this.mOutputPtr = ptr;
                            ptr = writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                    } else if (c < 127) {
                        if (c != 60 && c != 38 && c != 34) {
                            int i5 = ptr;
                            ptr++;
                            this.mOutputBuffer[i5] = (byte) c;
                        } else {
                            this.mOutputPtr = ptr;
                            ptr = writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                    } else if (c > 159 && c <= 255) {
                        int i6 = ptr;
                        ptr++;
                        this.mOutputBuffer[i6] = (byte) c;
                    } else {
                        if (c >= 55296 && c <= 57343) {
                            this.mSurrogate = c;
                            if (offset == inEnd) {
                                break;
                            }
                            offset++;
                            c = calcSurrogate(data[offset]);
                            this.mOutputPtr = ptr;
                            ptr = writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                        this.mOutputPtr = ptr;
                        ptr = writeAsEntity(c);
                        max -= inEnd - offset;
                        break;
                    }
                }
                len -= max;
            }
        }
        this.mOutputPtr = ptr;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected int writeCDataContent(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            writeRaw(data, 0, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (true) {
                if (offset < inEnd) {
                    int i = offset;
                    offset++;
                    int c = data.charAt(i);
                    if (c < 32) {
                        if (c != 10 && c != 13 && c != 9) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    } else if (c > 126) {
                        if (c > 255) {
                            this.mOutputPtr = ptr;
                            handleInvalidLatinChar(c);
                        } else if (this.mXml11 && c < 159 && c != 133) {
                            this.mOutputPtr = ptr;
                            c = handleInvalidChar(c);
                        }
                    } else if (c == 62 && offset > 2 && data.charAt(offset - 2) == ']' && data.charAt(offset - 3) == ']') {
                        if (!this.mFixContent) {
                            return offset - 3;
                        }
                        this.mOutputPtr = ptr;
                        writeCDataEnd();
                        writeCDataStart();
                        writeAscii((byte) 62);
                        ptr = this.mOutputPtr;
                        len = data.length() - offset;
                        break;
                    }
                    int i2 = ptr;
                    ptr++;
                    this.mOutputBuffer[i2] = (byte) c;
                } else {
                    len -= max;
                    break;
                }
            }
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected int writeCDataContent(char[] cbuf, int start, int len) throws IOException {
        if (!this.mCheckContent) {
            writeRaw(cbuf, start, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        int offset = start;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int i = offset;
                offset++;
                int c = cbuf[i];
                if (c < 32) {
                    if (c != 10 && c != 13 && c != 9) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c > 126) {
                    if (c > 255) {
                        this.mOutputPtr = ptr;
                        handleInvalidLatinChar(c);
                    } else if (this.mXml11 && c < 159 && c != 133) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c == 62 && offset >= start + 3 && cbuf[offset - 2] == ']' && cbuf[offset - 3] == ']') {
                    if (!this.mFixContent) {
                        return offset - 3;
                    }
                    this.mOutputPtr = ptr;
                    writeCDataEnd();
                    writeCDataStart();
                    writeAscii((byte) 62);
                    ptr = this.mOutputPtr;
                    max -= inEnd - offset;
                    break;
                }
                int i2 = ptr;
                ptr++;
                this.mOutputBuffer[i2] = (byte) c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected int writeCommentContent(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            writeRaw(data, 0, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int i = offset;
                offset++;
                int c = data.charAt(i);
                if (c < 32) {
                    if (c != 10 && c != 13 && c != 9) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c > 126) {
                    if (c > 255) {
                        this.mOutputPtr = ptr;
                        handleInvalidLatinChar(c);
                    } else if (this.mXml11 && c < 159 && c != 133) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c == 45 && offset > 1 && data.charAt(offset - 2) == '-') {
                    if (!this.mFixContent) {
                        return offset - 2;
                    }
                    int i2 = ptr;
                    int ptr2 = ptr + 1;
                    this.mOutputBuffer[i2] = 32;
                    if (ptr2 >= this.mOutputBuffer.length) {
                        this.mOutputPtr = ptr2;
                        flushBuffer();
                        ptr2 = 0;
                    }
                    int i3 = ptr2;
                    ptr = ptr2 + 1;
                    this.mOutputBuffer[i3] = 45;
                    max -= inEnd - offset;
                    break;
                }
                int i4 = ptr;
                ptr++;
                this.mOutputBuffer[i4] = (byte) c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected int writePIData(String data) throws XMLStreamException, IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            writeRaw(data, 0, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            int inEnd = offset + max;
            while (offset < inEnd) {
                int c = data.charAt(offset);
                if (c < 32) {
                    if (c != 10 && c != 13 && c != 9) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c > 126) {
                    if (c > 255) {
                        this.mOutputPtr = ptr;
                        handleInvalidLatinChar(c);
                    } else if (this.mXml11 && c < 159 && c != 133) {
                        this.mOutputPtr = ptr;
                        c = handleInvalidChar(c);
                    }
                } else if (c == 62 && offset > 0 && data.charAt(offset - 1) == '?') {
                    return offset - 2;
                }
                int i = ptr;
                ptr++;
                this.mOutputBuffer[i] = (byte) c;
                offset++;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected void writeTextContent(String data) throws IOException {
        int offset = 0;
        int len = data.length();
        while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int i = offset;
                offset++;
                int sec = data.charAt(i);
                writeAsEntity(calcSurrogate(sec));
                len--;
            } else {
                if (max > len) {
                    max = len;
                }
                int inEnd = offset + max;
                while (true) {
                    if (offset < inEnd) {
                        int i2 = offset;
                        offset++;
                        int c = data.charAt(i2);
                        if (c < 32) {
                            if (c == 10 || c == 9) {
                                byte[] bArr = this.mOutputBuffer;
                                int i3 = this.mOutputPtr;
                                this.mOutputPtr = i3 + 1;
                                bArr[i3] = (byte) c;
                            } else if (c == 13) {
                                if (!this.mEscapeCR) {
                                    byte[] bArr2 = this.mOutputBuffer;
                                    int i4 = this.mOutputPtr;
                                    this.mOutputPtr = i4 + 1;
                                    bArr2[i4] = (byte) c;
                                } else {
                                    writeAsEntity(c);
                                    len = data.length() - offset;
                                }
                            } else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                                int c2 = handleInvalidChar(c);
                                byte[] bArr3 = this.mOutputBuffer;
                                int i5 = this.mOutputPtr;
                                this.mOutputPtr = i5 + 1;
                                bArr3[i5] = (byte) c2;
                            } else {
                                writeAsEntity(c);
                                len = data.length() - offset;
                            }
                        } else if (c < 127) {
                            if (c != 60 && c != 38 && (c != 62 || (offset > 1 && data.charAt(offset - 2) != ']'))) {
                                byte[] bArr4 = this.mOutputBuffer;
                                int i6 = this.mOutputPtr;
                                this.mOutputPtr = i6 + 1;
                                bArr4[i6] = (byte) c;
                            } else {
                                writeAsEntity(c);
                                len = data.length() - offset;
                            }
                        } else if (c > 159 && c <= 255) {
                            byte[] bArr5 = this.mOutputBuffer;
                            int i7 = this.mOutputPtr;
                            this.mOutputPtr = i7 + 1;
                            bArr5[i7] = (byte) c;
                        } else {
                            if (c >= 55296 && c <= 57343) {
                                this.mSurrogate = c;
                                if (offset != inEnd) {
                                    offset++;
                                    c = calcSurrogate(data.charAt(offset));
                                }
                            }
                            writeAsEntity(c);
                            len = data.length() - offset;
                        }
                    }
                    len -= max;
                }
            }
        }
    }

    @Override // com.ctc.wstx.sw.EncodingXmlWriter
    protected void writeTextContent(char[] cbuf, int offset, int len) throws IOException {
        while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int i = offset;
                offset++;
                int sec = calcSurrogate(cbuf[i]);
                writeAsEntity(sec);
                len--;
            } else {
                if (max > len) {
                    max = len;
                }
                int inEnd = offset + max;
                while (offset < inEnd) {
                    int i2 = offset;
                    offset++;
                    int c = cbuf[i2];
                    if (c < 32) {
                        if (c == 10 || c == 9) {
                            byte[] bArr = this.mOutputBuffer;
                            int i3 = this.mOutputPtr;
                            this.mOutputPtr = i3 + 1;
                            bArr[i3] = (byte) c;
                        } else if (c == 13) {
                            if (!this.mEscapeCR) {
                                byte[] bArr2 = this.mOutputBuffer;
                                int i4 = this.mOutputPtr;
                                this.mOutputPtr = i4 + 1;
                                bArr2[i4] = (byte) c;
                            } else {
                                writeAsEntity(c);
                                max -= inEnd - offset;
                                break;
                            }
                        } else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                            int c2 = handleInvalidChar(c);
                            byte[] bArr3 = this.mOutputBuffer;
                            int i5 = this.mOutputPtr;
                            this.mOutputPtr = i5 + 1;
                            bArr3[i5] = (byte) c2;
                        } else {
                            writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                    } else if (c < 127) {
                        if (c != 60 && c != 38 && (c != 62 || (offset > 1 && cbuf[offset - 2] != ']'))) {
                            byte[] bArr4 = this.mOutputBuffer;
                            int i6 = this.mOutputPtr;
                            this.mOutputPtr = i6 + 1;
                            bArr4[i6] = (byte) c;
                        } else {
                            writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                    } else if (c > 159 && c <= 255) {
                        byte[] bArr5 = this.mOutputBuffer;
                        int i7 = this.mOutputPtr;
                        this.mOutputPtr = i7 + 1;
                        bArr5[i7] = (byte) c;
                    } else {
                        if (c >= 55296 && c <= 57343) {
                            this.mSurrogate = c;
                            if (offset == inEnd) {
                                break;
                            }
                            offset++;
                            c = calcSurrogate(cbuf[offset]);
                            writeAsEntity(c);
                            max -= inEnd - offset;
                            break;
                        }
                        writeAsEntity(c);
                        max -= inEnd - offset;
                        break;
                    }
                }
                len -= max;
            }
        }
    }

    protected void handleInvalidLatinChar(int c) throws IOException {
        flush();
        throw new IOException("Invalid XML character (0x" + Integer.toHexString(c) + "); can only be output using character entity when using ISO-8859-1 encoding");
    }
}
