package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/NullOutputStream.class */
class NullOutputStream extends OutputStream {
    NullOutputStream() {
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf) throws IOException {
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf, int off, int len) throws IOException {
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
    }
}
