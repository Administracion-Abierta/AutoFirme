package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import org.apache.pdfbox.io.RandomAccessRead;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/RandomAccessSource.class */
final class RandomAccessSource implements SequentialSource {
    private final RandomAccessRead reader;

    RandomAccessSource(RandomAccessRead reader) {
        this.reader = reader;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read() throws IOException {
        return this.reader.read();
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read(byte[] b) throws IOException {
        return this.reader.read(b);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read(byte[] b, int offset, int length) throws IOException {
        return this.reader.read(b, offset, length);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public long getPosition() throws IOException {
        return this.reader.getPosition();
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int peek() throws IOException {
        return this.reader.peek();
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(int b) throws IOException {
        this.reader.rewind(1);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(byte[] bytes) throws IOException {
        this.reader.rewind(bytes.length);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(byte[] bytes, int start, int len) throws IOException {
        this.reader.rewind(len);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public byte[] readFully(int length) throws IOException {
        return this.reader.readFully(length);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public boolean isEOF() throws IOException {
        return this.reader.isEOF();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
    }
}
