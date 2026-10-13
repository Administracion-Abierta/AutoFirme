package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/IDEAEngine.class */
public class IDEAEngine implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey = null;
    private boolean forEncryption;
    private static final int MASK = 65535;
    private static final int BASE = 65537;

    public IDEAEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to IDEA init - " + cipherParameters.getClass().getName());
        }
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        this.workingKey = generateWorkingKey(z, key);
        this.forEncryption = z;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), key.length * 8, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "IDEA";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("IDEA engine not initialised");
        }
        if (i + 8 > bArr.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 + 8 > bArr2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        ideaFunc(this.workingKey, bArr, i, bArr2, i2);
        return 8;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private int bytesToWord(byte[] bArr, int i) {
        return ((bArr[i] << 8) & 65280) + (bArr[i + 1] & 255);
    }

    private void wordToBytes(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >>> 8);
        bArr[i2 + 1] = (byte) i;
    }

    private int mul(int i, int i2) {
        int i3;
        if (i == 0) {
            i3 = BASE - i2;
        } else if (i2 == 0) {
            i3 = BASE - i;
        } else {
            int i4 = i * i2;
            int i5 = i4 & 65535;
            int i6 = i4 >>> 16;
            i3 = (i5 - i6) + (i5 < i6 ? 1 : 0);
        }
        return i3 & 65535;
    }

    private void ideaFunc(int[] iArr, byte[] bArr, int i, byte[] bArr2, int i2) {
        int i3 = 0;
        int iBytesToWord = bytesToWord(bArr, i);
        int iBytesToWord2 = bytesToWord(bArr, i + 2);
        int iBytesToWord3 = bytesToWord(bArr, i + 4);
        int iBytesToWord4 = bytesToWord(bArr, i + 6);
        for (int i4 = 0; i4 < 8; i4++) {
            int i5 = i3;
            int i6 = i3 + 1;
            int iMul = mul(iBytesToWord, iArr[i5]);
            int i7 = i6 + 1;
            int i8 = (iBytesToWord2 + iArr[i6]) & 65535;
            int i9 = i7 + 1;
            int i10 = (iBytesToWord3 + iArr[i7]) & 65535;
            int i11 = i9 + 1;
            int iMul2 = mul(iBytesToWord4, iArr[i9]);
            int i12 = i10 ^ iMul;
            int i13 = i8 ^ iMul2;
            int i14 = i11 + 1;
            int iMul3 = mul(i12, iArr[i11]);
            i3 = i14 + 1;
            int iMul4 = mul((i13 + iMul3) & 65535, iArr[i14]);
            int i15 = (iMul3 + iMul4) & 65535;
            iBytesToWord = iMul ^ iMul4;
            iBytesToWord4 = iMul2 ^ i15;
            iBytesToWord2 = iMul4 ^ i10;
            iBytesToWord3 = i15 ^ i8;
        }
        int i16 = i3;
        int i17 = i3 + 1;
        wordToBytes(mul(iBytesToWord, iArr[i16]), bArr2, i2);
        int i18 = i17 + 1;
        wordToBytes(iBytesToWord3 + iArr[i17], bArr2, i2 + 2);
        wordToBytes(iBytesToWord2 + iArr[i18], bArr2, i2 + 4);
        wordToBytes(mul(iBytesToWord4, iArr[i18 + 1]), bArr2, i2 + 6);
    }

    private int[] expandKey(byte[] bArr) {
        int[] iArr = new int[52];
        if (bArr.length < 16) {
            byte[] bArr2 = new byte[16];
            System.arraycopy(bArr, 0, bArr2, bArr2.length - bArr.length, bArr.length);
            bArr = bArr2;
        }
        for (int i = 0; i < 8; i++) {
            iArr[i] = bytesToWord(bArr, i * 2);
        }
        for (int i2 = 8; i2 < 52; i2++) {
            if ((i2 & 7) < 6) {
                iArr[i2] = (((iArr[i2 - 7] & 127) << 9) | (iArr[i2 - 6] >> 7)) & 65535;
            } else if ((i2 & 7) == 6) {
                iArr[i2] = (((iArr[i2 - 7] & 127) << 9) | (iArr[i2 - 14] >> 7)) & 65535;
            } else {
                iArr[i2] = (((iArr[i2 - 15] & 127) << 9) | (iArr[i2 - 14] >> 7)) & 65535;
            }
        }
        return iArr;
    }

    private int mulInv(int i) {
        if (i < 2) {
            return i;
        }
        int i2 = 1;
        int i3 = BASE / i;
        int i4 = BASE % i;
        while (i4 != 1) {
            int i5 = i / i4;
            i %= i4;
            i2 = (i2 + (i3 * i5)) & 65535;
            if (i == 1) {
                return i2;
            }
            int i6 = i4 / i;
            i4 %= i;
            i3 = (i3 + (i2 * i6)) & 65535;
        }
        return (1 - i3) & 65535;
    }

    int addInv(int i) {
        return (0 - i) & 65535;
    }

    private int[] invertKey(int[] iArr) {
        int[] iArr2 = new int[52];
        int i = 0 + 1;
        int iMulInv = mulInv(iArr[0]);
        int i2 = i + 1;
        int iAddInv = addInv(iArr[i]);
        int i3 = i2 + 1;
        int iAddInv2 = addInv(iArr[i2]);
        int i4 = i3 + 1;
        int i5 = 52 - 1;
        iArr2[i5] = mulInv(iArr[i3]);
        int i6 = i5 - 1;
        iArr2[i6] = iAddInv2;
        int i7 = i6 - 1;
        iArr2[i7] = iAddInv;
        int i8 = i7 - 1;
        iArr2[i8] = iMulInv;
        for (int i9 = 1; i9 < 8; i9++) {
            int i10 = i4;
            int i11 = i4 + 1;
            int i12 = iArr[i10];
            int i13 = i11 + 1;
            int i14 = i8 - 1;
            iArr2[i14] = iArr[i11];
            int i15 = i14 - 1;
            iArr2[i15] = i12;
            int i16 = i13 + 1;
            int iMulInv2 = mulInv(iArr[i13]);
            int i17 = i16 + 1;
            int iAddInv3 = addInv(iArr[i16]);
            int i18 = i17 + 1;
            int iAddInv4 = addInv(iArr[i17]);
            i4 = i18 + 1;
            int i19 = i15 - 1;
            iArr2[i19] = mulInv(iArr[i18]);
            int i20 = i19 - 1;
            iArr2[i20] = iAddInv3;
            int i21 = i20 - 1;
            iArr2[i21] = iAddInv4;
            i8 = i21 - 1;
            iArr2[i8] = iMulInv2;
        }
        int i22 = i4;
        int i23 = i4 + 1;
        int i24 = iArr[i22];
        int i25 = i23 + 1;
        int i26 = i8 - 1;
        iArr2[i26] = iArr[i23];
        int i27 = i26 - 1;
        iArr2[i27] = i24;
        int i28 = i25 + 1;
        int iMulInv3 = mulInv(iArr[i25]);
        int i29 = i28 + 1;
        int iAddInv5 = addInv(iArr[i28]);
        int iAddInv6 = addInv(iArr[i29]);
        int i30 = i27 - 1;
        iArr2[i30] = mulInv(iArr[i29 + 1]);
        int i31 = i30 - 1;
        iArr2[i31] = iAddInv6;
        int i32 = i31 - 1;
        iArr2[i32] = iAddInv5;
        iArr2[i32 - 1] = iMulInv3;
        return iArr2;
    }

    private int[] generateWorkingKey(boolean z, byte[] bArr) {
        return z ? expandKey(bArr) : invertKey(expandKey(bArr));
    }
}
