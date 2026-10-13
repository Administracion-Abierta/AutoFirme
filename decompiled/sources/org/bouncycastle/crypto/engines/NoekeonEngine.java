package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/NoekeonEngine.class */
public class NoekeonEngine implements BlockCipher {
    private static final int SIZE = 16;
    private static final byte[] roundConstants = {-128, 27, 54, 108, -40, -85, 77, -102, 47, 94, -68, 99, -58, -105, 53, 106, -44};
    private final int[] k = new int[4];
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Noekeon";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + cipherParameters.getClass().getName());
        }
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("Key length not 128 bits.");
        }
        Pack.bigEndianToInt(key, 0, this.k, 0, 4);
        if (!z) {
            int i = this.k[0];
            int i2 = this.k[1];
            int i3 = this.k[2];
            int i4 = this.k[3];
            int i5 = i ^ i3;
            int iRotateLeft = i5 ^ (Integers.rotateLeft(i5, 8) ^ Integers.rotateLeft(i5, 24));
            int i6 = i2 ^ i4;
            int iRotateLeft2 = i6 ^ (Integers.rotateLeft(i6, 8) ^ Integers.rotateLeft(i6, 24));
            int i7 = i ^ iRotateLeft2;
            int i8 = i2 ^ iRotateLeft;
            this.k[0] = i7;
            this.k[1] = i8;
            this.k[2] = i3 ^ iRotateLeft2;
            this.k[3] = i4 ^ iRotateLeft;
        }
        this._forEncryption = z;
        this._initialised = true;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(z)));
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        if (!this._initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (i > bArr.length - 16) {
            throw new DataLengthException("input buffer too short");
        }
        if (i2 > bArr2.length - 16) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBigEndianToInt = Pack.bigEndianToInt(bArr, i);
        int iBigEndianToInt2 = Pack.bigEndianToInt(bArr, i + 4);
        int iBigEndianToInt3 = Pack.bigEndianToInt(bArr, i + 8);
        int iBigEndianToInt4 = Pack.bigEndianToInt(bArr, i + 12);
        int i3 = this.k[0];
        int i4 = this.k[1];
        int i5 = this.k[2];
        int i6 = this.k[3];
        int i7 = 0;
        while (true) {
            int i8 = iBigEndianToInt ^ (roundConstants[i7] & 255);
            int i9 = i8 ^ iBigEndianToInt3;
            int iRotateLeft = i9 ^ (Integers.rotateLeft(i9, 8) ^ Integers.rotateLeft(i9, 24));
            int i10 = i8 ^ i3;
            int i11 = iBigEndianToInt2 ^ i4;
            int i12 = iBigEndianToInt3 ^ i5;
            int i13 = iBigEndianToInt4 ^ i6;
            int i14 = i11 ^ i13;
            int iRotateLeft2 = i14 ^ (Integers.rotateLeft(i14, 8) ^ Integers.rotateLeft(i14, 24));
            int i15 = i10 ^ iRotateLeft2;
            int i16 = i11 ^ iRotateLeft;
            int i17 = i12 ^ iRotateLeft2;
            int i18 = i13 ^ iRotateLeft;
            i7++;
            if (i7 > 16) {
                Pack.intToBigEndian(i15, bArr2, i2);
                Pack.intToBigEndian(i16, bArr2, i2 + 4);
                Pack.intToBigEndian(i17, bArr2, i2 + 8);
                Pack.intToBigEndian(i18, bArr2, i2 + 12);
                return 16;
            }
            int iRotateLeft3 = Integers.rotateLeft(i16, 1);
            int iRotateLeft4 = Integers.rotateLeft(i17, 5);
            int iRotateLeft5 = Integers.rotateLeft(i18, 2);
            int i19 = iRotateLeft3 ^ (iRotateLeft5 | iRotateLeft4);
            int i20 = i15 ^ (iRotateLeft4 & (i19 ^ (-1)));
            int i21 = ((iRotateLeft5 ^ (i19 ^ (-1))) ^ iRotateLeft4) ^ i20;
            int i22 = i19 ^ (i20 | i21);
            iBigEndianToInt = iRotateLeft5 ^ (i21 & i22);
            iBigEndianToInt2 = Integers.rotateLeft(i22, 31);
            iBigEndianToInt3 = Integers.rotateLeft(i21, 27);
            iBigEndianToInt4 = Integers.rotateLeft(i20, 30);
        }
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int iBigEndianToInt = Pack.bigEndianToInt(bArr, i);
        int iBigEndianToInt2 = Pack.bigEndianToInt(bArr, i + 4);
        int iBigEndianToInt3 = Pack.bigEndianToInt(bArr, i + 8);
        int iBigEndianToInt4 = Pack.bigEndianToInt(bArr, i + 12);
        int i3 = this.k[0];
        int i4 = this.k[1];
        int i5 = this.k[2];
        int i6 = this.k[3];
        int i7 = 16;
        while (true) {
            int i8 = iBigEndianToInt ^ iBigEndianToInt3;
            int iRotateLeft = i8 ^ (Integers.rotateLeft(i8, 8) ^ Integers.rotateLeft(i8, 24));
            int i9 = iBigEndianToInt ^ i3;
            int i10 = iBigEndianToInt2 ^ i4;
            int i11 = iBigEndianToInt3 ^ i5;
            int i12 = iBigEndianToInt4 ^ i6;
            int i13 = i10 ^ i12;
            int iRotateLeft2 = i13 ^ (Integers.rotateLeft(i13, 8) ^ Integers.rotateLeft(i13, 24));
            int i14 = i9 ^ iRotateLeft2;
            int i15 = i10 ^ iRotateLeft;
            int i16 = i11 ^ iRotateLeft2;
            int i17 = i12 ^ iRotateLeft;
            int i18 = i14 ^ (roundConstants[i7] & 255);
            i7--;
            if (i7 < 0) {
                Pack.intToBigEndian(i18, bArr2, i2);
                Pack.intToBigEndian(i15, bArr2, i2 + 4);
                Pack.intToBigEndian(i16, bArr2, i2 + 8);
                Pack.intToBigEndian(i17, bArr2, i2 + 12);
                return 16;
            }
            int iRotateLeft3 = Integers.rotateLeft(i15, 1);
            int iRotateLeft4 = Integers.rotateLeft(i16, 5);
            int iRotateLeft5 = Integers.rotateLeft(i17, 2);
            int i19 = iRotateLeft3 ^ (iRotateLeft5 | iRotateLeft4);
            int i20 = i18 ^ (iRotateLeft4 & (i19 ^ (-1)));
            int i21 = ((iRotateLeft5 ^ (i19 ^ (-1))) ^ iRotateLeft4) ^ i20;
            int i22 = i19 ^ (i20 | i21);
            iBigEndianToInt = iRotateLeft5 ^ (i21 & i22);
            iBigEndianToInt2 = Integers.rotateLeft(i22, 31);
            iBigEndianToInt3 = Integers.rotateLeft(i21, 27);
            iBigEndianToInt4 = Integers.rotateLeft(i20, 30);
        }
    }
}
