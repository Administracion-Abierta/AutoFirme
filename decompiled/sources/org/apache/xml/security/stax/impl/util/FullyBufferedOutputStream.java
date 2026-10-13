package org.apache.xml.security.stax.impl.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/util/FullyBufferedOutputStream.class */
public class FullyBufferedOutputStream extends FilterOutputStream {
    private UnsyncByteArrayOutputStream buf;

    public FullyBufferedOutputStream(OutputStream out) {
        super(out);
        this.buf = new UnsyncByteArrayOutputStream();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.buf.write(b);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        this.buf.write(b);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.buf.write(b, off, len);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.buf.writeTo(this.out);
        this.out.close();
        this.buf.close();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
    }
}
