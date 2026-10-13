package org.apache.pdfbox.pdfparser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/InputStreamSource.class */
final class InputStreamSource implements SequentialSource {
    private final PushbackInputStream input;
    private int position = 0;

    InputStreamSource(InputStream input) {
        this.input = new PushbackInputStream(input, 32767);
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read() throws IOException {
        int b = this.input.read();
        this.position++;
        return b;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read(byte[] b) throws IOException {
        int n = this.input.read(b);
        if (n > 0) {
            this.position += n;
            return n;
        }
        return -1;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int read(byte[] b, int offset, int length) throws IOException {
        int n = this.input.read(b, offset, length);
        if (n > 0) {
            this.position += n;
            return n;
        }
        return -1;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public long getPosition() throws IOException {
        return this.position;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public int peek() throws IOException {
        int b = this.input.read();
        if (b != -1) {
            this.input.unread(b);
        }
        return b;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(int b) throws IOException {
        this.input.unread(b);
        this.position--;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(byte[] bytes) throws IOException {
        this.input.unread(bytes);
        this.position -= bytes.length;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public void unread(byte[] bytes, int start, int len) throws IOException {
        this.input.unread(bytes, start, len);
        this.position -= len;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public byte[] readFully(int length) throws IOException {
        byte[] bytes = new byte[length];
        int bytesRead = 0;
        do {
            int count = read(bytes, bytesRead, length - bytesRead);
            if (count < 0) {
                throw new EOFException();
            }
            bytesRead += count;
        } while (bytesRead < length);
        return bytes;
    }

    @Override // org.apache.pdfbox.pdfparser.SequentialSource
    public boolean isEOF() throws IOException {
        return peek() == -1;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.input.close();
    }
}
