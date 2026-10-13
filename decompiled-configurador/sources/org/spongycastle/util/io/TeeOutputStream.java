package org.spongycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/io/TeeOutputStream.class */
public class TeeOutputStream extends OutputStream {
    private OutputStream output1;
    private OutputStream output2;

    public TeeOutputStream(OutputStream output1, OutputStream output2) {
        this.output1 = output1;
        this.output2 = output2;
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf) throws IOException {
        this.output1.write(buf);
        this.output2.write(buf);
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf, int off, int len) throws IOException {
        this.output1.write(buf, off, len);
        this.output2.write(buf, off, len);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.output1.write(b);
        this.output2.write(b);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.output1.flush();
        this.output2.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.output1.close();
        this.output2.close();
    }
}
