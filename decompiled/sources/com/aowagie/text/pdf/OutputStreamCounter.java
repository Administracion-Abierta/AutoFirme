package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/OutputStreamCounter.class */
public class OutputStreamCounter extends OutputStream {
    private final OutputStream out;
    private int counter = 0;

    public OutputStreamCounter(OutputStream out) {
        this.out = out;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.close();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        this.counter += b.length;
        this.out.write(b);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.counter++;
        this.out.write(b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.counter += len;
        this.out.write(b, off, len);
    }

    public int getCounter() {
        return this.counter;
    }
}
