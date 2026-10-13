package org.bouncycastle.operator;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/MacCaptureStream.class */
public class MacCaptureStream extends OutputStream {
    private final OutputStream cOut;
    private final byte[] mac;
    int macIndex = 0;

    public MacCaptureStream(OutputStream outputStream, int i) {
        this.cOut = outputStream;
        this.mac = new byte[i];
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        if (i2 >= this.mac.length) {
            this.cOut.write(this.mac, 0, this.macIndex);
            this.macIndex = this.mac.length;
            System.arraycopy(bArr, (i + i2) - this.mac.length, this.mac, 0, this.mac.length);
            this.cOut.write(bArr, i, i2 - this.mac.length);
            return;
        }
        for (int i3 = 0; i3 != i2; i3++) {
            write(bArr[i + i3]);
        }
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        if (this.macIndex == this.mac.length) {
            byte b = this.mac[0];
            System.arraycopy(this.mac, 1, this.mac, 0, this.mac.length - 1);
            this.mac[this.mac.length - 1] = (byte) i;
            this.cOut.write(b);
            return;
        }
        byte[] bArr = this.mac;
        int i2 = this.macIndex;
        this.macIndex = i2 + 1;
        bArr[i2] = (byte) i;
    }

    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }
}
