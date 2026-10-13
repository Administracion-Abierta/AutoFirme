package com.ctc.wstx.io;

import com.ctc.wstx.api.InvalidCharHandler;
import com.ctc.wstx.api.ReaderConfig;
import com.sun.jna.platform.win32.WinUser;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/UTF32Reader.class */
public final class UTF32Reader extends BaseReader {
    protected final boolean mBigEndian;
    protected boolean mXml11;
    protected char mSurrogate;
    protected int mCharCount;
    protected int mByteCount;

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    public UTF32Reader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer, boolean isBigEndian) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mSurrogate = (char) 0;
        this.mCharCount = 0;
        this.mByteCount = 0;
        this.mBigEndian = isBigEndian;
    }

    @Override // com.ctc.wstx.io.BaseReader
    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int start, int len) throws IOException {
        int ch;
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
        } else {
            int left = this.mByteBufferEnd - this.mBytePtr;
            if (left < 4 && !loadMore(left)) {
                if (left == 0) {
                    return -1;
                }
                reportUnexpectedEOF(this.mByteBufferEnd - this.mBytePtr, 4);
            }
        }
        byte[] buf = this.mByteBuffer;
        int lastValidInputStart = this.mByteBufferEnd - 4;
        while (outPtr < len2) {
            int ptr = this.mBytePtr;
            if (this.mBytePtr > lastValidInputStart) {
                break;
            }
            if (this.mBigEndian) {
                ch = (buf[ptr] << 24) | ((buf[ptr + 1] & 255) << 16) | ((buf[ptr + 2] & 255) << 8) | (buf[ptr + 3] & 255);
            } else {
                ch = (buf[ptr] & 255) | ((buf[ptr + 1] & 255) << 8) | ((buf[ptr + 2] & 255) << 16) | (buf[ptr + 3] << 24);
            }
            this.mBytePtr += 4;
            if (ch >= 127) {
                if (ch <= 159) {
                    if (this.mXml11) {
                        if (ch != 133) {
                            reportInvalid(ch, outPtr - start, "(can only be included via entity in xml 1.1)");
                        }
                        ch = 10;
                    }
                } else if (ch >= 55296) {
                    if (ch > 1114111) {
                        reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                    }
                    if (ch > 65535) {
                        int ch2 = ch - 65536;
                        int i = outPtr;
                        outPtr++;
                        cbuf[i] = (char) (InvalidCharHandler.FailingHandler.SURR1_FIRST + (ch2 >> 10));
                        ch = 56320 | (ch2 & WinUser.CF_GDIOBJLAST);
                        if (outPtr >= len2) {
                            this.mSurrogate = (char) ch;
                            break;
                        }
                    } else if (ch < 57344) {
                        reportInvalid(ch, outPtr - start, "(a surrogate char) ");
                    } else if (ch >= 65534) {
                        reportInvalid(ch, outPtr - start, "");
                    }
                } else if (ch == 8232 && this.mXml11) {
                    ch = 10;
                }
            }
            int i2 = outPtr;
            outPtr++;
            cbuf[i2] = (char) ch;
        }
        int len3 = outPtr - start;
        this.mCharCount += len3;
        return len3;
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this.mByteCount + gotBytes;
        int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = (this.mByteCount + this.mBytePtr) - 1;
        int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
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
        while (this.mByteBufferEnd - this.mBytePtr < 4) {
            int count2 = readBytesAt(this.mByteBufferEnd);
            if (count2 < 1) {
                if (count2 < 0) {
                    freeBuffers();
                    reportUnexpectedEOF(this.mByteBufferEnd, 4);
                }
                reportStrangeStream();
            }
        }
        return true;
    }
}
