package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/engines/LEAEngine.class */
public class LEAEngine implements BlockCipher {
    private static final int BASEROUNDS = 16;
    private static final int NUMWORDS = 4;
    private static final int NUMWORDS128 = 4;
    private static final int MASK128 = 3;
    private static final int NUMWORDS192 = 6;
    private static final int NUMWORDS256 = 8;
    private static final int MASK256 = 7;
    private static final int BLOCKSIZE = 16;
    private static final int KEY0 = 0;
    private static final int KEY1 = 1;
    private static final int KEY2 = 2;
    private static final int KEY3 = 3;
    private static final int KEY4 = 4;
    private static final int KEY5 = 5;
    private static final int ROT1 = 1;
    private static final int ROT3 = 3;
    private static final int ROT5 = 5;
    private static final int ROT6 = 6;
    private static final int ROT9 = 9;
    private static final int ROT11 = 11;
    private static final int ROT13 = 13;
    private static final int ROT17 = 17;
    private static final int[] DELTA = {-1007687205, 1147300610, 2044886154, 2027892972, 1902027934, -947529206, -531697110, -440137385};
    private final int[] theBlock = new int[4];
    private int theRounds;
    private int[][] theRoundKeys;
    private boolean forEncryption;

    @Override // org.bouncycastle.crypto.BlockCipher
    public void init(boolean z, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to LEA init - " + cipherParameters.getClass().getName());
        }
        byte[] key = ((KeyParameter) cipherParameters).getKey();
        int length = key.length;
        if ((length << 1) % 16 != 0 || length < 16 || length > 32) {
            throw new IllegalArgumentException("KeyBitSize must be 128, 192 or 256");
        }
        this.forEncryption = z;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(getAlgorithmName(), length * 8, cipherParameters, Utils.getPurpose(this.forEncryption)));
        generateRoundKeys(key);
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "LEA";
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        checkBuffer(bArr, i, false);
        checkBuffer(bArr2, i2, true);
        return this.forEncryption ? encryptBlock(bArr, i, bArr2, i2) : decryptBlock(bArr, i, bArr2, i2);
    }

    private static int bufLength(byte[] bArr) {
        if (bArr == null) {
            return 0;
        }
        return bArr.length;
    }

    private static void checkBuffer(byte[] bArr, int i, boolean z) {
        int iBufLength = bufLength(bArr);
        int i2 = i + 16;
        if ((i < 0 || i2 < 0) || i2 > iBufLength) {
            if (!z) {
                throw new DataLengthException("Input buffer too short.");
            }
        }
    }

    private int encryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        Pack.littleEndianToInt(bArr, i, this.theBlock, 0, 4);
        for (int i3 = 0; i3 < this.theRounds; i3++) {
            encryptRound(i3);
        }
        Pack.intToLittleEndian(this.theBlock, bArr2, i2);
        return 16;
    }

    private void encryptRound(int i) {
        int[] iArr = this.theRoundKeys[i];
        int i2 = (3 + i) % 4;
        int iLeftIndex = leftIndex(i2);
        this.theBlock[i2] = ror32((this.theBlock[iLeftIndex] ^ iArr[4]) + (this.theBlock[i2] ^ iArr[5]), 3);
        int iLeftIndex2 = leftIndex(iLeftIndex);
        this.theBlock[iLeftIndex] = ror32((this.theBlock[iLeftIndex2] ^ iArr[2]) + (this.theBlock[iLeftIndex] ^ iArr[3]), 5);
        this.theBlock[iLeftIndex2] = rol32((this.theBlock[leftIndex(iLeftIndex2)] ^ iArr[0]) + (this.theBlock[iLeftIndex2] ^ iArr[1]), 9);
    }

    private static int leftIndex(int i) {
        if (i == 0) {
            return 3;
        }
        return i - 1;
    }

    private int decryptBlock(byte[] bArr, int i, byte[] bArr2, int i2) {
        Pack.littleEndianToInt(bArr, i, this.theBlock, 0, 4);
        for (int i3 = this.theRounds - 1; i3 >= 0; i3--) {
            decryptRound(i3);
        }
        Pack.intToLittleEndian(this.theBlock, bArr2, i2);
        return 16;
    }

    private void decryptRound(int i) {
        int[] iArr = this.theRoundKeys[i];
        int i2 = i % 4;
        int iRightIndex = rightIndex(i2);
        this.theBlock[iRightIndex] = (ror32(this.theBlock[iRightIndex], 9) - (this.theBlock[i2] ^ iArr[0])) ^ iArr[1];
        int iRightIndex2 = rightIndex(iRightIndex);
        this.theBlock[iRightIndex2] = (rol32(this.theBlock[iRightIndex2], 5) - (this.theBlock[iRightIndex] ^ iArr[2])) ^ iArr[3];
        int iRightIndex3 = rightIndex(iRightIndex2);
        this.theBlock[iRightIndex3] = (rol32(this.theBlock[iRightIndex3], 3) - (this.theBlock[iRightIndex2] ^ iArr[4])) ^ iArr[5];
    }

    private static int rightIndex(int i) {
        if (i == 3) {
            return 0;
        }
        return i + 1;
    }

    private void generateRoundKeys(byte[] bArr) {
        this.theRounds = (bArr.length >> 1) + 16;
        this.theRoundKeys = new int[this.theRounds][6];
        int length = bArr.length / 4;
        int[] iArr = new int[length];
        Pack.littleEndianToInt(bArr, 0, iArr, 0, length);
        switch (length) {
            case 4:
                generate128RoundKeys(iArr);
                break;
            case 5:
            case 7:
            case 8:
            default:
                generate256RoundKeys(iArr);
                break;
            case 6:
                generate192RoundKeys(iArr);
                break;
        }
    }

    private void generate128RoundKeys(int[] iArr) {
        for (int i = 0; i < this.theRounds; i++) {
            int iRol32 = rol32(DELTA[i & 3], i);
            int i2 = 0 + 1;
            iArr[0] = rol32(iArr[0] + iRol32, 1);
            int i3 = iArr[i2];
            int i4 = i2 + 1;
            iArr[i2] = rol32(i3 + rol32(iRol32, i2), 3);
            int i5 = iArr[i4];
            int i6 = i4 + 1;
            iArr[i4] = rol32(i5 + rol32(iRol32, i4), 6);
            iArr[i6] = rol32(iArr[i6] + rol32(iRol32, i6), 11);
            int[] iArr2 = this.theRoundKeys[i];
            iArr2[0] = iArr[0];
            iArr2[1] = iArr[1];
            iArr2[2] = iArr[2];
            iArr2[3] = iArr[1];
            iArr2[4] = iArr[3];
            iArr2[5] = iArr[1];
        }
    }

    private void generate192RoundKeys(int[] iArr) {
        for (int i = 0; i < this.theRounds; i++) {
            int iRol32 = rol32(DELTA[i % 6], i);
            int i2 = 0 + 1;
            iArr[0] = rol32(iArr[0] + rol32(iRol32, 0), 1);
            int i3 = iArr[i2];
            int i4 = i2 + 1;
            iArr[i2] = rol32(i3 + rol32(iRol32, i2), 3);
            int i5 = iArr[i4];
            int i6 = i4 + 1;
            iArr[i4] = rol32(i5 + rol32(iRol32, i4), 6);
            int i7 = iArr[i6];
            int i8 = i6 + 1;
            iArr[i6] = rol32(i7 + rol32(iRol32, i6), 11);
            int i9 = iArr[i8];
            int i10 = i8 + 1;
            iArr[i8] = rol32(i9 + rol32(iRol32, i8), 13);
            iArr[i10] = rol32(iArr[i10] + rol32(iRol32, i10), 17);
            System.arraycopy(iArr, 0, this.theRoundKeys[i], 0, i10 + 1);
        }
    }

    private void generate256RoundKeys(int[] iArr) {
        int i = 0;
        for (int i2 = 0; i2 < this.theRounds; i2++) {
            int iRol32 = rol32(DELTA[i2 & 7], i2);
            int[] iArr2 = this.theRoundKeys[i2];
            iArr2[0] = rol32(iArr[i & 7] + iRol32, 1);
            int i3 = i;
            int i4 = i + 1;
            int i5 = 0 + 1;
            iArr[i3 & 7] = iArr2[0];
            iArr2[i5] = rol32(iArr[i4 & 7] + rol32(iRol32, i5), 3);
            int i6 = i4 + 1;
            int i7 = i5 + 1;
            iArr[i4 & 7] = iArr2[i5];
            iArr2[i7] = rol32(iArr[i6 & 7] + rol32(iRol32, i7), 6);
            int i8 = i6 + 1;
            int i9 = i7 + 1;
            iArr[i6 & 7] = iArr2[i7];
            iArr2[i9] = rol32(iArr[i8 & 7] + rol32(iRol32, i9), 11);
            int i10 = i8 + 1;
            int i11 = i9 + 1;
            iArr[i8 & 7] = iArr2[i9];
            iArr2[i11] = rol32(iArr[i10 & 7] + rol32(iRol32, i11), 13);
            int i12 = i10 + 1;
            int i13 = i11 + 1;
            iArr[i10 & 7] = iArr2[i11];
            iArr2[i13] = rol32(iArr[i12 & 7] + rol32(iRol32, i13), 17);
            i = i12 + 1;
            iArr[i12 & 7] = iArr2[i13];
        }
    }

    private static int rol32(int i, int i2) {
        return (i << i2) | (i >>> (32 - i2));
    }

    private static int ror32(int i, int i2) {
        return (i >>> i2) | (i << (32 - i2));
    }
}
