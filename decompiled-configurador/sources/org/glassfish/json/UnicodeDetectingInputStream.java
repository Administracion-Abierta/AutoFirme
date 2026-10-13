package org.glassfish.json;

import com.ctc.wstx.io.CharsetNames;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.json.JsonException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/glassfish/json/UnicodeDetectingInputStream.class */
class UnicodeDetectingInputStream extends FilterInputStream {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset UTF_16BE = Charset.forName(CharsetNames.CS_UTF16BE);
    private static final Charset UTF_16LE = Charset.forName(CharsetNames.CS_UTF16LE);
    private static final Charset UTF_32LE = Charset.forName(CharsetNames.CS_UTF32LE);
    private static final Charset UTF_32BE = Charset.forName(CharsetNames.CS_UTF32BE);
    private static final byte FF = -1;
    private static final byte FE = -2;
    private static final byte EF = -17;
    private static final byte BB = -69;
    private static final byte BF = -65;
    private static final byte NUL = 0;
    private final byte[] buf;
    private int bufLen;
    private int curIndex;
    private final Charset charset;

    UnicodeDetectingInputStream(InputStream is) {
        super(is);
        this.buf = new byte[4];
        this.charset = detectEncoding();
    }

    Charset getCharset() {
        return this.charset;
    }

    private void fillBuf() {
        try {
            int b1 = this.in.read();
            if (b1 == -1) {
                return;
            }
            int b2 = this.in.read();
            if (b2 == -1) {
                this.bufLen = 1;
                this.buf[0] = (byte) b1;
                return;
            }
            int b3 = this.in.read();
            if (b3 == -1) {
                this.bufLen = 2;
                this.buf[0] = (byte) b1;
                this.buf[1] = (byte) b2;
                return;
            }
            int b4 = this.in.read();
            if (b4 == -1) {
                this.bufLen = 3;
                this.buf[0] = (byte) b1;
                this.buf[1] = (byte) b2;
                this.buf[2] = (byte) b3;
                return;
            }
            this.bufLen = 4;
            this.buf[0] = (byte) b1;
            this.buf[1] = (byte) b2;
            this.buf[2] = (byte) b3;
            this.buf[3] = (byte) b4;
        } catch (IOException ioe) {
            throw new JsonException("I/O error while auto-detecting the encoding of stream", ioe);
        }
    }

    private Charset detectEncoding() {
        fillBuf();
        if (this.bufLen < 2) {
            throw new JsonException("Cannot auto-detect encoding, not enough chars");
        }
        if (this.bufLen == 4) {
            if (this.buf[0] == 0 && this.buf[1] == 0 && this.buf[2] == -2 && this.buf[3] == -1) {
                this.curIndex = 4;
                return UTF_32BE;
            }
            if (this.buf[0] == -1 && this.buf[1] == -2 && this.buf[2] == 0 && this.buf[3] == 0) {
                this.curIndex = 4;
                return UTF_32LE;
            }
            if (this.buf[0] == -2 && this.buf[1] == -1) {
                this.curIndex = 2;
                return UTF_16BE;
            }
            if (this.buf[0] == -1 && this.buf[1] == -2) {
                this.curIndex = 2;
                return UTF_16LE;
            }
            if (this.buf[0] == EF && this.buf[1] == BB && this.buf[2] == BF) {
                this.curIndex = 3;
                return UTF_8;
            }
            if (this.buf[0] == 0 && this.buf[1] == 0 && this.buf[2] == 0) {
                return UTF_32BE;
            }
            if (this.buf[0] == 0 && this.buf[2] == 0) {
                return UTF_16BE;
            }
            if (this.buf[1] == 0 && this.buf[2] == 0 && this.buf[3] == 0) {
                return UTF_32LE;
            }
            if (this.buf[1] == 0 && this.buf[3] == 0) {
                return UTF_16LE;
            }
        }
        return UTF_8;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.curIndex < this.bufLen) {
            byte[] bArr = this.buf;
            int i = this.curIndex;
            this.curIndex = i + 1;
            return bArr[i];
        }
        return this.in.read();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.curIndex < this.bufLen) {
            if (len == 0) {
                return 0;
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            int min = Math.min(this.bufLen - this.curIndex, len);
            System.arraycopy(this.buf, this.curIndex, b, off, min);
            this.curIndex += min;
            return min;
        }
        return this.in.read(b, off, len);
    }
}
