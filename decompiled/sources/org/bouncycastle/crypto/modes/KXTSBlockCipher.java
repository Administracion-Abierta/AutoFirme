package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/modes/KXTSBlockCipher.class */
public class KXTSBlockCipher extends DefaultBufferedBlockCipher {
    private static final long RED_POLY_128 = 135;
    private static final long RED_POLY_256 = 1061;
    private static final long RED_POLY_512 = 293;
    private final int blockSize;
    private final long reductionPolynomial;
    private final long[] tw_init;
    private final long[] tw_current;
    private int counter;

    protected static long getReductionPolynomial(int i) {
        switch (i) {
            case 16:
                return RED_POLY_128;
            case 32:
                return RED_POLY_256;
            case 64:
                return RED_POLY_512;
            default:
                throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
        }
    }

    public KXTSBlockCipher(BlockCipher blockCipher) {
        this.cipher = blockCipher;
        this.blockSize = blockCipher.getBlockSize();
        this.reductionPolynomial = getReductionPolynomial(this.blockSize);
        this.tw_init = new long[this.blockSize >>> 3];
        this.tw_current = new long[this.blockSize >>> 3];
        this.counter = -1;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int i) {
        return i;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int i) {
        return i;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameters passed");
        }
        ParametersWithIV parametersWithIV = (ParametersWithIV) cipherParameters;
        CipherParameters parameters = parametersWithIV.getParameters();
        byte[] iv = parametersWithIV.getIV();
        if (iv.length != this.blockSize) {
            throw new IllegalArgumentException("Currently only support IVs of exactly one block");
        }
        byte[] bArr = new byte[this.blockSize];
        System.arraycopy(iv, 0, bArr, 0, this.blockSize);
        this.cipher.init(true, parameters);
        this.cipher.processBlock(bArr, 0, bArr, 0);
        this.cipher.init(z, parameters);
        Pack.littleEndianToLong(bArr, 0, this.tw_init);
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int processByte(byte b, byte[] bArr, int i) {
        throw new IllegalStateException("unsupported operation");
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (bArr.length - i < i2) {
            throw new DataLengthException("Input buffer too short");
        }
        if (bArr2.length - i < i2) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (i2 % this.blockSize != 0) {
            throw new IllegalArgumentException("Partial blocks not supported");
        }
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= i2) {
                return i2;
            }
            processBlocks(bArr, i + i5, bArr2, i3 + i5);
            i4 = i5 + this.blockSize;
        }
    }

    private void processBlocks(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.counter == -1) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        this.counter++;
        GF_double(this.reductionPolynomial, this.tw_current);
        byte[] bArr3 = new byte[this.blockSize];
        Pack.longToLittleEndian(this.tw_current, bArr3, 0);
        byte[] bArr4 = new byte[this.blockSize];
        System.arraycopy(bArr3, 0, bArr4, 0, this.blockSize);
        for (int i3 = 0; i3 < this.blockSize; i3++) {
            int i4 = i3;
            bArr4[i4] = (byte) (bArr4[i4] ^ bArr[i + i3]);
        }
        this.cipher.processBlock(bArr4, 0, bArr4, 0);
        for (int i5 = 0; i5 < this.blockSize; i5++) {
            bArr2[i2 + i5] = (byte) (bArr4[i5] ^ bArr3[i5]);
        }
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] bArr, int i) {
        reset();
        return 0;
    }

    @Override // org.bouncycastle.crypto.DefaultBufferedBlockCipher, org.bouncycastle.crypto.BufferedBlockCipher
    public void reset() {
        this.cipher.reset();
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    private static void GF_double(long j, long[] jArr) {
        long j2 = 0;
        for (int i = 0; i < jArr.length; i++) {
            long j3 = jArr[i];
            jArr[i] = (j3 << 1) ^ j2;
            j2 = j3 >>> 63;
        }
        jArr[0] = jArr[0] ^ (j & (-j2));
    }
}
