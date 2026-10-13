package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/InputStreamWithMAC.class */
public final class InputStreamWithMAC extends InputStream {
    private final InputStream base;
    private MACProvider macProvider;
    private byte[] mac;
    private boolean baseFinished = false;
    private int index = 0;

    InputStreamWithMAC(InputStream inputStream, MACProvider mACProvider) {
        this.base = inputStream;
        this.macProvider = mACProvider;
    }

    public InputStreamWithMAC(InputStream inputStream, byte[] bArr) {
        this.base = inputStream;
        this.mac = bArr;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.baseFinished) {
            if (this.index >= this.mac.length) {
                return -1;
            }
            byte[] bArr = this.mac;
            int i = this.index;
            this.index = i + 1;
            return bArr[i] & 255;
        }
        int i2 = this.base.read();
        if (i2 >= 0) {
            return i2;
        }
        this.baseFinished = true;
        if (this.macProvider != null) {
            this.macProvider.init();
            this.mac = this.macProvider.getMAC();
        }
        byte[] bArr2 = this.mac;
        int i3 = this.index;
        this.index = i3 + 1;
        return bArr2[i3] & 255;
    }

    public byte[] getMAC() {
        if (this.baseFinished) {
            return Arrays.clone(this.mac);
        }
        throw new IllegalStateException("input stream not fully processed");
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (bArr == null) {
            throw new NullPointerException("input array is null");
        }
        if (i < 0 || bArr.length < i + i2) {
            throw new IndexOutOfBoundsException("invalid off(" + i + ") and len(" + i2 + ")");
        }
        if (this.baseFinished) {
            if (this.index >= this.mac.length) {
                return -1;
            }
            if (i2 < this.mac.length - this.index) {
                System.arraycopy(this.mac, this.index, bArr, i, i2);
                this.index += i2;
                return i2;
            }
            System.arraycopy(this.mac, this.index, bArr, i, this.mac.length - this.index);
            int length = this.mac.length - this.index;
            this.index = this.mac.length;
            return length;
        }
        int i3 = this.base.read(bArr, i, i2);
        if (i3 >= 0) {
            return i3;
        }
        this.baseFinished = true;
        if (this.macProvider != null) {
            this.macProvider.init();
            this.mac = this.macProvider.getMAC();
        }
        if (i2 >= this.mac.length) {
            System.arraycopy(this.mac, 0, bArr, i, this.mac.length);
            this.index = this.mac.length;
            return this.mac.length;
        }
        System.arraycopy(this.mac, 0, bArr, i, i2);
        this.index += i2;
        return i2;
    }
}
