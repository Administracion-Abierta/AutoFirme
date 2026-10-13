package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/ChaChaEngine.class */
public class ChaChaEngine extends Salsa20Engine {
    public ChaChaEngine() {
    }

    public ChaChaEngine(int i) {
        super(i);
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine, org.bouncycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "ChaCha" + this.rounds;
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void advanceCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        if (i > 0) {
            int[] iArr = this.engineState;
            iArr[13] = iArr[13] + i;
        }
        int i3 = this.engineState[12];
        int[] iArr2 = this.engineState;
        iArr2[12] = iArr2[12] + i2;
        if (i3 == 0 || this.engineState[12] >= i3) {
            return;
        }
        int[] iArr3 = this.engineState;
        iArr3[13] = iArr3[13] + 1;
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void advanceCounter() {
        int[] iArr = this.engineState;
        int i = iArr[12] + 1;
        iArr[12] = i;
        if (i == 0) {
            int[] iArr2 = this.engineState;
            iArr2[13] = iArr2[13] + 1;
        }
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void retreatCounter(long j) {
        int i = (int) (j >>> 32);
        int i2 = (int) j;
        if (i != 0) {
            if ((((long) this.engineState[13]) & 4294967295L) < (((long) i) & 4294967295L)) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            int[] iArr = this.engineState;
            iArr[13] = iArr[13] - i;
        }
        if ((((long) this.engineState[12]) & 4294967295L) >= (((long) i2) & 4294967295L)) {
            int[] iArr2 = this.engineState;
            iArr2[12] = iArr2[12] - i2;
        } else {
            if (this.engineState[13] == 0) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            int[] iArr3 = this.engineState;
            iArr3[13] = iArr3[13] - 1;
            int[] iArr4 = this.engineState;
            iArr4[12] = iArr4[12] - i2;
        }
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void retreatCounter() {
        if (this.engineState[12] == 0 && this.engineState[13] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        int[] iArr = this.engineState;
        int i = iArr[12] - 1;
        iArr[12] = i;
        if (i == -1) {
            int[] iArr2 = this.engineState;
            iArr2[13] = iArr2[13] - 1;
        }
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected long getCounter() {
        return (((long) this.engineState[13]) << 32) | (((long) this.engineState[12]) & 4294967295L);
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void resetCounter() {
        int[] iArr = this.engineState;
        this.engineState[13] = 0;
        iArr[12] = 0;
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void setKey(byte[] bArr, byte[] bArr2) {
        if (bArr != null) {
            if (bArr.length != 16 && bArr.length != 32) {
                throw new IllegalArgumentException(getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            packTauOrSigma(bArr.length, this.engineState, 0);
            Pack.littleEndianToInt(bArr, 0, this.engineState, 4, 4);
            Pack.littleEndianToInt(bArr, bArr.length - 16, this.engineState, 8, 4);
        }
        Pack.littleEndianToInt(bArr2, 0, this.engineState, 14, 2);
    }

    @Override // org.bouncycastle.crypto.engines.Salsa20Engine
    protected void generateKeyStream(byte[] bArr) {
        chachaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, bArr, 0);
    }

    public static void chachaCore(int i, int[] iArr, int[] iArr2) {
        if (iArr.length != 16) {
            throw new IllegalArgumentException();
        }
        if (iArr2.length != 16) {
            throw new IllegalArgumentException();
        }
        if (i % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int i2 = iArr[0];
        int i3 = iArr[1];
        int i4 = iArr[2];
        int i5 = iArr[3];
        int iRotateLeft = iArr[4];
        int iRotateLeft2 = iArr[5];
        int iRotateLeft3 = iArr[6];
        int iRotateLeft4 = iArr[7];
        int i6 = iArr[8];
        int i7 = iArr[9];
        int i8 = iArr[10];
        int i9 = iArr[11];
        int iRotateLeft5 = iArr[12];
        int iRotateLeft6 = iArr[13];
        int iRotateLeft7 = iArr[14];
        int iRotateLeft8 = iArr[15];
        for (int i10 = i; i10 > 0; i10 -= 2) {
            int i11 = i2 + iRotateLeft;
            int iRotateLeft9 = Integers.rotateLeft(iRotateLeft5 ^ i11, 16);
            int i12 = i6 + iRotateLeft9;
            int iRotateLeft10 = Integers.rotateLeft(iRotateLeft ^ i12, 12);
            int i13 = i11 + iRotateLeft10;
            int iRotateLeft11 = Integers.rotateLeft(iRotateLeft9 ^ i13, 8);
            int i14 = i12 + iRotateLeft11;
            int iRotateLeft12 = Integers.rotateLeft(iRotateLeft10 ^ i14, 7);
            int i15 = i3 + iRotateLeft2;
            int iRotateLeft13 = Integers.rotateLeft(iRotateLeft6 ^ i15, 16);
            int i16 = i7 + iRotateLeft13;
            int iRotateLeft14 = Integers.rotateLeft(iRotateLeft2 ^ i16, 12);
            int i17 = i15 + iRotateLeft14;
            int iRotateLeft15 = Integers.rotateLeft(iRotateLeft13 ^ i17, 8);
            int i18 = i16 + iRotateLeft15;
            int iRotateLeft16 = Integers.rotateLeft(iRotateLeft14 ^ i18, 7);
            int i19 = i4 + iRotateLeft3;
            int iRotateLeft17 = Integers.rotateLeft(iRotateLeft7 ^ i19, 16);
            int i20 = i8 + iRotateLeft17;
            int iRotateLeft18 = Integers.rotateLeft(iRotateLeft3 ^ i20, 12);
            int i21 = i19 + iRotateLeft18;
            int iRotateLeft19 = Integers.rotateLeft(iRotateLeft17 ^ i21, 8);
            int i22 = i20 + iRotateLeft19;
            int iRotateLeft20 = Integers.rotateLeft(iRotateLeft18 ^ i22, 7);
            int i23 = i5 + iRotateLeft4;
            int iRotateLeft21 = Integers.rotateLeft(iRotateLeft8 ^ i23, 16);
            int i24 = i9 + iRotateLeft21;
            int iRotateLeft22 = Integers.rotateLeft(iRotateLeft4 ^ i24, 12);
            int i25 = i23 + iRotateLeft22;
            int iRotateLeft23 = Integers.rotateLeft(iRotateLeft21 ^ i25, 8);
            int i26 = i24 + iRotateLeft23;
            int iRotateLeft24 = Integers.rotateLeft(iRotateLeft22 ^ i26, 7);
            int i27 = i13 + iRotateLeft16;
            int iRotateLeft25 = Integers.rotateLeft(iRotateLeft23 ^ i27, 16);
            int i28 = i22 + iRotateLeft25;
            int iRotateLeft26 = Integers.rotateLeft(iRotateLeft16 ^ i28, 12);
            i2 = i27 + iRotateLeft26;
            iRotateLeft8 = Integers.rotateLeft(iRotateLeft25 ^ i2, 8);
            i8 = i28 + iRotateLeft8;
            iRotateLeft2 = Integers.rotateLeft(iRotateLeft26 ^ i8, 7);
            int i29 = i17 + iRotateLeft20;
            int iRotateLeft27 = Integers.rotateLeft(iRotateLeft11 ^ i29, 16);
            int i30 = i26 + iRotateLeft27;
            int iRotateLeft28 = Integers.rotateLeft(iRotateLeft20 ^ i30, 12);
            i3 = i29 + iRotateLeft28;
            iRotateLeft5 = Integers.rotateLeft(iRotateLeft27 ^ i3, 8);
            i9 = i30 + iRotateLeft5;
            iRotateLeft3 = Integers.rotateLeft(iRotateLeft28 ^ i9, 7);
            int i31 = i21 + iRotateLeft24;
            int iRotateLeft29 = Integers.rotateLeft(iRotateLeft15 ^ i31, 16);
            int i32 = i14 + iRotateLeft29;
            int iRotateLeft30 = Integers.rotateLeft(iRotateLeft24 ^ i32, 12);
            i4 = i31 + iRotateLeft30;
            iRotateLeft6 = Integers.rotateLeft(iRotateLeft29 ^ i4, 8);
            i6 = i32 + iRotateLeft6;
            iRotateLeft4 = Integers.rotateLeft(iRotateLeft30 ^ i6, 7);
            int i33 = i25 + iRotateLeft12;
            int iRotateLeft31 = Integers.rotateLeft(iRotateLeft19 ^ i33, 16);
            int i34 = i18 + iRotateLeft31;
            int iRotateLeft32 = Integers.rotateLeft(iRotateLeft12 ^ i34, 12);
            i5 = i33 + iRotateLeft32;
            iRotateLeft7 = Integers.rotateLeft(iRotateLeft31 ^ i5, 8);
            i7 = i34 + iRotateLeft7;
            iRotateLeft = Integers.rotateLeft(iRotateLeft32 ^ i7, 7);
        }
        iArr2[0] = i2 + iArr[0];
        iArr2[1] = i3 + iArr[1];
        iArr2[2] = i4 + iArr[2];
        iArr2[3] = i5 + iArr[3];
        iArr2[4] = iRotateLeft + iArr[4];
        iArr2[5] = iRotateLeft2 + iArr[5];
        iArr2[6] = iRotateLeft3 + iArr[6];
        iArr2[7] = iRotateLeft4 + iArr[7];
        iArr2[8] = i6 + iArr[8];
        iArr2[9] = i7 + iArr[9];
        iArr2[10] = i8 + iArr[10];
        iArr2[11] = i9 + iArr[11];
        iArr2[12] = iRotateLeft5 + iArr[12];
        iArr2[13] = iRotateLeft6 + iArr[13];
        iArr2[14] = iRotateLeft7 + iArr[14];
        iArr2[15] = iRotateLeft8 + iArr[15];
    }
}
