package org.apache.fontbox.ttf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/MemoryTTFDataStream.class */
class MemoryTTFDataStream extends TTFDataStream {
    private byte[] data;
    private int currentPosition = 0;

    MemoryTTFDataStream(InputStream is) throws IOException {
        this.data = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(is.available());
            byte[] buffer = new byte[1024];
            while (true) {
                int amountRead = is.read(buffer);
                if (amountRead != -1) {
                    output.write(buffer, 0, amountRead);
                } else {
                    this.data = output.toByteArray();
                    return;
                }
            }
        } finally {
            is.close();
        }
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long readLong() throws IOException {
        return (((long) readSignedInt()) << 32) + (((long) readSignedInt()) & 4294967295L);
    }

    public int readSignedInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read() throws IOException {
        if (this.currentPosition >= this.data.length) {
            return -1;
        }
        byte b = this.data[this.currentPosition];
        this.currentPosition++;
        return (b + 256) % 256;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int readUnsignedShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + ch2;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public short readSignedShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + ch2);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public void seek(long pos) throws IOException {
        if (pos < 0 || pos > 2147483647L) {
            throw new IOException("Illegal seek position: " + pos);
        }
        this.currentPosition = (int) pos;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.currentPosition < this.data.length) {
            int amountRead = Math.min(len, this.data.length - this.currentPosition);
            System.arraycopy(this.data, this.currentPosition, b, off, amountRead);
            this.currentPosition += amountRead;
            return amountRead;
        }
        return -1;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getCurrentPosition() throws IOException {
        return this.currentPosition;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public InputStream getOriginalData() throws IOException {
        return new ByteArrayInputStream(this.data);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getOriginalDataSize() {
        return this.data.length;
    }
}
