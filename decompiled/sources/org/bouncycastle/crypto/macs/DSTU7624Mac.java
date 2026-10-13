package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/macs/DSTU7624Mac.class */
public class DSTU7624Mac implements Mac {
    private static final int BITS_IN_BYTE = 8;
    private byte[] buf;
    private int bufOff;
    private int macSize;
    private int blockSize;
    private DSTU7624Engine engine;
    private byte[] c;
    private byte[] cTemp;
    private byte[] kDelta;
    private boolean initCalled = false;

    public DSTU7624Mac(int i, int i2) {
        this.engine = new DSTU7624Engine(i);
        this.blockSize = i / 8;
        this.macSize = i2 / 8;
        this.c = new byte[this.blockSize];
        this.kDelta = new byte[this.blockSize];
        this.cTemp = new byte[this.blockSize];
        this.buf = new byte[this.blockSize];
    }

    @Override // org.bouncycastle.crypto.Mac
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
        }
        this.engine.init(true, cipherParameters);
        this.initCalled = true;
        reset();
    }

    @Override // org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return "DSTU7624Mac";
    }

    @Override // org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte b) {
        if (this.bufOff == this.buf.length) {
            processBlock(this.buf, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void update(byte[] bArr, int i, int i2) {
        if (i2 < 0) {
            throw new IllegalArgumentException("can't have a negative input length!");
        }
        int blockSize = this.engine.getBlockSize();
        int i3 = blockSize - this.bufOff;
        if (i2 > i3) {
            System.arraycopy(bArr, i, this.buf, this.bufOff, i3);
            processBlock(this.buf, 0);
            this.bufOff = 0;
            i2 -= i3;
            int i4 = i;
            int i5 = i3;
            while (true) {
                i = i4 + i5;
                if (i2 <= blockSize) {
                    break;
                }
                processBlock(bArr, i);
                i2 -= blockSize;
                i4 = i;
                i5 = blockSize;
            }
        }
        System.arraycopy(bArr, i, this.buf, this.bufOff, i2);
        this.bufOff += i2;
    }

    private void processBlock(byte[] bArr, int i) {
        xor(this.c, 0, bArr, i, this.cTemp);
        this.engine.processBlock(this.cTemp, 0, this.c, 0);
    }

    @Override // org.bouncycastle.crypto.Mac
    public int doFinal(byte[] bArr, int i) throws IllegalStateException, DataLengthException {
        if (this.bufOff % this.buf.length != 0) {
            throw new DataLengthException("input must be a multiple of blocksize");
        }
        xor(this.c, 0, this.buf, 0, this.cTemp);
        xor(this.cTemp, 0, this.kDelta, 0, this.c);
        this.engine.processBlock(this.c, 0, this.c, 0);
        if (this.macSize + i > bArr.length) {
            throw new OutputLengthException("output buffer too short");
        }
        System.arraycopy(this.c, 0, bArr, i, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.bouncycastle.crypto.Mac
    public void reset() {
        Arrays.fill(this.c, (byte) 0);
        Arrays.fill(this.cTemp, (byte) 0);
        Arrays.fill(this.kDelta, (byte) 0);
        Arrays.fill(this.buf, (byte) 0);
        this.engine.reset();
        if (this.initCalled) {
            this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
        }
        this.bufOff = 0;
    }

    private void xor(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3) {
        if (bArr.length - i < this.blockSize || bArr2.length - i2 < this.blockSize || bArr3.length < this.blockSize) {
            throw new IllegalArgumentException("some of input buffers too short");
        }
        for (int i3 = 0; i3 < this.blockSize; i3++) {
            bArr3[i3] = (byte) (bArr[i3 + i] ^ bArr2[i3 + i2]);
        }
    }
}
