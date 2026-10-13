package org.apache.pdfbox.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/RandomAccessFile.class */
public class RandomAccessFile implements RandomAccess {
    private final java.io.RandomAccessFile ras;
    private boolean isClosed;

    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        this.ras = new java.io.RandomAccessFile(file, mode);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.ras.close();
        this.isClosed = true;
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void clear() throws IOException {
        checkClosed();
        this.ras.seek(0L);
        this.ras.setLength(0L);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void seek(long position) throws IOException {
        checkClosed();
        this.ras.seek(position);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long getPosition() throws IOException {
        checkClosed();
        return this.ras.getFilePointer();
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read() throws IOException {
        checkClosed();
        return this.ras.read();
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b) throws IOException {
        checkClosed();
        return this.ras.read(b);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        return this.ras.read(b, offset, length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long length() throws IOException {
        checkClosed();
        return this.ras.length();
    }

    private void checkClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("RandomAccessFile already closed");
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        this.ras.write(b, offset, length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(int b) throws IOException {
        checkClosed();
        this.ras.write(b);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int peek() throws IOException {
        int result = read();
        if (result != -1) {
            rewind(1);
        }
        return result;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void rewind(int bytes) throws IOException {
        checkClosed();
        this.ras.seek(this.ras.getFilePointer() - ((long) bytes));
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public byte[] readFully(int length) throws IOException {
        checkClosed();
        byte[] b = new byte[length];
        this.ras.readFully(b);
        return b;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isEOF() throws IOException {
        return peek() == -1;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int available() throws IOException {
        checkClosed();
        return (int) Math.min(this.ras.length() - getPosition(), 2147483647L);
    }
}
