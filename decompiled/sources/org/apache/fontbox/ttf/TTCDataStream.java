package org.apache.fontbox.ttf;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TTCDataStream.class */
class TTCDataStream extends TTFDataStream {
    private final TTFDataStream stream;

    TTCDataStream(TTFDataStream stream) {
        this.stream = stream;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read() throws IOException {
        return this.stream.read();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long readLong() throws IOException {
        return this.stream.readLong();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int readUnsignedShort() throws IOException {
        return this.stream.readUnsignedShort();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public short readSignedShort() throws IOException {
        return this.stream.readSignedShort();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public void seek(long pos) throws IOException {
        this.stream.seek(pos);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.stream.read(b, off, len);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getCurrentPosition() throws IOException {
        return this.stream.getCurrentPosition();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public InputStream getOriginalData() throws IOException {
        return this.stream.getOriginalData();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getOriginalDataSize() {
        return this.stream.getOriginalDataSize();
    }
}
