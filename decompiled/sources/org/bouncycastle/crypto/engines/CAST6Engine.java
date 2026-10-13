package org.bouncycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/CAST6Engine.class */
public final class CAST6Engine extends CAST5Engine {
    protected static final int ROUNDS = 12;
    protected static final int BLOCK_SIZE = 16;
    protected int[] _Kr = new int[48];
    protected int[] _Km = new int[48];
    protected int[] _Tr = new int[192];
    protected int[] _Tm = new int[192];
    private int[] _workingKey = new int[8];

    @Override // org.bouncycastle.crypto.engines.CAST5Engine, org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "CAST6";
    }

    @Override // org.bouncycastle.crypto.engines.CAST5Engine, org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.bouncycastle.crypto.engines.CAST5Engine, org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.engines.CAST5Engine
    protected void setKey(byte[] bArr) {
        int i = 1518500249;
        int i2 = 19;
        for (int i3 = 0; i3 < 24; i3++) {
            for (int i4 = 0; i4 < 8; i4++) {
                this._Tm[(i3 * 8) + i4] = i;
                i += 1859775393;
                this._Tr[(i3 * 8) + i4] = i2;
                i2 = (i2 + 17) & 31;
            }
        }
        byte[] bArr2 = new byte[64];
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
        for (int i5 = 0; i5 < 8; i5++) {
            this._workingKey[i5] = BytesTo32bits(bArr2, i5 * 4);
        }
        for (int i6 = 0; i6 < 12; i6++) {
            int i7 = i6 * 2 * 8;
            int[] iArr = this._workingKey;
            iArr[6] = iArr[6] ^ F1(this._workingKey[7], this._Tm[i7], this._Tr[i7]);
            int[] iArr2 = this._workingKey;
            iArr2[5] = iArr2[5] ^ F2(this._workingKey[6], this._Tm[i7 + 1], this._Tr[i7 + 1]);
            int[] iArr3 = this._workingKey;
            iArr3[4] = iArr3[4] ^ F3(this._workingKey[5], this._Tm[i7 + 2], this._Tr[i7 + 2]);
            int[] iArr4 = this._workingKey;
            iArr4[3] = iArr4[3] ^ F1(this._workingKey[4], this._Tm[i7 + 3], this._Tr[i7 + 3]);
            int[] iArr5 = this._workingKey;
            iArr5[2] = iArr5[2] ^ F2(this._workingKey[3], this._Tm[i7 + 4], this._Tr[i7 + 4]);
            int[] iArr6 = this._workingKey;
            iArr6[1] = iArr6[1] ^ F3(this._workingKey[2], this._Tm[i7 + 5], this._Tr[i7 + 5]);
            int[] iArr7 = this._workingKey;
            iArr7[0] = iArr7[0] ^ F1(this._workingKey[1], this._Tm[i7 + 6], this._Tr[i7 + 6]);
            int[] iArr8 = this._workingKey;
            iArr8[7] = iArr8[7] ^ F2(this._workingKey[0], this._Tm[i7 + 7], this._Tr[i7 + 7]);
            int i8 = ((i6 * 2) + 1) * 8;
            int[] iArr9 = this._workingKey;
            iArr9[6] = iArr9[6] ^ F1(this._workingKey[7], this._Tm[i8], this._Tr[i8]);
            int[] iArr10 = this._workingKey;
            iArr10[5] = iArr10[5] ^ F2(this._workingKey[6], this._Tm[i8 + 1], this._Tr[i8 + 1]);
            int[] iArr11 = this._workingKey;
            iArr11[4] = iArr11[4] ^ F3(this._workingKey[5], this._Tm[i8 + 2], this._Tr[i8 + 2]);
            int[] iArr12 = this._workingKey;
            iArr12[3] = iArr12[3] ^ F1(this._workingKey[4], this._Tm[i8 + 3], this._Tr[i8 + 3]);
            int[] iArr13 = this._workingKey;
            iArr13[2] = iArr13[2] ^ F2(this._workingKey[3], this._Tm[i8 + 4], this._Tr[i8 + 4]);
            int[] iArr14 = this._workingKey;
            iArr14[1] = iArr14[1] ^ F3(this._workingKey[2], this._Tm[i8 + 5], this._Tr[i8 + 5]);
            int[] iArr15 = this._workingKey;
            iArr15[0] = iArr15[0] ^ F1(this._workingKey[1], this._Tm[i8 + 6], this._Tr[i8 + 6]);
            int[] iArr16 = this._workingKey;
            iArr16[7] = iArr16[7] ^ F2(this._workingKey[0], this._Tm[i8 + 7], this._Tr[i8 + 7]);
            this._Kr[i6 * 4] = this._workingKey[0] & 31;
            this._Kr[(i6 * 4) + 1] = this._workingKey[2] & 31;
            this._Kr[(i6 * 4) + 2] = this._workingKey[4] & 31;
            this._Kr[(i6 * 4) + 3] = this._workingKey[6] & 31;
            this._Km[i6 * 4] = this._workingKey[7];
            this._Km[(i6 * 4) + 1] = this._workingKey[5];
            this._Km[(i6 * 4) + 2] = this._workingKey[3];
            this._Km[(i6 * 4) + 3] = this._workingKey[1];
        }
    }

    @Override // org.bouncycastle.crypto.engines.CAST5Engine
    protected int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArr = new int[4];
        CAST_Encipher(BytesTo32bits(bArr, i), BytesTo32bits(bArr, i + 4), BytesTo32bits(bArr, i + 8), BytesTo32bits(bArr, i + 12), iArr);
        Bits32ToBytes(iArr[0], bArr2, i2);
        Bits32ToBytes(iArr[1], bArr2, i2 + 4);
        Bits32ToBytes(iArr[2], bArr2, i2 + 8);
        Bits32ToBytes(iArr[3], bArr2, i2 + 12);
        return 16;
    }

    @Override // org.bouncycastle.crypto.engines.CAST5Engine
    protected int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArr = new int[4];
        CAST_Decipher(BytesTo32bits(bArr, i), BytesTo32bits(bArr, i + 4), BytesTo32bits(bArr, i + 8), BytesTo32bits(bArr, i + 12), iArr);
        Bits32ToBytes(iArr[0], bArr2, i2);
        Bits32ToBytes(iArr[1], bArr2, i2 + 4);
        Bits32ToBytes(iArr[2], bArr2, i2 + 8);
        Bits32ToBytes(iArr[3], bArr2, i2 + 12);
        return 16;
    }

    protected final void CAST_Encipher(int i, int i2, int i3, int i4, int[] iArr) {
        for (int i5 = 0; i5 < 6; i5++) {
            int i6 = i5 * 4;
            i3 ^= F1(i4, this._Km[i6], this._Kr[i6]);
            i2 ^= F2(i3, this._Km[i6 + 1], this._Kr[i6 + 1]);
            i ^= F3(i2, this._Km[i6 + 2], this._Kr[i6 + 2]);
            i4 ^= F1(i, this._Km[i6 + 3], this._Kr[i6 + 3]);
        }
        for (int i7 = 6; i7 < 12; i7++) {
            int i8 = i7 * 4;
            i4 ^= F1(i, this._Km[i8 + 3], this._Kr[i8 + 3]);
            i ^= F3(i2, this._Km[i8 + 2], this._Kr[i8 + 2]);
            i2 ^= F2(i3, this._Km[i8 + 1], this._Kr[i8 + 1]);
            i3 ^= F1(i4, this._Km[i8], this._Kr[i8]);
        }
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
    }

    protected final void CAST_Decipher(int i, int i2, int i3, int i4, int[] iArr) {
        for (int i5 = 0; i5 < 6; i5++) {
            int i6 = (11 - i5) * 4;
            i3 ^= F1(i4, this._Km[i6], this._Kr[i6]);
            i2 ^= F2(i3, this._Km[i6 + 1], this._Kr[i6 + 1]);
            i ^= F3(i2, this._Km[i6 + 2], this._Kr[i6 + 2]);
            i4 ^= F1(i, this._Km[i6 + 3], this._Kr[i6 + 3]);
        }
        for (int i7 = 6; i7 < 12; i7++) {
            int i8 = (11 - i7) * 4;
            i4 ^= F1(i, this._Km[i8 + 3], this._Kr[i8 + 3]);
            i ^= F3(i2, this._Km[i8 + 2], this._Kr[i8 + 2]);
            i2 ^= F2(i3, this._Km[i8 + 1], this._Kr[i8 + 1]);
            i3 ^= F1(i4, this._Km[i8], this._Kr[i8]);
        }
        iArr[0] = i;
        iArr[1] = i2;
        iArr[2] = i3;
        iArr[3] = i4;
    }
}
