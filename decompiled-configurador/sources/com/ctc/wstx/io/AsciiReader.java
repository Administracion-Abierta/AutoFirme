package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/AsciiReader.class */
public final class AsciiReader extends BaseReader {
    boolean mXml11;
    int mCharCount;

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    public AsciiReader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mCharCount = 0;
    }

    @Override // com.ctc.wstx.io.BaseReader
    public void setXmlCompliancy(int xmlVersion) {
        this.mXml11 = xmlVersion == 272;
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int start, int len) throws IOException {
        if (start < 0 || start + len > cbuf.length) {
            reportBounds(cbuf, start, len);
        }
        if (this.mByteBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return 0;
        }
        int avail = this.mByteBufferEnd - this.mBytePtr;
        if (avail <= 0) {
            this.mCharCount += this.mByteBufferEnd;
            int count = readBytes();
            if (count <= 0) {
                if (count == 0) {
                    reportStrangeStream();
                }
                freeBuffers();
                return -1;
            }
            avail = count;
        }
        if (len > avail) {
            len = avail;
        }
        int i = this.mBytePtr;
        int last = i + len;
        while (i < last) {
            int i2 = i;
            i++;
            char c = (char) this.mByteBuffer[i2];
            if (c >= 127) {
                if (c > 127) {
                    reportInvalidAscii(c);
                } else if (this.mXml11) {
                    int pos = this.mCharCount + this.mBytePtr;
                    reportInvalidXml11(c, pos, pos);
                }
            }
            int i3 = start;
            start++;
            cbuf[i3] = c;
        }
        this.mBytePtr = last;
        return len;
    }

    private void reportInvalidAscii(char c) throws IOException {
        throw new CharConversionException("Invalid ascii byte; value above 7-bit ascii range (" + ((int) c) + "; at pos #" + (this.mCharCount + this.mBytePtr) + ")");
    }
}
