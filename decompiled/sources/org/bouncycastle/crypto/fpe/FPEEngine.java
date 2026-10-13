package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/fpe/FPEEngine.class */
public abstract class FPEEngine {
    protected final BlockCipher baseCipher;
    protected boolean forEncryption;
    protected FPEParameters fpeParameters;

    protected FPEEngine(BlockCipher blockCipher) {
        this.baseCipher = blockCipher;
    }

    public int processBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        if (this.fpeParameters == null) {
            throw new IllegalStateException("FPE engine not initialized");
        }
        if (i2 < 0) {
            throw new IllegalArgumentException("input length cannot be negative");
        }
        if (bArr == null || bArr2 == null) {
            throw new NullPointerException("buffer value is null");
        }
        if (bArr.length < i + i2) {
            throw new DataLengthException("input buffer too short");
        }
        if (bArr2.length < i3 + i2) {
            throw new OutputLengthException("output buffer too short");
        }
        return this.forEncryption ? encryptBlock(bArr, i, i2, bArr2, i3) : decryptBlock(bArr, i, i2, bArr2, i3);
    }

    protected static short[] toShortArray(byte[] bArr) {
        if ((bArr.length & 1) != 0) {
            throw new IllegalArgumentException("data must be an even number of bytes for a wide radix");
        }
        short[] sArr = new short[bArr.length / 2];
        for (int i = 0; i != sArr.length; i++) {
            sArr[i] = Pack.bigEndianToShort(bArr, i * 2);
        }
        return sArr;
    }

    protected static byte[] toByteArray(short[] sArr) {
        byte[] bArr = new byte[sArr.length * 2];
        for (int i = 0; i != sArr.length; i++) {
            Pack.shortToBigEndian(sArr[i], bArr, i * 2);
        }
        return bArr;
    }

    public abstract void init(boolean z, CipherParameters cipherParameters);

    public abstract String getAlgorithmName();

    protected abstract int encryptBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3);

    protected abstract int decryptBlock(byte[] bArr, int i, int i2, byte[] bArr2, int i3);
}
