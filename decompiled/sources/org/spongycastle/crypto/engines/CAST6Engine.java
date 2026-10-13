package org.spongycastle.crypto.engines;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/CAST6Engine.class */
public final class CAST6Engine extends CAST5Engine {
    protected static final int ROUNDS = 12;
    protected static final int BLOCK_SIZE = 16;
    protected int[] _Kr = new int[48];
    protected int[] _Km = new int[48];
    protected int[] _Tr = new int[192];
    protected int[] _Tm = new int[192];
    private int[] _workingKey = new int[8];

    @Override // org.spongycastle.crypto.engines.CAST5Engine, org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "CAST6";
    }

    @Override // org.spongycastle.crypto.engines.CAST5Engine, org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.spongycastle.crypto.engines.CAST5Engine, org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.engines.CAST5Engine
    protected void setKey(byte[] key) {
        int Cm = 1518500249;
        int Cr = 19;
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 8; j++) {
                this._Tm[(i * 8) + j] = Cm;
                Cm += 1859775393;
                this._Tr[(i * 8) + j] = Cr;
                Cr = (Cr + 17) & 31;
            }
        }
        byte[] tmpKey = new byte[64];
        int length = key.length;
        System.arraycopy(key, 0, tmpKey, 0, length);
        for (int i2 = 0; i2 < 8; i2++) {
            this._workingKey[i2] = BytesTo32bits(tmpKey, i2 * 4);
        }
        for (int i3 = 0; i3 < 12; i3++) {
            int i4 = i3 * 2 * 8;
            int[] iArr = this._workingKey;
            iArr[6] = iArr[6] ^ F1(this._workingKey[7], this._Tm[i4], this._Tr[i4]);
            int[] iArr2 = this._workingKey;
            iArr2[5] = iArr2[5] ^ F2(this._workingKey[6], this._Tm[i4 + 1], this._Tr[i4 + 1]);
            int[] iArr3 = this._workingKey;
            iArr3[4] = iArr3[4] ^ F3(this._workingKey[5], this._Tm[i4 + 2], this._Tr[i4 + 2]);
            int[] iArr4 = this._workingKey;
            iArr4[3] = iArr4[3] ^ F1(this._workingKey[4], this._Tm[i4 + 3], this._Tr[i4 + 3]);
            int[] iArr5 = this._workingKey;
            iArr5[2] = iArr5[2] ^ F2(this._workingKey[3], this._Tm[i4 + 4], this._Tr[i4 + 4]);
            int[] iArr6 = this._workingKey;
            iArr6[1] = iArr6[1] ^ F3(this._workingKey[2], this._Tm[i4 + 5], this._Tr[i4 + 5]);
            int[] iArr7 = this._workingKey;
            iArr7[0] = iArr7[0] ^ F1(this._workingKey[1], this._Tm[i4 + 6], this._Tr[i4 + 6]);
            int[] iArr8 = this._workingKey;
            iArr8[7] = iArr8[7] ^ F2(this._workingKey[0], this._Tm[i4 + 7], this._Tr[i4 + 7]);
            int i5 = ((i3 * 2) + 1) * 8;
            int[] iArr9 = this._workingKey;
            iArr9[6] = iArr9[6] ^ F1(this._workingKey[7], this._Tm[i5], this._Tr[i5]);
            int[] iArr10 = this._workingKey;
            iArr10[5] = iArr10[5] ^ F2(this._workingKey[6], this._Tm[i5 + 1], this._Tr[i5 + 1]);
            int[] iArr11 = this._workingKey;
            iArr11[4] = iArr11[4] ^ F3(this._workingKey[5], this._Tm[i5 + 2], this._Tr[i5 + 2]);
            int[] iArr12 = this._workingKey;
            iArr12[3] = iArr12[3] ^ F1(this._workingKey[4], this._Tm[i5 + 3], this._Tr[i5 + 3]);
            int[] iArr13 = this._workingKey;
            iArr13[2] = iArr13[2] ^ F2(this._workingKey[3], this._Tm[i5 + 4], this._Tr[i5 + 4]);
            int[] iArr14 = this._workingKey;
            iArr14[1] = iArr14[1] ^ F3(this._workingKey[2], this._Tm[i5 + 5], this._Tr[i5 + 5]);
            int[] iArr15 = this._workingKey;
            iArr15[0] = iArr15[0] ^ F1(this._workingKey[1], this._Tm[i5 + 6], this._Tr[i5 + 6]);
            int[] iArr16 = this._workingKey;
            iArr16[7] = iArr16[7] ^ F2(this._workingKey[0], this._Tm[i5 + 7], this._Tr[i5 + 7]);
            this._Kr[i3 * 4] = this._workingKey[0] & 31;
            this._Kr[(i3 * 4) + 1] = this._workingKey[2] & 31;
            this._Kr[(i3 * 4) + 2] = this._workingKey[4] & 31;
            this._Kr[(i3 * 4) + 3] = this._workingKey[6] & 31;
            this._Km[i3 * 4] = this._workingKey[7];
            this._Km[(i3 * 4) + 1] = this._workingKey[5];
            this._Km[(i3 * 4) + 2] = this._workingKey[3];
            this._Km[(i3 * 4) + 3] = this._workingKey[1];
        }
    }

    @Override // org.spongycastle.crypto.engines.CAST5Engine
    protected int encryptBlock(byte[] src, int srcIndex, byte[] dst, int dstIndex) {
        int[] result = new int[4];
        int A = BytesTo32bits(src, srcIndex);
        int B = BytesTo32bits(src, srcIndex + 4);
        int C = BytesTo32bits(src, srcIndex + 8);
        int D = BytesTo32bits(src, srcIndex + 12);
        CAST_Encipher(A, B, C, D, result);
        Bits32ToBytes(result[0], dst, dstIndex);
        Bits32ToBytes(result[1], dst, dstIndex + 4);
        Bits32ToBytes(result[2], dst, dstIndex + 8);
        Bits32ToBytes(result[3], dst, dstIndex + 12);
        return 16;
    }

    @Override // org.spongycastle.crypto.engines.CAST5Engine
    protected int decryptBlock(byte[] src, int srcIndex, byte[] dst, int dstIndex) {
        int[] result = new int[4];
        int A = BytesTo32bits(src, srcIndex);
        int B = BytesTo32bits(src, srcIndex + 4);
        int C = BytesTo32bits(src, srcIndex + 8);
        int D = BytesTo32bits(src, srcIndex + 12);
        CAST_Decipher(A, B, C, D, result);
        Bits32ToBytes(result[0], dst, dstIndex);
        Bits32ToBytes(result[1], dst, dstIndex + 4);
        Bits32ToBytes(result[2], dst, dstIndex + 8);
        Bits32ToBytes(result[3], dst, dstIndex + 12);
        return 16;
    }

    protected final void CAST_Encipher(int A, int B, int C, int D, int[] result) {
        for (int i = 0; i < 6; i++) {
            int x = i * 4;
            C ^= F1(D, this._Km[x], this._Kr[x]);
            B ^= F2(C, this._Km[x + 1], this._Kr[x + 1]);
            A ^= F3(B, this._Km[x + 2], this._Kr[x + 2]);
            D ^= F1(A, this._Km[x + 3], this._Kr[x + 3]);
        }
        for (int i2 = 6; i2 < 12; i2++) {
            int x2 = i2 * 4;
            D ^= F1(A, this._Km[x2 + 3], this._Kr[x2 + 3]);
            A ^= F3(B, this._Km[x2 + 2], this._Kr[x2 + 2]);
            B ^= F2(C, this._Km[x2 + 1], this._Kr[x2 + 1]);
            C ^= F1(D, this._Km[x2], this._Kr[x2]);
        }
        result[0] = A;
        result[1] = B;
        result[2] = C;
        result[3] = D;
    }

    protected final void CAST_Decipher(int A, int B, int C, int D, int[] result) {
        for (int i = 0; i < 6; i++) {
            int x = (11 - i) * 4;
            C ^= F1(D, this._Km[x], this._Kr[x]);
            B ^= F2(C, this._Km[x + 1], this._Kr[x + 1]);
            A ^= F3(B, this._Km[x + 2], this._Kr[x + 2]);
            D ^= F1(A, this._Km[x + 3], this._Kr[x + 3]);
        }
        for (int i2 = 6; i2 < 12; i2++) {
            int x2 = (11 - i2) * 4;
            D ^= F1(A, this._Km[x2 + 3], this._Kr[x2 + 3]);
            A ^= F3(B, this._Km[x2 + 2], this._Kr[x2 + 2]);
            B ^= F2(C, this._Km[x2 + 1], this._Kr[x2 + 1]);
            C ^= F1(D, this._Km[x2], this._Kr[x2]);
        }
        result[0] = A;
        result[1] = B;
        result[2] = C;
        result[3] = D;
    }
}
