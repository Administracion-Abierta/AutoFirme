package org.apache.commons.codec.binary;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/BaseNCodecInputStream.class */
public class BaseNCodecInputStream extends FilterInputStream {
    private final BaseNCodec baseNCodec;
    private final boolean doEncode;
    private final byte[] singleByte;
    private final byte[] buf;
    private final BaseNCodec.Context context;

    protected BaseNCodecInputStream(InputStream inputStream, BaseNCodec baseNCodec, boolean doEncode) {
        super(inputStream);
        this.singleByte = new byte[1];
        this.context = new BaseNCodec.Context();
        this.doEncode = doEncode;
        this.baseNCodec = baseNCodec;
        this.buf = new byte[doEncode ? 4096 : 8192];
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return this.context.eof ? 0 : 1;
    }

    public boolean isStrictDecoding() {
        return this.baseNCodec.isStrictDecoding();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void mark(int readLimit) {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int r;
        int i = read(this.singleByte, 0, 1);
        while (true) {
            r = i;
            if (r != 0) {
                break;
            }
            i = read(this.singleByte, 0, 1);
        }
        if (r > 0) {
            byte b = this.singleByte[0];
            return b < 0 ? 256 + b : b;
        }
        return -1;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] array, int offset, int len) throws IOException {
        Objects.requireNonNull(array, "array");
        if (offset < 0 || len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset > array.length || offset + len > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int i = 0;
        while (true) {
            int readLen = i;
            if (readLen < len) {
                if (!this.baseNCodec.hasData(this.context)) {
                    int c = this.in.read(this.buf);
                    if (this.doEncode) {
                        this.baseNCodec.encode(this.buf, 0, c, this.context);
                    } else {
                        this.baseNCodec.decode(this.buf, 0, c, this.context);
                    }
                }
                int read = this.baseNCodec.readResults(array, offset + readLen, len - readLen, this.context);
                if (read < 0) {
                    if (readLen != 0) {
                        return readLen;
                    }
                    return -1;
                }
                i = readLen + read;
            } else {
                return readLen;
            }
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        long todo;
        int len;
        if (n < 0) {
            throw new IllegalArgumentException("Negative skip length: " + n);
        }
        byte[] b = new byte[512];
        long j = n;
        while (true) {
            todo = j;
            if (todo <= 0 || (len = read(b, 0, (int) Math.min(b.length, todo))) == -1) {
                break;
            }
            j = todo - ((long) len);
        }
        return n - todo;
    }
}
