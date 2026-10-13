package com.ctc.wstx.io;

import com.ctc.wstx.api.WriterConfig;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/UTF8Writer.class */
public final class UTF8Writer extends Writer implements CompletelyCloseable {
    private static final int DEFAULT_BUF_LEN = 4000;
    static final int SURR1_FIRST = 55296;
    static final int SURR1_LAST = 56319;
    static final int SURR2_FIRST = 56320;
    static final int SURR2_LAST = 57343;
    final WriterConfig mConfig;
    final boolean mAutoCloseOutput;
    final OutputStream mOut;
    byte[] mOutBuffer;
    final int mOutBufferLast;
    int mOutPtr;
    int mSurrogate = 0;

    public UTF8Writer(WriterConfig cfg, OutputStream out, boolean autoclose) {
        this.mConfig = cfg;
        this.mAutoCloseOutput = autoclose;
        this.mOut = out;
        this.mOutBuffer = this.mConfig == null ? new byte[4000] : cfg.allocFullBBuffer(4000);
        this.mOutBufferLast = this.mOutBuffer.length - 4;
        this.mOutPtr = 0;
    }

    @Override // com.ctc.wstx.io.CompletelyCloseable
    public void closeCompletely() throws IOException {
        _close(true);
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        _close(this.mAutoCloseOutput);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        if (this.mOutPtr > 0 && this.mOutBuffer != null) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        this.mOut.flush();
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                write(cbuf[off]);
                return;
            }
            return;
        }
        if (this.mSurrogate > 0) {
            off++;
            char second = cbuf[off];
            len--;
            write(_convertSurrogate(second));
        }
        int outPtr = this.mOutPtr;
        byte[] outBuf = this.mOutBuffer;
        int outBufLast = this.mOutBufferLast;
        int len2 = len + off;
        while (off < len2) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            int i = off;
            off++;
            int c = cbuf[i];
            if (c < 128) {
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = (byte) c;
                int maxInCount = len2 - off;
                int maxOutCount = outBufLast - outPtr;
                if (maxInCount > maxOutCount) {
                    maxInCount = maxOutCount;
                }
                int maxInCount2 = maxInCount + off;
                while (true) {
                    if (off >= maxInCount2) {
                        continue;
                    } else {
                        int i3 = off;
                        off++;
                        c = cbuf[i3];
                        if (c < 128) {
                            int i4 = outPtr;
                            outPtr++;
                            outBuf[i4] = (byte) c;
                        }
                    }
                }
            }
            if (c < 2048) {
                int i5 = outPtr;
                int outPtr2 = outPtr + 1;
                outBuf[i5] = (byte) (192 | (c >> 6));
                outPtr = outPtr2 + 1;
                outBuf[outPtr2] = (byte) (128 | (c & 63));
            } else if (c < 55296 || c > 57343) {
                int i6 = outPtr;
                int outPtr3 = outPtr + 1;
                outBuf[i6] = (byte) (224 | (c >> 12));
                int outPtr4 = outPtr3 + 1;
                outBuf[outPtr3] = (byte) (128 | ((c >> 6) & 63));
                outPtr = outPtr4 + 1;
                outBuf[outPtr4] = (byte) (128 | (c & 63));
            } else {
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len2) {
                    break;
                }
                int i7 = off;
                off++;
                int c2 = _convertSurrogate(cbuf[i7]);
                if (c2 > 1114111) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c2);
                }
                int i8 = outPtr;
                int outPtr5 = outPtr + 1;
                outBuf[i8] = (byte) (240 | (c2 >> 18));
                int outPtr6 = outPtr5 + 1;
                outBuf[outPtr5] = (byte) (128 | ((c2 >> 12) & 63));
                int outPtr7 = outPtr6 + 1;
                outBuf[outPtr6] = (byte) (128 | ((c2 >> 6) & 63));
                outPtr = outPtr7 + 1;
                outBuf[outPtr7] = (byte) (128 | (c2 & 63));
            }
        }
        this.mOutPtr = outPtr;
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        int ptr;
        if (this.mSurrogate > 0) {
            c = _convertSurrogate(c);
        } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
                throwIllegal(c);
            }
            this.mSurrogate = c;
            return;
        }
        if (this.mOutPtr >= this.mOutBufferLast) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        if (c < 128) {
            byte[] bArr = this.mOutBuffer;
            int i = this.mOutPtr;
            this.mOutPtr = i + 1;
            bArr[i] = (byte) c;
            return;
        }
        int ptr2 = this.mOutPtr;
        if (c < 2048) {
            int ptr3 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) (192 | (c >> 6));
            ptr = ptr3 + 1;
            this.mOutBuffer[ptr3] = (byte) (128 | (c & 63));
        } else if (c <= 65535) {
            int ptr4 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) (224 | (c >> 12));
            int ptr5 = ptr4 + 1;
            this.mOutBuffer[ptr4] = (byte) (128 | ((c >> 6) & 63));
            ptr = ptr5 + 1;
            this.mOutBuffer[ptr5] = (byte) (128 | (c & 63));
        } else {
            if (c > 1114111) {
                throwIllegal(c);
            }
            int ptr6 = ptr2 + 1;
            this.mOutBuffer[ptr2] = (byte) (240 | (c >> 18));
            int ptr7 = ptr6 + 1;
            this.mOutBuffer[ptr6] = (byte) (128 | ((c >> 12) & 63));
            int ptr8 = ptr7 + 1;
            this.mOutBuffer[ptr7] = (byte) (128 | ((c >> 6) & 63));
            ptr = ptr8 + 1;
            this.mOutBuffer[ptr8] = (byte) (128 | (c & 63));
        }
        this.mOutPtr = ptr;
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                write(str.charAt(off));
                return;
            }
            return;
        }
        if (this.mSurrogate > 0) {
            off++;
            char second = str.charAt(off);
            len--;
            write(_convertSurrogate(second));
        }
        int outPtr = this.mOutPtr;
        byte[] outBuf = this.mOutBuffer;
        int outBufLast = this.mOutBufferLast;
        int len2 = len + off;
        while (off < len2) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            int i = off;
            off++;
            int c = str.charAt(i);
            if (c < 128) {
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = (byte) c;
                int maxInCount = len2 - off;
                int maxOutCount = outBufLast - outPtr;
                if (maxInCount > maxOutCount) {
                    maxInCount = maxOutCount;
                }
                int maxInCount2 = maxInCount + off;
                while (true) {
                    if (off >= maxInCount2) {
                        continue;
                    } else {
                        int i3 = off;
                        off++;
                        c = str.charAt(i3);
                        if (c < 128) {
                            int i4 = outPtr;
                            outPtr++;
                            outBuf[i4] = (byte) c;
                        }
                    }
                }
            }
            if (c < 2048) {
                int i5 = outPtr;
                int outPtr2 = outPtr + 1;
                outBuf[i5] = (byte) (192 | (c >> 6));
                outPtr = outPtr2 + 1;
                outBuf[outPtr2] = (byte) (128 | (c & 63));
            } else if (c < 55296 || c > 57343) {
                int i6 = outPtr;
                int outPtr3 = outPtr + 1;
                outBuf[i6] = (byte) (224 | (c >> 12));
                int outPtr4 = outPtr3 + 1;
                outBuf[outPtr3] = (byte) (128 | ((c >> 6) & 63));
                outPtr = outPtr4 + 1;
                outBuf[outPtr4] = (byte) (128 | (c & 63));
            } else {
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len2) {
                    break;
                }
                int i7 = off;
                off++;
                int c2 = _convertSurrogate(str.charAt(i7));
                if (c2 > 1114111) {
                    this.mOutPtr = outPtr;
                    throwIllegal(c2);
                }
                int i8 = outPtr;
                int outPtr5 = outPtr + 1;
                outBuf[i8] = (byte) (240 | (c2 >> 18));
                int outPtr6 = outPtr5 + 1;
                outBuf[outPtr5] = (byte) (128 | ((c2 >> 12) & 63));
                int outPtr7 = outPtr6 + 1;
                outBuf[outPtr6] = (byte) (128 | ((c2 >> 6) & 63));
                outPtr = outPtr7 + 1;
                outBuf[outPtr7] = (byte) (128 | (c2 & 63));
            }
        }
        this.mOutPtr = outPtr;
    }

    private final void _close(boolean forceClosing) throws IOException {
        byte[] buf = this.mOutBuffer;
        if (buf != null) {
            this.mOutBuffer = null;
            if (this.mOutPtr > 0) {
                this.mOut.write(buf, 0, this.mOutPtr);
                this.mOutPtr = 0;
            }
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(buf);
            }
        }
        if (forceClosing) {
            this.mOut.close();
        }
        int code = this.mSurrogate;
        if (code > 0) {
            this.mSurrogate = 0;
            throwIllegal(code);
        }
    }

    private final int _convertSurrogate(int secondPart) throws IOException {
        int firstPart = this.mSurrogate;
        this.mSurrogate = 0;
        if (secondPart < 56320 || secondPart > 57343) {
            throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + ((firstPart - 55296) << 10) + (secondPart - 56320);
    }

    private void throwIllegal(int code) throws IOException {
        if (code > 1114111) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 3629");
        }
        if (code >= 55296) {
            if (code <= 56319) {
                throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}
