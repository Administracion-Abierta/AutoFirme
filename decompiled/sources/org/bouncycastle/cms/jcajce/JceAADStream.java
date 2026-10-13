package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceAADStream.class */
class JceAADStream extends OutputStream {
    private final byte[] SINGLE_BYTE = new byte[1];
    private Cipher cipher;

    JceAADStream(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        this.cipher.updateAAD(bArr, i, i2);
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        this.SINGLE_BYTE[0] = (byte) i;
        this.cipher.updateAAD(this.SINGLE_BYTE, 0, 1);
    }
}
