package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/ISOLatinReader.class */
public final class ISOLatinReader extends BaseReader {
    boolean mXml11;
    int mByteCount;

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader
    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    @Override // com.ctc.wstx.io.BaseReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    public ISOLatinReader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mByteCount = 0;
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
            this.mByteCount += this.mByteBufferEnd;
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
        if (this.mXml11) {
            while (i < last) {
                int i2 = i;
                i++;
                char c = (char) (this.mByteBuffer[i2] & 255);
                if (c >= 127 && c <= 159) {
                    if (c == 133) {
                        c = '\n';
                    } else {
                        int pos = this.mByteCount + i;
                        reportInvalidXml11(c, pos, pos);
                    }
                }
                int i3 = start;
                start++;
                cbuf[i3] = c;
            }
        } else {
            while (i < last) {
                int i4 = start;
                start++;
                int i5 = i;
                i++;
                cbuf[i4] = (char) (this.mByteBuffer[i5] & 255);
            }
        }
        this.mBytePtr = last;
        return len;
    }
}
