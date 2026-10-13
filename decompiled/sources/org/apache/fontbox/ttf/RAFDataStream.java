package org.apache.fontbox.ttf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/RAFDataStream.class */
class RAFDataStream extends TTFDataStream {
    private RandomAccessFile raf;
    private File ttfFile;
    private static final int BUFFERSIZE = 16384;

    RAFDataStream(String name, String mode) throws IOException {
        this(new File(name), mode);
    }

    RAFDataStream(File file, String mode) throws IOException {
        this.raf = null;
        this.ttfFile = null;
        this.raf = new BufferedRandomAccessFile(file, mode, 16384);
        this.ttfFile = file;
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public short readSignedShort() throws IOException {
        return this.raf.readShort();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getCurrentPosition() throws IOException {
        return this.raf.getFilePointer();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.raf != null) {
            this.raf.close();
            this.raf = null;
        }
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read() throws IOException {
        return this.raf.read();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int readUnsignedShort() throws IOException {
        return this.raf.readUnsignedShort();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long readLong() throws IOException {
        return this.raf.readLong();
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public void seek(long pos) throws IOException {
        this.raf.seek(pos);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.raf.read(b, off, len);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public InputStream getOriginalData() throws IOException {
        return new FileInputStream(this.ttfFile);
    }

    @Override // org.apache.fontbox.ttf.TTFDataStream
    public long getOriginalDataSize() {
        return this.ttfFile.length();
    }
}
