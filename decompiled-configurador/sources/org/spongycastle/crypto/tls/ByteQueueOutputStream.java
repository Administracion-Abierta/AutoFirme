package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/ByteQueueOutputStream.class */
public class ByteQueueOutputStream extends OutputStream {
    private ByteQueue buffer = new ByteQueue();

    public ByteQueue getBuffer() {
        return this.buffer;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this.buffer.addData(new byte[]{(byte) b}, 0, 1);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.buffer.addData(b, off, len);
    }
}
