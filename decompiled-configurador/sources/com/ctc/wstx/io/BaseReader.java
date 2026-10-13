package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/BaseReader.class */
abstract class BaseReader extends Reader {
    protected static final char NULL_CHAR = 0;
    protected static final char NULL_BYTE = 0;
    protected static final char CONVERT_NEL_TO = '\n';
    protected static final char CONVERT_LSEP_TO = '\n';
    static final char CHAR_DEL = 127;
    protected final ReaderConfig mConfig;
    private InputStream mIn;
    protected byte[] mByteBuffer;
    protected int mBytePtr;
    protected int mByteBufferEnd;
    private final boolean mRecycleBuffer;
    protected char[] mTmpBuf = null;

    public abstract void setXmlCompliancy(int i);

    protected BaseReader(ReaderConfig cfg, InputStream in, byte[] buf, int ptr, int len, boolean recycleBuffer) {
        this.mConfig = cfg;
        this.mIn = in;
        this.mByteBuffer = buf;
        this.mBytePtr = ptr;
        this.mByteBufferEnd = len;
        this.mRecycleBuffer = recycleBuffer;
    }

    protected final boolean canModifyBuffer() {
        return this.mRecycleBuffer;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream in = this.mIn;
        if (in != null) {
            this.mIn = null;
            freeBuffers();
            in.close();
        }
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.mTmpBuf == null) {
            this.mTmpBuf = new char[1];
        }
        if (read(this.mTmpBuf, 0, 1) < 1) {
            return -1;
        }
        return this.mTmpBuf[0];
    }

    protected final InputStream getStream() {
        return this.mIn;
    }

    protected final int readBytes() throws IOException {
        this.mBytePtr = 0;
        this.mByteBufferEnd = 0;
        if (this.mIn != null) {
            int count = this.mIn.read(this.mByteBuffer, 0, this.mByteBuffer.length);
            if (count > 0) {
                this.mByteBufferEnd = count;
            }
            return count;
        }
        return -1;
    }

    protected final int readBytesAt(int offset) throws IOException {
        if (this.mIn != null) {
            int count = this.mIn.read(this.mByteBuffer, offset, this.mByteBuffer.length - offset);
            if (count > 0) {
                this.mByteBufferEnd += count;
            }
            return count;
        }
        return -1;
    }

    public final void freeBuffers() {
        byte[] buf;
        if (this.mRecycleBuffer && (buf = this.mByteBuffer) != null) {
            this.mByteBuffer = null;
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(buf);
            }
        }
    }

    protected void reportBounds(char[] cbuf, int start, int len) throws IOException {
        throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
    }

    protected void reportStrangeStream() throws IOException {
        throw new IOException("Strange I/O stream, returned 0 bytes on read");
    }

    protected void reportInvalidXml11(int value, int bytePos, int charPos) throws IOException {
        throw new CharConversionException("Invalid character 0x" + Integer.toHexString(value) + ", can only be included in xml 1.1 using character entities (at char #" + charPos + ", byte #" + bytePos + ")");
    }
}
