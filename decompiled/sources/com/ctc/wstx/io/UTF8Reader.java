package com.ctc.wstx.io;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.ReaderConfig;
import com.sun.jna.platform.win32.WinUser;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/UTF8Reader.class */
public final class UTF8Reader extends BaseReader {
    boolean mXml11;
    char mSurrogate;
    int mCharCount;
    int mByteCount;

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    public UTF8Reader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mSurrogate = (char) 0;
        this.mCharCount = 0;
        this.mByteCount = 0;
    }

    @Override // com.ctc.wstx.io.BaseReader
    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int start, int len) throws IOException {
        int needed;
        if (start < 0 || start + len > cbuf.length) {
            reportBounds(cbuf, start, len);
        }
        if (this.mByteBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return 0;
        }
        int len2 = len + start;
        int outPtr = start;
        if (this.mSurrogate != 0) {
            outPtr++;
            cbuf[outPtr] = this.mSurrogate;
            this.mSurrogate = (char) 0;
            if (this.mBytePtr >= this.mByteBufferEnd) {
                this.mCharCount++;
                return 1;
            }
        } else {
            int left = this.mByteBufferEnd - this.mBytePtr;
            if (left < 4 && ((left < 1 || this.mByteBuffer[this.mBytePtr] < 0) && !loadMore(left))) {
                return -1;
            }
        }
        byte[] buf = this.mByteBuffer;
        int inPtr = this.mBytePtr;
        int inBufLen = this.mByteBufferEnd;
        loop0: while (outPtr < len2) {
            int i = inPtr;
            inPtr++;
            int c = buf[i];
            if (c >= 0) {
                if (c == 127 && this.mXml11) {
                    int bytePos = (this.mByteCount + inPtr) - 1;
                    int charPos = this.mCharCount + (outPtr - start);
                    reportInvalidXml11(c, bytePos, charPos);
                }
                int i2 = outPtr;
                outPtr++;
                cbuf[i2] = (char) c;
                int outMax = len2 - outPtr;
                int inMax = inBufLen - inPtr;
                int inEnd = inPtr + (inMax < outMax ? inMax : outMax);
                while (true) {
                    if (inPtr >= inEnd) {
                        break loop0;
                    }
                    int i3 = inPtr;
                    inPtr++;
                    c = buf[i3] & 255;
                    if (c < 127) {
                        int i4 = outPtr;
                        outPtr++;
                        cbuf[i4] = (char) c;
                    } else if (c == 127) {
                        if (this.mXml11) {
                            int bytePos2 = (this.mByteCount + inPtr) - 1;
                            int charPos2 = this.mCharCount + (outPtr - start);
                            reportInvalidXml11(c, bytePos2, charPos2);
                        }
                        int i5 = outPtr;
                        outPtr++;
                        cbuf[i5] = (char) c;
                        if (inPtr >= inEnd) {
                            break;
                        }
                    }
                }
            }
            if ((c & 224) == 192) {
                c &= 31;
                needed = 1;
            } else if ((c & 240) == 224) {
                c &= 15;
                needed = 2;
            } else if ((c & 248) == 240) {
                c &= 15;
                needed = 3;
            } else {
                reportInvalidInitial(c & 255, outPtr - start);
                needed = 1;
            }
            if (inBufLen - inPtr < needed) {
                inPtr--;
                break;
            }
            int i6 = inPtr;
            inPtr++;
            byte b = buf[i6];
            if ((b & 192) != 128) {
                reportInvalidOther(b & 255, outPtr - start);
            }
            int c2 = (c << 6) | (b & 63);
            if (needed > 1) {
                inPtr++;
                byte b2 = buf[inPtr];
                if ((b2 & 192) != 128) {
                    reportInvalidOther(b2 & 255, outPtr - start);
                }
                c2 = (c2 << 6) | (b2 & 63);
                if (needed > 2) {
                    inPtr++;
                    byte b3 = buf[inPtr];
                    if ((b3 & 192) != 128) {
                        reportInvalidOther(b3 & 255, outPtr - start);
                    }
                    int c3 = (c2 << 6) | (b3 & 63);
                    if (c3 > 1114111) {
                        reportInvalid(c3, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                    }
                    int c4 = c3 - 65536;
                    int i7 = outPtr;
                    outPtr++;
                    cbuf[i7] = (char) (InvalidCharHandler.FailingHandler.SURR1_FIRST + (c4 >> 10));
                    c2 = 56320 | (c4 & WinUser.CF_GDIOBJLAST);
                    if (outPtr >= len2) {
                        this.mSurrogate = (char) c2;
                        break;
                    }
                } else if (c2 >= 55296) {
                    if (c2 < 57344) {
                        reportInvalid(c2, outPtr - start, "(a surrogate character) ");
                    } else if (c2 >= 65534) {
                        reportInvalid(c2, outPtr - start, "");
                    }
                } else if (this.mXml11 && c2 == 8232) {
                    if (outPtr > start && cbuf[outPtr - 1] == '\r') {
                        cbuf[outPtr - 1] = '\n';
                    }
                    c2 = 10;
                }
            } else if (this.mXml11 && c2 <= 159) {
                if (c2 == 133) {
                    c2 = 10;
                } else if (c2 >= 127) {
                    int bytePos3 = (this.mByteCount + inPtr) - 1;
                    int charPos3 = this.mCharCount + (outPtr - start);
                    reportInvalidXml11(c2, bytePos3, charPos3);
                }
            }
            int i8 = outPtr;
            outPtr++;
            cbuf[i8] = (char) c2;
            if (inPtr >= inBufLen) {
                break;
            }
        }
        this.mBytePtr = inPtr;
        int len3 = outPtr - start;
        this.mCharCount += len3;
        return len3;
    }

    private void reportInvalidInitial(int mask, int offset) throws IOException {
        int bytePos = (this.mByteCount + this.mBytePtr) - 1;
        int charPos = this.mCharCount + offset + 1;
        throw new CharConversionException("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalidOther(int mask, int offset) throws IOException {
        int bytePos = (this.mByteCount + this.mBytePtr) - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this.mByteCount + gotBytes;
        int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a multi-byte char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = (this.mByteCount + this.mBytePtr) - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
        int needed;
        this.mByteCount += this.mByteBufferEnd - available;
        if (available > 0) {
            if (this.mBytePtr > 0 && canModifyBuffer()) {
                for (int i = 0; i < available; i++) {
                    this.mByteBuffer[i] = this.mByteBuffer[this.mBytePtr + i];
                }
                this.mBytePtr = 0;
                this.mByteBufferEnd = available;
            }
        } else {
            int count = readBytes();
            if (count < 1) {
                if (count < 0) {
                    freeBuffers();
                    return false;
                }
                reportStrangeStream();
            }
        }
        byte b = this.mByteBuffer[this.mBytePtr];
        if (b >= 0) {
            return true;
        }
        if ((b & 224) == 192) {
            needed = 2;
        } else if ((b & 240) == 224) {
            needed = 3;
        } else if ((b & 248) == 240) {
            needed = 4;
        } else {
            reportInvalidInitial(b & 255, 0);
            needed = 1;
        }
        while (this.mBytePtr + needed > this.mByteBufferEnd) {
            int count2 = readBytesAt(this.mByteBufferEnd);
            if (count2 < 1) {
                if (count2 < 0) {
                    freeBuffers();
                    reportUnexpectedEOF(this.mByteBufferEnd, needed);
                }
                reportStrangeStream();
            }
        }
        return true;
    }
}
